package advisor.model.service;

import advisor.model.AdvisorException;
import advisor.model.dto.*;

import java.util.List;

public final class FakeAdvisor implements Advisor {

    private static final List<Category> CATEGORIES = List.of(
            new Category("Mellow Morning", "mellowMorning"),
            new Category("Wake Up and Smell the Coffee", "wakeUpAndSmellTheCoffee"),
            new Category("Monday Motivation", "mondayMotivation"),
            new Category("Songs to Sing in the Shower", "songsToSingInTheShower"),
            new Category("Good mood", "goodMood"));

    private static final List<Release> RELEASES = List.of(
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
            Release.builder().title("All Out Life")
                    .artists(List.of(new Artist("Slipknot")))
                    .link("https://open.spotify.com/album/GGrstgsdf")
                    .build());

    private static final List<Playlist> GOOD_MOOD_CATEGORY_PLAYLISTS = List.of(
            Playlist.builder().title("Walk Like A Badass")
                    .link("https://open.spotify.com/playlist/4654").build(),
            Playlist.builder().title("Rage Beats")
                    .link("https://open.spotify.com/playlist/gfgdf4").build(),
            Playlist.builder().title("Arab Mood Booster ")
                    .link("https://open.spotify.com/playlist/fdfsdfE789").build(),
            Playlist.builder().title("Sunday Stroll")
                    .link("https://open.spotify.com/playlist/FGrr342BB").build());

    private static final List<Playlist> FEATURED_PLAYLISTS = List.of(
            Playlist.builder().title("Mellow Morning")
                    .link("https://open.spotify.com/playlist/Gfdg4543").build(),
            Playlist.builder().title("Wake Up and Smell the Coffee")
                    .link("https://open.spotify.com/playlist/798GDGBe33f").build(),
            Playlist.builder().title("Monday Motivation")
                    .link("https://open.spotify.com/playlist/faef789vdfs").build(),
            Playlist.builder().title("Songs to Sing in the Shower")
                    .link("https://open.spotify.com/playlist/r4rst43t4").build());

    private final int pageSize;

    public FakeAdvisor(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public Page<Category> getCategories(int pageNumber) {
        if (pageNumber * pageSize > CATEGORIES.size()) {
            return new Page<>(List.of(), CATEGORIES.size(), pageNumber);
        }
        return new Page<>(CATEGORIES.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize)), CATEGORIES.size(), pageNumber);
    }

    @Override
    public Page<Category> getCategories() {
        return new Page<>(CATEGORIES, CATEGORIES.size(), 0);
    }

    @Override
    public Page<Release> getNewReleases(int pageNumber) {
        if (pageNumber * pageSize > RELEASES.size()) {
            return new Page<>(List.of(), RELEASES.size(), pageNumber);
        }
        return new Page<>(RELEASES.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize)), RELEASES.size(), pageNumber);
    }

    @Override
    public Page<Playlist> getCategoryPlaylists(Category category, int pageNumber) throws AdvisorException {
        if (category.equals(new Category("Good mood", "goodMood"))) {
            if (pageNumber * pageSize > GOOD_MOOD_CATEGORY_PLAYLISTS.size()) {
                return new Page<>(List.of(), GOOD_MOOD_CATEGORY_PLAYLISTS.size(), pageNumber);
            }
            return new Page<>(GOOD_MOOD_CATEGORY_PLAYLISTS.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize)), GOOD_MOOD_CATEGORY_PLAYLISTS.size(), pageNumber);

        }
        throw new AdvisorException("Unknown category name.");
    }

    @Override
    public Page<Playlist> getFeaturedPlaylists(int pageNumber) {
        if (pageNumber * pageSize > FEATURED_PLAYLISTS.size()) {
            return new Page<>(List.of(), FEATURED_PLAYLISTS.size(), pageNumber);
        }
        return new Page<>(FEATURED_PLAYLISTS.subList((pageNumber - 1) * pageSize, (pageNumber * pageSize)), FEATURED_PLAYLISTS.size(), pageNumber);
    }
}
