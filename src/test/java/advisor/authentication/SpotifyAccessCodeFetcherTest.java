package advisor.authentication;

import advisor.view.CommandLineView;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

import static org.hamcrest.core.Is.is;

public final class SpotifyAccessCodeFetcherTest {
    private final String spotifyAccessHost = "mySpotifyHost";
    private final String clientId = "myClientId";
    private final int serverPort = 45456;
    private final String redirectUri = "http://localhost" + ":" + serverPort;
    private final int accessCodeServerTimeoutSeconds = 1;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final CommandLineView commandLineView;
    private final ExecutorService executorService;
    private final SpotifyAccessCodeFetcher target;
    private final HttpClient client;
    private final URIBuilder uriBuilder;
    private final int testExecutionServiceAwaitSeconds = 2;

    public SpotifyAccessCodeFetcherTest() {
        commandLineView = new CommandLineView(new Scanner(System.in), new PrintStream(output), 5);
        executorService = Executors.newSingleThreadExecutor();
        target = new SpotifyAccessCodeFetcher(
                spotifyAccessHost, clientId, redirectUri, commandLineView, accessCodeServerTimeoutSeconds);
        client = HttpClient.newBuilder().build();
        uriBuilder = new URIBuilder();
    }

    @Before
    public void setUriSchemeHostAndPort() {
        uriBuilder.setScheme("http").setHost("localhost").setPort(serverPort);
    }

    @After
    public void shutDownExecutor() {
        executorService.shutdownNow();
    }

    @Test
    public void givenQueryToRedirectUriContainsAccessCode_whenFetchingTheAccessCode_thenItIsReturned() throws IOException, InterruptedException, ExecutionException, URISyntaxException {
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
        executorService.awaitTermination(testExecutionServiceAwaitSeconds, TimeUnit.SECONDS);
        
        // THEN
        Assert.assertTrue(result.isDone());
        Optional<String> optionalAccessCode = result.get();
        Assert.assertTrue(optionalAccessCode.isPresent());
        Assert.assertThat(optionalAccessCode.get(), is(accessCode));
    }

    @Test
    public void givenQueryToRedirectUriDoesNotContainAccessCode_whenFetchingTheAccessCode_thenItIsNotReturned() throws ExecutionException, InterruptedException, IOException, URISyntaxException {
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
        executorService.awaitTermination(testExecutionServiceAwaitSeconds, TimeUnit.SECONDS);
        
        // THEN
        Assert.assertTrue(result.isDone());
        Optional<String> optionalAccessCode = result.get();
        Assert.assertTrue(optionalAccessCode.isEmpty());
    }

    @Test
    public void givenQueryToRedirectUriIsEmpty_whenFetchingTheAccessCode_thenItIsNotReturned() throws ExecutionException, InterruptedException, IOException, URISyntaxException {
        // GIVEN
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .GET()
                .build();
        
        // WHEN
        Future<Optional<String>> result = executorService.submit(target::fetchAccessCode);
        executorService.shutdown();
        client.send(request, BodyHandlers.ofString());
        executorService.awaitTermination(testExecutionServiceAwaitSeconds, TimeUnit.SECONDS);
        
        // THEN
        Assert.assertTrue(result.isDone());
        Optional<String> optionalAccessCode = result.get();
        Assert.assertTrue(optionalAccessCode.isEmpty());
    }

    @Test
    public void whenFetchingTheAccessCode_thenCommandLineMessagesArePrinted() throws InterruptedException, IOException, URISyntaxException {
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
        executorService.awaitTermination(testExecutionServiceAwaitSeconds, TimeUnit.SECONDS);
        
        // THEN
        String accessCodeUrl = String.format("%s?client_id=%s&redirect_uri=%s&response_type=code",
                spotifyAccessHost + "/authorize", clientId, redirectUri);
        String expectedMessages = "use this link to request the access code:" + System.lineSeparator()
                + accessCodeUrl + System.lineSeparator()
                + "waiting for code..." + System.lineSeparator();
        Assert.assertThat(output.toString(), is(expectedMessages));
    }

    @Test
    public void givenQueryToRedirectUriContainsAccessCode_whenFetchingTheAccessCode_thenResponseContainsCorrectMessage() throws IOException, InterruptedException, URISyntaxException {
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
        executorService.awaitTermination(testExecutionServiceAwaitSeconds, TimeUnit.SECONDS);

        // THEN        
        Assert.assertThat(httpResponse.statusCode(), is(HttpStatus.SC_OK));
        Assert.assertThat(httpResponse.body(), is("Got the code. Return back to your program."));
    }

    @Test
    public void givenQueryToRedirectUriDoesNotContainAccessCode_whenFetchingTheAccessCode_thenResponseContainsCorrectMessage() throws InterruptedException, IOException, URISyntaxException {
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
        executorService.awaitTermination(testExecutionServiceAwaitSeconds, TimeUnit.SECONDS);

        // THEN        
        Assert.assertThat(httpResponse.statusCode(), is(HttpStatus.SC_OK));
        Assert.assertThat(httpResponse.body(), is("Not found authorization code. Try again."));
    }

    @Test(timeout = testExecutionServiceAwaitSeconds * 1000)
    public void givenNoRequestToAccessCodeServerIsDone_whenFetchingTheAccessCode_thenServerTimesOutAfterSpecifiedTimeout() throws InterruptedException, ExecutionException {
        
        // WHEN
        Future<Optional<String>> result = executorService.submit(target::fetchAccessCode);
        executorService.shutdown();
        executorService.awaitTermination(testExecutionServiceAwaitSeconds, TimeUnit.SECONDS);
        
        // THEN
        Assert.assertTrue(result.isDone());
        Optional<String> optionalAccessCode = result.get();
        Assert.assertTrue(optionalAccessCode.isEmpty());
    }
}
