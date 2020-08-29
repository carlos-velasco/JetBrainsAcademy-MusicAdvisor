package advisor;

import advisor.authentication.*;
import advisor.controller.CommandLineController;
import advisor.model.service.Advisor;
import advisor.model.service.SpotifyAdvisor;
import advisor.runner.AdvisorRunner;
import advisor.runner.CommandLineAdvisorRunner;
import advisor.view.CommandLineView;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.PrintStream;
import java.util.Scanner;

@Configuration
@PropertySource("application.properties")
@RequiredArgsConstructor
public class AppConfig {

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    @Value("${redirect-uri}")
    private String redirectUri;

    @Value("${spotify.host.access}")
    private String spotifyAccessHost;

    @Value("${spotify.host.resource}")
    private String spotifyResourceHost;

    @Value("${access-code-server.timeout-seconds}")
    private Integer accessCodeServerTimeoutSeconds;

    @Value("${locale}")
    private String locale;

    @Value("${page-size}")
    private Integer pageSize;

    @Bean
    Scanner scanner() {
        return new Scanner(System.in);
    }

    @Bean
    PrintStream printStream() {
        return new PrintStream(System.out);
    }

    @Bean
    CommandLineView commandLineView() {
        return new CommandLineView(scanner(), printStream(), pageSize);
    }

    @Bean
    SpotifyAccessCodeFetcher spotifyAccessCodeFetcher() {
        return new SpotifyAccessCodeFetcher(
                spotifyAccessHost,
                spotifyClientId,
                redirectUri,
                commandLineView(),
                accessCodeServerTimeoutSeconds);
    }

    @Bean
    SpotifyAccessTokenFetcher spotifyAccessTokenFetcher() {
        return new SpotifyAccessTokenFetcher(
                spotifyAccessHost,
                spotifyClientId,
                spotifyClientSecret,
                redirectUri,
                commandLineView());
    }

    @Bean
    UserCommandAuthentication userCommandAuthentication() {
        return new SpotifyOAuthUserCommandAuthentication(
                spotifyAccessCodeFetcher(),
                spotifyAccessTokenFetcher(),
                commandLineView());
    }

    @Bean
    CommandLineController commandLineController() {
        return new CommandLineController(
                commandLineView(),
                advisor(),
                userCommandAuthentication(),
                pageSize);
    }

    @Bean
    Advisor advisor() {
        return new SpotifyAdvisor(
                spotifyResourceHost,
                new UserCommandAuthenticationFacade(userCommandAuthentication()),
                pageSize,
                locale);
    }

    @Bean
    AdvisorRunner advisorRunner() {
        return new CommandLineAdvisorRunner(commandLineController());
    }
}
