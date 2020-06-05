package advisor.authentication;

import advisor.view.CommandLineView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userCommandAuthentication")
public class SpotifyOAuthUserCommandAuthentication implements UserCommandAuthentication {

    private boolean isAuthenticated;
    private String accessToken;
    private CommandLineView commandLineView;
    private SpotifyAccessCodeFetcher spotifyAccessCodeFetcher;
    private SpotifyAccessTokenFetcher spotifyAccessTokenFetcher;

    @Autowired
    public SpotifyOAuthUserCommandAuthentication(
            SpotifyAccessCodeFetcher spotifyAccessCodeFetcher,
            SpotifyAccessTokenFetcher spotifyAccessTokenFetcher,
            CommandLineView commandLineView) {
        this.spotifyAccessCodeFetcher = spotifyAccessCodeFetcher;
        this.spotifyAccessTokenFetcher = spotifyAccessTokenFetcher;
        this.commandLineView = commandLineView;
    }

    @Override
    public boolean authenticate() {
        try {
            final Optional<String> accessCode = spotifyAccessCodeFetcher.fetchAccessCode();
            if (accessCode.isEmpty()) {
                commandLineView.printMessage("code not received");
                return false;
            }
            commandLineView.printMessage("code received");

            final Optional<String> accessToken = spotifyAccessTokenFetcher.fetchAccessToken(accessCode.get());
            if (accessToken.isEmpty()) {
                commandLineView.printMessage("token not received");
                return false;
            }
            isAuthenticated = true;
            this.accessToken = accessToken.get();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
