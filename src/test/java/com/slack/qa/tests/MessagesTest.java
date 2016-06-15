package com.slack.qa.tests;

import com.slack.qa.common.SeleniumSetup;
import com.slack.qa.common.WebDriverUtils;
import com.slack.qa.pages.MessagePage;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.log4testng.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by sharanya on 4/12/16.
 */
public class MessagesTest extends SeleniumSetup {
    protected static Logger log = Logger.getLogger(MessagesTest.class);
    private MessagePage messagePage;
    private static final String PROPERTIES_FILE = "slack.properties";
    private static String DOMAIN;
    private static String EMAIL;
    private static String PASS;

    @BeforeClass
    @Parameters({"driver", "baseURL"})
    public void setUp(@Optional("chrome") String driver,
                      @Optional("https://slack.com/") String baseURL) throws Exception {
        try {
            Properties props = new Properties();
            props.load(MessagesTest.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
            DOMAIN = props.getProperty("domain");
            EMAIL = props.getProperty("email");
            PASS = props.getProperty("password");
        } catch (FileNotFoundException e) {
            log.error("Properties file not found.", e);
        } catch (IOException e) {
            log.error("Error reading from properties file.", e);
        }
        super.setUp(driver, baseURL);
        messagePage = PageFactory.initElements(this.driver, MessagePage.class);

        // Navigate to the homepage
        this.driver.navigate().to(BASE_URL);

        // Sign in
        messagePage.clickSignInButton();
        messagePage.setDomainText(DOMAIN);
        messagePage.clickSubmitTeamDomainButton();
        messagePage.setEmailText(EMAIL);
        messagePage.setPasswordText(PASS);
        messagePage.clickUserSignInButton();

        // Wait till you see the channels section after login.
        // You can subsitute this with any element that you expect should be visible once the login is complete.
        // If the login fails due to any reason, this would thrown a TimeoutException.
        webDriverUtils.waitForElement(messagePage.getChannelsScroller());
    }

    /**
     * Entering text in the box at the bottom of the client. Message will appear in the current channel.
     * Hover over the message, you'll see a star. Enter the string has:star in the search field and submit it.
     * Verify that your message appears in the search result.
     */
    @Test
    public void starMessageAndSearchTest() {
        String randomMessage = "This is a message at " + (new Date());
        messagePage.sendMessage(randomMessage);
        // Wait a second for the message to be sent and appear in the sent messages.
        waitSeconds(1);
        webDriverUtils.textToBePresentInElement(messagePage.getWebelementForEnteredInput(), randomMessage);
        String href = messagePage.starMessage();
        waitSeconds(1);
        messagePage.clickSearchTextBox();
        webDriverUtils.textToBePresentInElement(messagePage.getStarredWebElement(), randomMessage);

        Assert.assertEquals(driver.getTitle(), "general | General Slack");

        //Verify that your message appears in the search result.
        Assert.assertEquals(messagePage.getStarredMessageText(), randomMessage);
        //Verify that the href value is same on current channel and search result.
        Assert.assertEquals(messagePage.gethrefValue(), href);
    }

    /**
     * Entering text in the box at the bottom of the client. Message will appear in the current channel.
     * Hover over the message, you'll see a star. Click the star.
     * Click the star icon on the upper right.
     * Verify that your message appears in this list.
     */
    @Test
    public void starMessageAndFilter() {
        String randomMessage = "This is a message at " + (new Date());
        messagePage.sendMessage(randomMessage);
        // Wait a second for the message to be sent and appear in the sent messages.
        waitSeconds(1);
        webDriverUtils.textToBePresentInElement(messagePage.getWebelementForEnteredInput(), randomMessage);
        String href = messagePage.starMessage();
        messagePage.clickStarToggleButton();
        waitSeconds(1);
        webDriverUtils.textToBePresentInElement(messagePage.getStarredWebElement(), randomMessage);

        //Verify that your message appears in the search list.
        Assert.assertEquals(messagePage.getStarredText(), randomMessage);
        //Verify that the href value is same on current channel and search result.
        Assert.assertEquals(messagePage.getStarredHrefValue(), href);
        //To close the search result list.
        messagePage.clickStarToggleButton();

    }

    @DataProvider(name = "specialInputs")
    public static Object[][] specialInputs() {
        return new Object[][]{
                {"`aaa`", "special_formatting", "`\naaa\n`"}, {"```aaa```", "special_formatting", "```\naaa\n```"}, {">aaa", "content", ">\naaa"}};
    }

    /**
     * Testing for various input.
     *
     * @param input
     * @param classname
     * @param expectedResult
     */
    @Test(dataProvider = "specialInputs")
    public void codeInput(String input, String classname, String expectedResult) {
        messagePage.sendMessage(input);
        // Wait a second for the message to be sent and appear in the sent messages.
        waitSeconds(1);
        webDriverUtils.textToBePresentInElement(messagePage.getWebelementForEnteredInput(), input);
        String actualResult = messagePage.getEnteredInput(classname);
        log.info("messagePage.getEnteredInput()" + actualResult);
        Assert.assertEquals(actualResult, expectedResult);
        String href = messagePage.starMessage();

        messagePage.clickStarToggleButton();
        waitSeconds(1);
        webDriverUtils.textToBePresentInElement(messagePage.getStarredWebElement(), input);

        //Verify that your message appears in the search list.
        Assert.assertEquals(messagePage.getStarredText(), expectedResult);
        //Verify that the href value is same on current channel and search result.
        Assert.assertEquals(messagePage.getStarredHrefValue(), href);
        messagePage.clickStarToggleButton();
    }
}
