package com.ernx.automation;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import java.time.Duration;

import static com.ernx.automation.ScriptHelper.log;

public class ErnxAutomation {
    
    public WebDriver driver;
    public WebDriverWait wait;

    @BeforeClass
    public void setup() {
        log("Starting test execution...");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        driver.get("https://ernx-consumer.vercel.app/login");
        log("Navigated to login page");
    }
    
    @AfterClass
    public void teardown() {
        if (driver != null) {
            log("Closing browser...");
            driver.quit();
            log("Test execution completed");
        }
    }

}
