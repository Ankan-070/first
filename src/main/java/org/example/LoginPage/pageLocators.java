

package org.example.LoginPage;



import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class pageLocators {

    WebDriver driver;




    @FindBy(name = "username")
    WebElement username;
    @FindBy(name = "password")
    WebElement password;
    @FindBy(css = "button[type='submit']")
    WebElement loginbt;
    @FindBy(css = "span.oxd-userdropdown-tab")
    WebElement droplst;
    @FindBy(xpath = "//a[normalize-space(text())='Logout']")
    WebElement logoutbtn;

    public pageLocators(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }
    public void enterUserName(String name) {
        username.clear();
        username.sendKeys(name);
    }
    public void enterPassword(String pass) {
        password.clear();
        password.sendKeys(pass);
    }

    /** Clicks the Login / Submit button. */
    public void clickLoginButton() {
        loginbt.click();
    }

    public void clickLogoutButton() {
        droplst.click();
        logoutbtn.click();
    }
public boolean isLoginButtonEnabled() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOf(loginbt));
        boolean enabled = loginbt.isEnabled();
        System.out.println("[INFO] Login button enabled: " + enabled);
        return enabled;
    }
}
