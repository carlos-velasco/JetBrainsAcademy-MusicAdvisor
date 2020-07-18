package advisor;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class AdvisorProperties {

    private final String spotifyClientId;
    private final String spotifyClientSecret;
    private final String redirectUri;
    private String spotifyAccessHost;
    private String spotifyResourceHost;
    private Integer pageSize;

    public AdvisorProperties(String[] args) throws IOException {
        final String accessCommandLineArgument = "-access";
        final String resourceCommandLineArgument = "-resource";
        final String pageCommandLineArgument = "-page";
        for (int index = 0; (index < args.length - 1)
                && !(spotifyAccessHost != null && spotifyResourceHost != null && pageSize != null);
             index++) {
            if (args[index].equals(accessCommandLineArgument) && ((args.length - 1) >= index)) {
                spotifyAccessHost = args[index + 1];
            }

            if (args[index].equals(resourceCommandLineArgument) && ((args.length - 1) >= index)) {
                spotifyResourceHost = args[index + 1];
            }

            if (args[index].equals(pageCommandLineArgument) && ((args.length - 1) >= index)) {
                pageSize = Integer.parseInt(args[index + 1]);
            }
        }

        // Initialize authentication with defaults
        final String propertiesFileName = "application.properties";
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(
                this.getClass().getClassLoader().getResourceAsStream(propertiesFileName)));
        spotifyClientId = properties.getProperty("spotify.clientid");
        spotifyClientSecret = properties.getProperty("spotify.client_secret");
        redirectUri = properties.getProperty("redirect_uri");
        spotifyAccessHost = Optional.ofNullable(spotifyAccessHost)
                .orElse(properties.getProperty("spotify.default_access_host"));
        spotifyResourceHost = Optional.ofNullable(spotifyResourceHost)
                .orElse(properties.getProperty("spotify.default_resource_host"));
        pageSize = Optional.ofNullable(pageSize)
                .orElse(Integer.parseInt(properties.getProperty("page_size")));
    }

    String getSpotifyClientId() {
        return spotifyClientId;
    }

    String getSpotifyClientSecret() {
        return spotifyClientSecret;
    }

    String getRedirectUri() {
        return redirectUri;
    }

    String getSpotifyAccessHost() {
        return spotifyAccessHost;
    }

    String getSpotifyResourceHost() {
        return spotifyResourceHost;
    }

    int getPageSize() {
        return pageSize;
    }
}
