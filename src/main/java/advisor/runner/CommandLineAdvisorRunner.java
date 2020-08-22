package advisor.runner;

import advisor.controller.CommandLineController;
import advisor.controller.UserCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandLineAdvisorRunner implements AdvisorRunner {

    private final CommandLineController commandLineController;

    public void run() {
        UserCommand userCommand;
        do {
            userCommand = commandLineController.processInput();
        } while (!userCommand.equals(UserCommand.EXIT));
    }
}
