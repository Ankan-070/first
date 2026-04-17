package org.example.AdminPage;

import org.example.Base.BaseTest;
import org.example.LoginPage.pageLocators;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class TestCases extends BaseTest {

    private pageLocators       loginPage;
    private AdminPageLocators  adminPage;


    @BeforeClass(alwaysRun = true)
    public void loginFirst() {
        loginPage = new pageLocators(driver);
        loginPage.enterUserName("Admin");
        loginPage.enterPassword("admin123");
        loginPage.clickLoginButton();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.urlContains("dashboard"));
        System.out.println("[SETUP] AdminPage.TestCases — Login successful.");
    }


    @Test(priority = 1,
          description = "TC_A01 - Verify Admin tab navigates to Admin module")
    public void tc_A01_verifyAdminTabNavigation() {
        adminPage = new AdminPageLocators(driver);
        adminPage.clickAdminTab();

        String currentUrl = driver.getCurrentUrl();
        System.out.println("[INFO] URL after Admin tab click: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("admin"),
                "[FAIL] TC_A01: Admin module did not load. URL: " + currentUrl);
        System.out.println("[PASS] TC_A01: Admin tab navigation successful.");
    }


    @Test(priority = 2,
          dependsOnMethods = "tc_A01_verifyAdminTabNavigation",
          description = "TC_A02 - Verify Job menu expands and shows Job Titles")
    public void tc_A02_verifyJobMenuExpands() {
        adminPage = new AdminPageLocators(driver);
        adminPage.clickJobMenu();

        boolean isVisible = adminPage.isJobTitlesVisible();
        System.out.println("[RESULT] TC_A02 — Job Titles visible: " + isVisible);

        Assert.assertTrue(isVisible,
                "[FAIL] TC_A02: 'Job Titles' not visible after clicking Job menu.");
        System.out.println("[PASS] TC_A02: Job menu expanded and Job Titles is visible.");
    }
}
