package com.slack.qa.common;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;

public class WebDriverUtils {
    // The default timeout.
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;
    private WebDriver driver;
    private WebDriverWait wait;


    public WebDriverUtils(WebDriver driver, int timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    public WebDriverUtils(WebDriver driver) {
        this(driver, DEFAULT_TIMEOUT_SECONDS);
    }


    public void waitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clicks on an element that is visible or not.
     * This is useful instead of when we are not sure if the element is displayed or not.
     *
     * @param element Web element.
     */
    public void jsClick(WebElement element) {
        ((JavascriptExecutor) this.driver).executeScript("return arguments[0].click();", element);
    }


    public void waitForElement(WebElement webelement) {
        (new WebDriverWait(driver, 35)).until(ExpectedConditions.visibilityOf(webelement));

    }


    public void textToBePresentInElement(WebElement locator, String text){
        wait = new WebDriverWait(driver, 10);
        try{
            wait.until(ExpectedConditions.textToBePresentInElement(locator, text));
        }catch (Exception e){

        }
    }


}
