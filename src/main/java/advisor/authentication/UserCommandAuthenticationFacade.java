package advisor.authentication;

public class UserCommandAuthenticationFacade {

    private final UserCommandAuthentication userCommandAuthentication;

    public UserCommandAuthenticationFacade(
            UserCommandAuthentication userCommandAuthentication) {
        this.userCommandAuthentication = userCommandAuthentication;
    }

    public String getAccessToken() {
        return userCommandAuthentication.getAccessToken();
    }
}
