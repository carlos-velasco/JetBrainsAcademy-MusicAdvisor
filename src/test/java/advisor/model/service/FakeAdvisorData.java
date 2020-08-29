package advisor.model.service;

import advisor.model.dto.Artist;
import advisor.model.dto.Category;
import advisor.model.dto.Playlist;
import advisor.model.dto.Release;

import java.util.List;

public final class FakeAdvisorData {

    public static final Category GOOD_MOOD_CATEGORY = new Category("Good mood", "goodMood");
    public static final List<Category> CATEGORIES = List.of(
            new Category("Mellow Morning", "mellowMorning"),
            GOOD_MOOD_CATEGORY,
            new Category("Wake Up and Smell the Coffee", "wakeUpAndSmellTheCoffee"),
            new Category("Monday Motivation", "mondayMotivation"),
            new Category("Songs to Sing in the Shower", "songsToSingInTheShower"),
            new Category("Pop", "pop"));

    public static final List<Release> RELEASES = List.of(
            Release.builder().title("Mountains")
                    .artists(List.of(new Artist("Sia"),
                            new Artist("Diplo"),
                            new Artist("Labrinth")))
                    .link("https://open.spotify.com/album/fefdsfs")
                    .build(),
            Release.builder().title("Runaway")
                    .artists(List.of(new Artist("Lil Peep")))
                    .link("https://open.spotify.com/album/gfgdf$T%$GD")
                    .build(),
            Release.builder().title("The Greatest Show")
                    .artists(List.of(new Artist("Panic! At The Disco")))
                    .link("https://open.spotify.com/album/REgfgree45")
                    .build(),
            Release.builder().title("El mar querer")
                    .artists(List.of(new Artist("Rosalia")))
                    .link("https://open.spotify.com/album/fd78Frs4")
                    .build(),
            Release.builder().title("Grandes éxitos")
                    .artists(List.of(new Artist("Celtas Cortos")))
                    .link("https://open.spotify.com/album/REgfgree45")
                    .build(),
            Release.builder().title("All Out Life")
                    .artists(List.of(new Artist("Slipknot")))
                    .link("https://open.spotify.com/album/GGrstgsdf")
                    .build());

    public static final List<Playlist> GOOD_MOOD_CATEGORY_PLAYLISTS = List.of(
            Playlist.builder().title("Walk Like A Badass")
                    .link("https://open.spotify.com/playlist/4654").build(),
            Playlist.builder().title("Rage Beats")
                    .link("https://open.spotify.com/playlist/gfgdf4").build(),
            Playlist.builder().title("Arab Mood Booster")
                    .link("https://open.spotify.com/playlist/fdfsdfE789").build(),
            Playlist.builder().title("Party time")
                    .link("https://open.spotify.com/playlist/gf4d87E4f").build(),
            Playlist.builder().title("Friday vibes 2020")
                    .link("https://open.spotify.com/playlist/fsd87899").build(),
            Playlist.builder().title("Sunday Stroll")
                    .link("https://open.spotify.com/playlist/FGrr342BB").build());

    public static final List<Playlist> FEATURED_PLAYLISTS = List.of(
            Playlist.builder().title("Mellow Morning")
                    .link("https://open.spotify.com/playlist/Gfdg4543").build(),
            Playlist.builder().title("Wake Up and Smell the Coffee")
                    .link("https://open.spotify.com/playlist/798GDGBe33f").build(),
            Playlist.builder().title("Monday Motivation")
                    .link("https://open.spotify.com/playlist/faef789vdfs").build(),
            Playlist.builder().title("Weekend mode")
                    .link("https://open.spotify.com/playlist/rr5343Darff").build(),
            Playlist.builder().title("Sport vibes")
                    .link("https://open.spotify.com/playlist/33fg98aaA2w").build(),
            Playlist.builder().title("Songs to Sing in the Shower")
                    .link("https://open.spotify.com/playlist/r4rst43t4").build());
}
