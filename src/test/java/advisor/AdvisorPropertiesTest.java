package advisor;

import org.junit.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public final class AdvisorPropertiesTest {

    private final AdvisorProperties advisorProperties = new AdvisorProperties();

    @Test
    public void givenNoAccessCommandLineArgument_whenInitializingAdvisorProperties_thenSpotifyHostTakesDefaultValue() throws IOException {
        // GIVEN
        String defaultSpotifyAccessHost = "https://accounts.spotify.com";
        String[] args = {};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getSpotifyAccessHost()).isEqualTo(defaultSpotifyAccessHost);
    }

    @Test
    public void givenAccessCommandLineArgument_whenInitializingAdvisorProperties_thenSpotifyHostTakesCommandLineValue() throws IOException {
        // GIVEN
        String expectedSpotifyAccessHost = "http://example.com";
        String[] args = {"-access", expectedSpotifyAccessHost};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getSpotifyAccessHost()).isEqualTo(expectedSpotifyAccessHost);
    }

    @Test
    public void givenNoResourceCommandLineArgument_whenInitializingAdvisorProperties_thenSpotifyResourceTakesCommandLineValue() throws IOException {
        // GIVEN
        String defaultSpotifyResourceHost = "https://api.spotify.com";
        String[] args = {};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getSpotifyResourceHost()).isEqualTo(defaultSpotifyResourceHost);
    }

    @Test
    public void givenResourceCommandLineArgument_whenInitializingAdvisorProperties_thenSpotifyResourceTakesCommandLineValue() throws IOException {
        // GIVEN
        String expectedSpotifyResourceHost = "http://example.com";
        String[] args = {"-resource", expectedSpotifyResourceHost};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getSpotifyResourceHost()).isEqualTo(expectedSpotifyResourceHost);
    }

    @Test
    public void givenNoPageCommandLineArgument_whenInitializingAdvisorProperties_thenPageSizeTakesCommandLineValue() throws IOException {
        // GIVEN
        int defaultPageSize = 5;
        String[] args = {};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getPageSize()).isEqualTo(defaultPageSize);
    }

    @Test
    public void givenPageCommandLineArgument_whenInitializingAdvisorProperties_thenPageSizeTakesCommandLineValue() throws IOException {
        // GIVEN
        int expectedPageSize = 9;
        String[] args = {"-page", String.valueOf(expectedPageSize)};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getPageSize()).isEqualTo(expectedPageSize);
    }

    @Test
    public void givenAccessAndResourceCommandLineArguments_whenInitializingAdvisorProperties_thenSpotifyAccessAndResourceAndPageTakeCommandLineValues() throws IOException {
        // GIVEN
        String expectedSpotifyAccessHost = "http://exampleHost.com";
        String expectedSpotifyResourceHost = "http://exampleResource.com";
        int expectedPageSize = 15;
        String[] args = {"-resource", expectedSpotifyResourceHost,
                "-access", expectedSpotifyAccessHost,
                "-page", String.valueOf(expectedPageSize) };

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getSpotifyResourceHost()).isEqualTo(expectedSpotifyResourceHost);
        assertThat(advisorProperties.getSpotifyAccessHost()).isEqualTo(expectedSpotifyAccessHost);
        assertThat(advisorProperties.getPageSize()).isEqualTo(expectedPageSize);
    }

    @Test
    public void whenInitializingAdvisorProperties_thenSpotifyClientIdTakesValueFromProperties() throws IOException {
        // GIVEN
        String[] args = {};
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("application.properties")));
        String expectedSpotifyClientId = properties.getProperty("spotify.clientid");

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getSpotifyClientId()).isNotEmpty();
        assertThat(advisorProperties.getSpotifyClientId()).isEqualTo(expectedSpotifyClientId);
    }

    @Test
    public void whenInitializingAdvisorProperties_thenSpotifyClientSecretTakesValueFromProperties() throws IOException {
        // GIVEN
        String[] args = {};
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("application.properties")));
        String expectedSpotifyClientSecret = properties.getProperty("spotify.client_secret");

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getSpotifyClientSecret()).isNotEmpty();
        assertThat(advisorProperties.getSpotifyClientSecret()).isEqualTo(expectedSpotifyClientSecret);
    }

    @Test
    public void whenInitializingAdvisorProperties_thenRedirectUriTakesValueFromProperties() throws IOException {
        // GIVEN
        String[] args = {};
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("application.properties")));
        String expectedRedirectUri = properties.getProperty("redirect_uri");

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getRedirectUri()).isNotEmpty();
        assertThat(advisorProperties.getRedirectUri()).isEqualTo(expectedRedirectUri);
    }

    @Test
    public void whenInitializingAdvisorProperties_thenAccessCodeServerTimeoutTakesValueFromProperties() throws IOException {
        // GIVEN
        String[] args = {};
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("application.properties")));
        Integer serverTimeoutSeconds = Integer.parseInt(properties.getProperty("access_code_server_timeout_seconds"));

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getAccessCodeServerTimeoutSeconds()).isEqualTo(serverTimeoutSeconds);
    }

    @Test
    public void whenInitializingAdvisorProperties_thenLocaleTakesValueFromProperties() throws IOException {
        // GIVEN
        String[] args = {};
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("application.properties")));
        String locale = properties.getProperty("locale");

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        assertThat(advisorProperties.getLocale()).isEqualTo(locale);
    }
}
