package advisor.view;

import advisor.model.dto.Category;
import advisor.model.dto.Page;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

final class CommandLineViewTest {

    private static final int PAGE_SIZE = 10;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private CommandLineView commandLineView;

    @Test
    void givenPageTotalIsOneUnitLessThanPageSize_whenPrintingAPage_thenTotalOfPagesIsOne() {
        // GIVEN
        commandLineView = new CommandLineView(new Scanner(System.in), new PrintStream(output), PAGE_SIZE);
        Page<Category> page = new Page<>(List.of(), PAGE_SIZE - 1, 1);

        // WHEN
        commandLineView.printPage(page);

        // THEN
        assertThat(output).hasToString("---PAGE 1 OF 1---" + System.lineSeparator());
    }

    @Test
    void givenPageTotalIsEqualToPageSize_whenPrintingAPage_thenTotalOfPagesIsOne() {
        // GIVEN
        commandLineView = new CommandLineView(new Scanner(System.in), new PrintStream(output), PAGE_SIZE);
        Page<Category> page = new Page<>(List.of(), PAGE_SIZE, 1);

        // WHEN
        commandLineView.printPage(page);

        // THEN
        assertThat(output).hasToString("---PAGE 1 OF 1---" + System.lineSeparator());
    }

    @Test
    void givenPageTotalIsOneUnitGreaterThanPageSize_whenPrintingAPage_thenTotalOfPagesIsTwo() {
        // GIVEN
        commandLineView = new CommandLineView(new Scanner(System.in), new PrintStream(output), PAGE_SIZE);
        Page<Category> page = new Page<>(List.of(), PAGE_SIZE + 1, 1);

        // WHEN
        commandLineView.printPage(page);

        // THEN
        assertThat(output).hasToString("---PAGE 1 OF 2---" + System.lineSeparator());
    }
}
