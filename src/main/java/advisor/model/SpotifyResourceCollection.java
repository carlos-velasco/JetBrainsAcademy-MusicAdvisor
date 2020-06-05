package advisor.model;

import advisor.model.dto.CommandLinePrintable;
import advisor.model.dto.Page;

public abstract class SpotifyResourceCollection {

    private int pageSize;
    protected int pageNumber;
    protected Integer totalResources;

    protected SpotifyResourceCollection(int pageSize) {
        this.pageSize = pageSize;
    }

    protected <T extends CommandLinePrintable> Page<T> firstPage(SpotifyAdvisorFunction<Integer, Page<T>> advisorFunction) throws AdvisorException {
        Page<T> firstPage = advisorFunction.apply(1);
        pageNumber = 1;
        totalResources = firstPage.getTotal();
        return firstPage;
    }

    protected <T extends CommandLinePrintable> Page<T>  nextPage(SpotifyAdvisorFunction<Integer, Page<T>> advisorFunction) throws AdvisorException {
        validateNextPage(pageNumber);
        Page<T> nextPage = advisorFunction.apply(pageNumber + 1);
        totalResources = nextPage.getTotal();
        pageNumber++;
        return nextPage;
    }

    protected <T extends CommandLinePrintable> Page<T> previousPage(SpotifyAdvisorFunction<Integer, Page<T>> advisorFunction) throws AdvisorException {
        validatePreviousPage(pageNumber);
        Page<T> previousPage = advisorFunction.apply(pageNumber - 1);
        totalResources = previousPage.getTotal();
        pageNumber--;
        return previousPage;
    }

    protected void validateNextPage(int currentPageNumber) throws AdvisorException {
        if (totalResources ==  null || currentPageNumber * pageSize >= totalResources) {
            throw new AdvisorException("No more pages");
        }
    }

    protected void validatePreviousPage(int currentPageNumber) throws AdvisorException {
        if (currentPageNumber <= 1) {
            throw new AdvisorException("No previous pages");
        }
    }
}

@FunctionalInterface
interface SpotifyAdvisorFunction<T, R> {
    R apply(T t) throws AdvisorException;
}
