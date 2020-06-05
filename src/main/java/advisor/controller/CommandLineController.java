package advisor.controller;

import advisor.authentication.UserCommandAuthentication;
import advisor.model.*;
import advisor.model.service.Advisor;
import advisor.model.AdvisorException;
import advisor.view.CommandLineView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class CommandLineController {

    private static final String COMMA_SEPARATED_CONTENT_COMMAND_NAMES = String.join(", ", List.of(
            UserCommand.NEW_RELEASES.getCommandText(),
            UserCommand.CATEGORIES.getCommandText(),
            UserCommand.FEATURED_PLAYLISTS.getCommandText(),
            UserCommand.PLAYLISTS.getCommandText()));

    private final CommandLineView view;
    private final UserCommandAuthentication userCommandAuthentication;
    private PageableSpotifyModel pageableSpotifyModel;

    private final Categories categories;
    private final FeaturedPlaylists featuredPlaylists;
    private final NewReleases newReleases;
    private final PlaylistsByCategory playlistsByCategory;

    @Autowired
    public CommandLineController(
            CommandLineView view,
            Advisor advisor,
            UserCommandAuthentication userCommandAuthentication,
            int pageSize) {
        this.view = view;
        this.userCommandAuthentication = userCommandAuthentication;
        this.categories = new Categories(advisor, pageSize);
        this.featuredPlaylists = new FeaturedPlaylists(advisor, pageSize);
        this.newReleases = new NewReleases(advisor, pageSize);
        this.playlistsByCategory = new PlaylistsByCategory(advisor, pageSize);
    }

    public UserCommand processInput() {
        UserCommand command = view.getUserInputCommand();

        if (isAuthenticationNeededForUserCommand(command)) {
            view.printMessage("Please, provide access for application.");
            return command;
        }

        try {
            switch (command) {
                case NEW_RELEASES:
                    view.printPage(newReleases.firstPage());
                    pageableSpotifyModel = newReleases;
                    break;
                case FEATURED_PLAYLISTS:
                    view.printPage(featuredPlaylists.firstPage());
                    pageableSpotifyModel = featuredPlaylists;
                    break;
                case CATEGORIES:
                    view.printPage(categories.firstPage());
                    pageableSpotifyModel = categories;
                    break;
                case PLAYLISTS:
                    String categoryName = view.getInputCommandOptions();
                    view.printPage(playlistsByCategory.firstPage(categoryName));
                    pageableSpotifyModel = playlistsByCategory;
                    break;
                case PREVIOUS:
                    if (pageableSpotifyModel == null) {
                        view.printMessage(String.format("Execute first one of the following commands: %s",
                                COMMA_SEPARATED_CONTENT_COMMAND_NAMES));
                        break;
                    }
                    view.printPage(pageableSpotifyModel.previousPage());
                    break;
                case NEXT:
                    if (pageableSpotifyModel == null) {
                        view.printMessage(String.format("Execute first one of the following commands: %s",
                                COMMA_SEPARATED_CONTENT_COMMAND_NAMES));
                        break;
                    }
                    view.printPage(pageableSpotifyModel.nextPage());
                    break;
                case EXIT:
                    view.printMessage("---GOODBYE!---");
                    break;
                case AUTH:
                    if (userCommandAuthentication.authenticate()) {
                        view.printMessage("Success!");
                    }
                    break;
                case NOT_SUPPORTED:
                default:
                    view.printMessage("Unsupported operation");
                    break;
            }
        } catch (AdvisorException e) {
            view.printMessage(e.getMessage());
        }
        return command;
    }

    private boolean isAuthenticationNeededForUserCommand(UserCommand userCommand) {
        return !userCommandAuthentication.isAuthenticated() && userCommand.needsAuth();
    }
}
