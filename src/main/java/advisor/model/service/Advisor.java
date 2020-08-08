package advisor.model.service;

import advisor.model.dto.Category;
import advisor.model.dto.Page;
import advisor.model.dto.Playlist;
import advisor.model.dto.Release;

public interface Advisor {

    Page<Category> getCategories(int pageNumber);

    Page<Category> getCategories();

    Page<Release> getNewReleases(int pageNumber);

    Page<Playlist> getCategoryPlaylists(Category category, int pageNumber);

    Page<Playlist> getFeaturedPlaylists(int pageNumber);
}
