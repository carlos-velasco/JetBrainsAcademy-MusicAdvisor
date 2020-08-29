package advisor.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import org.junit.rules.ExternalResource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@Getter
public class ChromeDriverSetupRule extends ExternalResource {

    private WebDriver driver;

    @Override
    protected void before() {
        WebDriverManager.chromedriver().setup();
        driver = buildChromeDriver();
    }

    @Override
    protected void after() {
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
