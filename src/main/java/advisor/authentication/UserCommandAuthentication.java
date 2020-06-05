package advisor.authentication;

public interface UserCommandAuthentication {

    boolean authenticate();

    boolean isAuthenticated();

    String getAccessToken();
}
