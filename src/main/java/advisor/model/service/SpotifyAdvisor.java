package advisor.model.service;

import advisor.authentication.UserCommandAuthenticationFacade;
import advisor.model.AdvisorException;
import advisor.model.deserializer.PlaylistDeserializer;
import advisor.model.deserializer.ReleaseDeserializer;
import advisor.model.dto.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Locale;

public class SpotifyAdvisor implements Advisor {

    private static final String RESOURCE_COMMON_PATH = "/v1/browse/";

    private final String spotifyResourceHost;
    private final UserCommandAuthenticationFacade userCommandAuthenticationFacade;
    private final int pageSize;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Release.class, new ReleaseDeserializer())
            .registerTypeAdapter(Playlist.class, new PlaylistDeserializer())
            .create();

    public SpotifyAdvisor(String spotifyResourceHost,
                          UserCommandAuthenticationFacade userCommandAuthenticationFacade,
                          int pageSize) {
        this.spotifyResourceHost = spotifyResourceHost;
        this.userCommandAuthenticationFacade = userCommandAuthenticationFacade;
        this.pageSize = pageSize;
    }

    @Override
    public Page<Category> getCategories(int pageNumber) throws AdvisorException {
        return getResourceChunk(buildResourceURI("categories", pageNumber),
                pageNumber, "categories", Category.class);
    }

    @Override
    public Page<Category> getCategories() throws AdvisorException {
        return getResourceChunk(buildResourceURI("categories"),
                0, "categories", Category.class);
    }

    @Override
    public Page<Release> getNewReleases(int pageNumber) throws AdvisorException {
        return getResourceChunk(buildResourceURI(
                "new-releases", pageNumber),
                pageNumber, "albums", Release.class);
    }

    @Override
    public Page<Playlist> getCategoryPlaylists(Category category, int pageNumber) throws AdvisorException {
        return getResourceChunk(
                buildResourceURI("categories/" + category.getId() + "/playlists", pageNumber),
                pageNumber,"playlists", Playlist.class);
    }

    @Override
    public Page<Playlist> getFeaturedPlaylists(int pageNumber) throws AdvisorException {
        return getResourceChunk(
                buildResourceURI("featured-playlists", pageNumber),
                pageNumber, "playlists", Playlist.class);
    }

    private URI buildResourceURI(String resourceCollectionPath) {
        return buildResourceURI(resourceCollectionPath, null);
    }

    private URI buildResourceURI(String resourceCollectionPath, Integer pageNumber) {
        try {
            URIBuilder uriBuilder = new URIBuilder(spotifyResourceHost)
                    .setPath(RESOURCE_COMMON_PATH + resourceCollectionPath)
                    .addParameter("country", Locale.getDefault().getCountry())
                    .addParameter("locale", Locale.getDefault().toString());

            if (pageNumber != null) {
                uriBuilder
                        .addParameter("limit", String.valueOf(pageSize))
                        .addParameter("offset", String.valueOf((pageNumber - 1) * pageSize));
            }
            return uriBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends CommandLinePrintable> Page<T> getResourceChunk(
            URI resourceCollectionURI,
            int pageNumber,
            String entityKey,
            Class<T> type) throws AdvisorException {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(resourceCollectionURI)
                    .GET();

            String responseBody = sendRequest(requestBuilder);
            JsonObject collection = JsonParser.parseString(responseBody).getAsJsonObject()
                    .get(entityKey).getAsJsonObject();
            return new Page<>(gson.fromJson(
                    collection.getAsJsonArray("items"), TypeToken.getParameterized(List.class, type).getType()),
                    collection.get("total").getAsInt(),
                    pageNumber);
        } catch (AdvisorException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String sendRequest(HttpRequest.Builder requestBuilder) throws IOException, InterruptedException, AdvisorException {
        requestBuilder
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + userCommandAuthenticationFacade.getAccessToken());

        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.SC_OK) {
            String errorMessage = "";
            try {
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                if (jsonObject.keySet().contains("error")) {
                    JsonObject errorObject = jsonObject.get("error").getAsJsonObject();
                    if (errorObject.keySet().contains("message")) {
                        errorMessage = errorObject.get("message").getAsString();
                    }
                }
            } catch (Exception ignored) {
                errorMessage = response.body();
            }
            throw new AdvisorException(errorMessage);
        }
        return response.body();
    }
}
