package advisor.authentication;

import advisor.authentication.dto.SpotifyAccessTokenRequest;
import advisor.authentication.dto.SpotifyAccessTokenResponse;
import advisor.model.service.SpotifyAdvisorException;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface SpotifyAccessTokenClient {

    @RequestLine("POST /api/token")
    @Headers({
            "Content-Type: application/x-www-form-urlencoded",
            "Authorization: Basic {base64EncodedClientData}"})
    SpotifyAccessTokenResponse fetchAccessToken(SpotifyAccessTokenRequest spotifyAccessTokenRequest,
                                                @Param("base64EncodedClientData") String base64EncodedClientData) throws SpotifyAdvisorException;
}
