package org.example.AdminPage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
public class AdminPageLocators {

    private final WebDriver    driver;
    private final WebDriverWait wait;


    @FindBy(xpath = "//a[contains(@href,'viewAdminModule')]")
    private WebElement adminMenuTab;


    @FindBy(xpath = "//span[contains(@class,'oxd-topbar-body-nav-tab-item') and normalize-space(text())='Job']")
    private WebElement jobMenu;


    @FindBy(xpath = "//a[normalize-space(text())='Job Titles']")
    private WebElement jobTitlesLink;


    public AdminPageLocators(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }


    public void clickAdminTab() {
        wait.until(ExpectedConditions.elementToBeClickable(adminMenuTab));
        adminMenuTab.click();
        System.out.println("[INFO] Clicked Admin tab.");
    }


    public void clickJobMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(jobMenu));
        jobMenu.click();
        System.out.println("[INFO] Clicked Job menu.");
    }

    public void clickJobTitles() {
        // Wait for the link to appear in the DOM after dropdown opens
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space(text())='Job Titles']")));

        // JavaScript click — bypasses Vue Router's synthetic event handling
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link);
        System.out.println("[INFO] Clicked Job Titles (JS click).");
    }


    public boolean isJobTitlesVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[normalize-space(text())='Job Titles']")));
            System.out.println("[INFO] 'Job Titles' link is visible.");
            return true;
        } catch (Exception e) {
            System.out.println("[ERROR] 'Job Titles' link not found: " + e.getMessage());
            return false;
        }
    }
}
