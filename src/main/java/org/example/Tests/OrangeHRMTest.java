package org.example.Tests;
import org.example.AdminPage.AdminPageLocators;
import org.example.Base.BaseTest;
import org.example.JobTitlesPage.JobTitlesPageLocators;
import org.example.LoginPage.pageLocators;
import org.example.LogoutPage.LogoutPageLocators;
import org.example.ReadData.ExcelUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;


public class OrangeHRMTest extends BaseTest {

    /* ── Page Object references ── */
    private pageLocators       loginPage;
    private AdminPageLocators  adminPage;
    private JobTitlesPageLocators jobTitlesPage;
    private LogoutPageLocators logoutPage;


    private WebDriverWait explicitWait;



    @DataProvider(name = "jobData")
    public Object[][] getJobData() {

        Object[][] data = ExcelUtils.getTestData(DATA_FILE_PATH, "jobs", 2);
        if (data == null || data.length == 0) {
            System.out.println("[WARNING] 'jobs' sheet not found in Excel. Using default hardcoded job data.");
            return new Object[][]{
                    {"Automation Tester", "Automates regression and functional test suites"}
            };
        }
        return data;
    }
    // TC_001 — Verify Login Button Is Enabled

    @Test(priority = 1,
          description = "TC_001 - Verify Login button is enabled on the login page")
    public void tc001_verifyLoginButtonEnabled() {
        explicitWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginPage    = new pageLocators(driver);

        boolean isEnabled = loginPage.isLoginButtonEnabled();

        System.out.println("[RESULT] TC_001 — Login button enabled: " + isEnabled);
        Assert.assertTrue(isEnabled,
                "[FAIL] TC_001: Login button is NOT enabled on the login page.");
        System.out.println("[PASS] TC_001: Login button is enabled.");
        takeScreenshot("TC_001_PASS_LoginButtonEnabled");
    }

    // TC_002 — Login and Verify Dashboard URL

    @Test(priority = 2,
          description = "TC_002 - Login with Excel credentials and verify dashboard URL")
    public void tc002_loginAndVerifyDashboard() {
        explicitWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        /* ── Read first valid credential row from Excel (2 input cols only) ── */
        Object[][] credentials = ExcelUtils.getTestData(DATA_FILE_PATH, "login", 2);
        if (credentials == null || credentials.length == 0) {
            Assert.fail("[FAIL] TC_002: No login data found in TestData.xlsx 'login' sheet.");
        }
        String username = (String) credentials[0][0];
        String password = (String) credentials[0][1];
        System.out.println("[INFO] TC_002 — Logging in as: " + username);

        loginPage = new pageLocators(driver);
        loginPage.enterUserName(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();

        /* ── Wait for dashboard URL — explicit wait avoids Thread.sleep() ── */
        explicitWait.until(ExpectedConditions.urlContains("dashboard"));
        String currentUrl = driver.getCurrentUrl();
        System.out.println("[INFO] Current URL after login: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("dashboard"),
                "[FAIL] TC_002: URL does not contain 'dashboard'. Actual: " + currentUrl);

        /* ── Write result to Excel (best-effort — log warning if file is locked) ── */
        try {
            ExcelUtils.setCellDatas(DATA_FILE_PATH, 0, 1, 3, "Login Successful | URL: " + currentUrl);
            ExcelUtils.setCellDatas(DATA_FILE_PATH, 0, 1, 4, "Pass");
            ExcelUtils.fillGreenColor(DATA_FILE_PATH, "login", 1, 4);
        } catch (Exception ex) {
            System.out.println("[WARNING] TC_002: Could not write result to Excel (file may be open): " + ex.getMessage());
        }
        System.out.println("[PASS] TC_002: Dashboard URL verified.");
        takeScreenshot("TC_002_PASS_DashboardVerified");
    }

    // TC_003 — Navigate Admin > Job > Job Titles

    @Test(priority = 3,
          dependsOnMethods = "tc002_loginAndVerifyDashboard",
          description = "TC_003 - Navigate Admin > Job > Job Titles and verify it exists")
    public void tc003_navigateToJobTitles() {
        adminPage = new AdminPageLocators(driver);

        adminPage.clickAdminTab();
        adminPage.clickJobMenu();

        /* Verify 'Job Titles' option appears in the dropdown */
        boolean isVisible = adminPage.isJobTitlesVisible();
        System.out.println("[RESULT] TC_003 — Job Titles link visible: " + isVisible);
        Assert.assertTrue(isVisible,
                "[FAIL] TC_003: 'Job Titles' option is NOT visible under Job menu.");

        adminPage.clickJobTitles();
        System.out.println("[PASS] TC_003: Navigated to Job Titles page successfully.");
        takeScreenshot("TC_003_PASS_JobTitlesPage");
    }


    // TC_004 — Get List of All Job Titles

    @Test(priority = 4,
          dependsOnMethods = "tc003_navigateToJobTitles",
          description = "TC_004 - Retrieve and log all existing Job Titles")
    public void tc004_getAllJobTitles() {
        jobTitlesPage = new JobTitlesPageLocators(driver);

        List<WebElement> jobs = jobTitlesPage.getAllJobTitles();
        Assert.assertNotNull(jobs, "[FAIL] TC_004: Could not retrieve job titles list.");

        System.out.println("[PASS] TC_004: Found " + jobs.size() + " job title(s).");
        takeScreenshot("TC_004_PASS_JobTitlesList");
    }

    // TC_005 — Add Job Title Using Excel Data

    @Test(priority = 5,
          dataProvider = "jobData",
          dependsOnMethods = "tc004_getAllJobTitles",
          description = "TC_005 - Add or Edit Job Title using data from Excel")
    public void tc005_addJobTitle(String jobTitle, String jobDescription) throws IOException {
        jobTitlesPage = new JobTitlesPageLocators(driver);
        explicitWait  = new WebDriverWait(driver, Duration.ofSeconds(15));

        // XPath for the Job Title input field — same on both Add and Edit forms
        By jobTitleInputLocator = By.xpath(
                "//label[normalize-space(text())='Job Title']/following::input[1]");

        if (jobTitlesPage.jobExistsInList(jobTitle)) {
            // ── Job already exists → open Edit form and re-save ──────────────
            System.out.println("[INFO] TC_005 — '" + jobTitle + "' exists. Opening Edit form.");
            jobTitlesPage.clickEditForJob(jobTitle);

            // Wait for the Edit form to load — check that the Job Title input is visible
            // (more reliable than URL check because OrangeHRM edit URL includes an ID)
            explicitWait.until(ExpectedConditions.visibilityOfElementLocated(jobTitleInputLocator));
            System.out.println("[INFO] TC_005 — Edit form loaded.");

            jobTitlesPage.enterJobDescription(jobDescription);
            jobTitlesPage.clickSaveButton();

        } else {
            // ── Job does not exist → normal Add flow ─────────────────────────
            Assert.assertTrue(jobTitlesPage.isAddButtonDisplayed(),
                    "[FAIL] TC_005: 'Add' button not displayed on Job Titles page.");

            jobTitlesPage.clickAddButton();

            // Wait for Add form — Job Title input becomes visible
            explicitWait.until(ExpectedConditions.visibilityOfElementLocated(jobTitleInputLocator));
            System.out.println("[INFO] TC_005 — Add Job Title form loaded.");

            jobTitlesPage.enterJobTitle(jobTitle);
            jobTitlesPage.enterJobDescription(jobDescription);
            jobTitlesPage.clickSaveButton();
        }

        /* After save, OrangeHRM redirects back to the Job Titles list */
        explicitWait.until(ExpectedConditions.urlContains("viewJobTitleList"));

        String postSaveUrl = driver.getCurrentUrl();
        System.out.println("[INFO] TC_005 — URL after save: " + postSaveUrl);
        String expted=ExcelUtils.getCellData("C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\src\\main\\java\\Data\\TestData.xlsx","Jobs",1,2);
        if(expted.equals(jobTitle)){
            ExcelUtils.setCellDatas("C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\src\\main\\java\\Data\\TestData.xlsx", 1, 1, 3, jobTitle);
            ExcelUtils.setCellDatas("C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\src\\main\\java\\Data\\TestData.xlsx", 1, 1, 4, "Pass");
            ExcelUtils.fillGreenColor("C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\src\\main\\java\\Data\\TestData.xlsx", "Jobs", 1, 4);
        }else{
            ExcelUtils.setCellDatas("C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\src\\main\\java\\Data\\TestData.xlsx", 1, 1, 3, jobTitle);
            ExcelUtils.setCellDatas("C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\src\\main\\java\\Data\\TestData.xlsx", 1, 1, 4, "Fail");
            ExcelUtils.fillRedColor("C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\src\\main\\java\\Data\\TestData.xlsx", "Jobs", 1, 4);
        }
        Assert.assertNotNull(postSaveUrl);
        Assert.assertTrue(postSaveUrl.contains("viewJobTitleList"),
                "[FAIL] TC_005: Expected redirect to Job Titles list. Actual URL: " + postSaveUrl);

        System.out.println("[PASS] TC_005: Job Title '" + jobTitle + "' saved successfully.");
        takeScreenshot("TC_005_PASS_JobTitleSaved");
    }

    // TC_006 — Logout

    @Test(priority = 6,
          dependsOnMethods = "tc005_addJobTitle",
          description = "TC_006 - Logout and verify redirect to login page")
    public void tc006_logout() {
        logoutPage   = new LogoutPageLocators(driver);
        explicitWait = new WebDriverWait(driver, Duration.ofSeconds(15));

        logoutPage.performLogout();


        explicitWait.until(ExpectedConditions.urlContains("login"));

        String currentUrl = driver.getCurrentUrl();
        System.out.println("[INFO] TC_006 — URL after logout: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("login"),
                "[FAIL] TC_006: Did not redirect to login page. Actual URL: " + currentUrl);

        System.out.println("[PASS] TC_006: Logout successful. Login page confirmed.");
        takeScreenshot("TC_006_PASS_LogoutConfirmed");
    }
}
