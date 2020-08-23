package advisor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    @Bean
    public AdvisorProperties advisorProperties() throws IOException {
        final AdvisorProperties advisorProperties = new AdvisorProperties();
        advisorProperties.initializeProperties(new String[] {});
        return advisorProperties;
    }
}
