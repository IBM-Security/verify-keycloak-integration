package com.ibm.security.bdd.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class TestUtils {

	public static final String PATH_SEPARATOR = "/";
	public static final String EASTERN_TIMEZONE = "US/Eastern";

	public static final int ONE_SECOND_IN_MS = 1000;

	private static final int MAX_UI_TIMEOUT_IN_SEC = 60;

	/**
	 * Gets the current active @WebDriver. Use this method to get the active
	 * driver from within this class.
	 * 
	 * @return The current active @WebDriver for the test case.
	 */
	private static WebDriver getDriver() {
		return WebDriverFactory.getDriver();
	}

	/**
	 * Get the maximum time we want to wait before we consider the load a
	 * performance failure.
	 * 
	 * @return The maximum time out value for the UI.
	 */
	public static int getMaxTimeOutValue() {
		Integer maxTimeOut = TestSetup.getMaxTimeOut();
		if (maxTimeOut != null) {
			return maxTimeOut;
		}
		return MAX_UI_TIMEOUT_IN_SEC;
	}

	/**
	 * Returns the status if the element is present on the page.
	 * 
	 * @param by
	 *            The element to look for on the page.
	 * @return true if the element is present on the page, and false otherwise.
	 */
	public static boolean isElementPresent(By by) {
		try {
			getDriver().findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * Assert the element has appeared on the page using the default maximum
	 * time for the UI from {@link #getMaxTimeOutValue() getMaxTimeOutValue}
	 * 
	 * @param element
	 *            The element to check for represented as a @WebElement
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertElementAppears(WebElement element) throws Throwable {
		assertElementAppears(element, getMaxTimeOutValue());
	}

	/**
	 * Assert the element has appeared on the page within the specified wait
	 * time.
	 * 
	 * @param element
	 *            The element to check for represented as a @WebElement
	 * @param timeToWaitInSeconds
	 *            The time to wait for the element to appear in seconds.
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertElementAppears(WebElement element, int timeToWaitInSeconds) throws Throwable {
		for (int second = 0;; second++) {
			try {
				assertTrue(element.isDisplayed());
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds) {
					fail("A timeout occured after " + timeToWaitInSeconds + " second(s): " + error.getMessage());
				}
			} catch (Exception exception) {
				if (second >= timeToWaitInSeconds) {
					fail("A timeout occured after " + timeToWaitInSeconds + " second(s): " + exception.getMessage());
				}
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	/**
	 * Assert the expected text has been found in the element using the default
	 * maximum time for the UI from @link #getMaxTimeOutValue()
	 * getMaxTimeOutValue}
	 * 
	 * @param webElement
	 *            The @WebElement to check.
	 * @param expectedText
	 *            The expected text to check for.
	 * 
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertTextAppears(WebElement webElement, String expectedText) throws Throwable {
		assertTextAppears(webElement, expectedText, getMaxTimeOutValue());
	}

	/**
	 * Assert the expected text has been found in the element.
	 * 
	 * @param webElement
	 *            The @WebElement to check.
	 * @param expectedText
	 *            The expected text to check for.
	 * @param timeToWaitInSeconds
	 *            The time to wait for the element to appear in seconds.
	 * 
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertTextAppears(WebElement webElement, String expectedText, int timeToWaitInSeconds)
			throws Throwable {
		for (int second = 0;; second++) {
			try {
				String browserName = TestSetup.getGridBrowserName();
				if (browserName != null && browserName.equals("MicrosoftEdge")) {
					String TxtWithSpaces = webElement.getText();
					System.out.println("WebElement " + TxtWithSpaces + " = " + "[" + TxtWithSpaces + "]");
					String TxtWithNoSpaces = TxtWithSpaces.trim();
					System.out.println("WebElement " + TxtWithSpaces + " = " + "[" + TxtWithNoSpaces + "]");
					assert expectedText.equals(TxtWithNoSpaces);
					break;
				}
				assertEquals(expectedText, webElement.getText());
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds)
					fail("A timeout occured after " + timeToWaitInSeconds + " second(s): " + error.getMessage());
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	/**
	 * Loop to continue checking for an element using it's ID using the default
	 * maximum time for the UI from @link #getMaxTimeOutValue()
	 * getMaxTimeOutValue}
	 * 
	 * @param id
	 *            The id for identifying the element.
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertElementAppears(String id) throws Throwable {
		assertElementAppears(id, getMaxTimeOutValue());
	}

	/**
	 * Loop to continue checking for an element using it's ID until the
	 * specified timeout occurs.
	 * 
	 * @param id
	 *            The id for identifying the element.
	 * @param timeToWaitInSeconds
	 *            The time to wait for the element to appear in seconds.
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertElementAppears(String id, int timeToWaitInSeconds) throws Throwable {
		for (int second = 0;; second++) {
			try {
				assertTrue(isElementPresent(By.id(id)));
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds) {
					fail("A timeout occured after " + timeToWaitInSeconds + " second(s): " + error.getMessage());
				}
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	/**
	 * Assert the expected text has been found in the element using it's ID
	 * using the default maximum time for the UI from @link
	 * #getMaxTimeOutValue() getMaxTimeOutValue}
	 * 
	 * @param id
	 *            The id for the element.
	 * @param expectedText
	 *            The expected text to check for.
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertTextAppearsWithin(String id, String expectedText) throws Throwable {
		assertTextAppearsWithin(id, expectedText, getMaxTimeOutValue());
	}

	/**
	 * Assert the expected text has been found in the element using it's ID
	 * until the specified timeout occurs.
	 * 
	 * @param id
	 *            The id for the element.
	 * @param expectedText
	 *            The expected text to check for.
	 * @param timeToWaitInSeconds
	 *            The time to wait for the element to appear in seconds.
	 * 
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertTextAppearsWithin(String id, String expectedText, int timeToWaitInSeconds)
			throws Throwable {
		for (int second = 0;; second++) {
			try {
				assertEquals(expectedText, getDriver().findElement(By.id(id)).getText());
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds)
					fail("timeout after " + second + " second(s): " + error.getMessage());
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	/**
	 * Loop to check for certain text appears within an element using it's id
	 * within the specified time
	 * 
	 * @param id
	 *            The id for the element.
	 * @param containingText
	 *            The expected text to check if it exists.
	 * @param timeToWaitInSeconds
	 *            The time to wait for the element to appear in seconds.
	 *
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertIdTextContains(String id, String containingText, int timeToWaitInSeconds)
			throws Throwable {
		for (int second = 0;; second++) {
			try {
				assertThat(getDriver().findElement(By.id(id)).getText(),
						org.hamcrest.CoreMatchers.containsString(containingText));
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds)
					fail("timeout after " + second + " second(s): " + error.getMessage());
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	public static void assertElementIdNotAppearsWithin(String id) throws Throwable {
		try {
			assertFalse(isElementPresent(By.id(id)));
		} catch (Error error) {
			fail("Element appeared on the screen" + error.getMessage());
		}
	}

	/**
	 * Loop to continue checking for an element using cssSelector until the
	 * timeout occurs.
	 * 
	 * @param cssSelector
	 *            The cssSelector for identifying the element.
	 * @param timeToWaitInSeconds
	 *            The time to wait for the element to appear in seconds.
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void cssElementAppearsWithin(String cssSelector, int timeToWaitInSeconds) throws Throwable {
		for (int second = 0;; second++) {
			try {
				assertTrue(isElementPresent(By.cssSelector(cssSelector)));
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds) {
					fail("timeout after " + second + " second(s): " + cssSelector + error.getMessage());
				}
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	/**
	 * Loop to check for certain text appears within an element using
	 * cssSelector.
	 * 
	 * @param expectedText
	 *            The expected text to check for.
	 * @param cssSelector
	 *            The cssSelector for the element.
	 * @param timeToWaitInSeconds
	 *            The time to wait for the element to appear in seconds.
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void cssTextAppearsWithin(String expectedText, String cssSelector, int timeToWaitInSeconds)
			throws Throwable {
		for (int second = 0;; second++) {
			try {
				String browserName = TestSetup.getGridBrowserName();
				if (browserName != null && browserName.equals("MicrosoftEdge")) {
					String TxtWithSpaces = getDriver().findElement(By.cssSelector(cssSelector)).getText();
					System.out.println("WebElement " + TxtWithSpaces + " = " + "[" + TxtWithSpaces + "]");
					String TxtWithNoSpaces = TxtWithSpaces.trim();
					System.out.println("WebElement " + TxtWithSpaces + " = " + "[" + TxtWithNoSpaces + "]");
					assert expectedText.equals(TxtWithNoSpaces);
					break;
				}
				assertEquals(expectedText, getDriver().findElement(By.cssSelector(cssSelector)).getText());
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds)
					fail("timeout after " + second + " second(s): " + error.getMessage());
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	public static void cssValueAppearsWithin(String expectedValue, String cssSelector, int timeToWaitInSeconds)
			throws Throwable {
		for (int second = 0;; second++) {
			try {
				assertEquals(expectedValue, getDriver().findElement(By.cssSelector(cssSelector)).getAttribute("value"));
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds)
					fail("timeout after " + second + " second(s): " + error.getMessage());
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	public static void valueAppearsWithin(String expectedValue, WebElement element, int timeToWaitInSeconds)
			throws Throwable {
		for (int second = 0;; second++) {
			try {
				assertEquals(expectedValue, element.getAttribute("value"));
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds)
					fail("timeout after " + second + " second(s): " + error.getMessage());
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	public static void cssAssertElementNotAppearsWithin(String cssSelector, int timeToWaitInSeconds) throws Throwable {
		for (int second = 0;; second++) {
			try {
				assertFalse(isElementPresent(By.cssSelector(cssSelector)));
				break;
			} catch (Error error) {
				if (second >= timeToWaitInSeconds) {
					fail("timeout after " + second + " second(s). The css selector was: " + cssSelector
							+ " , the error message was: " + error.getMessage() + " , and the stack trace is: "
							+ error.getStackTrace());
				}
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}

	/**
	 * Check using cssSelector to make sure an element hasn't appeared on the
	 * page.
	 * 
	 * @param xPath
	 *            The xPath to the element.
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertCssElementNotPresent(String cssSelector) throws Throwable {
		try {
			assertFalse(isElementPresent(By.cssSelector(cssSelector)));
		} catch (Throwable error) {
			fail("Element appeared on the screen " + error.getMessage());
		}
	}
	
	
	/**
	 * Assert the expected text has been found in the dropdown element using the default
	 * maximum time for the UI from @link #getMaxTimeOutValue()
	 * getMaxTimeOutValue}
	 * 
	 * @param webElement
	 *            The @WebElement to check.
	 * @param expectedText
	 *            The expected text to check for in the dropdown.
	 * 
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertTextAppearsInDropDown(WebElement elements, String expectedText) throws Throwable {
		assertTextAppearsInDropDown(elements, expectedText, getMaxTimeOutValue());
	}
	
	/**
	 * Assert the expected text has been found in the dropdown element.
	 * 
	 * @param webElement
	 *            The @WebElement to check.
	 * @param expectedText
	 *            The expected text to check for in the downdown.
	 * @param timeToWaitInSeconds
	 *            The time to wait for the element to appear in seconds.
	 * 
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertTextAppearsInDropDown(WebElement elements, String expectedText, int timeToWaitInSeconds)
			throws Throwable {
		
		for (int second = 0;; second++) {
			try {
				
				Select select = new Select(elements);
				List<WebElement> listOfElements = select.getOptions();
				boolean found = false;
				
				for (WebElement ele:listOfElements) {
					if (expectedText.equals(ele.getText())) {
						found = true;
						break;
					}
				}
	
				assertTrue(found);
				break;
				
			} catch (Error error) {
				if (second >= timeToWaitInSeconds)
					fail("A timeout occured after " + timeToWaitInSeconds + " second(s): " + error.getMessage());
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}
	
	/**
	 * Assert the expected text was not found in the dropdown element using the default
	 * maximum time for the UI from @link #getMaxTimeOutValue()
	 * getMaxTimeOutValue}
	 * 
	 * @param webElement
	 *            The @WebElement to check.
	 * @param expectedText
	 *            The expected text to check for in the dropdown.
	 * 
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertTextNotAppearsInDropDown(WebElement elements, String expectedText) throws Throwable {
		assertTextNotAppearsInDropDown(elements, expectedText, getMaxTimeOutValue());
	}
	
	/**
	 * Assert the expected text has been found in the dropdown element.
	 * 
	 * @param webElement
	 *            The @WebElement to check.
	 * @param expectedText
	 *            The expected text to check for in the downdown.
	 * @param timeToWaitInSeconds
	 *            The time to wait for the element to appear in seconds.
	 * 
	 * @throws Throwable
	 *             Throws the error that the test case fails with.
	 */
	public static void assertTextNotAppearsInDropDown(WebElement elements, String expectedText, int timeToWaitInSeconds)
			throws Throwable {
		
		for (int second = 0;; second++) {
			try {
				
				Select select = new Select(elements);
				List<WebElement> listOfElements = select.getOptions();
				boolean found = false;
				
				for (WebElement ele:listOfElements) {
					if (expectedText.equals(ele.getText())) {
						found = true;
						break;
					}
				}
				
				assertFalse(found);
				break;
				
			} catch (Error error) {
				if (second >= timeToWaitInSeconds)
					fail("A timeout occured after " + timeToWaitInSeconds + " second(s): " + error.getMessage());
			}
			Thread.sleep(ONE_SECOND_IN_MS);
		}
	}
	

	/**
	 * Retrieve the unique identifier from the element ID for custom attributes
	 * 
	 * @param elementId
	 *            The element ID to be parsed
	 * @return The unique identifier
	 */
	public static String getRowIdentifier(String elementId) {
		int dot = elementId.indexOf(".");
		String temp = elementId.substring(dot, elementId.length());
		int dash = temp.indexOf("-");

		String unique = temp.substring(0, dash);
		return unique;
	}

	/**
	 * This method will send the keys to the @WebElement, and then verify they
	 * have been sent. If not, it will continue to retry for the number of
	 * retries specified, with a default of three.
	 * 
	 * @param element
	 *            The element to do the sendkeys too.
	 * @param keys
	 *            The keys to send.
	 * @throws Throwable
	 *             The exception thrown if something goes wrong.
	 */
	public static void verifiedSendKeys(WebElement element, String keys) throws Throwable {
		int maxRetries = 3;
		verifiedSendKeys(element, keys, maxRetries);
		// Wait for debounce
		Thread.sleep(ONE_SECOND_IN_MS);

	}

	/**
	 * This method will send the keys to the @WebElement, and then verify they
	 * have been sent. If not, it will continue to retry for the number of
	 * retries specified, with a default of three.
	 * 
	 * @param element
	 *            The element to do the sendkeys too.
	 * @param keys
	 *            The keys to send.
	 * @param retries
	 *            The number of times to retry.
	 * @throws Throwable
	 *             The exception thrown if something goes wrong.
	 */
	public static void verifiedSendKeys(WebElement element, String keys, int retries) throws Throwable {
		for (int tries = 0;; tries++) {
			try {
				element.clear();
				element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
				element.click();
				element.sendKeys(keys);
				String elementText = element.getAttribute("value");
				assertTrue(elementText.equals(keys));
				break;
			} catch (Throwable throwable) {
				if (tries > retries) {
					fail("A failure occured after " + tries + " tries: " + throwable.getMessage());
				}
			}
			Thread.sleep(500);
		}
	}

	/**
	 * This method to allow the view move to the target UI element
	 * 
	 * @param webElement
	 *            The @WebElement to check.
	 */
	public static void moveToElement(WebElement element) throws Throwable {
		try {
			Actions a = new Actions(WebDriverFactory.getDriver());
			a.moveToElement(element).perform();
			Thread.sleep(200);
		} catch (Throwable t) {
			fail("Unable to move to the target element." + t.getMessage());
		}
	}

	/**
	 * A method to check the disabled attribute of a @WebElement
	 * 
	 * @param webElement
	 *            The @WebElement to check.
	 * @param value
	 *            The expected value of the disabled attribute.
	 */
	public static void buttonDisabled(WebElement webElement, String value) {
		String buttonDisabledValue = webElement.getAttribute("disabled");
		try {
			assertTrue(buttonDisabledValue.equals(value));
		} catch (Throwable t) {
			fail("The element was having its disabled attribute checked and something went wrong." + t.getMessage());
		}
	}

	/**
	 * This method wraps getting the current time from the Eastern Time Zone
	 * formatted in the way the caller specifies.
	 * 
	 * @param dateFormatting
	 *            The formatting type from @SimpleDateFormat
	 * @return The time for the eastern time zone in the formatting requested.
	 */
	public static String getEasternTime(String dateFormatting) {
		DateFormat dateformat = new SimpleDateFormat(dateFormatting);
		dateformat.setTimeZone(TimeZone.getTimeZone(EASTERN_TIMEZONE));
		return dateformat.format(new Date());
	}
}