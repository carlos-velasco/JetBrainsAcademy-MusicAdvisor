package advisor.view;

import advisor.model.dto.Category;
import advisor.model.dto.Page;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.hamcrest.core.Is.is;

public class CommandLineViewTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private CommandLineView target;
    private static final int PAGE_SIZE = 10;

    @Test
    public void givenPageTotalIsOneUnitLessThanPageSize_whenPrintingAPage_thenTotalOfPagesIsOne() {
        // GIVEN
        target = new CommandLineView(new Scanner(System.in), new PrintStream(output), PAGE_SIZE);
        Page<Category> page = new Page<>(List.of(), PAGE_SIZE - 1, 1);

        // WHEN
        target.printPage(page);

        // THEN
        Assert.assertThat(output.toString(), is("---PAGE 1 OF 1---" + System.lineSeparator()));
    }

    @Test
    public void givenPageTotalIsEqualToPageSize_whenPrintingAPage_thenTotalOfPagesIsOne() {
        // GIVEN
        target = new CommandLineView(new Scanner(System.in), new PrintStream(output), PAGE_SIZE);
        Page<Category> page = new Page<>(List.of(), PAGE_SIZE, 1);

        // WHEN
        target.printPage(page);

        // THEN
        Assert.assertThat(output.toString(), is("---PAGE 1 OF 1---" + System.lineSeparator()));
    }

    @Test
    public void givenPageTotalIsOneUnitGreaterThanPageSize_whenPrintingAPage_thenTotalOfPagesIsTwo() {
        // GIVEN
        target = new CommandLineView(new Scanner(System.in), new PrintStream(output), PAGE_SIZE);
        Page<Category> page = new Page<>(List.of(), PAGE_SIZE + 1, 1);

        // WHEN
        target.printPage(page);

        // THEN
        Assert.assertThat(output.toString(), is("---PAGE 1 OF 2---" + System.lineSeparator()));
    }
}
