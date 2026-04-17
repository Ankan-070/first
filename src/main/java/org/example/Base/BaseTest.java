package org.example.Base;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTest {

    // ── static: ONE driver shared across ALL subclass instances in the suite ──
    protected static WebDriver driver;

    // Prevents tearDown() being called more than once when multiple subclasses inherit it
    private static volatile boolean tearDownDone = false;

    protected static final String DATA_FILE_PATH =
            "C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\src\\main\\java\\Data\\TestData.xlsx";

    private static final String SCREENSHOTS_DIR =
            "C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\screenshots\\";

    @BeforeClass
    @Parameters({"Browser", "Url"})
    public void setUp(String browser,
                      @Optional("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login") String url) {

        // Only create the browser ONCE — subsequent @BeforeClass calls reuse the same driver
        if (driver == null) {
            System.out.println("============================================================");
            System.out.println("[SETUP] Launching browser : " + browser.toUpperCase());
            System.out.println("[SETUP] Target URL        : " + url);
            System.out.println("============================================================");

            switch (browser.toLowerCase().trim()) {
                case "chrome":
                    driver = new ChromeDriver();
                    break;
                case "firefox":
                    driver = new FirefoxDriver();
                    break;
                default:
                    System.out.println("[WARNING] Unknown browser '" + browser + "'. Defaulting to Chrome.");
                    driver = new ChromeDriver();
                    break;
            }

            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
            System.out.println("[SETUP] Browser created successfully.");
        }

        // Always navigate to the target URL at the start of each test class
        driver.get(url);
        System.out.println("[SETUP] Page loaded: " + url);
    }

    /**
     * Takes a screenshot and saves it to the screenshots/ directory.
     * @param screenshotName descriptive label (e.g. "TC_001_PASS_LoginButtonEnabled")
     */
    public String takeScreenshot(String screenshotName) {
        if (driver == null) {
            System.out.println("[SCREENSHOT] Skipped — driver is null.");
            return null;
        }
        try {
            File dir = new File(SCREENSHOTS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String fileName = screenshotName + "_" + timestamp + ".png";
            String filePath = SCREENSHOTS_DIR + fileName;

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), Paths.get(filePath));
            System.out.println("[SCREENSHOT] Saved: screenshots/" + fileName);
            return filePath;
        } catch (IOException e) {
            System.out.println("[SCREENSHOT] ERROR — could not save screenshot: " + e.getMessage());
            return null;
        }
    }

    /** Auto-captures a screenshot on every test method failure. */
    @AfterMethod
    public void captureOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot("FAIL_" + result.getName());
        }
    }

    /** Runs ONCE at the very end of the suite — quits the shared driver. */
    @AfterSuite
    public void tearDown() {
        if (!tearDownDone && driver != null) {
            tearDownDone = true;
            driver.quit();
            driver = null;
            System.out.println("[TEARDOWN] Browser closed successfully.");
        }
    }
}
