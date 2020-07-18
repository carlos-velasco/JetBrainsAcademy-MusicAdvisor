package advisor.authentication.dto;

import feign.form.FormEncoder;
import feign.form.FormProperty;
import lombok.Getter;

/**
 * Fields in this class are not final, so that {@link FormEncoder} can serialize it to application/x-www-form-urlencoded
 */
@Getter
public class SpotifyAccessTokenRequest {

    @FormProperty("grant_type")
    private String grantType = "authorization_code";
    private String code;
    @FormProperty("redirect_uri")
    private String redirectUri;

    public SpotifyAccessTokenRequest(String code, String redirectUri) {
        this.code = code;
        this.redirectUri = redirectUri;
    }
}
