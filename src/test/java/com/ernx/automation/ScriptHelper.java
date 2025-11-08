package com.ernx.automation;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class ScriptHelper {

    public static void enterTextByXPath(WebDriver driver, String xPath, String text) {
        sleepAndWait(1000l);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
        field.clear();
        field.sendKeys(text);
    }

    public static void clickButtonByXPath(WebDriver driver, String xPath) {
        sleepAndWait(1000l);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
        button.click();
    }

    public static boolean isElementVisible(WebDriver driver, String xPath) {
        sleepAndWait(1000l);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
        return element.isDisplayed();
    }

    public static void sleepAndWait(Long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void log(String message){
        System.out.println(message);
    }
}
