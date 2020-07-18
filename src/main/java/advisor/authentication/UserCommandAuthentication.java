package advisor.authentication;

public interface UserCommandAuthentication {

    void authenticate();

    boolean isAuthenticated();

    String getAccessToken();
}
