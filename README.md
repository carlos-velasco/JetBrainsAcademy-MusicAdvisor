![CI](https://github.com/carlos-velasco/JetBrainsAcademy-MusicAdvisor/workflows/CI/badge.svg)

# JetBrains Academy - Music Advisor
This repository contains my implementation of the [Music Advisor](https://hyperskill.org/projects/62) project in [JetBrains Academy](https://www.jetbrains.com/academy/).

## Configuration
This Java application interacts with Spotify's API via a [Spotify application](https://developer.spotify.com/). 

Some data of the Spotify application must be specified in `application.properties`: Client ID, Client Secret, and Redirect URI.
```
spotify.clientid=your_spotify_app_client_id
spotify.client_secret=your_spotify_app_client_secret
# e.g. redirect_uri=http://localhost:8080
redirect_uri=your_spotify_app_redirect_uri
```

For more information on how to configure / obtain that information in your Spotify application, check https://developer.spotify.com/documentation/general/guides/app-settings/.


## Disclaimer
The tests in the package ``jetbrainsacademy`` were implemented by [HyperSkill](https://hyperskill.org/) and they are the tests that are run to verify the implementation of the project in JetBrains Academy. They are included in this repo as a way to quickly verify changes over the code in the `main` module.

The author of this repo claims no ownership over the the tests in package ``jetbrainsacademy``.

Tests in the ``test`` module not in the ``jetbrainsacademy`` package are authored and owned by the owner of this repository.
