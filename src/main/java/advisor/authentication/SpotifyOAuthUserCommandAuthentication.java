package advisor.authentication;

import advisor.model.service.SpotifyAdvisorException;
import advisor.view.CommandLineView;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class SpotifyOAuthUserCommandAuthentication implements UserCommandAuthentication {

    private boolean isAuthenticated;
    private String accessToken;
    private final SpotifyAccessCodeFetcher spotifyAccessCodeFetcher;
    private final SpotifyAccessTokenFetcher spotifyAccessTokenFetcher;
    private final CommandLineView commandLineView;

    @Override
    public void authenticate() {
        try {
            fetchAccessCodeAndAccessToken();
        } catch (Exception e) {
            throw new SpotifyAdvisorException(e);
        }
    }

    private void fetchAccessCodeAndAccessToken() throws IOException, InterruptedException {
        spotifyAccessCodeFetcher.fetchAccessCode().ifPresentOrElse(
                accessCode -> {
                    commandLineView.printMessage("code received");
                    fetchAccessTokenAndSetAuthenticationState(accessCode); },
                () -> commandLineView.printMessage("code not received"));
    }

    private void fetchAccessTokenAndSetAuthenticationState(String accessCode) {
        spotifyAccessTokenFetcher.fetchAccessToken(accessCode).ifPresentOrElse(
                token -> {
                    this.accessToken = token;
                    isAuthenticated = true; },
                () -> commandLineView.printMessage("token not received"));
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
