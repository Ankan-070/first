package org.example.JobTitlesPage;



import org.example.AdminPage.AdminPageLocators;
import org.example.Base.BaseTest;
import org.example.LoginPage.pageLocators;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class TestCases extends BaseTest {

    private JobTitlesPageLocators jobTitlesPage;
    private WebDriverWait         wait;


    @BeforeClass(alwaysRun = true)
    public void loginAndNavigateToJobTitles() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Step 1: Login
        pageLocators loginPage = new pageLocators(driver);
        loginPage.enterUserName("Admin");
        loginPage.enterPassword("admin123");
        loginPage.clickLoginButton();
        wait.until(ExpectedConditions.urlContains("dashboard"));
        System.out.println("[SETUP] JobTitlesPage.TestCases — Login successful.");

        // Step 2: Navigate to Admin > Job > Job Titles
        AdminPageLocators adminPage = new AdminPageLocators(driver);
        adminPage.clickAdminTab();
        adminPage.clickJobMenu();
        adminPage.clickJobTitles();
        wait.until(ExpectedConditions.urlContains("viewJobTitleList"));
        System.out.println("[SETUP] JobTitlesPage.TestCases — Navigated to Job Titles page.");
    }


    @Test(priority = 1,
          description = "TC_J01 - Get and verify list of all Job Titles")
    public void tc_J01_getAllJobTitles() {
        jobTitlesPage = new JobTitlesPageLocators(driver);
        List<WebElement> jobs = jobTitlesPage.getAllJobTitles();

        Assert.assertNotNull(jobs, "[FAIL] TC_J01: Job titles list is null.");
        Assert.assertTrue(jobs.size() > 0, "[FAIL] TC_J01: No job titles found in the list.");
        System.out.println("[PASS] TC_J01: " + jobs.size() + " job title(s) retrieved.");
    }


    @Test(priority = 2,
          description = "TC_J02 - Verify Add button is present on Job Titles page")
    public void tc_J02_verifyAddButtonPresent() {
        jobTitlesPage = new JobTitlesPageLocators(driver);

        boolean displayed = jobTitlesPage.isAddButtonDisplayed();
        Assert.assertTrue(displayed, "[FAIL] TC_J02: Add button is not displayed.");
        System.out.println("[PASS] TC_J02: Add button is present and visible.");
    }


    @Test(priority = 3,
          dependsOnMethods = "tc_J02_verifyAddButtonPresent",
          description = "TC_J03 - Add or Edit 'Automation Tester' job title and save")
    public void tc_J03_addAutomationTesterJobTitle() {
        jobTitlesPage = new JobTitlesPageLocators(driver);
        final String jobTitle       = "Automation Tester";
        final String jobDescription = "Automates regression and functional test suites";

        By jobTitleInputLocator = By.xpath(
                "//label[normalize-space(text())='Job Title']/following::input[1]");

        if (jobTitlesPage.jobExistsInList(jobTitle)) {
            // Already exists → open Edit form and re-save
            System.out.println("[INFO] TC_J03 — '" + jobTitle + "' exists. Opening Edit form.");
            jobTitlesPage.clickEditForJob(jobTitle);
            wait.until(ExpectedConditions.visibilityOfElementLocated(jobTitleInputLocator));
            System.out.println("[INFO] TC_J03 — Edit form loaded.");
            jobTitlesPage.enterJobDescription(jobDescription);
        } else {
            // Does not exist → normal Add flow
            jobTitlesPage.clickAddButton();
            wait.until(ExpectedConditions.visibilityOfElementLocated(jobTitleInputLocator));
            System.out.println("[INFO] TC_J03 — Add form loaded.");
            jobTitlesPage.enterJobTitle(jobTitle);
            jobTitlesPage.enterJobDescription(jobDescription);
        }

        jobTitlesPage.clickSaveButton();

        // Verify redirect back to list
        wait.until(ExpectedConditions.urlContains("viewJobTitleList"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("viewJobTitleList"),
                "[FAIL] TC_J03: Did not redirect to Job Titles list after save. URL: " + currentUrl);
        System.out.println("[PASS] TC_J03: '" + jobTitle + "' saved. Redirected to: " + currentUrl);
    }
}
