package org.example.LoginPage;

import org.example.Base.BaseTest;
import org.example.ReadData.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

public class TestCases extends BaseTest {

    pageLocators tst;
    public static String path;
    public int i = 1; // tracks current Excel data row (row 0 = header, skip it)

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        path = "C:\\Users\\2479574\\Desktop\\Interim\\OrangeHRM - Copy\\OrangeHRM\\src\\main\\java\\Data\\TestData.xlsx";
        // colCount=2 → read only username + password columns; ignore result columns
        // from previous runs so the DataProvider never has a column-count mismatch.
        return ExcelUtils.getTestData(path, "login", 2);
    }

    @Test(dataProvider = "loginData")
    public void setCredentials(String name, String pass) throws IOException {
        boolean usernameInvalid = (name == null
                || name.trim().isEmpty()
                || name.trim().equalsIgnoreCase("<null>"));
        boolean passwordInvalid = (pass == null
                || pass.trim().isEmpty()
                || pass.trim().equalsIgnoreCase("<null>"));

        if (usernameInvalid || passwordInvalid) {
            System.out.println("[INFO] Row " + i
                    + " — null/empty credential detected. Marking Fail without login attempt."
                    + " Username=[" + name + "] Password=[" + pass + "]");
            ExcelUtils.setCellDatas(path, 0, i, 3, "Invalid credentials");
            ExcelUtils.setCellDatas(path, 0, i, 4, "Fail");
            ExcelUtils.fillRedColor(path, "login", i, 4);
            i++;
            Assert.fail("Login failed — null or empty credential."
                    + " Username: [" + name + "] Password: [" + pass + "]");
            return; // unreachable, but keeps the intent clear
        }

        // ── NORMAL LOGIN FLOW ─────────────────────────────────────────────────
        tst = new pageLocators(driver);
        tst.enterUserName(name);
        tst.enterPassword(pass);
        tst.clickLoginButton();

        String link = driver.getCurrentUrl();
        Assert.assertNotNull(link, "getCurrentUrl() returned null — driver may have crashed.");

        if (link.contains("dashboard")) {
            ExcelUtils.setCellDatas(path, 0, i, 3, "Login Successful");
            ExcelUtils.setCellDatas(path, 0, i, 4, "Pass");
            ExcelUtils.fillGreenColor(path, "login", i, 4);
            takeScreenshot("PASS_setCredentials_row" + i + "_" + name);
            tst.clickLogoutButton();
            i++;
            Assert.assertTrue(true);
        } else {
            ExcelUtils.setCellDatas(path, 0, i, 3, "Invalid credentials");
            ExcelUtils.setCellDatas(path, 0, i, 4, "Fail");
            ExcelUtils.fillRedColor(path, "login", i, 4);
            i++;
            Assert.fail("Login failed — invalid credentials for username: " + name);
        }
    }
}
