package advisor.model.service;

import advisor.authentication.AlwaysAuthenticatedUserCommandAuthentication;
import advisor.authentication.UserCommandAuthenticationFacade;
import advisor.model.AdvisorException;
import advisor.model.dto.*;
import com.github.jenspiegsa.wiremockextension.Managed;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(WireMockExtension.class)
final class SpotifyAdvisorTest {

    private static final String RESOURCE_COMMON_PATH = "/v1/browse/";
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String LOCALE = "es-ES";
    private final UserCommandAuthenticationFacade userCommandAuthenticationFacade =
            new UserCommandAuthenticationFacade(new AlwaysAuthenticatedUserCommandAuthentication());
    private SpotifyAdvisor spotifyAdvisor;

    @Managed
    private final WireMockServer wireMockServer = with(wireMockConfig().dynamicPort());

    @BeforeEach
    void prepareTarget() {
        String spotifyResourceHost = "http://localhost";
        spotifyAdvisor = new SpotifyAdvisor(
                spotifyResourceHost + ":" + wireMockServer.port(),
                userCommandAuthenticationFacade,
                DEFAULT_PAGE_SIZE,
                LOCALE);
    }

    @Test
    void givenCategoriesReturnedFromSpotify_whenGettingCategories_thenCategoryPageIsReturned() {
        // GIVEN
        List<Category> expectedCategories = List.of(
                new Category("Top Lists", "toplists"),
                new Category("Mood", "mood"),
                new Category("Party", "party"),
                new Category("Pop", "pop"),
                new Category("Workout", "workout"));
        int total = 31;

        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("categories.json")));

        // WHEN
        final int pageNumber = 5;
        Page<Category> categories = spotifyAdvisor.getCategories(pageNumber);

        // THEN
        assertThat(categories.getElements())
                .as("Categories")
                .containsExactlyElementsOf(expectedCategories);
        assertThat(categories.getTotal())
                .as("Total")
                .isEqualTo(total);
        assertThat(categories.getPageNumber())
                .as("Page number")
                .isEqualTo(pageNumber);
    }

    @Test
    void whenGettingCategories_ThenCorrectHeadersAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("categories.json")));

        // WHEN
        spotifyAdvisor.getCategories(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON.getMimeType()))
                .withHeader("Accept", equalTo(APPLICATION_JSON.getMimeType())));
    }

    @Test
    void whenGettingCategoriesFirstPage_ThenCorrectQueryParamsAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("categories.json")));

        // WHEN
        spotifyAdvisor.getCategories(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withQueryParam("offset", equalTo(String.valueOf(0)))
                .withQueryParam("limit", equalTo(String.valueOf(DEFAULT_PAGE_SIZE))));
    }

    @Test
    void whenGettingCategoriesNthPage_ThenCorrectQueryParamsAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("categories.json")));

        // WHEN
        final int pageNumber = 3;
        spotifyAdvisor.getCategories(pageNumber);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withQueryParam("offset", equalTo(String.valueOf((pageNumber - 1) * DEFAULT_PAGE_SIZE)))
                .withQueryParam("limit", equalTo(String.valueOf(DEFAULT_PAGE_SIZE))));
    }

    @Test
    void whenGettingCategoriesWithNoPaging_ThenNoPagingQueryParamsAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("categories.json")));

        // WHEN
        spotifyAdvisor.getCategories();

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withQueryParam("offset", absent())
                .withQueryParam("limit", absent()));
    }

    @Test
    void givenCategoriesReturnedFromSpotify_whenGettingCategoriesWithNoPaging_thenCategoryPageIsReturned() {
        // GIVEN
        List<Category> expectedCategories = List.of(
                new Category("Top Lists", "toplists"),
                new Category("Mood", "mood"),
                new Category("Party", "party"),
                new Category("Pop", "pop"),
                new Category("Workout", "workout"));
        int total = 31;

        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("categories.json")));

        // WHEN
        Page<Category> categories = spotifyAdvisor.getCategories();

        // THEN
        assertThat(categories.getElements())
                .as("Categories")
                .containsExactlyElementsOf(expectedCategories);
        assertThat(categories.getTotal())
                .as("Total")
                .isEqualTo(total);
    }

    @Test
    void whenGettingCategoriesWithNoPaging_ThenCorrectHeadersAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("categories.json")));

        // WHEN
        spotifyAdvisor.getCategories();

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON.getMimeType()))
                .withHeader("Accept", equalTo(APPLICATION_JSON.getMimeType())));
    }

    @Test
    void givenErrorResponseReturnedFromSpotify_whenGettingCategories_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        String errorMessage = "error when getting categories";
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 400,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> spotifyAdvisor.getCategories(1));

        // THEN
        assertThat(thrown)
                .isInstanceOf(AdvisorException.class).hasMessage(errorMessage);
    }

    @Test
    void givenErrorResponseReturnedFromSpotify_whenGettingCategoriesWithNoPaging_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        String errorMessage = "error when getting categories";
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 400,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> spotifyAdvisor.getCategories());

        // THEN
        assertThat(thrown)
                .isInstanceOf(AdvisorException.class).hasMessage(errorMessage);
    }

    @Test
    void givenNewReleasesReturnedFromSpotify_whenGettingNewReleases_thenNewReleasesPageIsReturned() {
        // GIVEN
        List<Release> expectedReleases = List.of(
                Release.builder().title("Runnin'")
                        .artists(List.of(new Artist("Pharrell Williams"), new Artist("Rosalía")))
                        .link("https://open.spotify.com/album/5ZX4m5aVSmWQ5iHAPQpT71")
                        .build(),
                Release.builder().title("Sneakin’")
                        .artists(List.of(new Artist("Drake")))
                        .link("https://open.spotify.com/album/0geTzdk2InlqIoB16fW9Nd")
                        .build());
        final int total = 500;

        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("new-releases.json")));

        // WHEN
        final int pageNumber = 5;
        Page<Release> releases = spotifyAdvisor.getNewReleases(pageNumber);

        // THEN
        assertThat(releases.getElements())
                .as("New Releases")
                .containsExactlyElementsOf(expectedReleases);
        assertThat(releases.getTotal())
                .as("Total")
                .isEqualTo(total);
        assertThat(releases.getPageNumber())
                .as("Page number")
                .isEqualTo(pageNumber);
    }

    @Test
    void givenErrorResponseReturnedFromSpotify_whenGettingNewReleases_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        String errorMessage = "error when getting releases";
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 400,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> spotifyAdvisor.getNewReleases(1));

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage(errorMessage);
    }

    @Test
    void whenGettingNewReleases_ThenCorrectHeadersAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("new-releases.json")));

        // WHEN
        spotifyAdvisor.getNewReleases(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON.getMimeType()))
                .withHeader("Accept", equalTo(APPLICATION_JSON.getMimeType())));
    }

    @Test
    void whenGettingNewReleasesFirstPage_ThenCorrectQueryParamsAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("new-releases.json")));

        // WHEN
        spotifyAdvisor.getNewReleases(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .withQueryParam("offset", equalTo(String.valueOf(0)))
                .withQueryParam("limit", equalTo(String.valueOf(DEFAULT_PAGE_SIZE))));
    }

    @Test
    void whenGettingNewReleasesNthPage_ThenCorrectQueryParamsAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("new-releases.json")));

        // WHEN
        final int pageNumber = 3;
        spotifyAdvisor.getNewReleases(pageNumber);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .withQueryParam("offset", equalTo(String.valueOf((pageNumber - 1) * DEFAULT_PAGE_SIZE)))
                .withQueryParam("limit", equalTo(String.valueOf(DEFAULT_PAGE_SIZE))));
    }

    @Test
    void givenFeaturedPlaylistsReturnedFromSpotify_whenGettingReleases_thenFeaturedPlaylistsPageIsReturned() {
        // GIVEN
        List<Playlist> expectedFeaturedPlaylists = List.of(
                Playlist.builder()
                        .title("Monday Morning Mood")
                        .link("http://open.spotify.com/user/spotify/playlist/6ftJBzU2LLQcaKefMi7ee7")
                        .build(),
                Playlist.builder()
                        .title("Upp och hoppa!")
                        .link("http://open.spotify.com/user/spotify__sverige/playlist/4uOEx4OUrkoGNZoIlWMUbO")
                        .build());
        final int total = 12;

        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("featured-playlists.json")));

        // WHEN
        final int pageNumber = 5;
        Page<Playlist> featuredPlaylists = spotifyAdvisor.getFeaturedPlaylists(pageNumber);

        // THEN
        assertThat(featuredPlaylists.getElements())
                .as("Featured playlists")
                .containsExactlyElementsOf(expectedFeaturedPlaylists);
        assertThat(featuredPlaylists.getTotal())
                .as("Total")
                .isEqualTo(total);
        assertThat(featuredPlaylists.getPageNumber())
                .as("Page Number")
                .isEqualTo(pageNumber);
    }

    @Test
    void givenErrorResponseReturnedFromSpotify_whenGettingFeaturedPlaylists_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        String errorMessage = "error when getting featured playlists";
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 400,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> spotifyAdvisor.getFeaturedPlaylists(1));

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage(errorMessage);
    }

    @Test
    void whenGettingFeaturedPlaylists_ThenCorrectHeadersAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("featured-playlists.json")));

        // WHEN
        spotifyAdvisor.getFeaturedPlaylists(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON.getMimeType()))
                .withHeader("Accept", equalTo(APPLICATION_JSON.getMimeType())));
    }

    @Test
    void whenGettingFeaturedPlaylistsFirstPage_ThenCorrectQueryParamsAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("featured-playlists.json")));

        // WHEN
        spotifyAdvisor.getFeaturedPlaylists(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .withQueryParam("offset", equalTo(String.valueOf(0)))
                .withQueryParam("limit", equalTo(String.valueOf(DEFAULT_PAGE_SIZE))));
    }

    @Test
    void whenGettingFeaturedPlaylistsNthPage_ThenCorrectQueryParamsAreSentInRequest() {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("featured-playlists.json")));

        // WHEN
        final int pageNumber = 3;
        spotifyAdvisor.getFeaturedPlaylists(pageNumber);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .withQueryParam("offset", equalTo(String.valueOf((pageNumber - 1) * DEFAULT_PAGE_SIZE)))
                .withQueryParam("limit", equalTo(String.valueOf(DEFAULT_PAGE_SIZE))));
    }

    @Test
    void givenExistingCategoryAndCategoryPlaylistsAreReturnedFromSpotify_whenGettingPlaylistsByCategory_thenCategoryPlaylistsPageIsReturned() {
        // GIVEN
        Category category = new Category("Party", "party");
        List<Playlist> expectedCategoryPlaylists = List.of(
                Playlist.builder()
                        .title("Sexta")
                        .link("https://open.spotify.com/playlist/37i9dQZF1DX8mBRYewE6or")
                        .build(),
                Playlist.builder()
                        .title("Segue o Baile")
                        .link("https://open.spotify.com/playlist/37i9dQZF1DWWmaszSfZpom")
                        .build());
        final int total = 37;

        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("category-playlists.json")));

        // WHEN
        final int pageNumber = 5;
        Page<Playlist> categoryPlaylists = spotifyAdvisor.getCategoryPlaylists(category, pageNumber);

        // THEN
        assertThat(categoryPlaylists.getElements())
                .as("Featured playlists")
                .containsExactlyElementsOf(expectedCategoryPlaylists);
        assertThat(categoryPlaylists.getTotal())
                .as("Total")
                .isEqualTo(total);
        assertThat(categoryPlaylists.getPageNumber())
                .as("Page Number")
                .isEqualTo(pageNumber);
    }

    @Test
    void givenErrorResponseReturnedFromSpotifyForCategoryPlaylists_whenGettingPlaylistsByCategory_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        Category category = new Category("Party", "party");
        String errorMessage = "error message when category not found";

        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_NOT_FOUND)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 404,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> spotifyAdvisor.getCategoryPlaylists(category, 1));

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage(errorMessage);
    }

    @Test
    void whenGettingPlaylistsByCategory_ThenCorrectHeadersAreSentInRequest() {
        // GIVEN
        Category category = new Category("Party", "party");
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("category-playlists.json")));

        // WHEN
        spotifyAdvisor.getCategoryPlaylists(category, 1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON.getMimeType()))
                .withHeader("Accept", equalTo(APPLICATION_JSON.getMimeType())));
    }


    @Test
    void whenGettingPlaylistsByCategoryFirstPage_ThenCorrectQueryParamsAreSentInRequest() {
        // GIVEN
        Category category = new Category("Party", "party");
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("category-playlists.json")));

        // WHEN
        spotifyAdvisor.getCategoryPlaylists(category, 1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .withQueryParam("offset", equalTo(String.valueOf(0)))
                .withQueryParam("limit", equalTo(String.valueOf(DEFAULT_PAGE_SIZE))));
    }

    @Test
    void whenGettingPlaylistsByCategoryNthPage_ThenCorrectQueryParamsAreSentInRequest() {
        // GIVEN
        Category category = new Category("Party", "party");
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                        .withBodyFile("category-playlists.json")));

        // WHEN
        final int pageNumber = 3;
        spotifyAdvisor.getCategoryPlaylists(category, pageNumber);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .withQueryParam("offset", equalTo(String.valueOf((pageNumber - 1) * DEFAULT_PAGE_SIZE)))
                .withQueryParam("limit", equalTo(String.valueOf(DEFAULT_PAGE_SIZE))));
    }
}
