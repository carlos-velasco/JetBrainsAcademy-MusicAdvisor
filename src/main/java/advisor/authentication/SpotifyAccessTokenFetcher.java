package advisor.authentication;

import advisor.authentication.dto.SpotifyAccessTokenRequest;
import advisor.authentication.dto.SpotifyAccessTokenResponse;
import advisor.model.service.SpotifyAdvisorException;
import advisor.view.CommandLineView;
import com.google.gson.Gson;
import feign.Feign;
import feign.form.FormEncoder;
import feign.gson.GsonDecoder;

import java.util.Base64;
import java.util.Optional;

public class SpotifyAccessTokenFetcher {

    private final String redirectUri;
    private final CommandLineView commandLineView;
    private final String base64EncodedClientData;
    private final SpotifyAccessTokenClient client;

    public SpotifyAccessTokenFetcher(
            String spotifyAccessHost,
            String clientId,
            String clientSecret,
            String redirectUri,
            CommandLineView commandLineView) {
        this.redirectUri = redirectUri;
        this.commandLineView = commandLineView;

        base64EncodedClientData = Base64.getEncoder().encodeToString(
                String.join(":", clientId, clientSecret).getBytes());

        client = Feign.builder()
                .encoder(new FormEncoder())
                .errorDecoder(new AccessTokenErrorDecoder())
                .decoder(new GsonDecoder())
                .target(SpotifyAccessTokenClient.class, spotifyAccessHost);
    }

    public Optional<String> fetchAccessToken(String accessCode) {
        commandLineView.printMessage("making http request for access_token...");
        try {
            SpotifyAccessTokenResponse spotifyAccessTokenResponse = client.fetchAccessToken(
                    new SpotifyAccessTokenRequest(accessCode, redirectUri), base64EncodedClientData);
            commandLineView.printMessage("response:");
            commandLineView.printMessage(new Gson().toJson(spotifyAccessTokenResponse));
            return Optional.of(spotifyAccessTokenResponse.getAccessToken());
        }
        catch (SpotifyAdvisorException e) {
            commandLineView.printMessage(e.getMessage());
            return Optional.empty();
        }
    }
}
