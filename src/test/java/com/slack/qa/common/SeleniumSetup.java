package com.slack.qa.common;

import io.appium.java_client.ios.IOSDriver;
import org.apache.commons.io.FileUtils;
import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.math.Rational;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.log4testng.Logger;

import java.awt.Dimension;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;


public class SeleniumSetup {
    protected static Logger log = Logger.getLogger(SeleniumSetup.class);
    protected WebDriver driver;
    protected WebDriverUtils webDriverUtils;
    protected String BASE_URL = "https://slack.com/";

    private BrowserType browserType = BrowserType.UNKNOWN;
    private SeleniumScreenRecorder screenRecorder;

    public void setUp(String driver, String baseURL) throws Exception {
        BASE_URL = baseURL;
        if ("chrome".equals(driver)) {
            setupChromeDriver();
        } else if ("firefox".equals(driver)) {
            setupFirefoxDriver();
        }
        this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        this.webDriverUtils = new WebDriverUtils(this.driver);
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

    private void setupChromeDriver() throws MalformedURLException {
        this.browserType = BrowserType.CHROME;
        System.out.println("Setting up the chrome driver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("-incognito");
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        capabilities.setBrowserName("chrome");
        capabilities.setPlatform(Platform.MAC);
        String seleniumGridServer = System.getenv("SELENIUM_GRID");
        if (seleniumGridServer == null) {
            seleniumGridServer = "localhost";
        }
        driver = new RemoteWebDriver(new URL("http://" + seleniumGridServer + ":4444/wd/hub"),
                capabilities);
    }

    private void setupFirefoxDriver() {
        this.browserType = BrowserType.FIREFOX;
    }

    public BrowserType getBrowserType() {
        return this.browserType;
    }

    /**
     * Takes a screenshot when the test fails.
     */
    @AfterMethod
    public void takeScreenshotOnFailure(ITestResult result) {
        //Stopped recording
        if (!result.isSuccess()) {
            String screenshotFileName = getScreenShotFileName(this.getClass().getName(),
                    result.getMethod().getMethodName());
            try {
                File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                try {
                    File screenshot = new File(screenshotFileName);
                    log.info("Screenshot file path: " + screenshot.getAbsolutePath());
                    FileUtils.copyFile(file, screenshot);
                } catch (IOException e) {
                    log.error("Exception copying screenshot", e);
                }
            } catch (Exception e) {
                log.error("Error occured taking screenshot.", e);
            }
        }
    }

    public void waitSeconds(int seconds) {
        webDriverUtils.waitSeconds(seconds);
    }

    public void startRecording(String videoName) throws Exception {
        File folderName = new File("target/videos");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;

        Rectangle captureSize = new Rectangle(0, 0, width, height);

        GraphicsConfiguration gc =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                        .getDefaultConfiguration();

        this.screenRecorder = new SeleniumScreenRecorder(gc, captureSize,
                new Format(FormatKeys.MediaTypeKey, MediaType.FILE, FormatKeys.MimeTypeKey, FormatKeys.MIME_AVI),
                new Format(FormatKeys.MediaTypeKey, MediaType.VIDEO, FormatKeys.EncodingKey,
                        ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, CompressorNameKey,
                        ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FormatKeys.FrameRateKey,
                        Rational.valueOf(15), QualityKey, 1.0f, FormatKeys.KeyFrameIntervalKey, 15 * 60),
                new Format(FormatKeys.MediaTypeKey, MediaType.VIDEO, FormatKeys.EncodingKey, "black", FormatKeys.FrameRateKey,
                        Rational.valueOf(30)), null, folderName, videoName);
        this.screenRecorder.start();
    }

    /**
     * Gets an unique name for the screenshot file.
     *
     * @param className
     * @param methodName
     * @return
     * @author sharanyaciddu
     */
    public static String getScreenShotFileName(String className, String methodName) {
        DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
        DateFormat dateFormat1 = new SimpleDateFormat("HHmmssSSS");
        String now = dateFormat1.format(new Date());
        String today = dateFormat2.format(new Date());
        String fileName;
        if (methodName != null)
            fileName = className + "." + methodName;
        else
            fileName = className;
        StringBuffer screenshotFileName = new StringBuffer("target/screenshots/");
        screenshotFileName.append(today).append("/").append(now).append("_");
        screenshotFileName.append(fileName.replaceAll("\\.|\\[|\\]", "_")).append(".png");
        return screenshotFileName.toString();
    }
}
