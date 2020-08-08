package advisor.model;

import advisor.model.dto.Artist;
import advisor.model.dto.Page;
import advisor.model.dto.Release;
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
public class NewReleasesTest {

    private static final int pageSize = 2;
    private static final Page<Release> NEW_RELEASES_FIRST_PAGE = new Page<>(
            List.of(
                    Release.builder().title("Page 1 Release 1 title")
                            .artists(List.of(new Artist("Page 1 Release 1 Artist")))
                            .link("Page 1 Release 1 link")
                            .build(),
                    Release.builder().title("Page 1 Release 2 title")
                            .artists(List.of(new Artist("Page 1 Release 2 Artist")))
                            .link("Page 1 Release 2 link")
                            .build()),
            (pageSize * 2) + 1,
            1);

    private static final Page<Release> NEW_RELEASES_SECOND_PAGE = new Page<>(
            List.of(
                    Release.builder().title("Page 2 Release 1 title")
                            .artists(List.of(new Artist("Page 2 Release 1 Artist")))
                            .link("Page 2 Release 1 link")
                            .build(),
                    Release.builder().title("Page 2 Release 2 title")
                            .artists(List.of(new Artist("Page 2 Release 2 Artist")))
                            .link("Page 2 Release 2 link")
                            .build()),
            (pageSize * 2) + 1,
            2);

    private static final Page<Release> NEW_RELEASES_THIRD_PAGE = new Page<>(
            List.of(
                    Release.builder().title("Page 3 Release 1 title")
                            .artists(List.of(new Artist("Page 3 Release 1 Artist")))
                            .link("Page 3 Release 1 link")
                            .build()),
                    (pageSize * 2) + 1,
                    3);

    @Mock
    private Advisor advisor;

    private NewReleases target;

    @Before
    public void prepareTarget() {
        target = new NewReleases(advisor, pageSize);
        when(advisor.getNewReleases(1)).thenReturn(NEW_RELEASES_FIRST_PAGE);
        when(advisor.getNewReleases(2)).thenReturn(NEW_RELEASES_SECOND_PAGE);
        when(advisor.getNewReleases(3)).thenReturn(NEW_RELEASES_THIRD_PAGE);
    }

    @Test
    public void whenGettingTheFirstNewReleasesPage_thenTheFirstNewReleasesPageIsReturned() {
        // WHEN
        Page<Release> newReleasesPage = target.firstPage();

        // THEN
        assertThat(newReleasesPage).as("First new releases page").isEqualTo(NEW_RELEASES_FIRST_PAGE);
    }

    @Test
    public void givenFirstNewReleasesPageHasNotBeenObtained_whenGettingTheNextNewReleasesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    public void givenFirstNewReleasesPageHasNotBeenObtained_whenGettingThePreviousNewReleasesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    public void givenFirstNewReleasesPageHasBeenObtained_whenGettingTheNextNewReleasesPage_thenTheNextNewReleasesPageIsReturned() {
        // GIVEN
        target.firstPage();

        // WHEN
        Page<Release> newReleasesPage = target.nextPage();

        // THEN
        assertThat(newReleasesPage).as("Second new releases page").isEqualTo(NEW_RELEASES_SECOND_PAGE);
    }

    @Test
    public void givenAllWholeNewReleasesPagesHaveBeenObtained_whenGettingTheNextNewReleasesPage_thenTheLastNewReleasesPageIsReturned() {
        // GIVEN
        target.firstPage();
        target.nextPage();

        // WHEN
        Page<Release> newReleasesPage = target.nextPage();

        // THEN
        assertThat(newReleasesPage).as("Third new releases page").isEqualTo(NEW_RELEASES_THIRD_PAGE);
    }

    @Test
    public void givenAllNextNewReleasePagesHaveBeenObtained_whenGettingTheNextNewReleasesPage_thenAnExceptionIsThrown() {
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
    public void givenFirstNewReleasesPageHasBeenObtained_whenGettingThePreviousNewReleasesPage_thenAnExceptionIsThrown() {
        // GIVEN
        target.firstPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    public void givenFirstTwoNewReleasesPagesHaveBeenObtained_whenGettingThePreviousNewReleasesPage_thenFirstPageIsObtained() {
        // GIVEN
        target.firstPage();
        target.nextPage();

        // WHEN
        Page<Release> newReleasesPage = target.previousPage();

        // THEN
        assertThat(newReleasesPage).as("First new releases page").isEqualTo(NEW_RELEASES_FIRST_PAGE);
    }

    @Test
    public void givenFirstTwoNewReleasesPagesHaveBeenObtainedAndOnePreviousNewReleasesPageHasBeenObtained_whenGettingThePreviousNewReleasesPage_thenAnExceptionIsThrown() {
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
    public void givenAllNextNewReleasesPagesHaveBeenObtained_whenGettingFirstNewReleasesPage_thenFirstNewReleasesPageIsObtained() {
        // GIVEN
        target.firstPage();
        target.nextPage();
        target.nextPage();

        // WHEN
        Page<Release> newReleasesPage = target.firstPage();

        // THEN
        assertThat(newReleasesPage).as("First new releases page").isEqualTo(NEW_RELEASES_FIRST_PAGE);
    }    
}
