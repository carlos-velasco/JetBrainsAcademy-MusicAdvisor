package advisor.model;

import advisor.model.dto.Page;
import advisor.model.dto.Playlist;
import advisor.model.service.Advisor;

public class FeaturedPlaylists extends SpotifyResourceCollection implements PageableSpotifyModel<Playlist> {
    private final Advisor advisor;

    public FeaturedPlaylists(Advisor advisor, int pageSize) {
        super(pageSize);
        this.advisor = advisor;
    }

    public Page<Playlist> firstPage() {
        return firstPage(advisor::getFeaturedPlaylists);
    }

    @Override
    public Page<Playlist> nextPage() {
        return nextPage(advisor::getFeaturedPlaylists);
    }

    @Override
    public Page<Playlist> previousPage() {
        return previousPage(advisor::getFeaturedPlaylists);
    }
}
