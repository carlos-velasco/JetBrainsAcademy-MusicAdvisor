package advisor.utils;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@RequiredArgsConstructor
public class SpotifyAppUIAuthenticator {

    private static final String USERNAME = System.getProperty("spotify.username");
    private static final String PASSWORD = System.getProperty("spotify.password");
    private static final By USERNAME_INPUT_LOCATOR = By.cssSelector("#login-username");
    private static final By PASSWORD_INPUT_LOCATOR = By.cssSelector("#login-password");
    private static final By LOGIN_BUTTON_LOCATOR = By.cssSelector("#login-button");
    private static final By AUTHORIZE_BUTTON_LOCATOR = By.cssSelector("#auth-accept");

    private final WebDriver driver;
    private final String redirectUri;

    private boolean usernameAndPasswordFilledIn;

    public void authenticateApp(String authenticationUrl) {
        driver.get(authenticationUrl);
        WebDriverWait wait = new WebDriverWait(driver, 10);

        if (!usernameAndPasswordFilledIn) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT_LOCATOR));

            final WebElement usernameInput = driver.findElement(USERNAME_INPUT_LOCATOR);
            usernameInput.sendKeys(USERNAME);
            final WebElement passwordInput = driver.findElement(PASSWORD_INPUT_LOCATOR);
            passwordInput.sendKeys(PASSWORD);
            final WebElement submitButton = driver.findElement(LOGIN_BUTTON_LOCATOR);
            submitButton.click();
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(AUTHORIZE_BUTTON_LOCATOR));
        usernameAndPasswordFilledIn = true;
        final WebElement authorizeButton = driver.findElement(AUTHORIZE_BUTTON_LOCATOR);
        authorizeButton.click();

        wait.until(ExpectedConditions.urlContains(redirectUri));
    }
}
