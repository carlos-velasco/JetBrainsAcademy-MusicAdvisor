package advisor;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Getter(AccessLevel.PACKAGE)
public class AdvisorProperties {

    private String spotifyClientId;
    private String spotifyClientSecret;
    private String redirectUri;
    private String spotifyAccessHost;
    private String spotifyResourceHost;
    private Integer pageSize;

    public void initializeProperties(String[] args) throws IOException {
        initializePropertiesFromCommandLineArguments(args);
        initializePropertiesFromPropertyFile();
    }

    private void initializePropertiesFromCommandLineArguments(String[] args) {
        for (int index = 0; isNotLastCommandLineArgument(args, index) && !allCommandLinePropertiesInitialized(); index++) {
            if (args[index].equals("-access")) {
                spotifyAccessHost = args[index + 1];
            }

            if (args[index].equals("-resource")) {
                spotifyResourceHost = args[index + 1];
            }

            if (args[index].equals("-page")) {
                pageSize = Integer.parseInt(args[index + 1]);
            }
        }
    }

    private void initializePropertiesFromPropertyFile() throws IOException {
        final String propertiesFileName = "application.properties";
        Properties properties = new Properties();
        properties.load(Objects.requireNonNull(
                this.getClass().getClassLoader().getResourceAsStream(propertiesFileName)));
        spotifyClientId = properties.getProperty("spotify.clientid");
        spotifyClientSecret = properties.getProperty("spotify.client_secret");
        redirectUri = properties.getProperty("redirect_uri");
        spotifyAccessHost = spotifyAccessHost != null
                ? spotifyAccessHost
                : properties.getProperty("spotify.default_access_host");
        spotifyResourceHost = spotifyResourceHost != null
                ? spotifyResourceHost
                : properties.getProperty("spotify.default_resource_host");
        pageSize = pageSize != null
                ? pageSize
                : Integer.parseInt(properties.getProperty("page_size"));
    }

    private boolean isNotLastCommandLineArgument(String[] args, int index) {
        return index < (args.length - 1);
    }

    private boolean allCommandLinePropertiesInitialized() {
        return spotifyAccessHost != null && spotifyResourceHost != null && pageSize != null;
    }
}
