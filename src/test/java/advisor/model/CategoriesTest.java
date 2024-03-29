package advisor.model;

import advisor.model.dto.Category;
import advisor.model.dto.Page;
import advisor.model.service.FakeAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static advisor.model.service.FakeAdvisorData.CATEGORIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

final class CategoriesTest {

    private static final int PAGE_SIZE = 2;
    private static final int TOTAL_CATEGORIES = CATEGORIES.size();
    private static final Page<Category> CATEGORIES_FIRST_PAGE = new Page<>(
            CATEGORIES.subList(0, PAGE_SIZE), TOTAL_CATEGORIES, 1);
    private static final Page<Category> CATEGORIES_SECOND_PAGE = new Page<>(
            CATEGORIES.subList(PAGE_SIZE, (PAGE_SIZE * 2)), TOTAL_CATEGORIES, 2);
    private static final Page<Category> CATEGORIES_THIRD_PAGE = new Page<>(
            CATEGORIES.subList(PAGE_SIZE * 2, (PAGE_SIZE * 3)), TOTAL_CATEGORIES, 3);

    private Categories categories;

    @BeforeEach
    void prepareTarget() {
        categories = new Categories(new FakeAdvisor(PAGE_SIZE), PAGE_SIZE);
    }

    @Test
    void whenGettingTheFirstCategoriesPage_thenTheFirstCategoriesPageIsReturned() {
        // WHEN
        Page<Category> categoryPage = categories.firstPage();

        // THEN
        assertThat(categoryPage).as("First categories page").isEqualTo(CATEGORIES_FIRST_PAGE);
    }

    @Test
    void givenFirstCategoriesPageHasNotBeenObtained_whenGettingTheNextCategoriesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> categories.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    void givenFirstCategoriesPageHasNotBeenObtained_whenGettingThePreviousCategoriesPage_thenAnExceptionIsThrown() {
        // WHEN
        Throwable thrown = catchThrowable(() -> categories.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenFirstCategoriesPageHasBeenObtained_whenGettingTheNextCategoriesPage_thenTheNextCategoryPageIsReturned() {
        // GIVEN
        categories.firstPage();

        // WHEN
        Page<Category> categoryPage = categories.nextPage();

        // THEN
        assertThat(categoryPage).as("Second categories page").isEqualTo(CATEGORIES_SECOND_PAGE);
    }

    @Test
    void givenAllWholeCategoriesPagesHaveBeenObtained_whenGettingTheNextCategoriesPage_thenTheLastCategoryPageIsReturned() {
        // GIVEN
        categories.firstPage();
        categories.nextPage();

        // WHEN
        Page<Category> categoryPage = categories.nextPage();

        // THEN
        assertThat(categoryPage).as("Third categories page").isEqualTo(CATEGORIES_THIRD_PAGE);
    }

    @Test
    void givenAllNextCategoryPagesHaveBeenObtained_whenGettingTheNextCategoriesPage_thenAnExceptionIsThrown() {
        // GIVEN
        categories.firstPage();
        categories.nextPage();
        categories.nextPage();


        // WHEN
        Throwable thrown = catchThrowable(() -> categories.nextPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No more pages");
    }

    @Test
    void givenFirstCategoriesPageHasBeenObtained_whenGettingThePreviousCategoriesPage_thenAnExceptionIsThrown() {
        // GIVEN
        categories.firstPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> categories.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenFirstTwoCategoriesPagesHaveBeenObtained_whenGettingThePreviousCategoriesPage_thenFirstPageIsObtained() {
        // GIVEN
        categories.firstPage();
        categories.nextPage();

        // WHEN
        Page<Category> categoryPage = categories.previousPage();

        // THEN
        assertThat(categoryPage).as("First categories page").isEqualTo(CATEGORIES_FIRST_PAGE);
    }

    @Test
    void givenFirstTwoCategoriesPagesHaveBeenObtainedAndOnePreviousCategoryPageHasBeenObtained_whenGettingThePreviousCategoriesPage_thenAnExceptionIsThrown() {
        // GIVEN
        categories.firstPage();
        categories.nextPage();
        categories.previousPage();

        // WHEN
        Throwable thrown = catchThrowable(() -> categories.previousPage());

        // THEN
        assertThat(thrown).isInstanceOf(AdvisorException.class)
                .hasMessage("No previous pages");
    }

    @Test
    void givenAllNextCategoriesPagesHaveBeenObtained_whenGettingFirstCategoriesPage_thenFirstCategoriesPageIsObtained() {
        // GIVEN
        categories.firstPage();
        categories.nextPage();
        categories.nextPage();

        // WHEN
        Page<Category> categoryPage = categories.firstPage();

        // THEN
        assertThat(categoryPage).as("First categories page").isEqualTo(CATEGORIES_FIRST_PAGE);
    }
}
