package advisor.model.dto;

import lombok.Value;

@Value
public class Category implements CommandLinePrintable {

    private String name;
    private String id;

    @Override
    public String commandLineStringRepresentation() {
        return name;
    }
}
