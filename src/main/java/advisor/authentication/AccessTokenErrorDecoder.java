package advisor.authentication;

import advisor.model.service.SpotifyAdvisorException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class AccessTokenErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        StringBuilder message = new StringBuilder("Auth token not retrieved").append(System.lineSeparator());
        message.append("Status code: ").append(response.status()).append(System.lineSeparator());
        if (response.body() != null) {
            message.append("Response body: ").append(response.body());
        }
        return new SpotifyAdvisorException(message.toString());
    }
}
