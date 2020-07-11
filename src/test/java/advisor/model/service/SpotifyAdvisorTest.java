package advisor.model.service;

import advisor.authentication.AlwaysAuthenticatedUserCommandAuthentication;
import advisor.authentication.UserCommandAuthenticationFacade;
import advisor.model.AdvisorException;
import advisor.model.dto.*;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class SpotifyAdvisorTest {

    private static final String RESOURCE_COMMON_PATH = "/v1/browse/";
    private final UserCommandAuthenticationFacade userCommandAuthenticationFacade =
            new UserCommandAuthenticationFacade(new AlwaysAuthenticatedUserCommandAuthentication());
    private final int defaultPageSize = 5;
    private SpotifyAdvisor target;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void prepareTarget() {
        String spotifyResourceHost = "http://localhost";
        target = new SpotifyAdvisor(
                spotifyResourceHost + ":" + wireMockRule.port(), userCommandAuthenticationFacade, defaultPageSize);
    }

    @Test
    public void givenCategoriesReturnedFromSpotify_whenGettingCategories_thenCategoryPageIsReturned() throws AdvisorException {
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
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("categories.json")));

        // WHEN
        final int pageNumber = 5;
        Page<Category> categories = target.getCategories(pageNumber);

        // THEN
        Assertions.assertThat(categories.getElements())
                .as("Categories")
                .containsExactlyElementsOf(expectedCategories);
        Assertions.assertThat(categories.getTotal())
                .as("Total")
                .isEqualTo(total);
        Assertions.assertThat(categories.getPageNumber())
                .as("Page number")
                .isEqualTo(pageNumber);
    }

    @Test
    public void whenGettingCategories_ThenCorrectHeadersAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("categories.json")));

        // WHEN
        target.getCategories(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    public void whenGettingCategoriesFirstPage_ThenCorrectQueryParamsAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("categories.json")));

        // WHEN
        target.getCategories(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withQueryParam("offset", equalTo(String.valueOf(0)))
                .withQueryParam("limit", equalTo(String.valueOf(defaultPageSize))));
    }

    @Test
    public void whenGettingCategoriesNthPage_ThenCorrectQueryParamsAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("categories.json")));

        // WHEN
        final int pageNumber = 3;
        target.getCategories(pageNumber);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withQueryParam("offset", equalTo(String.valueOf((pageNumber - 1) * defaultPageSize)))
                .withQueryParam("limit", equalTo(String.valueOf(defaultPageSize))));
    }

    @Test
    public void whenGettingCategoriesWithNoPaging_ThenNoPagingQueryParamsAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("categories.json")));

        // WHEN
        target.getCategories();

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withQueryParam("offset", absent())
                .withQueryParam("limit", absent()));
    }

    @Test
    public void givenCategoriesReturnedFromSpotify_whenGettingCategoriesWithNoPaging_thenCategoryPageIsReturned() throws AdvisorException {
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
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("categories.json")));

        // WHEN
        Page<Category> categories = target.getCategories();

        // THEN
        Assertions.assertThat(categories.getElements())
                .as("Categories")
                .containsExactlyElementsOf(expectedCategories);
        Assertions.assertThat(categories.getTotal())
                .as("Total")
                .isEqualTo(total);
    }

    @Test
    public void whenGettingCategoriesWithNoPaging_ThenCorrectHeadersAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("categories.json")));

        // WHEN
        target.getCategories();

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    public void givenErrorResponseReturnedFromSpotify_whenGettingCategories_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        String errorMessage = "error when getting categories";
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 400,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> target.getCategories(1));

        // THEN
        Assertions.assertThat(thrown)
                .isInstanceOf(AdvisorException.class).hasMessage(errorMessage);
    }

    @Test
    public void givenErrorResponseReturnedFromSpotify_whenGettingCategoriesWithNoPaging_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        String errorMessage = "error when getting categories";
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 400,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> target.getCategories());

        // THEN
        Assertions.assertThat(thrown)
                .isInstanceOf(AdvisorException.class).hasMessage(errorMessage);
    }

    @Test
    public void givenNewReleasesReturnedFromSpotify_whenGettingNewReleases_thenNewReleasesPageIsReturned() throws AdvisorException {
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
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("new-releases.json")));

        // WHEN
        final int pageNumber = 5;
        Page<Release> releases = target.getNewReleases(pageNumber);

        // THEN
        Assertions.assertThat(releases.getElements())
                .as("New Releases")
                .containsExactlyElementsOf(expectedReleases);
        Assertions.assertThat(releases.getTotal())
                .as("Total")
                .isEqualTo(total);
        Assertions.assertThat(releases.getPageNumber())
                .as("Page number")
                .isEqualTo(pageNumber);
    }

    @Test
    public void givenErrorResponseReturnedFromSpotify_whenGettingNewReleases_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        String errorMessage = "error when getting releases";
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 400,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> target.getNewReleases(1));

        // THEN
        Assertions.assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage(errorMessage);
    }

    @Test
    public void whenGettingNewReleases_ThenCorrectHeadersAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("new-releases.json")));

        // WHEN
        target.getNewReleases(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    public void whenGettingNewReleasesFirstPage_ThenCorrectQueryParamsAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("new-releases.json")));

        // WHEN
        target.getNewReleases(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .withQueryParam("offset", equalTo(String.valueOf(0)))
                .withQueryParam("limit", equalTo(String.valueOf(defaultPageSize))));
    }

    @Test
    public void whenGettingNewReleasesNthPage_ThenCorrectQueryParamsAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("new-releases.json")));

        // WHEN
        final int pageNumber = 3;
        target.getNewReleases(pageNumber);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "new-releases"))
                .withQueryParam("offset", equalTo(String.valueOf((pageNumber - 1) * defaultPageSize)))
                .withQueryParam("limit", equalTo(String.valueOf(defaultPageSize))));
    }

    @Test
    public void givenFeaturedPlaylistsReturnedFromSpotify_whenGettingReleases_thenFeaturedPlaylistsPageIsReturned() throws AdvisorException {
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
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("featured-playlists.json")));

        // WHEN
        final int pageNumber = 5;
        Page<Playlist> featuredPlaylists = target.getFeaturedPlaylists(pageNumber);

        // THEN
        Assertions.assertThat(featuredPlaylists.getElements())
                .as("Featured playlists")
                .containsExactlyElementsOf(expectedFeaturedPlaylists);
        Assertions.assertThat(featuredPlaylists.getTotal())
                .as("Total")
                .isEqualTo(total);
        Assertions.assertThat(featuredPlaylists.getPageNumber())
                .as("Page Number")
                .isEqualTo(pageNumber);
    }

    @Test
    public void givenErrorResponseReturnedFromSpotify_whenGettingFeaturedPlaylists_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        String errorMessage = "error when getting featured playlists";
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .willReturn(aResponse().withStatus(HttpStatus.SC_BAD_REQUEST)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 400,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> target.getFeaturedPlaylists(1));

        // THEN
        Assertions.assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage(errorMessage);
    }

    @Test
    public void whenGettingFeaturedPlaylists_ThenCorrectHeadersAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("featured-playlists.json")));

        // WHEN
        target.getFeaturedPlaylists(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    public void whenGettingFeaturedPlaylistsFirstPage_ThenCorrectQueryParamsAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("featured-playlists.json")));

        // WHEN
        target.getFeaturedPlaylists(1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .withQueryParam("offset", equalTo(String.valueOf(0)))
                .withQueryParam("limit", equalTo(String.valueOf(defaultPageSize))));
    }

    @Test
    public void whenGettingFeaturedPlaylistsNthPage_ThenCorrectQueryParamsAreSentInRequest() throws AdvisorException {
        // GIVEN
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("featured-playlists.json")));

        // WHEN
        final int pageNumber = 3;
        target.getFeaturedPlaylists(pageNumber);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "featured-playlists"))
                .withQueryParam("offset", equalTo(String.valueOf((pageNumber - 1) * defaultPageSize)))
                .withQueryParam("limit", equalTo(String.valueOf(defaultPageSize))));
    }

    @Test
    public void givenExistingCategoryAndCategoryPlaylistsAreReturnedFromSpotify_whenGettingPlaylistsByCategory_thenCategoryPlaylistsPageIsReturned() throws AdvisorException {
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
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("category-playlists.json")));

        // WHEN
        final int pageNumber = 5;
        Page<Playlist> categoryPlaylists = target.getCategoryPlaylists(category, pageNumber);

        // THEN
        Assertions.assertThat(categoryPlaylists.getElements())
                .as("Featured playlists")
                .containsExactlyElementsOf(expectedCategoryPlaylists);
        Assertions.assertThat(categoryPlaylists.getTotal())
                .as("Total")
                .isEqualTo(total);
        Assertions.assertThat(categoryPlaylists.getPageNumber())
                .as("Page Number")
                .isEqualTo(pageNumber);
    }

    @Test
    public void givenErrorResponseReturnedFromSpotifyForCategoryPlaylists_whenGettingPlaylistsByCategory_thenExceptionWithErrorMessageIsThrown() {
        // GIVEN
        Category category = new Category("Party", "party");
        String errorMessage = "error message when category not found";

        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_NOT_FOUND)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n  " +
                                "\"error\": " +
                                "{\n    \"status\": 404,\n    " +
                                "       \"message\": \"" + errorMessage + "\"\n  " +
                                "}\n" +
                                "}")));

        // WHEN
        Throwable thrown = Assertions.catchThrowable(() -> target.getCategoryPlaylists(category, 1));

        // THEN
        Assertions.assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage(errorMessage);
    }

    @Test
    public void whenGettingPlaylistsByCategory_ThenCorrectHeadersAreSentInRequest() throws AdvisorException {
        // GIVEN
        Category category = new Category("Party", "party");
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("category-playlists.json")));

        // WHEN
        target.getCategoryPlaylists(category, 1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .withHeader("Authorization", equalTo("Bearer " + userCommandAuthenticationFacade.getAccessToken()))
                .withHeader("Content-Type", equalTo("application/json"))
                .withHeader("Accept", equalTo("application/json")));
    }


    @Test
    public void whenGettingPlaylistsByCategoryFirstPage_ThenCorrectQueryParamsAreSentInRequest() throws AdvisorException {
        // GIVEN
        Category category = new Category("Party", "party");
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("category-playlists.json")));

        // WHEN
        target.getCategoryPlaylists(category, 1);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .withQueryParam("offset", equalTo(String.valueOf(0)))
                .withQueryParam("limit", equalTo(String.valueOf(defaultPageSize))));
    }

    @Test
    public void whenGettingPlaylistsByCategoryNthPage_ThenCorrectQueryParamsAreSentInRequest() throws AdvisorException {
        // GIVEN
        Category category = new Category("Party", "party");
        stubFor(get(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("category-playlists.json")));

        // WHEN
        final int pageNumber = 3;
        target.getCategoryPlaylists(category, pageNumber);

        // THEN
        verify(getRequestedFor(urlPathEqualTo(RESOURCE_COMMON_PATH + "categories/" + category.getId() + "/playlists"))
                .withQueryParam("offset", equalTo(String.valueOf((pageNumber - 1) * defaultPageSize)))
                .withQueryParam("limit", equalTo(String.valueOf(defaultPageSize))));
    }
}
