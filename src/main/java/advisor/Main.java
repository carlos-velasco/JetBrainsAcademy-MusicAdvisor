package advisor;

import advisor.controller.CommandLineController;
import advisor.controller.UserCommand;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        AdvisorProperties advisorProperties = new AdvisorProperties();
        advisorProperties.initializeProperties(args);

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(AppConfig.class, advisorProperties);
        context.refresh();
        CommandLineController commandLineController = context.getBean(CommandLineController.class);

        UserCommand userCommand;
        do {
            userCommand = commandLineController.processInput();
        } while (!userCommand.equals(UserCommand.EXIT));
    }
}
