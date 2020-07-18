package advisor.controller;

import advisor.authentication.AlwaysAuthenticatedUserCommandAuthentication;
import advisor.authentication.NeverAuthenticatedUserCommandAuthentication;
import advisor.authentication.UserCommandAuthentication;
import advisor.model.AdvisorException;
import advisor.model.dto.Category;
import advisor.model.service.Advisor;
import advisor.model.service.FakeAdvisor;
import advisor.view.CommandLineView;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;

public final class CommandLineControllerAndViewTest {

    private final int defaultPageSize = 2;
    private CommandLineController target;
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private Advisor fakeAdvisor = new FakeAdvisor(defaultPageSize);
    private CommandLineView commandLineView;
    private UserCommandAuthentication userCommandAuthentication = new AlwaysAuthenticatedUserCommandAuthentication();

    @Test
    public void whenInputNew_thenNewReleasesFirstPageIsPrinted() throws AdvisorException {
        // GIVEN
        String input = "new";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getNewReleases(1).getElements().forEach((release) ->
                expectedOutput.append(release.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 2---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void whenInputFeatured_thenFeaturedPlaylistsArePrinted() throws AdvisorException {
        // GIVEN
        String input = "featured";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getFeaturedPlaylists(1).getElements().forEach((playlist) ->
            expectedOutput
                    .append(playlist.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 2---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void whenInputCategories_thenCategoriesArePrinted() throws AdvisorException {
        // GIVEN
        String input = "categories";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategories(1).getElements().forEach((category) ->
                expectedOutput.append(category.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void whenInputPlaylistsAndExistingCategory_thenCategoryPlaylistsArePrinted() throws AdvisorException {
        // GIVEN
        String input = "playlists Good mood";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategoryPlaylists(new Category("Good mood", "goodMood"), 1).getElements().forEach((playlist) ->
            expectedOutput.append(playlist.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 2---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void whenInputPlaylistsAndNonExistingCategory_thenErrorIsPrinted() {
        // GIVEN
        String input = "playlists NonExistingCategory";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        Assert.assertThat(output.toString(), is("Unknown category name." + System.lineSeparator()));
    }

    @Test
    public void whenInputExit_thenGoodbyeIsPrinted() {
        // GIVEN
        String input = "exit";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, userCommandAuthentication, defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        Assert.assertThat(output.toString(), is("---GOODBYE!---" + System.lineSeparator()));
    }

    @Test
    public void whenInputAuthAndAuthenticationIsSuccessful_thenSuccessMessageIsPrinted() {
        // GIVEN
        String input = "auth";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        String expectedOutput = "Success!" + System.lineSeparator();
        Assert.assertThat(output.toString(), is(expectedOutput));
    }

    @Test
    public void whenInputAuthAndAuthenticationIsSuccessful_thenNoMessageIsPrinted() {
        // GIVEN
        String input = "auth";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new NeverAuthenticatedUserCommandAuthentication(), defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        Assert.assertThat(output.toString(), emptyString());
    }

    @Test
    public void givenCategoriesFirstPageHasBeenDisplayed_whenUserInputsNext_thenNextCategoriesPageIsDisplayed() throws AdvisorException {
        // GIVEN
        String input = "categories" + System.lineSeparator() + "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategories(2).getElements().forEach((category) ->
                expectedOutput.append(category.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 2 OF 3---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void givenCategoriesFirstTwoPagesHasBeenDisplayed_whenUserInputsPrev_thenPreviousCategoriesPageIsDisplayed() throws AdvisorException {
        // GIVEN
        String input = "categories" + System.lineSeparator() + "next" + System.lineSeparator() + "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);
        target.processInput();
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategories(1).getElements().forEach((category) ->
                expectedOutput.append(category.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 3---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void givenNewReleasesFirstPageHasBeenDisplayed_whenUserInputsNext_thenNextNewReleasesPageIsDisplayed() throws AdvisorException {
        // GIVEN
        String input = "new" + System.lineSeparator() + "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getNewReleases(2).getElements().forEach((release) ->
                expectedOutput.append(release.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 2 OF 2---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void givenNewReleasesFirstTwoPagesHasBeenDisplayed_whenUserInputsPrev_thenPreviousNewReleasesPageIsDisplayed() throws AdvisorException {
        // GIVEN
        String input = "new" + System.lineSeparator() + "next" + System.lineSeparator() + "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);
        target.processInput();
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getNewReleases(1).getElements().forEach((release) ->
                expectedOutput.append(release.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 2---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void givenFeaturedPlaylistsFirstPageHasBeenDisplayed_whenUserInputsNext_thenNextFeaturedPlaylistsPageIsDisplayed() throws AdvisorException {
        // GIVEN
        String input = "featured" + System.lineSeparator() + "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getFeaturedPlaylists(2).getElements().forEach((playlist) ->
                expectedOutput.append(playlist.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 2 OF 2---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void givenFeaturedPlaylistsFirstTwoPagesHasBeenDisplayed_whenUserInputsPrev_thenPreviousFeaturedPlaylistsPageIsDisplayed() throws AdvisorException {
        // GIVEN
        String input = "featured" + System.lineSeparator() + "next" + System.lineSeparator() + "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);
        target.processInput();
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getFeaturedPlaylists(1).getElements().forEach((playlist) ->
                expectedOutput.append(playlist.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 2---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void givenPlaylistsByCategoryFirstPageHasBeenDisplayed_whenUserInputsNext_thenNextPlaylistsByCategoryPageIsDisplayed() throws AdvisorException {
        // GIVEN
        String input = "playlists Good mood" + System.lineSeparator() + "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategoryPlaylists(new Category("Good mood", "goodMood"), 2).getElements().forEach((playlist) ->
                expectedOutput.append(playlist.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 2 OF 2---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void givenPlaylistsByCategoryFirstTwoPagesHasBeenDisplayed_whenUserInputsPrev_thenPreviousPlaylistsByCategoryPageIsDisplayed() throws AdvisorException {
        // GIVEN
        String input = "playlists Good mood" + System.lineSeparator() + "next" + System.lineSeparator() + "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);
        target.processInput();
        target.processInput();
        output.reset();

        // WHEN
        target.processInput();

        // THEN
        StringBuilder expectedOutput = new StringBuilder();
        fakeAdvisor.getCategoryPlaylists(new Category("Good mood", "goodMood"),1).getElements().forEach((playlist) ->
                expectedOutput.append(playlist.commandLineStringRepresentation()).append(System.lineSeparator()));
        expectedOutput.append("---PAGE 1 OF 2---").append(System.lineSeparator());
        Assert.assertThat(output.toString(), is(expectedOutput.toString()));
    }

    @Test
    public void givenNoContentCommandHasBeenExecuted_whenUserInputsNext_thenAnErrorMessageIsDisplayed() {
        // GIVEN
        String input = "next";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        String expectedOutput = "Execute first one of the following commands: new, categories, featured, playlists"
                + System.lineSeparator();
        Assert.assertThat(output.toString(), is(expectedOutput));
    }

    @Test
    public void givenNoContentCommandHasBeenExecuted_whenUserInputsPrev_thenAnErrorMessageIsDisplayed() {
        // GIVEN
        String input = "prev";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, fakeAdvisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        String expectedOutput = "Execute first one of the following commands: new, categories, featured, playlists"
                + System.lineSeparator();
        Assert.assertThat(output.toString(), is(expectedOutput));
    }
}
