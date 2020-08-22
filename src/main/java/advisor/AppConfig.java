package advisor;

import advisor.authentication.*;
import advisor.controller.CommandLineController;
import advisor.model.service.Advisor;
import advisor.model.service.SpotifyAdvisor;
import advisor.runner.AdvisorRunner;
import advisor.runner.CommandLineAdvisorRunner;
import advisor.view.CommandLineView;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.PrintStream;
import java.util.Scanner;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final AdvisorProperties advisorProperties;

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
        return new CommandLineView(scanner(), printStream(), advisorProperties.getPageSize());
    }

    @Bean
    SpotifyAccessCodeFetcher spotifyAccessCodeFetcher() {
        return new SpotifyAccessCodeFetcher(
                advisorProperties.getSpotifyAccessHost(),
                advisorProperties.getSpotifyClientId(),
                advisorProperties.getRedirectUri(),
                commandLineView(),
                advisorProperties.getAccessCodeServerTimeoutSeconds());
    }

    @Bean
    SpotifyAccessTokenFetcher spotifyAccessTokenFetcher() {
        return new SpotifyAccessTokenFetcher(
                advisorProperties.getSpotifyAccessHost(),
                advisorProperties.getSpotifyClientId(),
                advisorProperties.getSpotifyClientSecret(),
                advisorProperties.getRedirectUri(),
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
                advisorProperties.getPageSize());
    }

    @Bean
    Advisor advisor() {
        return new SpotifyAdvisor(
                advisorProperties.getSpotifyResourceHost(),
                new UserCommandAuthenticationFacade(userCommandAuthentication()),
                advisorProperties.getPageSize());
    }

    @Bean
    AdvisorRunner advisorRunner() {
        return new CommandLineAdvisorRunner(commandLineController());
    }
}
