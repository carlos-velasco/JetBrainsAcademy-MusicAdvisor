package advisor.authentication;

import advisor.view.CommandLineView;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service("userCommandAuthentication")
public class SpotifyOAuthUserCommandAuthentication implements UserCommandAuthentication {

    private boolean isAuthenticated;
    private String accessToken;
    private final SpotifyAccessCodeFetcher spotifyAccessCodeFetcher;
    private final SpotifyAccessTokenFetcher spotifyAccessTokenFetcher;
    private final CommandLineView commandLineView;

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
