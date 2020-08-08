package advisor.model.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Release implements CommandLinePrintable {

    String title;
    List<Artist> artists;
    String link;

    @Override
    public String commandLineStringRepresentation() {
        return title + System.lineSeparator()
                + artists.toString() + System.lineSeparator()
                + link;
    }
}
