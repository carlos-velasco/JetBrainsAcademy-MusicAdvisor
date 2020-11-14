package advisor;

import advisor.runner.CommandLineAdvisorRunner;
import advisor.utils.ChromeDriverClassExtension;
import advisor.utils.SpotifyAppUIAuthenticator;
import advisor.utils.SystemInMockClassExtension;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringJUnitConfig(classes = {AppConfig.class, TestConfig.class})
@PropertySource("application.properties")
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
final class CommandLineSpotifyAdvisorEndToEndTest {

    private static final int COMMAND_PROCESSING_TIMEOUT_SECONDS = 20;
    private static final String REDIRECT_URI = "http://localhost:8080";
    private static final String GOODBYE_MESSAGE = "---GOODBYE!---";
    private static final String AUTH_SUCCESS_MESSAGE = "Success!";
    private static final String FIRST_PAGE_MESSAGE = "---PAGE 1 OF";
    private static final Condition<String> PLAYLIST_LINK =
            new Condition<>(string -> string.startsWith("https://open.spotify.com/playlist/"), "Playlist");
    private static final Condition<String> ALBUM_LINK =
            new Condition<>(string -> string.startsWith("https://open.spotify.com/album/"), "Album");
    private static final Condition<String> ARTIST =
            new Condition<>(string -> string.matches("\\[.+]"), "Artist"); // [ArtistName]

    private static SpotifyAppUIAuthenticator spotifyAppUIAuthenticator;

    @Value("${spotify.host.access}")
    private String spotifyAccessHost;

    @Value("${page-size}")
    private Integer pageSize;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CommandLineAdvisorRunner runner;

    @RegisterExtension
    static final SystemInMockClassExtension systemInMock = new SystemInMockClassExtension();

    @RegisterExtension
    static final ChromeDriver chromeDriver = new ChromeDriverClassExtension();

    @BeforeAll
    static void initializeAuthenticator() {
        spotifyAppUIAuthenticator = new SpotifyAppUIAuthenticator(chromeDriver, REDIRECT_URI);
    }

    @Test
    void whenEnteringExitCommand_thenOutputEqualsGoodbyeMessage() throws InterruptedException {
        // GIVEN
        String[] commands = new String[]{"exit"};
        systemInMock.provideLines(commands);

        // WHEN
        enterCommandsAndAuthenticateAppIfRequired(commands);

        // THEN
        assertThat(getOutput()).isEqualTo(GOODBYE_MESSAGE + System.lineSeparator());
    }

    @Test
    void whenEnteringAuthCommand_thenOutputContainsAuthMessages() throws InterruptedException {
        // GIVEN
        String[] commands = new String[]{"auth", "exit"};
        systemInMock.provideLines(commands);

        // WHEN
        enterCommandsAndAuthenticateAppIfRequired(commands);

        // THEN
        assertThat(getOutput())
                .contains("use this link to request the access code:")
                .contains("waiting for code...")
                .contains("code received")
                .contains("making http request for access_token...")
                .contains(AUTH_SUCCESS_MESSAGE)
                .contains(GOODBYE_MESSAGE);
    }

    @Test
    void whenEnteringUnsupportedCommand_thenOutputContainsUnsupportedOperationMessage() throws InterruptedException {
        // GIVEN
        String[] commands = new String[]{"random", "exit"};
        systemInMock.provideLines(commands);

        // WHEN
        enterCommandsAndAuthenticateAppIfRequired(commands);

        // THEN
        assertThat(getOutput()).isEqualTo(
                "Unsupported operation" + System.lineSeparator() +
                        GOODBYE_MESSAGE + System.lineSeparator());
    }

    @Test
    void givenAppAuthenticated_whenEnteringFeaturedCommand_thenOutputContainsSpotifyPlaylists() throws InterruptedException {
        // GIVEN
        String[] commands = new String[]{"auth", "featured", "exit"};
        systemInMock.provideLines(commands);

        // WHEN
        enterCommandsAndAuthenticateAppIfRequired(commands);

        // THEN
        String output = getOutput();
        assertOutputContainsAuthFirstPageAndGoodbye(output);
        
        List<String> featuredPlaylistsLines = getResourceCommandFirstPageOutputLines(output);
        assertThat(featuredPlaylistsLines)
                .hasSize(pageSize * 2)
                .doesNotHaveDuplicates();
        try (AutoCloseableSoftAssertions softAssertions = new AutoCloseableSoftAssertions()) {
            for (int index = 0; index < featuredPlaylistsLines.size(); ) {
                softAssertions.assertThat(featuredPlaylistsLines.get(index++)).isNotEmpty();
                softAssertions.assertThat(featuredPlaylistsLines.get(index++)).is(PLAYLIST_LINK);
            }
        }
    }

    @Test
    void givenAppAuthenticated_whenEnteringNewCommand_thenOutputContainsSpotifyReleases() throws InterruptedException {
        // GIVEN
        String[] commands = new String[]{"auth", "new", "exit"};
        systemInMock.provideLines(commands);

        // WHEN
        enterCommandsAndAuthenticateAppIfRequired(commands);

        // THEN
        String output = getOutput();
        assertOutputContainsAuthFirstPageAndGoodbye(output);

        List<String> newReleasesLines = getResourceCommandFirstPageOutputLines(output);
        assertThat(newReleasesLines)
                .hasSize(pageSize * 3)
                .doesNotHaveDuplicates();
        try (AutoCloseableSoftAssertions softAssertions = new AutoCloseableSoftAssertions()) {
            for (int index = 0; index < newReleasesLines.size(); ) {
                softAssertions.assertThat(newReleasesLines.get(index++)).isNotEmpty();
                softAssertions.assertThat(newReleasesLines.get(index++)).is(ARTIST);
                softAssertions.assertThat(newReleasesLines.get(index++)).is(ALBUM_LINK);
            }
        }
    }

    @Test
    void givenAppAuthenticated_whenEnteringCategoriesCommand_thenOutputContainsCategories() throws InterruptedException {
        // GIVEN
        String[] commands = new String[]{"auth", "categories", "exit"};
        systemInMock.provideLines(commands);

        // WHEN
        enterCommandsAndAuthenticateAppIfRequired(commands);

        // THEN
        String output = getOutput();
        assertOutputContainsAuthFirstPageAndGoodbye(output);

        List<String> categoryLines = getResourceCommandFirstPageOutputLines(output);
        assertThat(categoryLines)
                .hasSize(pageSize)
                .allMatch(not(String::isEmpty))
                .doesNotHaveDuplicates();
    }

    @Test
    void givenAppAuthenticated_whenEnteringCategoryPlaylistsCommand_thenOutputContainsSpotifyPlaylists() throws InterruptedException {
        // GIVEN
        String[] commands = new String[]{"auth", "playlists Pop", "exit"};
        systemInMock.provideLines(commands);

        // WHEN
        enterCommandsAndAuthenticateAppIfRequired(commands);

        // THEN
        String output = getOutput();
        assertOutputContainsAuthFirstPageAndGoodbye(output);

        List<String> categoryPlaylistsLines = getResourceCommandFirstPageOutputLines(output);
        assertThat(categoryPlaylistsLines)
                .hasSize(pageSize * 2)
                .doesNotHaveDuplicates();
        try (AutoCloseableSoftAssertions softAssertions = new AutoCloseableSoftAssertions()) {
            for (int index = 0; index < categoryPlaylistsLines.size(); ) {
                softAssertions.assertThat(categoryPlaylistsLines.get(index++)).isNotEmpty();
                softAssertions.assertThat(categoryPlaylistsLines.get(index++)).is(PLAYLIST_LINK);
            }
        }
    }

    private void enterCommandsAndAuthenticateAppIfRequired(String... commands) throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future<?>> futures = new ArrayList<>();
        futures.add(executorService.submit(runner::run));
        if (Set.of(commands).contains("auth")) {
            futures.add(executorService.submit(this::waitForAuthUrlAndAuthenticate));
        }
        executorService.shutdown();
        executorService.awaitTermination(COMMAND_PROCESSING_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (futures.stream().anyMatch(future -> !future.isDone())) {
            throw new IllegalStateException(
                    String.format("Execution of commands not finished within %d seconds.\n" +
                            "Output: %s", COMMAND_PROCESSING_TIMEOUT_SECONDS, getOutput()));
        }
    }

    private void waitForAuthUrlAndAuthenticate() {
        String authenticationUrl = await().atMost(Duration.ofSeconds(10))
                .until(this::getAuthenticationUrlFromOutput, not(String::isEmpty));
        spotifyAppUIAuthenticator.authenticateApp(authenticationUrl);
    }

    private String getAuthenticationUrlFromOutput() {
        return Stream.of(getOutput().split("\\r?\\n"))
                .filter(line -> line.startsWith(spotifyAccessHost))
                .findFirst()
                .orElse("");
    }

    private String getOutput() {
        final ByteArrayOutputStream outputStream = applicationContext.getBean(ByteArrayOutputStream.class);
        return outputStream.toString();
    }

    private void assertOutputContainsAuthFirstPageAndGoodbye(String output) {
        assertThat(output)
                .contains(AUTH_SUCCESS_MESSAGE)
                .contains(FIRST_PAGE_MESSAGE)
                .contains(GOODBYE_MESSAGE);
    }

    private List<String> getResourceCommandFirstPageOutputLines(String output) {
        List<String> outputLines = Arrays.asList(output.split("\\r?\\n"));
        List<String> resourceLines = outputLines.stream()
                .dropWhile(line -> !line.contains(AUTH_SUCCESS_MESSAGE))
                .takeWhile(line -> !line.contains(FIRST_PAGE_MESSAGE))
                .collect(Collectors.toList());
        resourceLines.remove(0);
        return resourceLines;
    }
}
