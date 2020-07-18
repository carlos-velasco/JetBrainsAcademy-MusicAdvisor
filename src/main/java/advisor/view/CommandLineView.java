package advisor.view;

import advisor.controller.UserCommand;
import advisor.model.dto.CommandLinePrintable;
import advisor.model.dto.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class CommandLineView {

    private final Scanner scanner;
    private final PrintStream out;
    private final int pageSize;
    private String userCommandOptions;

    public UserCommand getUserInputCommand() {
        String textInput = scanner.nextLine();
        String[] commandAndOptions = textInput.split(" ");
        List<String> commandOptions = new ArrayList<>(Arrays.asList(commandAndOptions).subList(1, commandAndOptions.length));
        userCommandOptions = String.join(" ", commandOptions);
        return UserCommand.parse(commandAndOptions[0]);
    }

    public String getInputCommandOptions() {
        String temp = userCommandOptions;
        userCommandOptions = null;
        return temp;
    }

    public <T extends CommandLinePrintable> void printPage(Page<T> page) {
        page.getElements().forEach(element -> out.println(element.commandLineStringRepresentation()));
        out.println(String.format("---PAGE %d OF %d---",
                page.getPageNumber(),
                (int) Math.ceil((double) page.getTotal() / pageSize)));
    }

    public void printMessage(String message) {
        out.println(message);
    }
}
