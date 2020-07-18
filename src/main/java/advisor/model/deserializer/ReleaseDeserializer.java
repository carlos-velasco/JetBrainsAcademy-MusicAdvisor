package advisor.model.deserializer;

import advisor.model.dto.Artist;
import advisor.model.dto.Release;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ReleaseDeserializer implements JsonDeserializer<Release> {

    @Override
    public Release deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String title = jsonObject.get("name").getAsString();
        String link = jsonObject.getAsJsonObject("external_urls").get("spotify").getAsString();
        List<Artist> artists = new Gson().fromJson(jsonObject.getAsJsonArray("artists"),
                new TypeToken<List<Artist>>() {}.getType());
        return Release.builder()
                .title(title)
                .link(link)
                .artists(artists)
                .build();
    }
}
