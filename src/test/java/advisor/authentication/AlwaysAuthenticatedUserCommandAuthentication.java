package advisor.authentication;

public class AlwaysAuthenticatedUserCommandAuthentication implements UserCommandAuthentication {

    @Override
    public boolean authenticate() {
        return true;
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
