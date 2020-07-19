![CI](https://github.com/carlos-velasco/JetBrainsAcademy-MusicAdvisor/workflows/CI/badge.svg)

# JetBrains Academy - Music Advisor
This repository contains my implementation of the [Music Advisor](https://hyperskill.org/projects/62) project in [JetBrains Academy](https://www.jetbrains.com/academy/).

## Configuration
This Java application interacts with Spotify's API via a [Spotify application](https://developer.spotify.com/). 

Some data of the Spotify application must be specified in [`application.properties`](https://github.com/carlos-velasco/JetBrainsAcademy-MusicAdvisor/blob/master/src/main/resources/application.properties): Client ID, Client Secret, and Redirect URI.
```
spotify.clientid=your_spotify_app_client_id
spotify.client_secret=your_spotify_app_client_secret
# e.g. redirect_uri=http://localhost:8080
redirect_uri=your_spotify_app_redirect_uri
```

For more information on how to configure / obtain that information in your Spotify application, check https://developer.spotify.com/documentation/general/guides/app-settings/.
