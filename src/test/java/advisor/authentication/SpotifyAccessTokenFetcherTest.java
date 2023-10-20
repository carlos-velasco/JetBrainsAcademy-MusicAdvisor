package advisor.authentication;

import advisor.authentication.dto.SpotifyAccessTokenResponse;
import advisor.view.CommandLineView;
import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static junit.framework.TestCase.assertEquals;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(WireMockExtension.class)
final class SpotifyAccessTokenFetcherTest {
    private static final String CLIENT_ID = "myClientId";
    private static final String CLIENT_SECRET = "myClientSecret";
    private static final String REDIRECT_URI = "myRedirectUri";
    private static final String ACCESS_TOKEN = "BQBSZ0CA3KR0cf0LxmiNK_E87ZqnkJKDD89VOWAZ9f0QXJcsCiHtl5Om-" +
            "EVhkIfwt1AZs5WeXgfEF69e4JxL3YX6IIW9zl9WegTmgLkb4xLXWwhryty488CLoL2SM9VIY6H" +
            "aHgxYxdmRFGWSzrgH3dEqcvPoLpd26D8Y";
    private static final String API_TOKEN_URL_PATH = "/api/token";
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final CommandLineView commandLineView =
            new CommandLineView(new Scanner(System.in), new PrintStream(output), 5);
    private SpotifyAccessTokenFetcher target;

    @Managed
    private final WireMockServer wireMockServer = with(wireMockConfig().dynamicPort());

    @BeforeEach
    void configureTarget() {
        String spotifyAccessHost = "http://localhost";
        target = new SpotifyAccessTokenFetcher(
                spotifyAccessHost + ":" + wireMockServer.port(),
                CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, commandLineView);
    }

    @Test
    void givenValidResponse_whenRequestingAccessToken_thenAccessTokenIsReturned() {
        // GIVEN
        stubFor(post(API_TOKEN_URL_PATH)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody(new Gson().toJson(buildValidResponseBody()))));

        // WHEN
        Optional<String> accessToken = target.fetchAccessToken("myAccessCode");

        // THEN
        assertThat(accessToken).isNotEmpty().contains(ACCESS_TOKEN);
    }

    @Test
    void givenValidResponse_whenRequestingAccessToken_thenSuccessfulMessagesAndAccessTokenArePrinted() {
        // GIVEN
        final String requestBody = new Gson().toJson(buildValidResponseBody());
        stubFor(post(API_TOKEN_URL_PATH)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
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
    void givenInvalidResponse_whenRequestingAccessToken_thenNoAcessTokenIsReturned() {
        // GIVEN
        stubFor(post(API_TOKEN_URL_PATH)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody(buildInvalidResponseBody())));

        // WHEN
        Optional<String> accessToken = target.fetchAccessToken("myAccessCode");

        // THEN
        assertThat(accessToken).isEmpty();
    }

    @Test
    void givenInvalidResponse_whenRequestingAccessToken_thenErrorMessagesArePrinted() {
        // GIVEN
        stubFor(post(API_TOKEN_URL_PATH)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
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
    void givenValidResponse_whenRequestingAccessToken_thenCallDoneWithCorrectHeaderAndBody() {
        // GIVEN
        stubFor(post(API_TOKEN_URL_PATH)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody(new Gson().toJson(buildValidResponseBody()))));

        // WHEN
        String accessCode = "myAccessCode";
        target.fetchAccessToken(accessCode);

        // THEN
        String expectedBase64EncodedClientData =
                Base64.getEncoder().encodeToString(String.join(":", CLIENT_ID, CLIENT_SECRET).getBytes());
        String expectedRequestBody = String.join("&",
                List.of("code=" + accessCode,
                        "grant_type=authorization_code",
                        "redirect_uri=" + REDIRECT_URI));
        RequestPatternBuilder builder = postRequestedFor(urlEqualTo(API_TOKEN_URL_PATH)).withRequestBody(equalTo(expectedRequestBody));
        List<ContentPattern<?>> bodyPatterns = builder.build().getBodyPatterns();
        if (bodyPatterns.size() != 1) {
            throw new IllegalArgumentException("Incorrect number of request bodies.");
        }
        String requestBody = (String) bodyPatterns.get(0).getValue();
        String sortedRequestBody = sortPostRequestBody(requestBody);
        int idx = expectedRequestBody.indexOf("code=");
        String unsortedInput = expectedRequestBody.substring(idx);
        String sortedInput = sortPostRequestBody(unsortedInput);
        String sortedExpectedRequestBody = expectedRequestBody.substring(0, idx) + sortedInput;
        assertEquals(sortedExpectedRequestBody, sortedRequestBody);
        verify(postRequestedFor(urlEqualTo(API_TOKEN_URL_PATH))
                .withHeader("Authorization", equalTo("Basic " + expectedBase64EncodedClientData))
                .withHeader(CONTENT_TYPE, equalTo("application/x-www-form-urlencoded; charset=UTF-8")));
    }

    private SpotifyAccessTokenResponse buildValidResponseBody() {
        return new SpotifyAccessTokenResponse(SpotifyAccessTokenFetcherTest.ACCESS_TOKEN, 3600, "Bearer",
                "AQCSmdQsvsvpneadsdq1brfKlbEWleTE3nprDwPbZgNSge5dVe_svYBG-RG-_" +
                        "PxIGxVvA7gSnehFJjDRAczLDbbdWPjW1yUq2gtKbbNrCQVAH5ZB" +
                        "tY8wAYskmOIW7zn3IEiBzg", "");
    }

    private String buildInvalidResponseBody() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("error", "wrong data");
        return jsonObject.toString();
    }

    private String sortPostRequestBody(String input) {
        String[] unsortedFields = input.split("&");
        Arrays.sort(unsortedFields);
        StringBuilder sb = new StringBuilder();
        for (String s: unsortedFields) {
            sb.append(s);
            sb.append("&");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }
}