package advisor.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverClassExtension extends ChromeDriver implements AfterAllCallback {

    static {
        WebDriverManager.chromedriver().setup();
    }

    public ChromeDriverClassExtension () {
        super(getDefaultChromeOptions());
    }

    @Override
    public void afterAll(ExtensionContext context) {
        quit();
    }

    private static ChromeOptions getDefaultChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--headless",
                "--disable-gpu",
                "--window-size=1920,1200",
                // Spotify login page returns 400 BadRequest when using headless user agent
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko)" +
                        " Chrome/119.0.0.0 Safari/537.36",
                "--ignore-certificate-errors");
        return chromeOptions;
    }
}
