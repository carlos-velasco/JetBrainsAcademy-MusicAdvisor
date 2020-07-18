package advisor.authentication;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserCommandAuthenticationFacade {

    private final UserCommandAuthentication userCommandAuthentication;

    public String getAccessToken() {
        return userCommandAuthentication.getAccessToken();
    }
}
