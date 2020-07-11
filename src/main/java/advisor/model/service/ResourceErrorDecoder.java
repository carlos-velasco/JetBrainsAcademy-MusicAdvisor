package advisor.model.service;

import advisor.model.AdvisorException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import feign.Response;
import feign.codec.ErrorDecoder;

public class ResourceErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        StringBuilder errorMessage = new StringBuilder();
        try {
            JsonObject jsonObject = JsonParser.parseString(response.body().toString()).getAsJsonObject();
            if (jsonObject.keySet().contains("error")) {
                JsonObject errorObject = jsonObject.get("error").getAsJsonObject();
                if (errorObject.keySet().contains("message")) {
                    errorMessage.append(errorObject.get("message").getAsString());
                }
            }
        } catch (Exception ignored) {
            errorMessage.append(response.body());
        }
        return new AdvisorException(errorMessage.toString());
    }
}
