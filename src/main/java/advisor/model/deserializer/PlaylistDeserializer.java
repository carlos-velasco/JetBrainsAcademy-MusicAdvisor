package advisor.model.deserializer;

import advisor.model.dto.Playlist;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

public class PlaylistDeserializer implements JsonDeserializer<Playlist> {

    @Override
    public Playlist deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        String title = jsonObject.get("name").getAsString();
        String link = jsonObject.getAsJsonObject("external_urls").get("spotify").getAsString();
        return Playlist.builder()
                .title(title)
                .link(link)
                .build();
    }
}
