package advisor;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class AdvisorProperties {

    private static final String SPOTIFY_DEFAUL_ACCESS_THOST_PROPERTY_NAME = "spotify.default_access_host";
    private static final String SPOTIFY_DEFAUL_RESOURCE_HOST_PROPERTY_NAME = "spotify.default_resource_host";
    private static final String SPOTIFY_CLIENT_SECRET_PROPERTY_NAME = "spotify.client_secret";
    private static final String SPOTIFY_CLIENTID_PROPERTY_NAME = "spotify.clientid";
    private static final String REDIRECT_URI_PROPERTY_NAME = "redirect_uri";
    private static final String PAGE_SIZE_PROPERTY_NAME = "page_size";

    private String spotifyClientId;
    private String spotifyClientSecret;
    private String redirectUri;
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
        spotifyClientId = properties.getProperty(SPOTIFY_CLIENTID_PROPERTY_NAME);
        spotifyClientSecret = properties.getProperty(SPOTIFY_CLIENT_SECRET_PROPERTY_NAME);
        redirectUri = properties.getProperty(REDIRECT_URI_PROPERTY_NAME);
        spotifyAccessHost = Optional.ofNullable(spotifyAccessHost)
                .orElse(properties.getProperty(SPOTIFY_DEFAUL_ACCESS_THOST_PROPERTY_NAME));
        spotifyResourceHost = Optional.ofNullable(spotifyResourceHost)
                .orElse(properties.getProperty(SPOTIFY_DEFAUL_RESOURCE_HOST_PROPERTY_NAME));
        pageSize = Optional.ofNullable(pageSize)
                .orElse(Integer.parseInt(properties.getProperty(PAGE_SIZE_PROPERTY_NAME)));
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
