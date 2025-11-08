package com.ernx.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

// helper class for automation tests and common functions
public class ScriptHelper {

    // enter text in field
    public static void enterTextByXPath(WebDriver driver, String xPath, String text) {
        sleepAndWait(1000l);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
        field.clear();
        field.sendKeys(text);
    }

    // click element by xpath
    public static void clickByXPath(WebDriver driver, String xPath) {
        sleepAndWait(1000l);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
        button.click();
    }

    // check if element is visible
    public static boolean isElementVisible(WebDriver driver, String xPath) {
        sleepAndWait(1000l);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
        return element.isDisplayed();
    }

    // sleep for some time
    public static void sleepAndWait(Long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // print log message
    public static void log(String message){
        System.out.println(message);
    }
}
