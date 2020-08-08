package advisor.model;

import advisor.model.dto.Category;
import advisor.model.dto.Page;
import advisor.model.dto.Playlist;
import advisor.model.service.Advisor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlaylistsByCategoryTest {

    private static final int pageSize = 2;
    private static final Page<Playlist> CATEGORY_PLAYLISTS_FIRST_PAGE = new Page<>(
            List.of(
                    Playlist.builder().title("Page 1 playlist 1 title").link("Page 1 playlist 1 link")
                            .build(),
                    Playlist.builder().title("Page 1 playlist 2 title").link("Page 1 playlist 2 link")
                            .build()),
            (pageSize * 2) + 1,
            1);

    private static final Page<Playlist> CATEGORY_PLAYLISTS_SECOND_PAGE = new Page<>(
            List.of(
                    Playlist.builder().title("Page 2 playlist 1 title").link("Page 2 playlist 1 link")
                            .build(),
                    Playlist.builder().title("Page 2 playlist 2 title").link("Page 2 playlist 2 link")
                            .build()),
            (pageSize * 2) + 1,
            2);

    private static final Page<Playlist> CATEGORY_PLAYLISTS_THIRD_PAGE = new Page<>(
            List.of(
                    Playlist.builder().title("Page 3 playlist 1 title").link("Page 3 playlist 1 link")
                            .build()),
            (pageSize * 2) + 1,
            3);

    private static final Category EXISTING_CATEGORY = new Category("Existing category", "existingCategory");
    private static final Category NON_EXISTING_CATEGORY = new Category("Non existing category", "nonExistingCategory");
    
    private static final Page<Category> CATEGORIES_LIST = new Page<>(
            List.of(
                    EXISTING_CATEGORY,
                    new Category("Other existing category", "otherExistingCategory")),
            (pageSize * 2) + 1,
            1);
    
    @Mock
    private Advisor advisor;

    private PlaylistsByCategory target;

    @Before
    public void prepareTarget() {
        target = new PlaylistsByCategory(advisor, pageSize);
        when(advisor.getCategoryPlaylists(EXISTING_CATEGORY, 1)).thenReturn(CATEGORY_PLAYLISTS_FIRST_PAGE);
        when(advisor.getCategoryPlaylists(EXISTING_CATEGORY, 2)).thenReturn(CATEGORY_PLAYLISTS_SECOND_PAGE);
        when(advisor.getCategoryPlaylists(EXISTING_CATEGORY, 3)).thenReturn(CATEGORY_PLAYLISTS_THIRD_PAGE);
        when(advisor.getCategories()).thenReturn(CATEGORIES_LIST);
    }

    @Test
    public void whenGettingTheFirstPlaylistsByCategoryPage_thenTheFirstPlaylistsByCategoryPageIsReturned() {
        // WHEN
        Page<Playlist> newReleasesPage = target.firstPage(EXISTING_CATEGORY.getName());

        // THEN
        assertThat(newReleasesPage).as("First playlists by category page").isEqualTo(CATEGORY_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    public void givenFirstPlaylistsByCategoryPageHasNotBeenObtained_whenGettingTheNextPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("Category must be specified first");
    }

    @Test
    public void givenFirstPlaylistsByCategoryPageHasNotBeenObtained_whenGettingThePreviousPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("Category must be specified first");
    }

    @Test
    public void givenFirstPlaylistsByCategoryPageHasBeenObtained_whenGettingTheNextPlaylistsByCategoryPage_thenTheNextPlaylistsByCategoryPageIsReturned() {
        // GIVEN
        target.firstPage(EXISTING_CATEGORY.getName());

        // WHEN
        Page<Playlist> newReleasesPage = target.nextPage();

        // THEN
        assertThat(newReleasesPage).as("Second playlists by category page").isEqualTo(CATEGORY_PLAYLISTS_SECOND_PAGE);
    }

    @Test
    public void givenAllWholePlaylistsByCategoryPagesHaveBeenObtained_whenGettingTheNextPlaylistsByCategoryPage_thenTheLastPlaylistsByCategoryPageIsReturned() {
        // GIVEN
        target.firstPage(EXISTING_CATEGORY.getName());
        target.nextPage();

        // WHEN
        Page<Playlist> newReleasesPage = target.nextPage();

        // THEN
        assertThat(newReleasesPage).as("Third playlists by category page").isEqualTo(CATEGORY_PLAYLISTS_THIRD_PAGE);
    }

    @Test
    public void givenAllNextNewReleasePagesHaveBeenObtained_whenGettingTheNextPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // GIVEN
        target.firstPage(EXISTING_CATEGORY.getName());
        target.nextPage();
        target.nextPage();


        // WHEN
        Throwable thrown = catchThrowable(() -> target.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    public void givenFirstPlaylistsByCategoryPageHasBeenObtained_whenGettingThePreviousPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // GIVEN
        target.firstPage(EXISTING_CATEGORY.getName());

        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    public void givenFirstTwoPlaylistsByCategoryPagesHaveBeenObtained_whenGettingThePreviousPlaylistsByCategoryPage_thenFirstPageIsObtained() {
        // GIVEN
        target.firstPage(EXISTING_CATEGORY.getName());
        target.nextPage();

        // WHEN
        Page<Playlist> newReleasesPage = target.previousPage();

        // THEN
        assertThat(newReleasesPage).as("First playlists by category page").isEqualTo(CATEGORY_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    public void givenFirstTwoPlaylistsByCategoryPagesHaveBeenObtainedAndOnePreviousPlaylistsByCategoryPageHasBeenObtained_whenGettingThePreviousPlaylistsByCategoryPage_thenAnExceptionIsThrown() {
        // GIVEN
        target.firstPage(EXISTING_CATEGORY.getName());
        target.nextPage();
        target.previousPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    public void givenAllNextPlaylistsByCategoryPagesHaveBeenObtained_whenGettingFirstPlaylistsByCategoryPage_thenFirstPlaylistsByCategoryPageIsObtained() {
        // GIVEN
        target.firstPage(EXISTING_CATEGORY.getName());
        target.nextPage();
        target.nextPage();

        // WHEN
        Page<Playlist> newReleasesPage = target.firstPage(EXISTING_CATEGORY.getName());

        // THEN
        assertThat(newReleasesPage).as("First playlists by category page").isEqualTo(CATEGORY_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    public void givenCategoryDoesNotExist_whenGettingFirstPlaylistByCategoryPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.firstPage(NON_EXISTING_CATEGORY.getName()));

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("Unknown category name.");
    }
}
