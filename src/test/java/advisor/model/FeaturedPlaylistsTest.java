package advisor.model;

import advisor.model.dto.Page;
import advisor.model.dto.Playlist;
import advisor.model.service.FakeAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static advisor.model.service.FakeAdvisorData.FEATURED_PLAYLISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

final class FeaturedPlaylistsTest {

    private static final int PAGE_SIZE = 2;
    private static final int TOTAL_CATEGORIES = FEATURED_PLAYLISTS.size();
    private static final Page<Playlist> FEATURED_PLAYLISTS_FIRST_PAGE = new Page<>(
            FEATURED_PLAYLISTS.subList(0, PAGE_SIZE), TOTAL_CATEGORIES, 1);
    private static final Page<Playlist> FEATURED_PLAYLISTS_SECOND_PAGE = new Page<>(
            FEATURED_PLAYLISTS.subList(PAGE_SIZE, (PAGE_SIZE * 2)), TOTAL_CATEGORIES, 2);
    private static final Page<Playlist> FEATURED_PLAYLISTS_THIRD_PAGE = new Page<>(
            FEATURED_PLAYLISTS.subList(PAGE_SIZE * 2, (PAGE_SIZE * 3)), TOTAL_CATEGORIES, 3);

    private FeaturedPlaylists featuredPlaylists;

    @BeforeEach
    void prepareTarget() {
        featuredPlaylists = new FeaturedPlaylists(new FakeAdvisor(PAGE_SIZE), PAGE_SIZE);
    }

    @Test
    void whenGettingTheFirstFeaturedPlaylistsPage_thenTheFirstFeaturedPlaylistsPageIsReturned() {
        // WHEN
        Page<Playlist> featuredPlaylistsPage = featuredPlaylists.firstPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("First featured playlists page").isEqualTo(FEATURED_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    void givenFirstFeaturedPlaylistsPageHasNotBeenObtained_whenGettingTheNextFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> featuredPlaylists.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    void givenFirstFeaturedPlaylistsPageHasNotBeenObtained_whenGettingThePreviousFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> featuredPlaylists.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenFirstFeaturedPlaylistsPageHasBeenObtained_whenGettingTheNextFeaturedPlaylistsPage_thenTheNextFeaturedPlaylistsPageIsReturned() {
        // GIVEN
        featuredPlaylists.firstPage();

        // WHEN
        Page<Playlist> featuredPlaylistsPage = featuredPlaylists.nextPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("Second featured playlists page").isEqualTo(FEATURED_PLAYLISTS_SECOND_PAGE);
    }

    @Test
    void givenAllWholeFeaturedPlaylistsPagesHaveBeenObtained_whenGettingTheNextFeaturedPlaylistsPage_thenTheLastFeaturedPlaylistsPageIsReturned() {
        // GIVEN
        featuredPlaylists.firstPage();
        featuredPlaylists.nextPage();

        // WHEN
        Page<Playlist> featuredPlaylistsPage = featuredPlaylists.nextPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("Third featured playlists page").isEqualTo(FEATURED_PLAYLISTS_THIRD_PAGE);
    }

    @Test
    void givenAllNextFeaturedPlaylistsPagesHaveBeenObtained_whenGettingTheNextFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // GIVEN
        featuredPlaylists.firstPage();
        featuredPlaylists.nextPage();
        featuredPlaylists.nextPage();


        // WHEN
        Throwable thrown = catchThrowable(() -> featuredPlaylists.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    void givenFirstFeaturedPlaylistsPageHasBeenObtained_whenGettingThePreviousFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // GIVEN
        featuredPlaylists.firstPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> featuredPlaylists.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenFirstTwoFeaturedPlaylistsPagesHaveBeenObtained_whenGettingThePreviousFeaturedPlaylistsPage_thenFirstPageIsObtained() {
        // GIVEN
        featuredPlaylists.firstPage();
        featuredPlaylists.nextPage();

        // WHEN
        Page<Playlist> featuredPlaylistsPage = featuredPlaylists.previousPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("First featured playlists page").isEqualTo(FEATURED_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    void givenFirstTwoFeaturedPlaylistsPagesHaveBeenObtainedAndOnePreviousFeaturedPlaylistsPageHasBeenObtained_whenGettingThePreviousFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // GIVEN
        featuredPlaylists.firstPage();
        featuredPlaylists.nextPage();
        featuredPlaylists.previousPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> featuredPlaylists.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenAllNextFeaturedPlaylistsPagesHaveBeenObtained_whenGettingFirstFeaturedPlaylistsPage_thenFirstFeaturedPlaylistsPageIsObtained() {
        // GIVEN
        featuredPlaylists.firstPage();
        featuredPlaylists.nextPage();
        featuredPlaylists.nextPage();

        // WHEN
        Page<Playlist> featuredPlaylistsPage = featuredPlaylists.firstPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("First featured playlists page").isEqualTo(FEATURED_PLAYLISTS_FIRST_PAGE);
    }
}
