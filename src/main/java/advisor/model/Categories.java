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

    public Page<Category> firstPage() throws AdvisorException {
        return firstPage(advisor::getCategories);
    }

    @Override
    public Page<Category> nextPage() throws AdvisorException {
        return nextPage(advisor::getCategories);
    }

    @Override
    public Page<Category> previousPage() throws AdvisorException {
        return previousPage(advisor::getCategories);
    }
}
