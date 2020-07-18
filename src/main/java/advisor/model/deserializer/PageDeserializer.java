package advisor.model.deserializer;

import advisor.model.dto.CommandLinePrintable;
import advisor.model.dto.Page;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.List;

@RequiredArgsConstructor
public class PageDeserializer<T extends CommandLinePrintable> implements JsonDeserializer<Page<T>> {

    private final String entityKey;
    private final Class<T> type;
    private final int pageNumber;

    @Override
    public Page <T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject collection = json.getAsJsonObject().get(entityKey).getAsJsonObject();
        return new Page<T>(
                context.deserialize(collection.getAsJsonArray("items"),
                        TypeToken.getParameterized(List.class, type).getType()),
                collection.get("total").getAsInt(),
                pageNumber);
    }
}
