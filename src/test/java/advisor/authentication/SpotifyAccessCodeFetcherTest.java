package advisor.authentication;

import advisor.utils.FreePortExtension;
import advisor.view.CommandLineView;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.*;

import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

final class SpotifyAccessCodeFetcherTest {
    private static final String SPOTIFY_ACCESS_HOST = "mySpotifyHost";
    private static final String CLIENT_ID = "myClientId";
    private static final int TEST_EXECUTION_SERVICE_AWAIT_SECONDS = 2;
    private final HttpClient client = HttpClient.newBuilder().build();
    private final URIBuilder uriBuilder = new URIBuilder();
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final CommandLineView commandLineView = new CommandLineView(new Scanner(System.in), new PrintStream(output), 5);
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String redirectUri;
    private SpotifyAccessCodeFetcher target;

    @RegisterExtension
    final FreePortExtension freePort = new FreePortExtension();

    @BeforeEach
    void setUriSchemeHostAndPort() throws URISyntaxException {
        uriBuilder.setScheme("http").setHost("localhost").setPort(freePort.getPort());
        redirectUri = uriBuilder.build().toString();
        int accessCodeServerTimeoutSeconds = 1;
        target = new SpotifyAccessCodeFetcher(
                SPOTIFY_ACCESS_HOST, CLIENT_ID, redirectUri, commandLineView, accessCodeServerTimeoutSeconds);
    }

    @AfterEach
    void shutDownExecutor() {
        executorService.shutdownNow();
    }

    @Test
    void givenQueryToRedirectUriContainsAccessCode_whenFetchingTheAccessCode_thenItIsReturned() throws IOException, InterruptedException, ExecutionException, URISyntaxException {
        String accessCode = "ds7894564_fda0";
        uriBuilder.addParameter("code", accessCode);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .GET()
                .build();
        
        // WHEN
        Future<Optional<String>> result = executorService.submit(target::fetchAccessCode);
        executorService.shutdown();
        client.send(request, BodyHandlers.ofString());
        executorService.awaitTermination(TEST_EXECUTION_SERVICE_AWAIT_SECONDS, TimeUnit.SECONDS);
        
        // THEN
        assertThat(result.isDone()).isTrue();
        assertThat((result.get())).isNotEmpty().contains(accessCode);
    }

    @Test
    void givenQueryToRedirectUriDoesNotContainAccessCode_whenFetchingTheAccessCode_thenItIsNotReturned() throws ExecutionException, InterruptedException, IOException, URISyntaxException {
        // GIVEN
        uriBuilder.addParameter("randomParam", "123456");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .GET()
                .build();

        // WHEN
        Future<Optional<String>> result = executorService.submit(target::fetchAccessCode);
        executorService.shutdown();
        client.send(request, BodyHandlers.ofString());
        executorService.awaitTermination(TEST_EXECUTION_SERVICE_AWAIT_SECONDS, TimeUnit.SECONDS);
        
        // THEN
        assertThat(result.isDone()).isTrue();
        assertThat((result.get())).isEmpty();
    }

    @Test
    void givenQueryToRedirectUriIsEmpty_whenFetchingTheAccessCode_thenItIsNotReturned() throws ExecutionException, InterruptedException, IOException, URISyntaxException {
        // GIVEN
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .GET()
                .build();
        
        // WHEN
        Future<Optional<String>> result = executorService.submit(target::fetchAccessCode);
        executorService.shutdown();
        client.send(request, BodyHandlers.ofString());
        executorService.awaitTermination(TEST_EXECUTION_SERVICE_AWAIT_SECONDS, TimeUnit.SECONDS);
        
        // THEN
        assertThat(result.isDone()).isTrue();
        assertThat((result.get())).isEmpty();
    }

    @Test
    void whenFetchingTheAccessCode_thenCommandLineMessagesArePrinted() throws InterruptedException, IOException, URISyntaxException {
        // GIVEN
        uriBuilder.addParameter("randomParam", "123456");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .GET()
                .build();
        
        // WHEN
        executorService.submit(target::fetchAccessCode);
        executorService.shutdown();
        client.send(request, BodyHandlers.ofString());
        executorService.awaitTermination(TEST_EXECUTION_SERVICE_AWAIT_SECONDS, TimeUnit.SECONDS);
        
        // THEN
        String accessCodeUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=code",
                SPOTIFY_ACCESS_HOST + "/authorize", CLIENT_ID, redirectUri);
        String expectedMessages = "use this link to request the access code:" + System.lineSeparator()
                + accessCodeUrl + System.lineSeparator()
                + "waiting for code..." + System.lineSeparator();
        assertThat(output).hasToString(expectedMessages);
    }

    @Test
    void givenQueryToRedirectUriContainsAccessCode_whenFetchingTheAccessCode_thenResponseContainsCorrectMessage() throws IOException, InterruptedException, URISyntaxException {
        // GIVEN
        String accessCode = "ds7894564_fda0";
        uriBuilder.addParameter("code", accessCode);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .GET()
                .build();
        
        // WHEN
        executorService.submit(target::fetchAccessCode);
        executorService.shutdown();
        HttpResponse<String> httpResponse = client.send(request, BodyHandlers.ofString());
        executorService.awaitTermination(TEST_EXECUTION_SERVICE_AWAIT_SECONDS, TimeUnit.SECONDS);

        // THEN
        assertThat(httpResponse.statusCode()).isEqualTo(SC_OK);
        assertThat(httpResponse.body()).isEqualTo("Got the code. Return back to your program.");
    }

    @Test
    void givenQueryToRedirectUriDoesNotContainAccessCode_whenFetchingTheAccessCode_thenResponseContainsCorrectMessage() throws InterruptedException, IOException, URISyntaxException {
        // GIVEN
        uriBuilder.addParameter("randomParam", "123456");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .GET()
                .build();
        
        // WHEN
        executorService.submit(target::fetchAccessCode);
        executorService.shutdown();
        HttpResponse<String> httpResponse = client.send(request, BodyHandlers.ofString());
        executorService.awaitTermination(TEST_EXECUTION_SERVICE_AWAIT_SECONDS, TimeUnit.SECONDS);

        // THEN
        assertThat(httpResponse.statusCode()).isEqualTo(SC_OK);
        assertThat(httpResponse.body()).isEqualTo("Not found authorization code. Try again.");
    }

    @Test
    @Timeout(value = TEST_EXECUTION_SERVICE_AWAIT_SECONDS)
    void givenNoRequestToAccessCodeServerIsDone_whenFetchingTheAccessCode_thenServerTimesOutAfterSpecifiedTimeout() throws InterruptedException, ExecutionException {
        
        // WHEN
        Future<Optional<String>> result = executorService.submit(target::fetchAccessCode);
        executorService.shutdown();
        executorService.awaitTermination(TEST_EXECUTION_SERVICE_AWAIT_SECONDS, TimeUnit.SECONDS);
        
        // THEN
        assertThat(result.isDone()).isTrue();
        assertThat((result.get())).isEmpty();
    }
}

