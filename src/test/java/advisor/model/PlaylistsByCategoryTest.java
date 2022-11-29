package advisor.model;

import advisor.model.dto.Page;
import advisor.model.dto.Playlist;
import advisor.model.service.FakeAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static advisor.model.service.FakeAdvisorData.GOOD_MOOD_CATEGORY;
import static advisor.model.service.FakeAdvisorData.GOOD_MOOD_CATEGORY_PLAYLISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

final class PlaylistsByCategoryTest {

    private static final int PAGE_SIZE = 2;
    private static final int TOTAL_CATEGORIES = GOOD_MOOD_CATEGORY_PLAYLISTS.size();
    private static final Page<Playlist> CATEGORY_PLAYLISTS_FIRST_PAGE = new Page<>(
            GOOD_MOOD_CATEGORY_PLAYLISTS.subList(0, PAGE_SIZE), TOTAL_CATEGORIES, 1);
    private static final Page<Playlist> CATEGORY_PLAYLISTS_SECOND_PAGE = new Page<>(
            GOOD_MOOD_CATEGORY_PLAYLISTS.subList(PAGE_SIZE, (PAGE_SIZE * 2)), TOTAL_CATEGORIES, 2);
    private static final Page<Playlist> CATEGORY_PLAYLISTS_THIRD_PAGE = new Page<>(
            GOOD_MOOD_CATEGORY_PLAYLISTS.subList(PAGE_SIZE * 2, (PAGE_SIZE * 3)), TOTAL_CATEGORIES, 3);

    private PlaylistsByCategory playlistsByCategory;

    @BeforeEach
    void prepareTarget() {
        playlistsByCategory = new PlaylistsByCategory(new FakeAdvisor(PAGE_SIZE), PAGE_SIZE);
    }

    @Test
    void whenGettingTheFirstPlaylistsByCategoryPage_thenTheFirstPlaylistsByCategoryPageIsReturned() {
        // WHEN
        Page<Playlist> categoryPlaylistsPage = playlistsByCategory.firstPage(GOOD_MOOD_CATEGORY.getName());

        // THEN
        assertThat(categoryPlaylistsPage).as("Playlists by category first page").isEqualTo(CATEGORY_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    void givenFirstPlaylistsByCategoryPageHasNotBeenObtained_whenGettingTheNextPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> playlistsByCategory.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("Category must be specified first");
    }

    @Test
    void givenFirstPlaylistsByCategoryPageHasNotBeenObtained_whenGettingThePreviousPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> playlistsByCategory.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("Category must be specified first");
    }

    @Test
    void givenFirstPlaylistsByCategoryPageHasBeenObtained_whenGettingTheNextPlaylistsByCategoryPage_thenTheNextPlaylistsByCategoryPageIsReturned() {
        // GIVEN
        playlistsByCategory.firstPage(GOOD_MOOD_CATEGORY.getName());

        // WHEN
        Page<Playlist> categoryPlaylistsPage = playlistsByCategory.nextPage();

        // THEN
        assertThat(categoryPlaylistsPage).as("Playlists by category second page").isEqualTo(CATEGORY_PLAYLISTS_SECOND_PAGE);
    }

    @Test
    void givenAllWholePlaylistsByCategoryPagesHaveBeenObtained_whenGettingTheNextPlaylistsByCategoryPage_thenTheLastPlaylistsByCategoryPageIsReturned() {
        // GIVEN
        playlistsByCategory.firstPage(GOOD_MOOD_CATEGORY.getName());
        playlistsByCategory.nextPage();

        // WHEN
        Page<Playlist> categoryPlaylistsPage = playlistsByCategory.nextPage();

        // THEN
        assertThat(categoryPlaylistsPage).as("Playlists by category third page").isEqualTo(CATEGORY_PLAYLISTS_THIRD_PAGE);
    }

    @Test
    void givenAllNextNewReleasePagesHaveBeenObtained_whenGettingTheNextPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // GIVEN
        playlistsByCategory.firstPage(GOOD_MOOD_CATEGORY.getName());
        playlistsByCategory.nextPage();
        playlistsByCategory.nextPage();


        // WHEN
        Throwable thrown = catchThrowable(() -> playlistsByCategory.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    void givenFirstPlaylistsByCategoryPageHasBeenObtained_whenGettingThePreviousPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // GIVEN
        playlistsByCategory.firstPage(GOOD_MOOD_CATEGORY.getName());

        // WHEN
        Throwable thrown = catchThrowable(() -> playlistsByCategory.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenFirstTwoPlaylistsByCategoryPagesHaveBeenObtained_whenGettingThePreviousPlaylistsByCategoryPage_thenFirstPageIsObtained() {
        // GIVEN
        playlistsByCategory.firstPage(GOOD_MOOD_CATEGORY.getName());
        playlistsByCategory.nextPage();

        // WHEN
        Page<Playlist> categoryPlaylistsPage = playlistsByCategory.previousPage();

        // THEN
        assertThat(categoryPlaylistsPage).as("Playlists by category first page").isEqualTo(CATEGORY_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    void givenFirstTwoPlaylistsByCategoryPagesHaveBeenObtainedAndOnePreviousPlaylistsByCategoryPageHasBeenObtained_whenGettingThePreviousPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // GIVEN
        playlistsByCategory.firstPage(GOOD_MOOD_CATEGORY.getName());
        playlistsByCategory.nextPage();
        playlistsByCategory.previousPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> playlistsByCategory.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenAllNextPlaylistsByCategoryPagesHaveBeenObtained_whenGettingFirstPlaylistsByCategoryPage_thenFirstPlaylistsByCategoryPageIsObtained() {
        // GIVEN
        playlistsByCategory.firstPage(GOOD_MOOD_CATEGORY.getName());
        playlistsByCategory.nextPage();
        playlistsByCategory.nextPage();

        // WHEN
        Page<Playlist> categoryPlaylistsPage = playlistsByCategory.firstPage(GOOD_MOOD_CATEGORY.getName());

        // THEN
        assertThat(categoryPlaylistsPage).as("Playlists by category first page").isEqualTo(CATEGORY_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    void givenCategoryDoesNotExist_whenGettingFirstPlaylistByCategoryPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> playlistsByCategory.firstPage("non existing category"));

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("Unknown category name.");
    }
}
