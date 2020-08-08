package advisor.authentication;

import advisor.authentication.dto.SpotifyAccessTokenResponse;
import advisor.view.CommandLineView;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public final class SpotifyAccessTokenFetcherTest {
    private static final String clientId = "myClientId";
    private static final String clientSecret = "myClientSecret";
    private static final String redirectUri = "myRedirectUri";
    private static final String ACCESS_TOKEN = "BQBSZ0CA3KR0cf0LxmiNK_E87ZqnkJKDD89VOWAZ9f0QXJcsCiHtl5Om-" +
            "EVhkIfwt1AZs5WeXgfEF69e4JxL3YX6IIW9zl9WegTmgLkb4xLXWwhryty488CLoL2SM9VIY6H" +
            "aHgxYxdmRFGWSzrgH3dEqcvPoLpd26D8Y";
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final CommandLineView commandLineView =
            new CommandLineView(new Scanner(System.in), new PrintStream(output), 5);
    private SpotifyAccessTokenFetcher target;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void configureTarget() {
        String spotifyAccessHost = "http://localhost";
        target = new SpotifyAccessTokenFetcher(
                spotifyAccessHost + ":" + wireMockRule.port(),
                clientId, clientSecret, redirectUri, commandLineView);
    }

    @Test
    public void givenValidResponse_whenRequestingAccessToken_thenAccessTokenIsReturned() {
        // GIVEN
        stubFor(post("/api/token")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(new Gson().toJson(buildValidResponseBody(ACCESS_TOKEN)))));

        // WHEN
        Optional<String> accessToken = target.fetchAccessToken("myAccessCode");

        // THEN
        assertThat(accessToken).isNotEmpty().contains(ACCESS_TOKEN);
    }

    @Test
    public void givenValidResponse_whenRequestingAccessToken_thenSuccessfulMessagesAndAccessTokenArePrinted() {
        // GIVEN
        final String requestBody = new Gson().toJson(buildValidResponseBody(ACCESS_TOKEN));
        stubFor(post("/api/token")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(requestBody)));

        // WHEN
        target.fetchAccessToken("myAccessCode");

        // THEN
        String expectedMessages = "making http request for access_token..." + System.lineSeparator()
                + "response:" + System.lineSeparator()
                + requestBody + System.lineSeparator();
        assertThat(output).hasToString(expectedMessages);
    }

    @Test
    public void givenInvalidResponse_whenRequestingAccessToken_thenNoAcessTokenIsReturned() {
        // GIVEN
        stubFor(post("/api/token")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader("Content-Type", "application/json")
                        .withBody(buildInvalidResponseBody())));

        // WHEN
        Optional<String> accessToken = target.fetchAccessToken("myAccessCode");

        // THEN
        assertThat(accessToken).isEmpty();
    }

    @Test
    public void givenInvalidResponse_whenRequestingAccessToken_thenErrorMessagesArePrinted() {
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
        assertThat(output).hasToString(expectedMessages);
    }

    @Test
    public void givenValidResponse_whenRequestingAccessToken_thenCallDoneWithCorrectHeaderAndBody() {
        // GIVEN
        stubFor(post("/api/token")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBody(new Gson().toJson(buildValidResponseBody(ACCESS_TOKEN)))));

        // WHEN
        String accessCode = "myAccessCode";
        target.fetchAccessToken(accessCode);

        // THEN
        String expectedBase64EncodedClientData =
                Base64.getEncoder().encodeToString(String.join(":", clientId, clientSecret).getBytes());
        String expectedRequestBody = String.join("&",
                List.of("code=" + accessCode,
                        "grant_type=authorization_code",
                        "redirect_uri=" + redirectUri));
        verify(postRequestedFor(urlEqualTo("/api/token"))
                .withHeader("Authorization", equalTo("Basic " + expectedBase64EncodedClientData))
                .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded; charset=UTF-8"))
                .withRequestBody(equalTo(expectedRequestBody)));
    }

    private SpotifyAccessTokenResponse buildValidResponseBody(String accessToken) {
        return new SpotifyAccessTokenResponse(accessToken, 3600, "Bearer",
                "AQCSmdQsvsvpneadsdq1brfKlbEWleTE3nprDwPbZgNSge5dVe_svYBG-RG-_" +
                        "PxIGxVvA7gSnehFJjDRAczLDbbdWPjW1yUq2gtKbbNrCQVAH5ZB" +
                        "tY8wAYskmOIW7zn3IEiBzg", "");
    }

    private String buildInvalidResponseBody() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("error", "wrong data");
        return jsonObject.toString();
    }
}
