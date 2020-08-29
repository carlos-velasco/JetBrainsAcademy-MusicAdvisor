package advisor;

import advisor.runner.AdvisorRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args)  {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        AdvisorRunner advisorRunner = context.getBean(AdvisorRunner.class);
        advisorRunner.run();
    }
}
