package advisor.model.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
public class Page<T extends CommandLinePrintable> {

    private List<T> elements;
    private int total;
    private int pageNumber;
}
