package advisor.model.dto;

import lombok.Value;

@Value
public class Artist {

    String name;

    @Override
    public String toString() {
        return name;
    }
}
