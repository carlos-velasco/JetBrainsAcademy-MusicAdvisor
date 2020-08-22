package advisor;

import advisor.runner.AdvisorRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        AdvisorProperties advisorProperties = new AdvisorProperties();
        advisorProperties.initializeProperties(args);

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.registerBean(AppConfig.class, advisorProperties);
        context.refresh();

        AdvisorRunner advisorRunner = context.getBean(AdvisorRunner.class);
        advisorRunner.run();
    }
}
