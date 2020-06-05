package advisor.controller;

import java.util.stream.Stream;

public enum UserCommand {

    NEW_RELEASES("new", true),
    FEATURED_PLAYLISTS("featured", true),
    CATEGORIES("categories", true),
    PLAYLISTS("playlists", true),
    PREVIOUS("prev", true),
    NEXT("next", true),
    EXIT("exit", false),
    AUTH("auth", false),
    NOT_SUPPORTED("", false);

    private final String commandText;
    private final boolean needsAuth;

    UserCommand(String commandText, boolean needsAuth) {
        this.commandText = commandText;
        this.needsAuth = needsAuth;
    }

    public boolean needsAuth() {
        return needsAuth;
    }

    public String getCommandText() {
        return commandText;
    }

    public static UserCommand parse(String name) {
        return Stream.of(UserCommand.values())
                .filter(contactType -> contactType.commandText.equals(name))
                .findFirst()
                .orElse(NOT_SUPPORTED);
    }
}
