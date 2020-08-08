package advisor.model;

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
public class FeaturedPlaylistsTest {

    private static final int pageSize = 2;
    private static final Page<Playlist> FEATURED_PLAYLISTS_FIRST_PAGE = new Page<>(
            List.of(
                    Playlist.builder().title("Page 1 playlist 1 title").link("Page 1 playlist 1 link")
                            .build(),
                    Playlist.builder().title("Page 1 playlist 2 title").link("Page 1 playlist 2 link")
                            .build()),            
            (pageSize * 2) + 1,
            1);

    private static final Page<Playlist> FEATURED_PLAYLISTS_SECOND_PAGE = new Page<>(
            List.of(
                    Playlist.builder().title("Page 2 playlist 1 title").link("Page 2 playlist 1 link")
                            .build(),
                    Playlist.builder().title("Page 2 playlist 2 title").link("Page 2 playlist 2 link")
                            .build()),
            (pageSize * 2) + 1,
            2);

    private static final Page<Playlist> FEATURED_PLAYLISTS_THIRD_PAGE = new Page<>(
            List.of(
                    Playlist.builder().title("Page 3 playlist 1 title").link("Page 3 playlist 1 link")
                            .build()),
            (pageSize * 2) + 1,
            3);

    @Mock
    private Advisor advisor;

    private FeaturedPlaylists target;

    @Before
    public void prepareTarget() {
        target = new FeaturedPlaylists(advisor, pageSize);
        when(advisor.getFeaturedPlaylists(1)).thenReturn(FEATURED_PLAYLISTS_FIRST_PAGE);
        when(advisor.getFeaturedPlaylists(2)).thenReturn(FEATURED_PLAYLISTS_SECOND_PAGE);
        when(advisor.getFeaturedPlaylists(3)).thenReturn(FEATURED_PLAYLISTS_THIRD_PAGE);
    }

    @Test
    public void whenGettingTheFirstFeaturedPlaylistsPage_thenTheFirstFeaturedPlaylistsPageIsReturned() {
        // WHEN
        Page<Playlist> featuredPlaylistsPage = target.firstPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("First featured playlists page").isEqualTo(FEATURED_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    public void givenFirstFeaturedPlaylistsPageHasNotBeenObtained_whenGettingTheNextFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    public void givenFirstFeaturedPlaylistsPageHasNotBeenObtained_whenGettingThePreviousFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    public void givenFirstFeaturedPlaylistsPageHasBeenObtained_whenGettingTheNextFeaturedPlaylistsPage_thenTheNextFeaturedPlaylistsPageIsReturned() {
        // GIVEN
        target.firstPage();

        // WHEN
        Page<Playlist> featuredPlaylistsPage = target.nextPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("Second featured playlists page").isEqualTo(FEATURED_PLAYLISTS_SECOND_PAGE);
    }

    @Test
    public void givenAllWholeFeaturedPlaylistsPagesHaveBeenObtained_whenGettingTheNextFeaturedPlaylistsPage_thenTheLastFeaturedPlaylistsPageIsReturned() {
        // GIVEN
        target.firstPage();
        target.nextPage();

        // WHEN
        Page<Playlist> featuredPlaylistsPage = target.nextPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("Third featured playlists page").isEqualTo(FEATURED_PLAYLISTS_THIRD_PAGE);
    }

    @Test
    public void givenAllNextFeaturedPlaylistsPagesHaveBeenObtained_whenGettingTheNextFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // GIVEN
        target.firstPage();
        target.nextPage();
        target.nextPage();


        // WHEN
        Throwable thrown = catchThrowable(() -> target.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    public void givenFirstFeaturedPlaylistsPageHasBeenObtained_whenGettingThePreviousFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // GIVEN
        target.firstPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    public void givenFirstTwoFeaturedPlaylistsPagesHaveBeenObtained_whenGettingThePreviousFeaturedPlaylistsPage_thenFirstPageIsObtained() {
        // GIVEN
        target.firstPage();
        target.nextPage();

        // WHEN
        Page<Playlist> featuredPlaylistsPage = target.previousPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("First featured playlists page").isEqualTo(FEATURED_PLAYLISTS_FIRST_PAGE);
    }

    @Test
    public void givenFirstTwoFeaturedPlaylistsPagesHaveBeenObtainedAndOnePreviousFeaturedPlaylistsPageHasBeenObtained_whenGettingThePreviousFeaturedPlaylistsPage_thenAnExceptionIsThrown() {
        // GIVEN
        target.firstPage();
        target.nextPage();
        target.previousPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    public void givenAllNextFeaturedPlaylistsPagesHaveBeenObtained_whenGettingFirstFeaturedPlaylistsPage_thenFirstFeaturedPlaylistsPageIsObtained() {
        // GIVEN
        target.firstPage();
        target.nextPage();
        target.nextPage();

        // WHEN
        Page<Playlist> featuredPlaylistsPage = target.firstPage();

        // THEN
        assertThat(featuredPlaylistsPage).as("First featured playlists page").isEqualTo(FEATURED_PLAYLISTS_FIRST_PAGE);
    }
}
