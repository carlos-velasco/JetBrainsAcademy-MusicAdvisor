package advisor;

import advisor.authentication.*;
import advisor.controller.CommandLineController;
import advisor.controller.UserCommand;
import advisor.model.service.SpotifyAdvisor;
import advisor.view.CommandLineView;

import java.io.IOException;
import java.util.Scanner;

import static advisor.authentication.SpotifyAccessCodeFetcher.ACCESS_CODE_SERVER_TIMEOUT_SECONDS;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // Initialization using Spring (does not work with tests, since they do not initialize the bean)
//        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
//        CommandLineController commandLineController = context.getBean(CommandLineController.class);
//        OAuthUserCommandAuthentication oAuthUserCommandAuthentication = context.getBean(OAuthUserCommandAuthentication.class);
//        // TODO: the approach above does not take into account the commmand line arguments

        AdvisorProperties advisorProperties = new AdvisorProperties(args);
        CommandLineView commandLineView =
                new CommandLineView(new Scanner(System.in), System.out, advisorProperties.getPageSize());

        SpotifyAccessCodeFetcher accessCodeFetcher = new SpotifyAccessCodeFetcher(
                advisorProperties.getSpotifyAccessHost(),
                advisorProperties.getSpotifyClientId(),
                advisorProperties.getRedirectUri(),
                commandLineView,
                ACCESS_CODE_SERVER_TIMEOUT_SECONDS);

        SpotifyAccessTokenFetcher accessTokenFetcher = new SpotifyAccessTokenFetcher(
                advisorProperties.getSpotifyAccessHost(),
                advisorProperties.getSpotifyClientId(),
                advisorProperties.getSpotifyClientSecret(),
                advisorProperties.getRedirectUri(),
                commandLineView);

        UserCommandAuthentication userCommandAuthentication =
                new SpotifyOAuthUserCommandAuthentication(accessCodeFetcher, accessTokenFetcher, commandLineView);

        CommandLineController commandLineController = new CommandLineController(
                commandLineView,
                new SpotifyAdvisor(advisorProperties.getSpotifyResourceHost(),
                        new UserCommandAuthenticationFacade(userCommandAuthentication), advisorProperties.getPageSize()),
                userCommandAuthentication,
                advisorProperties.getPageSize());

        UserCommand userCommand;
        do {
            userCommand = commandLineController.processInput();
        } while (!userCommand.equals(UserCommand.EXIT));
    }

}
