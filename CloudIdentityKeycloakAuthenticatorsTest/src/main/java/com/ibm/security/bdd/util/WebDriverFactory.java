package com.ibm.security.bdd.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openqa.selenium.Platform;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class WebDriverFactory {

	public static final String CURRENT_VERSION = "current";

	private static String browserLanguage = "en-US";
	private static WebDriver driver = null;
	
	public static void setLanguage(String languageCode) throws Exception {
		if (driver == null) {
			if (TestSetup.getGridBrowserName().equals("chrome") || TestSetup.getGridBrowserName().equals("firefox")) {
				browserLanguage = languageCode;
				System.out.println("The browser langauge has been set to: " + languageCode);	
			} else {
				throw new Exception ("Language support is only available in chrome and firefox.");
			}
		} else {
			throw new Exception ("Unable to change the language code, as the web driver has already been started.");
		}
	}

	public synchronized static WebDriver getDriver() {
		if (driver == null) {
			int loop = 0;
			int retryCount = 3;
			while (loop <= retryCount) {
				try {
					return createDriver();
				} catch (Exception exception) {
					System.out.println ("The session was unable to be created. Trying again.");
					if (loop > retryCount) {
						throw exception;
					}
				}
				loop++;
			}
			return null;
		} else {
			return driver;
		}
	}

	private synchronized static WebDriver createDriver() {
		if (driver == null) {
			DesiredCapabilities capability;
			String browserName = TestSetup.getGridBrowserName();
			String platform = TestSetup.getGridPlatform();
			String version = TestSetup.getGridVersion();

			LoggingPreferences loggingPrefs = new LoggingPreferences();
			loggingPrefs.enable(LogType.BROWSER, Level.ALL);
			
			switch (browserName) {
				default:
				case "firefox":
					capability = DesiredCapabilities.firefox();
					capability.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
					capability.setCapability(CapabilityType.LOGGING_PREFS, loggingPrefs);
					capability.setCapability(FirefoxDriver.MARIONETTE, true);
	
					FirefoxProfile profile = new FirefoxProfile();
					profile.setPreference("intl.accept_languages", browserLanguage);
					capability.setCapability(FirefoxDriver.PROFILE, profile);
					break;
				case "chrome":
					capability = DesiredCapabilities.chrome();
					capability.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
					capability.setCapability(CapabilityType.LOGGING_PREFS, loggingPrefs);
					capability.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR,
							UnexpectedAlertBehaviour.ACCEPT);
	
					ChromeOptions options = new ChromeOptions();
					options.addArguments("--lang=" + browserLanguage);
					if (platform.equals("LINUX")) {
						Map<String, Object> experimentalOptions = new HashMap<String, Object>();
						experimentalOptions.put("intl.accept_languages", browserLanguage.replace('_', '-'));
						options.setExperimentalOption("prefs", experimentalOptions);
					}					
					capability.setCapability(ChromeOptions.CAPABILITY, options);
					break;
				case "ie":
				case "internetexplorer":
				case "internet explorer":
					browserName = "internet explorer";
					capability = DesiredCapabilities.internetExplorer();
					capability.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
					capability.setCapability("ie.ensureCleanSession", true);
					capability.setCapability("nativeEvents", false);
					capability.setCapability("unexpectedAlertBehavior", "accept");
					capability.setCapability("ignoreProtectedModeSettings", true);
					capability.setCapability("disable-popup-blocking", true);
					capability.setCapability("enablePersistantHover", true);
					capability.setCapability("ignoreZoomSetting", true);
					capability.setCapability("INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS", true);
					capability.setCapability("ie.fileUploadDialogTimeout", "3000");
					capability.setCapability("ie.usePerProcessProxy", true);
					capability.setCapability("ie.setProxyByServer", true);
					
					break;
				case "edge":
				case "MicrosoftEdge":
					browserName = "MicrosoftEdge";
					capability = DesiredCapabilities.edge();
					break;
			}

			capability.setBrowserName(browserName);
			capability.setPlatform(getPlatform(platform));
			if (version != null && !version.equals(CURRENT_VERSION)) {
				capability.setVersion(version);
			}

			try {
				driver = new RemoteWebDriver(new URL(TestSetup.getHubURL()), capability);
			} catch (MalformedURLException exception) {
				exception.printStackTrace();
			}
		}
		return driver;
	}

	public synchronized static void clearDriver() {
		try {
			if (TestSetup.getGridBrowserName().equals("firefox")) {
				// Seems to be needed on the new gecko driver
				driver.close();
			}
			// This will likely run unless close really has issues
			driver.quit();
		} catch (Exception exception) {
			// Let's try once more to see if we can't get rid of the dangling
			// sessions issue.
			try {
				driver.quit();
			} catch (Exception innerException) {
				innerException.printStackTrace();
			}
			exception.printStackTrace();
		} finally {
			// No matter how this ends, it needs to go to null so the next
			// scenario(s) don't fail.
			driver = null;
		}
	}

	private static Platform getPlatform(String platformName) {
		switch (platformName) {
			// Windows choices
			case "WIN10":
				return Platform.WIN10;
			case "WIN8_1":
				return Platform.WIN8_1;
			case "WINDOWS":
				return Platform.WINDOWS;
			case "LINUX":
				return Platform.LINUX;
			default:
				return Platform.ANY;
		}
	}
}