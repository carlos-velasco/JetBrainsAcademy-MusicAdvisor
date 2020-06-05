package advisor.authentication;

import advisor.view.CommandLineView;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.is;

public final class SpotifyAccessTokenFetcherTest {
    private final String spotifyAccessHost = "http://localhost";
    private final String clientId = "myClientId";
    private final String clientSecret = "myClientSecret";
    private final String redirectUri = "myRedirectUri";
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final CommandLineView commandLineView = new CommandLineView(new Scanner(System.in), new PrintStream(output), 5);
    private SpotifyAccessTokenFetcher target;
    
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void configureTarget() {
        target = new SpotifyAccessTokenFetcher(
                spotifyAccessHost + ":" + wireMockRule.port(), clientId, clientSecret, redirectUri, commandLineView);
    }

    @Test
    public void givenValidResponse_whenRequestingAccessToken_thenAccessTokenIsReturned() throws InterruptedException, IOException, URISyntaxException {
        // GIVEN
        String expectedAccessToken = "BQBSZ0CA3KR0cf0LxmiNK_E87ZqnkJKDD89VOWAZ9f0QXJcsCiHtl5Om-" +
                "EVhkIfwt1AZs5WeXgfEF69e4JxL3YX6IIW9zl9WegTmgLkb4xLXWwhryty488CLoL2SM9VIY6H" +
                "aHgxYxdmRFGWSzrgH3dEqcvPoLpd26D8Y";
        stubFor(post("/api/token")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildValidResponseBody(expectedAccessToken))));

        // WHEN
        Optional<String> accessToken = target.fetchAccessToken("myAccessCode");

        // THEN
        Assert.assertTrue(accessToken.isPresent());
        Assert.assertThat(accessToken.get(), is(expectedAccessToken));
    }

    @Test
    public void givenValidResponse_whenRequestingAccessToken_thenSuccessfulMessagesAndAccessTokenArePrinted() throws InterruptedException, IOException, URISyntaxException {
        // GIVEN
        String expectedAccessToken = "BQBSZ0CA3KR0cf0LxmiNK_E87ZqnkJKDD89VOWAZ9f0QXJcsCiHtl5Om-" +
                "EVhkIfwt1AZs5WeXgfEF69e4JxL3YX6IIW9zl9WegTmgLkb4xLXWwhryty488CLoL2SM9VIY6H" +
                "aHgxYxdmRFGWSzrgH3dEqcvPoLpd26D8Y";

        stubFor(post("/api/token")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildValidResponseBody(expectedAccessToken))));

        // WHEN
        target.fetchAccessToken("myAccessCode");

        // THEN
        String expectedMessages = "making http request for access_token..." + System.lineSeparator()
                + "response:" + System.lineSeparator()
                + buildValidResponseBody(expectedAccessToken) + System.lineSeparator();
        Assert.assertThat(output.toString(), is(expectedMessages));
    }

    @Test
    public void givenInvalidResponse_whenRequestingAccessToken_thenNoAcessTokenIsReturned() throws InterruptedException, IOException, URISyntaxException {
        // GIVEN
        stubFor(post("/api/token")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildInvalidResponseBody())));

        // WHEN
        Optional<String> accessToken = target.fetchAccessToken("myAccessCode");

        // THEN
        Assert.assertFalse(accessToken.isPresent());
    }

    @Test
    public void givenInvalidResponse_whenRequestingAccessToken_thenErrorMessagesArePrinted() throws InterruptedException, IOException, URISyntaxException {
        // GIVEN
        stubFor(post("/api/token")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildInvalidResponseBody())));

        // WHEN
        target.fetchAccessToken("myAccessCode");

        // THEN
        String expectedMessages = "making http request for access_token..." + System.lineSeparator()
                + "Auth token not retrieved" + System.lineSeparator()
                + "Status code: 400" + System.lineSeparator()
                + "Response body: " + buildInvalidResponseBody() + System.lineSeparator();
        Assert.assertThat(output.toString(), is(expectedMessages));
    }

    @Test
    public void givenValidResponse_whenRequestingAccessToken_thenCallDoneWithCorrectHeaderAndBody() throws InterruptedException, IOException, URISyntaxException {
        // GIVEN
        String expectedAccessToken = "BQBSZ0CA3KR0cf0LxmiNK_E87ZqnkJKDD89VOWAZ9f0QXJcsCiHtl5Om-" +
                "EVhkIfwt1AZs5WeXgfEF69e4JxL3YX6IIW9zl9WegTmgLkb4xLXWwhryty488CLoL2SM9VIY6H" +
                "aHgxYxdmRFGWSzrgH3dEqcvPoLpd26D8Y";
        stubFor(post("/api/token")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildValidResponseBody(expectedAccessToken))));

        // WHEN
        String accessCode = "myAccessCode";
        target.fetchAccessToken(accessCode);

        // THEN
        String expectedBase64EncodedClientData =
                Base64.getEncoder().encodeToString(String.join(":", clientId, clientSecret).getBytes());
        String expectedRequestBody = String.join("&",
                List.of("grant_type=authorization_code",
                        "code=" + accessCode,
                        "redirect_uri=" + redirectUri));
        WireMock.verify(postRequestedFor(urlEqualTo("/api/token"))
                .withHeader("Authorization", equalTo("Basic " + expectedBase64EncodedClientData))
                .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
                .withRequestBody(equalTo(expectedRequestBody)));
    }

    private String buildValidResponseBody(String accessToken) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("access_token", accessToken);
        jsonObject.addProperty("expires_in", 3600);
        jsonObject.addProperty("token_type", "Bearer");
        jsonObject.addProperty("refresh_token", "" +
                "AQCSmdQsvsvpneadsdq1brfKlbEWleTE3nprDwPbZgNSge5dVe_svYBG-RG-_" +
                "PxIGxVvA7gSnehFJjDRAczLDbbdWPjW1yUq2gtKbbNrCQVAH5ZB" +
                "tY8wAYskmOIW7zn3IEiBzg");
        jsonObject.addProperty("scope", "");
        return jsonObject.toString();
    }

    private String buildInvalidResponseBody() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("error", "wrong data");
        return jsonObject.toString();
    }
}
