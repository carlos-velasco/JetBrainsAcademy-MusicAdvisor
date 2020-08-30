package advisor.authentication;

import advisor.view.CommandLineView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class SpotifyOAuthUserCommandAuthenticationTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Spy
    private final CommandLineView commandLineView = new CommandLineView(new Scanner(System.in), new PrintStream(output), 5);

    @Mock
    private SpotifyAccessCodeFetcher spotifyAccessCodeFetcher;

    @Mock
    private SpotifyAccessTokenFetcher spotifyAccessTokenFetcher;

    @InjectMocks
    private SpotifyOAuthUserCommandAuthentication target;

    @Test
    void givenAccessTokenAndAuthTokenAreObtained_whenAuthenticating_thenAccessTokenIsPopulated() throws IOException, InterruptedException {
        // GIVEN
        String accessCode = "myAccessCode";
        String accessToken = "myAccessToken";
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.of(accessCode));
        when(spotifyAccessTokenFetcher.fetchAccessToken(accessCode)).thenReturn(Optional.of(accessToken));

        // WHEN
        target.authenticate();

        // THEN
        assertThat(target.getAccessToken()).isEqualTo(accessToken);
        assertThat(target.isAuthenticated()).isTrue();
    }

    @Test
    void givenAccessTokenAndAuthTokenAreObtained_whenAuthenticating_thenSuccessMessagesAreWritten() throws IOException, InterruptedException {
        // GIVEN
        String accessCode = "myAccessCode";
        String accessToken = "myAccessToken";
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.of(accessCode));
        when(spotifyAccessTokenFetcher.fetchAccessToken(accessCode)).thenReturn(Optional.of(accessToken));

        // WHEN
        target.authenticate();

        // THEN
        String expectedMessages = "code received" + System.lineSeparator();
        assertThat(output).hasToString(expectedMessages);
    }

    @Test
    void givenAccessCodeObtainedAndAccessTokenNotObtained_whenAuthenticating_thenAccessTokenIsPopulated() throws IOException, InterruptedException {
        // GIVEN
        String accessCode = "myAccessCode";
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.of(accessCode));
        when(spotifyAccessTokenFetcher.fetchAccessToken(accessCode)).thenReturn(Optional.empty());

        // WHEN
        target.authenticate();

        // THEN
        assertThat(target.getAccessToken()).isNull();
        assertThat(target.isAuthenticated()).isFalse();
    }

    @Test
    void givenAccessCodeObtainedAndAccessTokenNotObtained_whenAuthenticating_thenMessagesAreWritten() throws IOException, InterruptedException {
        // GIVEN
        String accessCode = "myAccessCode";
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.of(accessCode));
        when(spotifyAccessTokenFetcher.fetchAccessToken(accessCode)).thenReturn(Optional.empty());

        // WHEN
        target.authenticate();

        // THEN
        String expectedMessages = "code received" + System.lineSeparator()
                + "token not received" + System.lineSeparator();
        assertThat(output).hasToString(expectedMessages);
    }

    @Test
    void givenAccessCodeAndAccessTokenNotObtained_whenAuthenticating_thenAccessTokenIsPopulated() throws IOException, InterruptedException {
        // GIVEN
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.empty());

        // WHEN
        target.authenticate();

        // THEN
        assertThat(target.getAccessToken()).isNull();
        assertThat(target.isAuthenticated()).isFalse();
    }

    @Test
    void givenAccessCodeAndAccessTokenNotObtained_whenAuthenticating_thenErrorMessagesAreWritten() throws IOException, InterruptedException {
        // GIVEN
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.empty());

        // WHEN
        target.authenticate();

        // THEN
        String expectedMessages = "code not received" + System.lineSeparator();
        assertThat(output).hasToString(expectedMessages);
    }
}
