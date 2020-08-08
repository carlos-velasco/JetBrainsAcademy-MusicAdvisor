package advisor.model.service;

public class SpotifyAdvisorException extends RuntimeException {

    public SpotifyAdvisorException(String errorMessage) {
        super(errorMessage);
    }

    public SpotifyAdvisorException(Throwable throwable) {
        super(throwable);
    }
}
