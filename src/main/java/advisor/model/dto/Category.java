package advisor.model.dto;

import lombok.Value;

@Value
public class Category implements CommandLinePrintable {

    String name;
    String id;

    @Override
    public String commandLineStringRepresentation() {
        return name;
    }
}
