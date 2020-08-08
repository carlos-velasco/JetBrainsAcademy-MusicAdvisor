package advisor.authentication.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Value;

@Value
public class SpotifyAccessTokenResponse {

    @SerializedName("access_token")
    String accessToken;
    @SerializedName("expires_in")
    int expiresIn;
    @SerializedName("token_type")
    String tokenType;
    @SerializedName("refresh_token")
    String refreshToken;
    String scope;
}
