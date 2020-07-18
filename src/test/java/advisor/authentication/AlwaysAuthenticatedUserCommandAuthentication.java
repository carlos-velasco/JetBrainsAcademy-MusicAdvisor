package advisor.authentication;

public class AlwaysAuthenticatedUserCommandAuthentication implements UserCommandAuthentication {

    @Override
    public void authenticate() {
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public String getAccessToken() {
        return "accessToken";
    }
}
