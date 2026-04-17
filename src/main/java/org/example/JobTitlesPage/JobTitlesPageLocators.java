package org.example.JobTitlesPage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class JobTitlesPageLocators {

    private final WebDriver     driver;
    private final WebDriverWait wait;


    @FindBy(xpath = "//button[contains(@class,'oxd-button--secondary') and normalize-space(.)='Add']")
    private WebElement addButton;

    @FindBy(xpath = "//label[normalize-space(text())='Job Title']/following::input[1]")
    private WebElement jobTitleInput;

    @FindBy(xpath = "//label[normalize-space(text())='Job Description']/following::textarea[1]")
    private WebElement jobDescriptionTextarea;


    @FindBy(xpath = "//label[normalize-space(text())='Note']/following::textarea[1]")
    private WebElement noteTextarea;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement saveButton;


    public JobTitlesPageLocators(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
    }

    public List<WebElement> getAllJobTitles() {

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".oxd-table-body .oxd-table-card")));


        List<WebElement> titleCells = driver.findElements(
                By.cssSelector(".oxd-table-body .oxd-table-card .oxd-table-cell:nth-child(2)"));

        System.out.println("[INFO] ---- Job Titles List (" + titleCells.size() + " found) ----");
        for (int i = 0; i < titleCells.size(); i++) {
            System.out.println("[INFO]   " + (i + 1) + ". " + titleCells.get(i).getText());
        }
        System.out.println("[INFO] ---- End of List ----");

        return titleCells;
    }


    public void clickAddButton() {
        wait.until(ExpectedConditions.elementToBeClickable(addButton));
        addButton.click();
        System.out.println("[INFO] Clicked 'Add' button.");
    }


    public void enterJobTitle(String title) {
        wait.until(ExpectedConditions.visibilityOf(jobTitleInput));
        jobTitleInput.clear();
        jobTitleInput.sendKeys(title);
        System.out.println("[INFO] Entered Job Title: '" + title + "'");
    }


    public void enterJobDescription(String description) {
        wait.until(ExpectedConditions.visibilityOf(jobDescriptionTextarea));
        jobDescriptionTextarea.clear();
        jobDescriptionTextarea.sendKeys(description);
        System.out.println("[INFO] Entered Job Description.");
    }


    public void enterNote(String note) {
        wait.until(ExpectedConditions.visibilityOf(noteTextarea));
        noteTextarea.clear();
        noteTextarea.sendKeys(note);
        System.out.println("[INFO] Entered Note.");
    }

    public void clickSaveButton() {
        // Wait for the loading overlay (oxd-form-loader) to fully disappear
        // before clicking — otherwise it intercepts the click and throws
        // ElementClickInterceptedException.
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector(".oxd-form-loader")));
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        // JS click bypasses any residual overlay that Selenium's regular click would hit
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButton);
        System.out.println("[INFO] Clicked 'Save' button.");
    }


    public boolean jobExistsInList(String title) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".oxd-table-body .oxd-table-card")));

            List<WebElement> rows = driver.findElements(
                    By.cssSelector(".oxd-table-body .oxd-table-card"));

            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.cssSelector(".oxd-table-cell"));
                // Cell index 1 (2nd cell) holds the job title text
                if (cells.size() >= 2) {
                    String cellText = cells.get(1).getText().trim();
                    if (cellText.equalsIgnoreCase(title.trim())) {
                        System.out.println("[INFO] Job title '" + title + "' already exists in the list.");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[INFO] Could not check job list: " + e.getMessage());
        }
        System.out.println("[INFO] Job title '" + title + "' NOT found — will Add.");
        return false;
    }


    public void clickEditForJob(String title) {
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".oxd-table-body .oxd-table-card")));

        List<WebElement> rows = driver.findElements(
                By.cssSelector(".oxd-table-body .oxd-table-card"));

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.cssSelector(".oxd-table-cell"));
            if (cells.size() >= 2 && cells.get(1).getText().trim().equalsIgnoreCase(title.trim())) {

                // Scroll the row into view so the button is fully visible
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", row);

                // The last cell holds the action buttons; first button = Edit (pencil)
                WebElement lastCell = cells.get(cells.size() - 1);
                List<WebElement> buttons = lastCell.findElements(By.tagName("button"));

                if (buttons.isEmpty()) {
                    buttons = row.findElements(By.tagName("button"));
                }

                if (buttons.isEmpty()) {
                    throw new RuntimeException("[ERROR] No Edit button found in row for: " + title);
                }

                // Button order in OrangeHRM job titles row: [0]=Delete(trash), [1]=Edit(pencil)
                WebElement editBtn = buttons.get(1);
                wait.until(ExpectedConditions.elementToBeClickable(editBtn));
                editBtn.click();
                System.out.println("[INFO] Clicked Edit button for: '" + title + "'");
                return;
            }
        }
        throw new RuntimeException("[ERROR] Job title not found in table: '" + title + "'");
    }

    public boolean isAddButtonDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(addButton));
            boolean displayed = addButton.isDisplayed();
            System.out.println("[INFO] 'Add' button displayed: " + displayed);
            return displayed;
        } catch (Exception e) {
            System.out.println("[ERROR] 'Add' button not found: " + e.getMessage());
            return false;
        }
    }
}
