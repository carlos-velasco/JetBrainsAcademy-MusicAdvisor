package advisor.model;

import advisor.model.dto.Page;
import advisor.model.dto.Release;
import advisor.model.service.FakeAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static advisor.model.service.FakeAdvisorData.RELEASES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

final class NewReleasesTest {

    private static final int PAGE_SIZE = 2;
    private static final int TOTAL_CATEGORIES = RELEASES.size();
    private static final Page<Release> NEW_RELEASES_FIRST_PAGE = new Page<>(
            RELEASES.subList(0, PAGE_SIZE), TOTAL_CATEGORIES, 1);
    private static final Page<Release> NEW_RELEASES_SECOND_PAGE = new Page<>(
            RELEASES.subList(PAGE_SIZE, (PAGE_SIZE * 2)), TOTAL_CATEGORIES, 2);
    private static final Page<Release> NEW_RELEASES_THIRD_PAGE = new Page<>(
            RELEASES.subList(PAGE_SIZE * 2, (PAGE_SIZE * 3)), TOTAL_CATEGORIES, 3);

    private NewReleases newReleases;

    @BeforeEach
    void prepareTarget() {
        newReleases = new NewReleases(new FakeAdvisor(PAGE_SIZE), PAGE_SIZE);
    }

    @Test
    void whenGettingTheFirstNewReleasesPage_thenTheFirstNewReleasesPageIsReturned() {
        // WHEN
        Page<Release> newReleasesPage = newReleases.firstPage();

        // THEN
        assertThat(newReleasesPage).as("First new releases page").isEqualTo(NEW_RELEASES_FIRST_PAGE);
    }

    @Test
    void givenFirstNewReleasesPageHasNotBeenObtained_whenGettingTheNextNewReleasesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> newReleases.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    void givenFirstNewReleasesPageHasNotBeenObtained_whenGettingThePreviousNewReleasesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> newReleases.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenFirstNewReleasesPageHasBeenObtained_whenGettingTheNextNewReleasesPage_thenTheNextNewReleasesPageIsReturned() {
        // GIVEN
        newReleases.firstPage();

        // WHEN
        Page<Release> newReleasesPage = newReleases.nextPage();

        // THEN
        assertThat(newReleasesPage).as("Second new releases page").isEqualTo(NEW_RELEASES_SECOND_PAGE);
    }

    @Test
    void givenAllWholeNewReleasesPagesHaveBeenObtained_whenGettingTheNextNewReleasesPage_thenTheLastNewReleasesPageIsReturned() {
        // GIVEN
        newReleases.firstPage();
        newReleases.nextPage();

        // WHEN
        Page<Release> newReleasesPage = newReleases.nextPage();

        // THEN
        assertThat(newReleasesPage).as("Third new releases page").isEqualTo(NEW_RELEASES_THIRD_PAGE);
    }

    @Test
    void givenAllNextNewReleasePagesHaveBeenObtained_whenGettingTheNextNewReleasesPage_thenAnExceptionIsThrown() {
        // GIVEN
        newReleases.firstPage();
        newReleases.nextPage();
        newReleases.nextPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> newReleases.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    void givenFirstNewReleasesPageHasBeenObtained_whenGettingThePreviousNewReleasesPage_thenAnExceptionIsThrown() {
        // GIVEN
        newReleases.firstPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> newReleases.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenFirstTwoNewReleasesPagesHaveBeenObtained_whenGettingThePreviousNewReleasesPage_thenFirstPageIsObtained() {
        // GIVEN
        newReleases.firstPage();
        newReleases.nextPage();

        // WHEN
        Page<Release> newReleasesPage = newReleases.previousPage();

        // THEN
        assertThat(newReleasesPage).as("First new releases page").isEqualTo(NEW_RELEASES_FIRST_PAGE);
    }

    @Test
    void givenFirstTwoNewReleasesPagesHaveBeenObtainedAndOnePreviousNewReleasesPageHasBeenObtained_whenGettingThePreviousNewReleasesPage_thenAnExceptionIsThrown() {
        // GIVEN
        newReleases.firstPage();
        newReleases.nextPage();
        newReleases.previousPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> newReleases.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenAllNextNewReleasesPagesHaveBeenObtained_whenGettingFirstNewReleasesPage_thenFirstNewReleasesPageIsObtained() {
        // GIVEN
        newReleases.firstPage();
        newReleases.nextPage();
        newReleases.nextPage();

        // WHEN
        Page<Release> newReleasesPage = newReleases.firstPage();

        // THEN
        assertThat(newReleasesPage).as("First new releases page").isEqualTo(NEW_RELEASES_FIRST_PAGE);
    }
}
