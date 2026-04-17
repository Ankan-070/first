package org.example.LogoutPage;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LogoutPageLocators {

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "span.oxd-userdropdown-tab")
    private WebElement userDropdown;

    @FindBy(xpath = "//a[normalize-space(text())='Logout']")
    private WebElement logoutLink;

    public LogoutPageLocators(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }


    public void performLogout() {
        // Open the dropdown
        wait.until(ExpectedConditions.elementToBeClickable(userDropdown));
        userDropdown.click();
        System.out.println("[INFO] User dropdown opened.");

        // Click Logout
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
        logoutLink.click();
        System.out.println("[INFO] Logout link clicked.");
    }
}
