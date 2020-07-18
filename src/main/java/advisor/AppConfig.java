package advisor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.PrintStream;
import java.util.Scanner;

@Configuration
@PropertySource("application.properties")
@ComponentScan("advisor")
public class AppConfig {

    @Bean
    Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    PrintStream printStream() {
        return new PrintStream(System.out);
    }
}
