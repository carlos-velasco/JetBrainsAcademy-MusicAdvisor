package advisor.controller;

import advisor.authentication.AlwaysAuthenticatedUserCommandAuthentication;
import advisor.authentication.NeverAuthenticatedUserCommandAuthentication;
import advisor.model.service.Advisor;
import advisor.model.service.FakeAdvisor;
import advisor.view.CommandLineView;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

final class CommandLineControllerAuthenticationTest {
    
    private static final String PROVIDE_ACCESS_MESSAGE = "Please, provide access for application.".toLowerCase();
    private static final int DEFAULT_PAGE_SIZE = 5;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Advisor advisor = new FakeAdvisor(DEFAULT_PAGE_SIZE);
    private CommandLineController target;
    private CommandLineView commandLineView;

    @ParameterizedTest
    @MethodSource("getUserCommands")
    void givenUserIsAuthenticated_whenProcessingCommand_thenMessageDoesNotAskForAuthentication(String userCommandText) {
        // GIVEN
        InputStream inputStream = new ByteArrayInputStream(userCommandText.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, advisor, new AlwaysAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);
        
        // WHEN
        target.processInput();

        // THEN
        assertThat(output.toString().toLowerCase()).doesNotContain(PROVIDE_ACCESS_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("getUserCommandsWithAuthenticationRequired")
    void givenUserIsNotAuthenticated_whenProcessingCommandRequiringAuthentication_thenMessageAsksForAuthentication(String userCommandText) {
        // GIVEN
        InputStream inputStream = new ByteArrayInputStream(userCommandText.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, advisor, new NeverAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        assertThat(output.toString()).containsIgnoringCase(PROVIDE_ACCESS_MESSAGE);
    }

    @ParameterizedTest
    @MethodSource("getUserCommandsWithAuthenticationNotRequired")
    void givenUserIsNotAuthenticated_whenProcessingCommandNotRequiringAuthentication_thenMessageDoesNotAskForAuthentication(String userCommandText) {
        // GIVEN
        InputStream inputStream = new ByteArrayInputStream(userCommandText.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), DEFAULT_PAGE_SIZE);
        target = new CommandLineController(commandLineView, advisor, new NeverAuthenticatedUserCommandAuthentication(), DEFAULT_PAGE_SIZE);

        // WHEN
        target.processInput();

        // THEN
        assertThat(output.toString().toLowerCase()).doesNotContain(PROVIDE_ACCESS_MESSAGE);
    }

    private static Stream<String> getUserCommandsWithAuthenticationRequired() {
        return Stream.of(
                UserCommand.CATEGORIES.getCommandText(),
                UserCommand.NEW_RELEASES.getCommandText(),
                UserCommand.PLAYLISTS.getCommandText() + " Mood",
                UserCommand.FEATURED_PLAYLISTS.getCommandText(),
                UserCommand.PREVIOUS.getCommandText(),
                UserCommand.NEXT.getCommandText()
        );
    }

    private static Stream<String> getUserCommandsWithAuthenticationNotRequired() {
        return Stream.of(
                UserCommand.AUTH.getCommandText(),
                UserCommand.EXIT.getCommandText(),
                "not supported"
        );
    }

    private static Stream<String> getUserCommands() {
        return Stream.concat(
                getUserCommandsWithAuthenticationRequired(),
                getUserCommandsWithAuthenticationNotRequired()
        );
    }
}
