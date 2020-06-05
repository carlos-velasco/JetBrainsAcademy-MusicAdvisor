package advisor.authentication;

import advisor.view.CommandLineView;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SpotifyAccessCodeFetcher {

    public static final int ACCESS_CODE_SERVER_TIMEOUT_SECONDS = 3 * 60;

    private final String spotifyAccessHost;
    private final String clientId;
    private final String redirectUri;
    private final CommandLineView commandLineView;
    private int serverTimeoutSeconds;
    private String accessCode;

    public SpotifyAccessCodeFetcher(String spotifyAccessHost, String clientId, String redirectUri, CommandLineView commandLineView, int serverTimeoutSeconds) {
        this.spotifyAccessHost = spotifyAccessHost;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.commandLineView = commandLineView;
        this.serverTimeoutSeconds = serverTimeoutSeconds;
    }

    public Optional<String> fetchAccessCode() throws IOException, InterruptedException {
        final String codeReceivedMessage = "Got the code. Return back to your program.";
        final String codeNotReceivedMessage = "Not found authorization code. Try again.";
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(getRedirectUriPort()), 0);
        server.createContext("/",
                exchange -> {
                    Optional<String> theAccessCode = getAccessCode(exchange.getRequestURI().getRawQuery());
                    String message = theAccessCode.isPresent() ? codeReceivedMessage : codeNotReceivedMessage;
                    accessCode = theAccessCode.orElse(null);
                    exchange.sendResponseHeaders(HttpStatus.SC_OK, message.length());
                    exchange.getResponseBody().write(message.getBytes());
                    exchange.getResponseBody().close();
                    if (theAccessCode.isPresent()) {
                        server.stop(0);
                        executorService.shutdownNow();
                    }
                });
        server.setExecutor(executorService);
        server.start();
        commandLineView.printMessage("use this link to request the access code:");
        commandLineView.printMessage(buildAccessCodeUrl());
        commandLineView.printMessage("waiting for code...");
        if (!executorService.awaitTermination(serverTimeoutSeconds, TimeUnit.SECONDS)) {
            // Stop server if the timeout is elapsed, to be able to launch it again next time on the same port
            server.stop(0);
        }

        return Optional.ofNullable(accessCode);
}

    private Optional<String> getAccessCode(String query) {
        if (query == null) {
            return Optional.empty();
        }

        return Arrays.stream(query.split("&"))
                .map(parameter -> URLDecoder.decode(parameter, StandardCharsets.UTF_8))
                .map(parameter -> parameter.split("="))
                .filter(parameter -> parameter.length == 2 && parameter[0].equals("code"))
                .findFirst()
                .map(parameter -> parameter[1]);
    }

    private String buildAccessCodeUrl() {
        return String.format("%s?" +
                "client_id=%s" +
                "&redirect_uri=%s" +
                "&response_type=code", spotifyAccessHost + "/authorize", clientId, redirectUri);
    }

    private int getRedirectUriPort() {
        String[] redirectUriByPortSeparator = redirectUri.split(":");
        return Integer.parseInt(redirectUriByPortSeparator[redirectUriByPortSeparator.length - 1]);
    }
}
