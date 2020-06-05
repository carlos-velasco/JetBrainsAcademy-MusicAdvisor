package advisor.authentication;

import advisor.view.CommandLineView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class SpotifyAccessTokenFetcher {

    private final String spotifyAccessHost;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final CommandLineView commandLineView;
    private final HttpClient client = HttpClient.newHttpClient();

    public SpotifyAccessTokenFetcher(
            String spotifyAccessHost,
            String clientId,
            String clientSecret,
            String redirectUri,
            CommandLineView commandLineView) {
        this.spotifyAccessHost = spotifyAccessHost;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.commandLineView = commandLineView;
    }

    public Optional<String> fetchAccessToken(String accessCode) throws URISyntaxException, IOException, InterruptedException {
        URIBuilder uriBuilder = new URIBuilder(this.spotifyAccessHost)
                .setPath("/api/token");

        // Hacky way of creating application/x-www-form-urlencoded body
        String body = String.join("&",
                List.of("grant_type=authorization_code",
                        "code=" + accessCode,
                        "redirect_uri=" + redirectUri));

        String base64EncodedClientData = Base64.getEncoder().encodeToString(
                String.join(":", clientId, clientSecret).getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Basic " + base64EncodedClientData)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        commandLineView.printMessage("making http request for access_token...");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != HttpStatus.SC_OK) {
            commandLineView.printMessage("Auth token not retrieved");
            commandLineView.printMessage("Status code: " + response.statusCode());
            if (response.body() != null && !response.body().isEmpty()) {
                commandLineView.printMessage("Response body: " + response.body());
            }
            return Optional.empty();
        }

        commandLineView.printMessage("response:");
        commandLineView.printMessage(response.body());
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        final String accessToken = jsonObject.get("access_token").getAsString();
        return Optional.of(accessToken);
    }
}
