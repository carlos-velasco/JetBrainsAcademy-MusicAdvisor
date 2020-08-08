package advisor.model;

import advisor.model.dto.CommandLinePrintable;
import advisor.model.dto.Page;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SpotifyResourceCollection {

    private final int pageSize;
    protected int pageNumber;
    protected Integer totalResources;

    protected <T extends CommandLinePrintable> Page<T> firstPage(SpotifyResourcePageFetcher<Integer, Page<T>> resourcePageFetcher) {
        Page<T> firstPage = resourcePageFetcher.getResourcePage(1);
        pageNumber = 1;
        totalResources = firstPage.getTotal();
        return firstPage;
    }

    protected <T extends CommandLinePrintable> Page<T> nextPage(SpotifyResourcePageFetcher<Integer, Page<T>> resourcePageFetcher) {
        ensureNextPage(pageNumber);
        Page<T> nextPage = resourcePageFetcher.getResourcePage(pageNumber + 1);
        totalResources = nextPage.getTotal();
        pageNumber++;
        return nextPage;
    }

    protected <T extends CommandLinePrintable> Page<T> previousPage(SpotifyResourcePageFetcher<Integer, Page<T>> resourcePageFetcher) {
        ensurePreviousPage(pageNumber);
        Page<T> previousPage = resourcePageFetcher.getResourcePage(pageNumber - 1);
        totalResources = previousPage.getTotal();
        pageNumber--;
        return previousPage;
    }

    protected void ensureNextPage(int currentPageNumber) {
        if (totalResources ==  null || currentPageNumber * pageSize >= totalResources) {
            throw new AdvisorException("No more pages");
        }
    }

    protected void ensurePreviousPage(int currentPageNumber) {
        if (currentPageNumber <= 1) {
            throw new AdvisorException("No previous pages");
        }
    }
}

@FunctionalInterface
interface SpotifyResourcePageFetcher<T, R> {
    R getResourcePage(T t);
}
