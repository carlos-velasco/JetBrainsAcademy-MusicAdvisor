package advisor.authentication;

public class NeverAuthenticatedUserCommandAuthentication implements UserCommandAuthentication {

    @Override
    public void authenticate() {
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public String getAccessToken() {
        return null;
    }
}
