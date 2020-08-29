package advisor.controller;

import advisor.authentication.AlwaysAuthenticatedUserCommandAuthentication;
import advisor.authentication.NeverAuthenticatedUserCommandAuthentication;
import advisor.authentication.UserCommandAuthentication;
import advisor.model.service.Advisor;
import advisor.model.service.FakeAdvisor;
import advisor.view.CommandLineView;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static advisor.model.service.FakeAdvisorData.GOOD_MOOD_CATEGORY;
import static org.assertj.core.api.Assertions.assertThat;

final class CommandLineControllerAndViewTest {

    private static final int DEFAULT_PAGE_SIZE = 2;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Advisor fakeAdvisor = new FakeAdvisor(DEFAULT_PAGE_SIZE);
    private final UserCommandAuthentication userCommandAuthentication = new AlwaysAuthenticatedUserCommandAuthentication();
    private CommandLineView commandLineView;
    private CommandLineController target;

    @Test
    void whenProcessingUnsupportedCommand_thenUnsupportedCommandMessageIsPrinted() {
        // GIVEN
        String input = "not supported";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        assertThat(output).hasToString("Unsupported operation" + System.lineSeparator());
    }

    @Test
    void whenInputNew_thenNewReleasesFirstPageIsPrinted() {
        // GIVEN
        String input = "new";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getNewReleases(1).getElements().forEach((release) ->
                expectedOutput
                        .append(release.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void whenInputFeatured_thenFeaturedPlaylistsArePrinted() {
        // GIVEN
        String input = "featured";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getFeaturedPlaylists(1).getElements().forEach((playlist) ->
                expectedOutput
                        .append(playlist.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void whenInputCategories_thenCategoriesArePrinted() {
        // GIVEN
        String input = "categories";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategories(1).getElements().forEach((category) ->
                expectedOutput
                        .append(category.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void whenInputPlaylistsAndExistingCategory_thenCategoryPlaylistsArePrinted() {
        // GIVEN
        String input = "playlists Good mood";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategoryPlaylists(GOOD_MOOD_CATEGORY, 1).getElements().forEach((playlist) ->
                expectedOutput
                        .append(playlist.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void whenInputPlaylistsAndNonExistingCategory_thenErrorIsPrinted() {
        // GIVEN
        String input = "playlists NonExistingCategory";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        assertThat(output).hasToString("Unknown category name." + System.lineSeparator());
    }

    @Test
    void whenInputExit_thenGoodbyeIsPrinted() {
        // GIVEN
        String input = "exit";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        assertThat(output).hasToString("---GOODBYE!---" + System.lineSeparator());
    }

    @Test
    void whenInputAuthAndAuthenticationIsSuccessful_thenSuccessMessageIsPrinted() {
        // GIVEN
        String input = "auth";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        String expectedOutput = "Success!" + System.lineSeparator();
        assertThat(output).hasToString(expectedOutput);
    }

    @Test
    void whenInputAuthAndAuthenticationIsSuccessful_thenNoMessageIsPrinted() {
        // GIVEN
        String input = "auth";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new NeverAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        assertThat(output.toString()).isEmpty();
    }

    @Test
    void givenCategoriesFirstPageHasBeenDisplayed_whenUserInputsNext_thenNextCategoriesPageIsDisplayed() {
        // GIVEN
        String input = "categories" + System.lineSeparator() + "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategories(2).getElements().forEach((category) ->
                expectedOutput
                        .append(category.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 2 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void givenCategoriesFirstTwoPagesHasBeenDisplayed_whenUserInputsPrev_thenPreviousCategoriesPageIsDisplayed() {
        // GIVEN
        String input = "categories" + System.lineSeparator() + "next" + System.lineSeparator() + "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);
        target.processInput();
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategories(1).getElements().forEach((category) ->
                expectedOutput
                        .append(category.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void givenNewReleasesFirstPageHasBeenDisplayed_whenUserInputsNext_thenNextNewReleasesPageIsDisplayed() {
        // GIVEN
        String input = "new" + System.lineSeparator() + "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getNewReleases(2).getElements().forEach((release) ->
                expectedOutput
                        .append(release.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 2 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void givenNewReleasesFirstTwoPagesHasBeenDisplayed_whenUserInputsPrev_thenPreviousNewReleasesPageIsDisplayed() {
        // GIVEN
        String input = "new" + System.lineSeparator() + "next" + System.lineSeparator() + "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);
        target.processInput();
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getNewReleases(1).getElements().forEach((release) ->
                expectedOutput
                        .append(release.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void givenFeaturedPlaylistsFirstPageHasBeenDisplayed_whenUserInputsNext_thenNextFeaturedPlaylistsPageIsDisplayed() {
        // GIVEN
        String input = "featured" + System.lineSeparator() + "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getFeaturedPlaylists(2).getElements().forEach((playlist) ->
                expectedOutput
                        .append(playlist.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 2 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void givenFeaturedPlaylistsFirstTwoPagesHasBeenDisplayed_whenUserInputsPrev_thenPreviousFeaturedPlaylistsPageIsDisplayed() {
        // GIVEN
        String input = "featured" + System.lineSeparator() + "next" + System.lineSeparator() + "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);
        target.processInput();
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getFeaturedPlaylists(1).getElements().forEach((playlist) ->
                expectedOutput
                        .append(playlist.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void givenPlaylistsByCategoryFirstPageHasBeenDisplayed_whenUserInputsNext_thenNextPlaylistsByCategoryPageIsDisplayed() {
        // GIVEN
        String input = "playlists Good mood" + System.lineSeparator() + "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategoryPlaylists(GOOD_MOOD_CATEGORY, 2).getElements().forEach((playlist) ->
                expectedOutput
                        .append(playlist.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 2 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void givenPlaylistsByCategoryFirstTwoPagesHasBeenDisplayed_whenUserInputsPrev_thenPreviousPlaylistsByCategoryPageIsDisplayed() {
        // GIVEN
        String input = "playlists Good mood" + System.lineSeparator() + "next" + System.lineSeparator() + "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);
        target.processInput();
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategoryPlaylists(GOOD_MOOD_CATEGORY, 1).getElements().forEach((playlist) ->
                expectedOutput
                        .append(playlist.commandLineStringRepresentation())
                        .append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        assertThat(output).hasToString(expectedOutput.toString());
    }

    @Test
    void givenNoContentCommandHasBeenExecuted_whenUserInputsNext_thenAnErrorMessageIsDisplayed() {
        // GIVEN
        String input = "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        String expectedOutput = "Execute first one of the following commands: new, categories, featured, playlists"
                + System.lineSeparator();
        assertThat(output).hasToString(expectedOutput);
    }

    @Test
    void givenNoContentCommandHasBeenExecuted_whenUserInputsPrev_thenAnErrorMessageIsDisplayed() {
        // GIVEN
        String input = "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        String expectedOutput = "Execute first one of the following commands: new, categories, featured, playlists"
                + System.lineSeparator();
        assertThat(output).hasToString(expectedOutput);
    }
}
