package advisor;

import advisor.runner.CommandLineAdvisorRunner;
import advisor.utils.ChromeDriverSetupRule;
import advisor.utils.SpotifyAppUIAuthenticator;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.assertj.core.api.Condition;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class CommandLineSpotifyAdvisorEndToEndTest {

    private static final int TEST_TIMEOUT_MINUTES = 1;
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

    @ClassRule
    public static final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

    @ClassRule
    public static final ChromeDriverSetupRule chromeDriverSetupRule = new ChromeDriverSetupRule();

    @BeforeClass
    public static void initializeAuthenticator() {
        spotifyAppUIAuthenticator = new SpotifyAppUIAuthenticator(chromeDriverSetupRule.getDriver());
    }

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private CommandLineAdvisorRunner runner;

    @Autowired
    private AdvisorProperties advisorProperties;

    @Test
    public void whenEnteringExitCommand_thenOutputEqualsGoodbyeMessage() throws Exception {
        // GIVEN
        String[] commands = new String[]{"exit"};
        systemInMock.provideLines(commands);

        // WHEN
        enterCommandsAndAuthenticateAppIfRequired(commands);

        // THEN
        assertThat(getOutput()).isEqualTo(GOODBYE_MESSAGE + System.lineSeparator());
    }

    @Test
    public void whenEnteringAuthCommand_thenOutputContainsAuthMessages() throws InterruptedException {
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
    public void whenEnteringUnsupportedCommand_thenOutputContainsUnsupportedOperationMessage() throws Exception {
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
    public void givenAppAuthenticated_whenEnteringFeaturedCommand_thenOutputContainsSpotifyPlaylists() throws Exception {
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
                .hasSize(advisorProperties.getPageSize() * 2)
                .doesNotHaveDuplicates();
        try (AutoCloseableSoftAssertions softAssertions = new AutoCloseableSoftAssertions()) {
            for (int index = 0; index < featuredPlaylistsLines.size(); ) {
                softAssertions.assertThat(featuredPlaylistsLines.get(index++)).isNotEmpty();
                softAssertions.assertThat(featuredPlaylistsLines.get(index++)).is(PLAYLIST_LINK);
            }
        }
    }

    @Test
    public void givenAppAuthenticated_whenEnteringNewCommand_thenOutputContainsSpotifyReleases() throws Exception {
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
                .hasSize(advisorProperties.getPageSize() * 3)
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
    public void givenAppAuthenticated_whenEnteringCategoriesCommand_thenOutputContainsCategories() throws Exception {
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
                .hasSize(advisorProperties.getPageSize())
                .allMatch(line -> !line.isEmpty())
                .doesNotHaveDuplicates();
    }

    @Test
    public void givenAppAuthenticated_whenEnteringCategoryPlaylistsCommand_thenOutputContainsSpotifyPlaylists() throws Exception {
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
                .hasSize(advisorProperties.getPageSize() * 2)
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
        executorService.awaitTermination(TEST_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        if (futures.stream().anyMatch(future -> !future.isDone())) {
            throw new IllegalStateException(
                    String.format("Execution of commands not finished within %d minutes.\n" +
                            "Output: %s", TEST_TIMEOUT_MINUTES, getOutput()));
        }
    }

    private void waitForAuthUrlAndAuthenticate() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String authenticationUrl = Stream.of(getOutput().split("\\r?\\n"))
                .filter(line -> line.startsWith("https://accounts.spotify.com"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Authentication url not found in output"));
        spotifyAppUIAuthenticator.authenticateApp(authenticationUrl);
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
