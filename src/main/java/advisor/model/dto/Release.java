package advisor.model.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Release implements CommandLinePrintable {

    private String title;
    private List<Artist> artists;
    private String link;

    @Override
    public String commandLineStringRepresentation() {
        return title + System.lineSeparator()
                + artists.toString() + System.lineSeparator()
                + link;
    }
}
