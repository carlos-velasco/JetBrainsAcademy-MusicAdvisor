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
                "--ignore-certificate-errors");
        return chromeOptions;
    }
}
