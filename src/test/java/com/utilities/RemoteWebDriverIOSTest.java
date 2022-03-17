package com.utilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;

public class RemoteWebDriverIOSTest {
  public static void main(String[] args) throws Exception {
    String kobitonServerUrl = "https://Suresh.Sakamuri:1669cc35-1466-4984-976f-9b323693722d@api.kobiton.com/wd/hub";
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability("sessionName", "Automation test session on Android web");
    capabilities.setCapability("sessionDescription", "This is example Android web testing");
    capabilities.setCapability("deviceOrientation", "portrait");
    capabilities.setCapability("captureScreenshots", true);
    capabilities.setCapability("browserName", "chrome");
    capabilities.setCapability("deviceGroup", "KOBITON");
    capabilities.setCapability("deviceName", "Galaxy A20s");
    capabilities.setCapability("platformVersion", "10");
    capabilities.setCapability("platformName", "Android");
 
    RemoteWebDriver driver = new RemoteWebDriver(new URL(kobitonServerUrl), capabilities);
    /**
    * Prints Kobiton Session Id
    */
 
    String kobitonSessionId = driver.getCapabilities().getCapability("kobitonSessionId").toString();
    System.out.println("Your test session is: https://portal.kobiton.com/sessions/" + kobitonSessionId);
    
    driver.get("http://appium.io/");
 
    /**
    * Goes to Appium page and prints URL & title
    */
    System.out.println("Current URL is: " + driver.getCurrentUrl());
    System.out.println("Title of page is: " + driver.getTitle());
 
    driver.quit();
  }
}