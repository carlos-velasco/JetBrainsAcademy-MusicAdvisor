package advisor.authentication;

public class NeverAuthenticatedUserCommandAuthentication implements UserCommandAuthentication {

    @Override
    public boolean authenticate() {
        return false;
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
