package org.example.LogoutPage;



import org.example.Base.BaseTest;
import org.example.LoginPage.pageLocators;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class TestCases extends BaseTest {

    private LogoutPageLocators logoutPage;
    private WebDriverWait      wait;

    @BeforeClass(alwaysRun = true)
    public void loginFirst() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        pageLocators loginPage = new pageLocators(driver);
        loginPage.enterUserName("Admin");
        loginPage.enterPassword("admin123");
        loginPage.clickLoginButton();

        wait.until(ExpectedConditions.urlContains("dashboard"));
        System.out.println("[SETUP] LogoutPage.TestCases — Login successful.");
    }


    @Test(priority = 1,
          description = "TC_L01 - Logout and verify redirect to login page")
    public void tc_L01_verifyLogout() {
        logoutPage = new LogoutPageLocators(driver);
        logoutPage.performLogout();

        wait.until(ExpectedConditions.urlContains("login"));
        String currentUrl = driver.getCurrentUrl();
        System.out.println("[INFO] TC_L01 — URL after logout: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("login"),
                "[FAIL] TC_L01: Did not redirect to login page. URL: " + currentUrl);
        System.out.println("[PASS] TC_L01: Logout successful. Login page confirmed.");
    }
}
