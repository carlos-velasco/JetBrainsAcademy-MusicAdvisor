package advisor;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static org.hamcrest.Matchers.*;

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
        Assert.assertThat(advisorProperties.getSpotifyAccessHost(), is(defaultSpotifyAccessHost));
    }

    @Test
    public void givenAccessCommandLineArgument_whenInitializingAdvisorProperties_thenSpotifyHostTakesCommandLineValue() throws IOException {
        // GIVEN
        String expectedSpotifyAccessHost = "http://example.com";
        String[] args = {"-access", expectedSpotifyAccessHost};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        Assert.assertThat(advisorProperties.getSpotifyAccessHost(), is(expectedSpotifyAccessHost));
    }

    @Test
    public void givenNoResourceCommandLineArgument_whenInitializingAdvisorProperties_thenSpotifyResourceTakesCommandLineValue() throws IOException {
        // GIVEN
        String defaultSpotifyResourceHost = "https://api.spotify.com";
        String[] args = {};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        Assert.assertThat(advisorProperties.getSpotifyResourceHost(), is(defaultSpotifyResourceHost));
    }

    @Test
    public void givenResourceCommandLineArgument_whenInitializingAdvisorProperties_thenSpotifyResourceTakesCommandLineValue() throws IOException {
        // GIVEN
        String expectedSpotifyResourceHost = "http://example.com";
        String[] args = {"-resource", expectedSpotifyResourceHost};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        Assert.assertThat(advisorProperties.getSpotifyResourceHost(), is(expectedSpotifyResourceHost));
    }

    @Test
    public void givenNoPageCommandLineArgument_whenInitializingAdvisorProperties_thenPageSizeTakesCommandLineValue() throws IOException {
        // GIVEN
        int defaultPageSize = 5;
        String[] args = {};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        Assert.assertThat(advisorProperties.getPageSize(), is(defaultPageSize));
    }

    @Test
    public void givenPageCommandLineArgument_whenInitializingAdvisorProperties_thenPageSizeTakesCommandLineValue() throws IOException {
        // GIVEN
        int expectedPageSize = 9;
        String[] args = {"-page", String.valueOf(expectedPageSize)};

        // WHEN
        advisorProperties.initializeProperties(args);

        // THEN
        Assert.assertThat(advisorProperties.getPageSize(), is(expectedPageSize));
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
        Assert.assertThat(advisorProperties.getSpotifyResourceHost(), is(expectedSpotifyResourceHost));
        Assert.assertThat(advisorProperties.getSpotifyAccessHost(), is(expectedSpotifyAccessHost));
        Assert.assertThat(advisorProperties.getPageSize(), is(expectedPageSize));
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
        Assert.assertThat(advisorProperties.getSpotifyClientId(), not(emptyOrNullString()));
        Assert.assertThat(advisorProperties.getSpotifyClientId(), is(expectedSpotifyClientId));
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
        Assert.assertThat(advisorProperties.getSpotifyClientSecret(), not(emptyOrNullString()));
        Assert.assertThat(advisorProperties.getSpotifyClientSecret(), is(expectedSpotifyClientSecret));
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
        Assert.assertThat(advisorProperties.getRedirectUri(), not(emptyOrNullString()));
        Assert.assertThat(advisorProperties.getRedirectUri(), is(expectedRedirectUri));
    }
}
