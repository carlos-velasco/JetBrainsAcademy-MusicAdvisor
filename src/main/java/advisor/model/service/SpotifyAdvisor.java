package advisor.model.service;

import advisor.authentication.UserCommandAuthenticationFacade;
import advisor.model.deserializer.PageDeserializer;
import advisor.model.deserializer.PlaylistDeserializer;
import advisor.model.deserializer.ReleaseDeserializer;
import advisor.model.dto.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feign.Feign;
import feign.gson.GsonDecoder;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@RequiredArgsConstructor
public class SpotifyAdvisor implements Advisor {

    private static final String CATEGORIES_RESOURCE_PATH = "categories";
    private static final String CATEGORIES_ENTITY_KEY = "categories";
    private final String spotifyResourceHost;
    private final UserCommandAuthenticationFacade userCommandAuthenticationFacade;
    private final int pageSize;
    private final String locale;

    @Override
    public Page<Category> getCategories(int pageNumber) {
        return getResourcePage(CATEGORIES_RESOURCE_PATH, pageNumber,
                CATEGORIES_ENTITY_KEY, Category.class);
    }

    @Override
    public Page<Category> getCategories() {
        return getResourcePage(CATEGORIES_RESOURCE_PATH, null,
                CATEGORIES_ENTITY_KEY, Category.class);
    }

    @Override
    public Page<Release> getNewReleases(int pageNumber) {
        return getResourcePage(
                "new-releases", pageNumber,
                "albums", Release.class);
    }

    @Override
    public Page<Playlist> getCategoryPlaylists(Category category, int pageNumber) {
        return getResourcePage(
                "categories/" + category.getId() + "/playlists", pageNumber,
                "playlists", Playlist.class);
    }

    @Override
    public Page<Playlist> getFeaturedPlaylists(int pageNumber) {
        return getResourcePage(
                "featured-playlists", pageNumber,
                "playlists", Playlist.class);
    }

    private <T extends CommandLinePrintable> Page<T> getResourcePage(
            String resourcePath,
            Integer pageNumber,
            String entityKey,
            Class<T> type) {

        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(Release.class, new ReleaseDeserializer())
                .registerTypeAdapter(Playlist.class, new PlaylistDeserializer())
                .registerTypeAdapter(Page.class,
                        new PageDeserializer<>(entityKey, type, pageNumber == null ? 0 : pageNumber))
                .create();

        final SpotifyAdvisorClient spotifyAdvisorClient = Feign.builder()
                .decoder(new GsonDecoder(gson))
                .errorDecoder(new ResourceErrorDecoder())
                .target(SpotifyAdvisorClient.class, spotifyResourceHost);

        final String country = Locale.forLanguageTag(locale).getCountry();

        if (pageNumber == null) {
            return spotifyAdvisorClient.resourcePage(
                    resourcePath, userCommandAuthenticationFacade.getAccessToken(),
                    country, locale);
        }
        return spotifyAdvisorClient.resourcePage(
                resourcePath,
                userCommandAuthenticationFacade.getAccessToken(),
                pageSize, (pageNumber - 1) * pageSize,
                country, locale);
    }
}
