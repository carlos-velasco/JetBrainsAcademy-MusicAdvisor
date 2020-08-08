package advisor.model.dto;

import lombok.Value;

import java.util.List;

@Value
public class Page<T extends CommandLinePrintable> {

    List<T> elements;
    int total;
    int pageNumber;
}
