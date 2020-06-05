package advisor.model.service;

import advisor.model.AdvisorException;
import advisor.model.dto.Category;
import advisor.model.dto.Page;
import advisor.model.dto.Playlist;
import advisor.model.dto.Release;

public interface Advisor {

    Page<Category> getCategories(int pageNumber) throws AdvisorException;

    Page<Category> getCategories() throws AdvisorException;

    Page<Release> getNewReleases(int pageNumber) throws AdvisorException;

    Page<Playlist> getCategoryPlaylists(Category category, int pageNumber) throws AdvisorException;

    Page<Playlist> getFeaturedPlaylists(int pageNumber) throws AdvisorException;
}
