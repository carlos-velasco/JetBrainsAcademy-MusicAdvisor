package advisor.authentication.dto;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class SpotifyAccessTokenResponse {

    @SerializedName("access_token")
    private final String accessToken;
    @SerializedName("expires_in")
    private final int expiresIn;
    @SerializedName("token_type")
    private final String tokenType;
    @SerializedName("refresh_token")
    private final String refreshToken;
    private final String scope;
}
