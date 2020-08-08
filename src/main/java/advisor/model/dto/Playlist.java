package advisor.model.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Playlist implements CommandLinePrintable {

    Category category;
    String title;
    String link;

    @Override
    public String commandLineStringRepresentation() {
        return title + System.lineSeparator() + link;
    }
}
