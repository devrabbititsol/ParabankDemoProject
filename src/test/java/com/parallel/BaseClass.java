package com.parallel;
import java.awt.Toolkit;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

import com.aventstack.extentreports.Status;
import com.configurations.GlobalData;
import com.utilities.ConfigFilesUtility;
import com.utilities.Utilities;

import io.github.bonigarcia.wdm.WebDriverManager;

public abstract class BaseClass {
	
	private String driversPath = System.getProperty("user.dir") + File.separator + "resources" + File.separator;
	private String chromeDriverPath = driversPath + "chromedriver.exe";
	private String geckoFireFoxDriverPath = driversPath + "geckodriver.exe";
	private String iEDriverPath = driversPath + "IEDriverServer.exe";
	
    @AfterMethod
    protected void afterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
        	System.out.println("fialure");
        	ExtTest.getTest().log(Status.FAIL, result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
        	System.out.println("skip");
        	ExtTest.getTest().log(Status.SKIP, "Test skipped " + result.getThrowable());
        } else {
        	System.out.println("pass");
        	ExtTest.getTest().log(Status.PASS, "Test passed");
        }
        
        ExtTest.endTest();        
       
    }
    
    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
    
    public WebDriver getDriver() {
        return driver;
    }
    
    public WebDriver driver;
    @SuppressWarnings("deprecation")
	public WebDriver launchBrowser(String browserName, ConfigFilesUtility configFileObj) {
		if(browserName == null) {
			browserName = "chrome";
		}
		
		GlobalData.primaryInfoData(configFileObj);
		
		if (browserName.equalsIgnoreCase("chrome")) {
			//System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			WebDriverManager.chromedriver().setup();
			
			ChromeDriverService serivce = ChromeDriverService.createDefaultService();
			ChromeOptions options = new ChromeOptions();
			Map<String, Object> prefs=new HashMap<String,Object>();
			if(isWindows()) {
			  ////options.addArguments("user-data-dir=C://Users/"+System.getProperty("user.name")+"/AppData/Local/Google/Chrome/User Data/Default");
			}
			
			//options.setPageLoadStrategy(PageLoadStrategy.NONE);
			//options.addArguments("--user-data-dir", chromeDriverPath);
		//	options.addArguments("--user-data-dir="+chromeDriverPath);
			//1-Allow, 2-Block, 0-default
			options.setAcceptInsecureCerts(true);
			options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
			options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			
			prefs.put("profile.default_content_setting_values.notifications", 1);
			options.setExperimentalOption("prefs",prefs);
			
			
			if (isSolaris() || isUnix()) {

				options.addArguments("start-maximized"); // open Browser in maximized mode
				options.addArguments("disable-infobars"); // disabling infobars
				options.addArguments("--disable-extensions"); // disabling extensions
				options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
				options.addArguments("--no-sandbox"); // Bypass OS security model
				options.addArguments("--headless"); // this line makes run in linux environment with jenkins
				driver = new ChromeDriver(options);
			} else {
				driver = new ChromeDriver(serivce, options);
				Set<Cookie> cookies = driver.manage().getCookies();
			for(Cookie getCookie:cookies)
				driver.manage().addCookie(getCookie);
			}
			
			System.out.println("Chrome Browser is Launched");
		} else if (browserName.equalsIgnoreCase("mozilla")) {
			//System.setProperty("webdriver.gecko.driver", geckoFireFoxDriverPath);
			WebDriverManager.firefoxdriver().setup();
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			//			FirefoxProfile profile = new FirefoxProfile();
			//			//1-Allow, 2-Block, 0-default
			//			profile.setPreference("permissions.default.desktop-notification", 1);
			//			capabilities.setCapability(FirefoxDriver.PROFILE, profile);

			if (isSolaris() || isUnix()) {
				FirefoxBinary binary = new FirefoxBinary();

				capabilities.setCapability("marionette", true);
				FirefoxOptions firefoxOptions = new FirefoxOptions(capabilities);
				firefoxOptions.setBinary(binary);

				firefoxOptions.addArguments("--no-sandbox"); // Bypass OS security model
				firefoxOptions.addArguments("--headless");
				driver = new FirefoxDriver(firefoxOptions);
			} else {
				FirefoxOptions firefoxOptions = new FirefoxOptions(capabilities);
				driver = new FirefoxDriver(firefoxOptions);
				//	driver.manage().window().setPosition(new Point(-2000, 0));
			}

			System.out.println("FireFox Browser is Launched");
		} else if (browserName.equalsIgnoreCase("safari")) {
			// Note : Should AllowRemoteAutomation in safari browser DeveloperMenu
			// Directions -- > launchSafariBrowser --> Preferences --> Advanced Tab -->
			// Show Developer Menu --> Click on DevloperMenu --> Enable
			// AllowRemoteAutomation
			// System.setProperty("webdriver.safari.noinstall", "true");
			driver = new SafariDriver();
			//driver.get("http://www.google.com");
			System.out.println("Safari Browser is Launched");
		} else if (browserName.equalsIgnoreCase("ie")) {
			// To run Internet explorer you should enable below configuration in IE
			// Internet Explorer -> Settings -> Security tab -> Enable Protected mode in all zones

			if (!isWindows()) {
				System.out.println("IE Browser not supported for this OS.");
				return null;
			}
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability("ignoreProtectedModeSettings", true);
			capabilities.setCapability("ignoreZoomSetting", true);
			//capabilities.setCapability("nativeEvents", false);
			capabilities.setCapability("acceptSslCerts", true);
			
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			System.setProperty("webdriver.ie.driver", iEDriverPath);
			driver = new InternetExplorerDriver(capabilities);
			System.out.println("IE Browser is Launched");
		}

		
		driver.get(configFileObj.getProperty("URL"));
		if (isSolaris() || isUnix()) {
			Dimension d = new Dimension(1382, 744);
			// Resize the current window to the given dimension
			driver.manage().window().setSize(d);
		} else {
			java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;

			Dimension d = new Dimension(screenWidth, screenHeight);
			// Resize the current window to the given dimension
			driver.manage().window().setSize(d);
			//driver.manage().window().setPosition(new Point(-2000, 0));
			driver.manage().window().maximize();
		}

		return driver;
	}
    
    private String OS = System.getProperty("os.name").toLowerCase();

	public boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	public boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}


	// =================================================New style Reports========================================================

	//===================== For Report ========================
	JSONArray jsonArray;

	//For Web
	public void setTestcaseName(String browserName, String tescaseName) {

		String chromeURL = "chrome";
		String mozillaURL = "mozilla";
		String ieURL = "ie";
		String safariURL = "safari";
		String finalURL = "";
		
		if(browserName == null) return;
		if (browserName.equalsIgnoreCase("chrome")) {
			finalURL = chromeURL;
		} else if (browserName.equalsIgnoreCase("mozilla")) {
			finalURL = mozillaURL;
		} else if (browserName.equalsIgnoreCase("ie")) {
			finalURL = ieURL;
		} else if (browserName.equalsIgnoreCase("safari")) {
			finalURL = safariURL;
		} else {
			finalURL = "winium";
		}

		ExtTest.startTest(tescaseName,"Test");
	}

	public void printFailureLogAndReport(String className, String data) {
	
		String base64Data = Utilities.captureScreenshot(driver, "Error Screenshot");
		
		ExtTest.getTest(className, "fail", data ).log(Status.FAIL, base64Data);
		
	}

	public void printSuccessLogAndReport(String className, String data) {
		ExtTest.getTest(className, "pass", data ).log(Status.PASS, data);
		
	}

	
	public void printValidateLogAndReport(String className, String data) throws Exception {
		if(data.startsWith("Pass:")) {
			data = data.replace("Pass:", "");
			printSuccessLogAndReport(className, data);
		} else {
			data = data.replace("Fail:", "");
			printFailureLogAndReport(className, data);
		}
	}

	
	


}
