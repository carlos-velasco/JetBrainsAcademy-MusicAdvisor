package advisor.model.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Playlist implements CommandLinePrintable {

    private Category category;
    private String title;
    private String link;

    @Override
    public String commandLineStringRepresentation() {
        return title + System.lineSeparator() + link;
    }
}
