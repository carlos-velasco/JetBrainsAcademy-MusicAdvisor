package advisor.model;

import advisor.model.dto.Category;
import advisor.model.dto.Page;
import advisor.model.service.Advisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
final class CategoriesTest {

    private static final int PAGE_SIZE = 2;
    private static final Page<Category> CATEGORIES_FIRST_PAGE = new Page<>(
            List.of(
                    new Category("Page 1 Category 1", "page1Category1"),
                    new Category("Page 1 Category 2", "page1Category2")),
            (PAGE_SIZE * 2) + 1,
            1);

    private static final Page<Category> CATEGORIES_SECOND_PAGE = new Page<>(
            List.of(
                    new Category("Page 2 Category 1", "page2Category1"),
                    new Category("Page 2 Category 2", "page2Category2")),
            (PAGE_SIZE * 2) + 1,
            2);

    private static final Page<Category> CATEGORIES_THIRD_PAGE = new Page<>(
            List.of(
                    new Category("Page 3 Category 1", "page3Category1")),
            (PAGE_SIZE * 2) + 1,
            3);

    @Mock
    private Advisor advisor;

    private Categories target;

    @BeforeEach
    void prepareTarget() {
        target = new Categories(advisor, PAGE_SIZE);
        lenient().when(advisor.getCategories(1)).thenReturn(CATEGORIES_FIRST_PAGE);
        lenient().when(advisor.getCategories(2)).thenReturn(CATEGORIES_SECOND_PAGE);
        lenient().when(advisor.getCategories(3)).thenReturn(CATEGORIES_THIRD_PAGE);
    }

    @Test
    void whenGettingTheFirstCategoriesPage_thenTheFirstCategoriesPageIsReturned() {
        // WHEN
        Page<Category> categoryPage = target.firstPage();

        // THEN
        assertThat(categoryPage).as("First categories page").isEqualTo(CATEGORIES_FIRST_PAGE);
    }

    @Test
    void givenFirstCategoriesPageHasNotBeenObtained_whenGettingTheNextCategoriesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    void givenFirstCategoriesPageHasNotBeenObtained_whenGettingThePreviousCategoriesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenFirstCategoriesPageHasBeenObtained_whenGettingTheNextCategoriesPage_thenTheNextCategoryPageIsReturned() {
        // GIVEN
        target.firstPage();

        // WHEN
        Page<Category> categoryPage = target.nextPage();

        // THEN
        assertThat(categoryPage).as("Second categories page").isEqualTo(CATEGORIES_SECOND_PAGE);
    }

    @Test
    void givenAllWholeCategoriesPagesHaveBeenObtained_whenGettingTheNextCategoriesPage_thenTheLastCategoryPageIsReturned() {
        // GIVEN
        target.firstPage();
        target.nextPage();

        // WHEN
        Page<Category> categoryPage = target.nextPage();

        // THEN
        assertThat(categoryPage).as("Third categories page").isEqualTo(CATEGORIES_THIRD_PAGE);
    }

    @Test
    void givenAllNextCategoryPagesHaveBeenObtained_whenGettingTheNextCategoriesPage_thenAnExceptionIsThrown() {
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
    void givenFirstCategoriesPageHasBeenObtained_whenGettingThePreviousCategoriesPage_thenAnExceptionIsThrown() {
        // GIVEN
        target.firstPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> target.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenFirstTwoCategoriesPagesHaveBeenObtained_whenGettingThePreviousCategoriesPage_thenFirstPageIsObtained() {
        // GIVEN
        target.firstPage();
        target.nextPage();

        // WHEN
        Page<Category> categoryPage = target.previousPage();

        // THEN
        assertThat(categoryPage).as("First categories page").isEqualTo(CATEGORIES_FIRST_PAGE);
    }

    @Test
    void givenFirstTwoCategoriesPagesHaveBeenObtainedAndOnePreviousCategoryPageHasBeenObtained_whenGettingThePreviousCategoriesPage_thenAnExceptionIsThrown() {
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
    void givenAllNextCategoriesPagesHaveBeenObtained_whenGettingFirstCategoriesPage_thenFirstCategoriesPageIsObtained() {
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
