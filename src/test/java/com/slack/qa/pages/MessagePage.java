package com.slack.qa.pages;

import com.slack.qa.common.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Created by sharanya on 4/12/16.
 */
public class MessagePage {
    private WebDriver driver;
    public static String URL = "account-settings";
    private WebDriverUtils webDriverUtils;

    public MessagePage(WebDriver driver) {
        this.driver = driver;
        this.webDriverUtils = new WebDriverUtils(driver);

    }

    @FindBy(linkText = "Sign in")
    private WebElement signInButton;

    public void clickSignInButton() {
        signInButton.click();
    }

    @FindBy(id = "domain")
    private WebElement domainText;

    public void setDomainText(String domain) {
        domainText.clear();
        domainText.sendKeys(domain);
    }

    @FindBy(id = "submit_team_domain")
    private WebElement submitTeamDomainButton;

    public void clickSubmitTeamDomainButton() {
        submitTeamDomainButton.click();
    }

    @FindBy(id = "email")
    private WebElement emailText;

    public void setEmailText(String email) {
        emailText.clear();
        emailText.sendKeys(email);
    }

    @FindBy(id = "password")
    private WebElement passwordText;

    public void setPasswordText(String password) {
        passwordText.clear();
        passwordText.sendKeys(password);
    }

    @FindBy(id = "signin_btn")
    private WebElement signinButton;

    public void clickUserSignInButton() {
        signinButton.click();
    }

    @FindBy(id = "message-input")
    private WebElement messageInput;

    public void sendMessage(String message) {
        messageInput.clear();
        messageInput.sendKeys(message);
        messageInput.sendKeys(Keys.ENTER);
    }

    public String starMessage() {
        Actions action = new Actions(driver);
        // Gets all the messages in the history.
        List<WebElement> web = driver.findElements(By.className("day_msgs"));

        // Get the last day.
        WebElement day = web.get(web.size() - 1);

        // Get the last message in the day.
        List<WebElement> id = day.findElements(By.className("message"));
        WebElement message = id.get(id.size() - 1);

        String href = message.findElement(By.className("timestamp")).getAttribute("href");
        WebElement messageBody = message.findElement(By.className("message_body"));
        action.moveToElement(messageBody).perform();
        WebElement messageContent = message.findElement(By.className("message_content"));
        // Since this element is visible after hover, webElement.click() doesn't work and it throws an exception
        // waiting for the visibility of it. Hence using javascript to click on the star message.
        webDriverUtils.jsClick(messageContent.findElement(By.className("star_message")));
        return href;
    }

    @FindBy(id = "search_terms")
    private WebElement searchTextBox;

    public void clickSearchTextBox() {
        searchTextBox.clear();
        searchTextBox.sendKeys("has:star");
        searchTextBox.sendKeys(Keys.ENTER);
        searchTextBox.sendKeys(Keys.ENTER);
    }


    public String getEnteredInput(String path) {
        return getWebelementForEnteredInput().findElement(By.className(path)).getText();
    }

    public WebElement getWebelementForEnteredInput() {
        // Gets all the messages in the history.
        List<WebElement> web = driver.findElements(By.className("day_msgs"));

        // Get the last day.
        WebElement day = web.get(web.size() - 1);

        // Get the last message in the day.
        List<WebElement> id = day.findElements(By.className("message"));
        WebElement message = id.get(id.size() - 1);

        return message.findElement(By.className("message_body"));
    }


    @FindBy(id = "search_message_results")
    private WebElement searchMessageResults;

    public String getStarredMessageText() {
        List<WebElement> searchResults = searchMessageResults.findElements(By.className("search_result_with_extract"));
        return searchResults.get(0).findElement(By.className("message_body")).getText();
    }

    public String gethrefValue() {
        return searchMessageResults.findElement(By.xpath(".//div[1]/div[2]/div[3]/ts-message/div[2]/a[2]")).getAttribute("href");
    }

    @FindBy(id = "channels_scroller")
    private WebElement channelsScroller;

    public WebElement getChannelsScroller() {
        return channelsScroller;
    }

    @FindBy(id = "stars_toggle")
    private WebElement starToggleButton;

    public void clickStarToggleButton(){
        starToggleButton.findElement(By.className("ts_icon_star_o")).click();
    }


    public WebElement getStarredWebElement(){
        List<WebElement> starredItems = driver.findElements(By.className("star_item"));
        return starredItems.get(0).findElement(By.className("message_body"));
    }

    public String getStarredText(){
        return getStarredWebElement().getText();
    }


    public String getStarredHrefValue(){
        List<WebElement> starredItems = driver.findElements(By.className("star_item"));
        return starredItems.get(0).findElement(By.className("timestamp")).getAttribute("href");
    }
}
