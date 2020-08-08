package advisor.model;

import advisor.model.dto.Category;
import advisor.model.dto.Page;
import advisor.model.service.Advisor;

public class Categories extends SpotifyResourceCollection implements PageableSpotifyModel<Category> {

    private final Advisor advisor;

    public Categories(Advisor advisor, int pageSize) {
        super(pageSize);
        this.advisor = advisor;
    }

    public Page<Category> firstPage() {
        return firstPage(advisor::getCategories);
    }

    @Override
    public Page<Category> nextPage() {
        return nextPage(advisor::getCategories);
    }

    @Override
    public Page<Category> previousPage() {
        return previousPage(advisor::getCategories);
    }
}
