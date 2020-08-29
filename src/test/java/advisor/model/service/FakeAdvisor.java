package advisor.model.service;

import advisor.model.AdvisorException;
import advisor.model.dto.Category;
import advisor.model.dto.Page;
import advisor.model.dto.Playlist;
import advisor.model.dto.Release;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static advisor.model.service.FakeAdvisorData.*;

@RequiredArgsConstructor
public final class FakeAdvisor implements Advisor {

    private final int pageSize;

    @Override
    public Page<Category> getCategories(int pageNumber) {
        if (pageNumber * pageSize > CATEGORIES.size()) {
            return new Page<>(List.of(), CATEGORIES.size(), pageNumber);
        }
        return new Page<>(CATEGORIES.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize)),
                CATEGORIES.size(),
                pageNumber);
    }

    @Override
    public Page<Category> getCategories() {
        return new Page<>(CATEGORIES, CATEGORIES.size(), 0);
    }

    @Override
    public Page<Release> getNewReleases(int pageNumber) {
        if (pageNumber * pageSize > RELEASES.size()) {
            return new Page<>(List.of(), RELEASES.size(), pageNumber);
        }
        return new Page<>(RELEASES.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize)),
                RELEASES.size(),
                pageNumber);
    }

    @Override
    public Page<Playlist> getCategoryPlaylists(Category category, int pageNumber) {
        if (GOOD_MOOD_CATEGORY.getId().equals(category.getId())) {
            if (pageNumber * pageSize > GOOD_MOOD_CATEGORY_PLAYLISTS.size()) {
                return new Page<>(List.of(), GOOD_MOOD_CATEGORY_PLAYLISTS.size(), pageNumber);
            }
            return new Page<>(GOOD_MOOD_CATEGORY_PLAYLISTS.subList((pageNumber - 1) * pageSize,
                    (pageNumber * pageSize)), GOOD_MOOD_CATEGORY_PLAYLISTS.size(),
                    pageNumber);

        }
        throw new AdvisorException("Unknown category name.");
    }

    @Override
    public Page<Playlist> getFeaturedPlaylists(int pageNumber) {
        if (pageNumber * pageSize > FEATURED_PLAYLISTS.size()) {
            return new Page<>(List.of(), FEATURED_PLAYLISTS.size(), pageNumber);
        }
        return new Page<>(FEATURED_PLAYLISTS.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize)),
                FEATURED_PLAYLISTS.size(),
                pageNumber);
    }
}
