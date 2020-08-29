package advisor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@Configuration
public class TestConfig {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Bean
    ByteArrayOutputStream outputStream() {
        return output;
    }

    @Bean
    PrintStream printStream() {
        return new PrintStream(outputStream());
    }
}
