package advisor.model;

import advisor.model.dto.Category;
import advisor.model.dto.Page;
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
public class CategoriesTest {

    private static final int pageSize = 2;
    private static final Page<Category> CATEGORIES_FIRST_PAGE = new Page<>(
            List.of(
                    new Category("Page 1 Category 1", "page1Category1"),
                    new Category("Page 1 Category 2", "page1Category2")),
            (pageSize * 2) + 1,
            1);

    private static final Page<Category> CATEGORIES_SECOND_PAGE = new Page<>(
            List.of(
                    new Category("Page 2 Category 1", "page2Category1"),
                    new Category("Page 2 Category 2", "page2Category2")),
            (pageSize * 2) + 1,
            2);

    private static final Page<Category> CATEGORIES_THIRD_PAGE = new Page<>(
            List.of(
                    new Category("Page 3 Category 1", "page3Category1")),
            (pageSize * 2) + 1,
            3);

    @Mock
    private Advisor advisor;

    private Categories target;

    @Before
    public void prepareTarget() throws AdvisorException {
        target = new Categories(advisor, pageSize);
        when(advisor.getCategories(1)).thenReturn(CATEGORIES_FIRST_PAGE);
        when(advisor.getCategories(2)).thenReturn(CATEGORIES_SECOND_PAGE);
        when(advisor.getCategories(3)).thenReturn(CATEGORIES_THIRD_PAGE);
    }

    @Test
    public void whenGettingTheFirstCategoriesPage_thenTheFirstCategoriesPageIsReturned() throws AdvisorException {
        // WHEN
        Page<Category> categoryPage = target.firstPage();

        // THEN
        assertThat(categoryPage).as("First categories page").isEqualTo(CATEGORIES_FIRST_PAGE);
    }

    @Test
    public void givenFirstCategoriesPageHasNotBeenObtained_whenGettingTheNextCategoriesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    public void givenFirstCategoriesPageHasNotBeenObtained_whenGettingThePreviousCategoriesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    public void givenFirstCategoriesPageHasBeenObtained_whenGettingTheNextCategoriesPage_thenTheNextCategoryPageIsReturned() throws AdvisorException {
        // GIVEN
        target.firstPage();

        // WHEN
        Page<Category> categoryPage = target.nextPage();

        // THEN
        assertThat(categoryPage).as("Second categories page").isEqualTo(CATEGORIES_SECOND_PAGE);
    }

    @Test
    public void givenAllWholeCategoriesPagesHaveBeenObtained_whenGettingTheNextCategoriesPage_thenTheLastCategoryPageIsReturned() throws AdvisorException {
        // GIVEN
        target.firstPage();
        target.nextPage();

        // WHEN
        Page<Category> categoryPage = target.nextPage();

        // THEN
        assertThat(categoryPage).as("Third categories page").isEqualTo(CATEGORIES_THIRD_PAGE);
    }

    @Test
    public void givenAllNextCategoryPagesHaveBeenObtained_whenGettingTheNextCategoriesPage_thenAnExceptionIsThrown() throws AdvisorException {
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
    public void givenFirstCategoriesPageHasBeenObtained_whenGettingThePreviousCategoriesPage_thenAnExceptionIsThrown() throws AdvisorException {
        // GIVEN
        target.firstPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    public void givenFirstTwoCategoriesPagesHaveBeenObtained_whenGettingThePreviousCategoriesPage_thenFirstPageIsObtained() throws AdvisorException {
        // GIVEN
        target.firstPage();
        target.nextPage();

        // WHEN
        Page<Category> categoryPage = target.previousPage();

        // THEN
        assertThat(categoryPage).as("First categories page").isEqualTo(CATEGORIES_FIRST_PAGE);
    }

    @Test
    public void givenFirstTwoCategoriesPagesHaveBeenObtainedAndOnePreviousCategoryPageHasBeenObtained_whenGettingThePreviousCategoriesPage_thenAnExceptionIsThrown() throws AdvisorException {
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
    public void givenAllNextCategoriesPagesHaveBeenObtained_whenGettingFirstCategoriesPage_thenFirstCategoriesPageIsObtained() throws AdvisorException {
        // GIVEN
        target.firstPage();
        target.nextPage();
        target.nextPage();

        // WHEN
        Page<Category> categoryPage = target.firstPage();

        // THEN
        assertThat(categoryPage).as("First categories page").isEqualTo(CATEGORIES_FIRST_PAGE);
    }
}
