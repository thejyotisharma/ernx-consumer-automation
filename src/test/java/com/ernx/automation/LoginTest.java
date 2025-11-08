package com.ernx.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.ernx.automation.ScriptHelper.*;

public class LoginTest extends ErnxAutomation {

    // This test verifies the complete registration flow with OTP authentication and profile setup
    // Steps:
    // 1. Generate temporary email address
    // 2. Enter email and click Next
    // 3. Fetch OTP from email and enter it
    // 4. Verify redirect to complete-profile page
    // 5. Enter first name and last name
    // 6. Add child profile with nickname and gender
    // 7. Select ring element
    // 8. Complete profile and verify redirect to game page
    @Test(priority = 1)
    public void testRegisterAndCompleteProfile() {
        log("Test: Testing register and complete profile with OTP");
        
        EmailAPI emailAPI = new EmailAPI();
        String testEmail = emailAPI.getEmail();
        
        if (testEmail == null || testEmail.isEmpty()) {
            Assert.fail("Failed to generate email address");
        }
        
        enterTextByXPath(driver, "//input[@type='email']", testEmail);
        log("Entered email");
        
        clickButtonByXPath(driver, "//button[contains(text(),'Next')]");
        log("Clicked next button");
                
        String otp = emailAPI.getOTP(60);
        if (otp == null) {
            Assert.fail("Failed to fetch OTP from email");
        }
        
        enterTextByXPath(driver, "//input[@type='number']", otp);
        log("Entered OTP");
        
        clickButtonByXPath(driver, "//button");
        log("Clicked submit button");
        
        wait.until(ExpectedConditions.urlContains("complete-profile"));
        Assert.assertTrue( driver.getCurrentUrl().contains("complete-profile"), "Should be redirected to complete profile page");
        
        Assert.assertTrue(isElementVisible(driver, "//*[contains(text(),'Create Account')]"), "Create Account text should be displayed");
        
        enterTextByXPath(driver, "//input[@name='first_name']", "Test");
        log("Entered first name");
        
        enterTextByXPath(driver, "//input[@name='last_name']", "User");
        log("Entered last name");
        
        clickButtonByXPath(driver, "//button[contains(text(),'Next')]");
        log("Clicked next button on complete profile page");
        
        clickButtonByXPath(driver, "//button");
        log("Clicked Add Yourself Or the Your Child button");
        
        enterTextByXPath(driver, "//input", "TestNickname");
        log("Entered nickname");
        
        clickButtonByXPath(driver, "//img[@src='/genders/male.png']");
        log("Clicked male gender image");
        
        clickButtonByXPath(driver, "//button[contains(text(),'Next')]");
        log("Clicked next button after gender selection");
        
        clickButtonByXPath(driver, "//button[contains(text(),'Next')]");
        log("Clicked next button on next page");
        
        clickButtonByXPath(driver, "(//img[@alt='ERNX Dev test'])[1]");
        log("Clicked first element with ring class");
        
        clickButtonByXPath(driver, "//button[contains(text(),'Next')]");
        log("Clicked next button after ring element");
        
        clickButtonByXPath(driver, "//button[contains(text(),'Finish')]");
        log("Clicked finish button");
        
        wait.until(ExpectedConditions.urlContains("game"));

        Assert.assertTrue(driver.getCurrentUrl().contains("game"), "Should be redirected to game page");

        log("Test Done: Testing register and complete profile with OTP");
    }

    // This test verifies that practice activities can be confirmed and progress is tracked
    // Steps:
    // 1. Click logo to scroll page
    // 2. Press space key to scroll further
    // 3. Click Practice 1, 2, and 3 buttons
    // 4. Confirm each practice activity
    // 5. Verify progress shows 6/100
    @Test(priority = 2)
    public void testConfirmActivities() {
        log("Test: Testing brush teeth and other activities");
       
        // for scrolling page to bottom
        clickButtonByXPath(driver, "//img[@src='/logo/logo2.png']");
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.SPACE).perform();
        
        clickPracticeButton("1", wait);
        clickPracticeButton("2", wait);
        clickPracticeButton("3", wait);

        sleepAndWait(2500l);
        WebElement progress = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(., '/100')]")));
        Assert.assertEquals(progress.getText(), "6/100");
        log("Test Done: Testing brush teeth and other activities");
    }

    // This test verifies the logout functionality
    // Steps:
    // 1. Click Settings
    // 2. Click Log Out button
    // 3. Confirm logout by clicking Yes, log out
    // 4. Verify redirect to login page
    @Test(priority = 3)
    public void testLogOut() {
        log("Test: Testing log out");

        clickButtonByXPath(driver, "//span[contains(text(),'Settings')]");
        log("Clicked settings span");

        clickButtonByXPath(driver, "//button[contains(text(),'Log Out')]");
        log("Clicked log out button");

        clickButtonByXPath(driver, "//button[contains(text(),'Yes, log out')]");
        log("Clicked confirm button");

        wait.until(ExpectedConditions.urlContains("login"));
        Assert.assertTrue(driver.getCurrentUrl().contains("login"), "Should be redirected to login page");

        log("Test Done: Testing log out");
    }

    private void clickPracticeButton(String practiceNumber, WebDriverWait wait){
        clickButtonByXPath(driver, "//img[@alt='Practice " + practiceNumber + "']");
        clickButtonByXPath(driver, "//button[contains(text(),'Yes')]");
        log("Clicked practice button: " + practiceNumber);
    }
    
}

