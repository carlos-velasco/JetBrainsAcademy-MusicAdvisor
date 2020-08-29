package advisor.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Getter
public class ChromeDriverClassExtension implements BeforeAllCallback, AfterAllCallback {

    private WebDriver driver;

    @Override
    public void beforeAll(ExtensionContext context) {
        WebDriverManager.chromedriver().setup();
        driver = buildChromeDriver();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        driver.close();
    }

    private ChromeDriver buildChromeDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments(
                "--headless",
                "--disable-gpu",
                "--window-size=1920,1200",
                "--ignore-certificate-errors");
        return new ChromeDriver(chromeOptions);
    }
}
