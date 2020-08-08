package advisor.controller;

import advisor.authentication.AlwaysAuthenticatedUserCommandAuthentication;
import advisor.authentication.NeverAuthenticatedUserCommandAuthentication;
import advisor.model.service.Advisor;
import advisor.model.service.FakeAdvisor;
import advisor.view.CommandLineView;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@RunWith(Parameterized.class)
public final class CommandLineControllerAuthenticationTest {
    
    private static final String PROVIDE_ACCESS_MESSAGE = "Please, provide access for application.".toLowerCase();
    private final String userCommandInput;
    private final boolean needsAuthentication;
    private final int defaultPageSize = 5;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final Advisor advisor = new FakeAdvisor(defaultPageSize);
    private CommandLineController target;
    private CommandLineView commandLineView;

    @Parameters(
            name = "User input={0}, Needs authentication={1}"
    )
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[]{UserCommand.AUTH.getCommandText(), false},
                new Object[]{UserCommand.EXIT.getCommandText(), false},
                new Object[]{"not supported", false},
                new Object[]{UserCommand.CATEGORIES.getCommandText(), true},
                new Object[]{UserCommand.NEW_RELEASES.getCommandText(), true},
                new Object[]{UserCommand.PLAYLISTS.getCommandText() + " Mood", true},
                new Object[]{UserCommand.FEATURED_PLAYLISTS.getCommandText(), true},
                new Object[]{UserCommand.PREVIOUS.getCommandText(), true},
                new Object[]{UserCommand.NEXT.getCommandText(), true});
    }

    @Test
    public void whenAuthenticated_thenMessageDoesNotAskForAuthentication() {
        // GIVEN
        InputStream inputStream = new ByteArrayInputStream(userCommandInput.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, advisor, new AlwaysAuthenticatedUserCommandAuthentication(), defaultPageSize);
        
        // WHEN
        target.processInput();

        // THEN
        assertThat(output.toString().toLowerCase()).doesNotContain(PROVIDE_ACCESS_MESSAGE);
        if (!userCommandInput.equals("not supported")) {
            assertThat(output.toString()).doesNotContain("Unsupported operation");
        }
    }

    @Test
    public void whenNotAuthenticated_thenMessageAsksForAuthenticationIfCommandRequiresIt() {
        // GIVEN
        InputStream inputStream = new ByteArrayInputStream(userCommandInput.getBytes());
        commandLineView = new CommandLineView(new Scanner(inputStream), new PrintStream(output), defaultPageSize);
        target = new CommandLineController(commandLineView, advisor, new NeverAuthenticatedUserCommandAuthentication(), defaultPageSize);

        // WHEN
        target.processInput();

        // THEN
        if (needsAuthentication) {
            assertThat(output.toString()).containsIgnoringCase(PROVIDE_ACCESS_MESSAGE);
        } else {
            assertThat(output.toString().toLowerCase()).doesNotContain(PROVIDE_ACCESS_MESSAGE);
        }
        if (!userCommandInput.equals("not supported")) {
            assertThat(output.toString()).doesNotContain("Unsupported operation");
        }
    }
}
