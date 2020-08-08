package advisor.model;

import advisor.model.dto.Category;
import advisor.model.dto.Page;
import advisor.model.dto.Playlist;
import advisor.model.service.Advisor;

import java.util.Optional;

public class PlaylistsByCategory extends SpotifyResourceCollection implements PageableSpotifyModel<Playlist> {

    private final Advisor advisor;
    private Category category;

    public PlaylistsByCategory(Advisor advisor, int pageSize) {
        super(pageSize);
        this.advisor = advisor;
    }

    public Page<Playlist> firstPage(String categoryName) {
        category = advisor.getCategories().getElements().stream()
                .filter(cat -> cat.getName().equals(categoryName))
                .findFirst()
                .orElseThrow(() -> new AdvisorException("Unknown category name."));
        Page<Playlist> firstPage = advisor.getCategoryPlaylists(category, 1);
        pageNumber = 1;
        totalResources = firstPage.getTotal();
        return firstPage;
    }

    @Override
    public Page<Playlist> nextPage() {
        validateCategory();
        validateNextPage(pageNumber);
        Page<Playlist> nextPage = advisor.getCategoryPlaylists(category, pageNumber + 1);
        totalResources = nextPage.getTotal();
        pageNumber++;
        return nextPage;
    }

    @Override
    public Page<Playlist> previousPage() {
        validateCategory();
        validatePreviousPage(pageNumber);
        Page<Playlist> previousPage = advisor.getCategoryPlaylists(category, pageNumber - 1);
        totalResources = previousPage.getTotal();
        pageNumber--;
        return previousPage;
    }

    private void validateCategory() {
        Optional.ofNullable(category)
                .orElseThrow(() -> new AdvisorException("Category must be specified first"));
    }
}
