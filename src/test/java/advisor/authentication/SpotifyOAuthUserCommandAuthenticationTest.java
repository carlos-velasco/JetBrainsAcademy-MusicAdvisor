package advisor.authentication;

import advisor.view.CommandLineView;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Scanner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class SpotifyOAuthUserCommandAuthenticationTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final CommandLineView commandLineView = new CommandLineView(new Scanner(System.in), new PrintStream(output), 5);

    @Test
    public void givenAccessTokenAndAuthTokenAreObtained_whenAuthenticating_thenAccessTokenIsPopulated() throws IOException, InterruptedException, URISyntaxException {
        // GIVEN
        String accessCode = "myAccessCode";
        String accessToken = "myAccessToken";

        SpotifyAccessCodeFetcher spotifyAccessCodeFetcher = mock(SpotifyAccessCodeFetcher.class);
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.of(accessCode));

        SpotifyAccessTokenFetcher spotifyAccessTokenFetcher = mock(SpotifyAccessTokenFetcher.class);
        when(spotifyAccessTokenFetcher.fetchAccessToken(accessCode)).thenReturn(Optional.of(accessToken));

        SpotifyOAuthUserCommandAuthentication target =
                new SpotifyOAuthUserCommandAuthentication(spotifyAccessCodeFetcher, spotifyAccessTokenFetcher, commandLineView);

        // WHEN
        boolean result = target.authenticate();

        // THEN
        Assert.assertThat(result, is(true));
        Assert.assertThat(target.getAccessToken(), is(accessToken));
        Assert.assertThat(target.isAuthenticated(), is(true));
    }

    @Test
    public void givenAccessTokenAndAuthTokenAreObtained_whenAuthenticating_thenSuccessMessagesAreWritten() throws IOException, InterruptedException, URISyntaxException {
        // GIVEN
        String accessCode = "myAccessCode";
        String accessToken = "myAccessToken";

        SpotifyAccessCodeFetcher spotifyAccessCodeFetcher = mock(SpotifyAccessCodeFetcher.class);
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.of(accessCode));

        SpotifyAccessTokenFetcher spotifyAccessTokenFetcher = mock(SpotifyAccessTokenFetcher.class);
        when(spotifyAccessTokenFetcher.fetchAccessToken(accessCode)).thenReturn(Optional.of(accessToken));

        SpotifyOAuthUserCommandAuthentication target =
                new SpotifyOAuthUserCommandAuthentication(spotifyAccessCodeFetcher, spotifyAccessTokenFetcher, commandLineView);

        // WHEN
        target.authenticate();

        // THEN
        String expectedMessages = "code received" + System.lineSeparator();
        Assert.assertThat(output.toString(), is(expectedMessages));
    }

    @Test
    public void givenAccessCodeObtainedAndAccessTokenNotObtained_whenAuthenticating_thenAccessTokenIsPopulated() throws IOException, InterruptedException, URISyntaxException {
        // GIVEN
        String accessCode = "myAccessCode";
        SpotifyAccessCodeFetcher spotifyAccessCodeFetcher = mock(SpotifyAccessCodeFetcher.class);
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.of(accessCode));

        SpotifyAccessTokenFetcher spotifyAccessTokenFetcher = mock(SpotifyAccessTokenFetcher.class);
        when(spotifyAccessTokenFetcher.fetchAccessToken(accessCode)).thenReturn(Optional.empty());

        SpotifyOAuthUserCommandAuthentication target =
                new SpotifyOAuthUserCommandAuthentication(spotifyAccessCodeFetcher, spotifyAccessTokenFetcher, commandLineView);

        // WHEN
        boolean result = target.authenticate();

        // THEN
        Assert.assertThat(result, is(false));
        Assert.assertThat(target.getAccessToken(), nullValue());
        Assert.assertThat(target.isAuthenticated(), is(false));
    }

    @Test
    public void givenAccessCodeObtainedAndAccessTokenNotObtained_whenAuthenticating_thenMessagesAreWritten() throws IOException, InterruptedException, URISyntaxException {
        // GIVEN
        String accessCode = "myAccessCode";

        SpotifyAccessCodeFetcher spotifyAccessCodeFetcher = mock(SpotifyAccessCodeFetcher.class);
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.of(accessCode));

        SpotifyAccessTokenFetcher spotifyAccessTokenFetcher = mock(SpotifyAccessTokenFetcher.class);
        when(spotifyAccessTokenFetcher.fetchAccessToken(accessCode)).thenReturn(Optional.empty());

        SpotifyOAuthUserCommandAuthentication target = new SpotifyOAuthUserCommandAuthentication(spotifyAccessCodeFetcher, spotifyAccessTokenFetcher, commandLineView);

        // WHEN
        target.authenticate();

        // THEN
        String expectedMessages = "code received" + System.lineSeparator()
                + "token not received" + System.lineSeparator();
        Assert.assertThat(output.toString(), is(expectedMessages));
    }

    @Test
    public void givenAccessCodeAndAccessTokenNotObtained_whenAuthenticating_thenAccessTokenIsPopulated() throws IOException, InterruptedException {
        // GIVEN
        SpotifyAccessCodeFetcher spotifyAccessCodeFetcher = mock(SpotifyAccessCodeFetcher.class);
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.empty());

        SpotifyAccessTokenFetcher spotifyAccessTokenFetcher = mock(SpotifyAccessTokenFetcher.class);

        SpotifyOAuthUserCommandAuthentication target = new SpotifyOAuthUserCommandAuthentication(spotifyAccessCodeFetcher, spotifyAccessTokenFetcher, commandLineView);

        // WHEN
        boolean result = target.authenticate();

        // THEN
        Assert.assertThat(result, is(false));
        Assert.assertThat(target.getAccessToken(), nullValue());
        Assert.assertThat(target.isAuthenticated(), is(false));
    }

    @Test
    public void givenAccessCodeAndAccessTokenNotObtained_whenAuthenticating_thenErrorMessagesAreWritten() throws IOException, InterruptedException {
        // GIVEN
        SpotifyAccessCodeFetcher spotifyAccessCodeFetcher = mock(SpotifyAccessCodeFetcher.class);
        when(spotifyAccessCodeFetcher.fetchAccessCode()).thenReturn(Optional.empty());

        SpotifyAccessTokenFetcher spotifyAccessTokenFetcher = mock(SpotifyAccessTokenFetcher.class);

        SpotifyOAuthUserCommandAuthentication target = new SpotifyOAuthUserCommandAuthentication(spotifyAccessCodeFetcher, spotifyAccessTokenFetcher, commandLineView);

        // WHEN
        target.authenticate();

        // THEN
        String expectedMessages = "code not received" + System.lineSeparator();
        Assert.assertThat(output.toString(), is(expectedMessages));
    }
}
