package advisor.model;

import advisor.model.dto.CommandLinePrintable;
import advisor.model.dto.Page;

public interface PageableSpotifyModel<T extends CommandLinePrintable> {

    Page<T> nextPage();
    Page<T> previousPage();
}
