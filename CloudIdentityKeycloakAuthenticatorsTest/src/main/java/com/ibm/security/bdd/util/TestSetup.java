package com.ibm.security.bdd.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class TestSetup {

	private static final String TESTDATA_RESOURCE_FILE = "com.ibm.security.bdd.util.TestSetup";

	private static final String BASE_URL_PROPERTY = "server.base.url";
	private static final String SYSTEM_PROPERTY_BASE_URL = "baseURL";

	private static final String ACTIVE_SERVER_PROPERTY = "active.server";
	private static final String ACTIVE_SERVER_DEFAULT = "localhost";
	private static final String SYSTEM_PROPERTY_ACTIVE_SERVER = "activeServer";

	private static final String USERNAME_FOR_PROPERTY = "username.for";
	private static final String PASSWORD_FOR_PROPERTY = "password.for";
	private static final String USERNAME_DEFAULT = "admin";
	private static final String PASSWORD_DEFAULT = "admin";

	private static final String SELENIUM_HUB_URL_PROPERTY = "selenium.hub.url";
	private static final String SYSTEM_PROPERTY_SELENIUM_HUB = "seleniumHub";

	private static final String GRID_BROWSER_NAME_PROPERTY = "grid.browser.name";
	private static final String SYSTEM_PROPERTY_BROWSER_NAME = "browserName";

	private static final String GRID_PLATFORM_PROPERTY = "grid.platform";
	private static final String SYSTEM_PROPERTY_PLATFORM = "platform";

	private static final String GRID_VERSION_PROPERTY = "grid.version";
	private static final String SYSTEM_PROPERTY_VERSION = "version";

	private static final String MAX_TIMEOUT_PROPERTY = "max.timeout";
	private static final String SYSTEM_PROPERTY_MAX_TIMEOUT = "maxTimeout";

	private static final String SEPARATOR = ".";

	/**
	 * Gets the active server type, and returns the default if none is found.
	 *
	 * @return The active server type.
	 */
	public static String getActiveServer() {
		String activeServer = getProperty(SYSTEM_PROPERTY_ACTIVE_SERVER,
				ACTIVE_SERVER_PROPERTY);

		if (activeServer == null) {
			activeServer = ACTIVE_SERVER_DEFAULT;
		}
		return activeServer;
	}

	/**
	 * Gets the server base URL for the eniv, and returns the default for the active server
	 * if none is set in the properties file.
	 *
	 * @return The server base URL
	 */
	public static String getServerBaseURL() {
		String activeServer = getActiveServer();
		String baseURL = getProperty(SYSTEM_PROPERTY_BASE_URL, activeServer + SEPARATOR
				+ BASE_URL_PROPERTY);

		if (baseURL == null || baseURL.isEmpty()) {
			switch (activeServer) {
				default:
            	case "localhost":
            		return "http://vbauto2.rtp.raleigh.ibm.com:8080/";
            		
			}
		}
		return baseURL;
	}

	/**
	 * Gets the Selenium hub URL.
	 *
	 * @return The Selenium hub URL.
	 */
	public static String getHubURL() {
		return getProperty(SYSTEM_PROPERTY_SELENIUM_HUB,
				SELENIUM_HUB_URL_PROPERTY);
	}
	
	/**
	 * Gets the user name for the specified person on the active server.
	 *
	 * @param person
	 *            The person name that the request is for.
	 * @return Returns the user name for the person on the active server, and
	 *         the passed in person name if the value is not set in the properties file.
	 */
	public static String getUserNameForPerson(String person) {
		Properties props = loadTestDataProperties();

		String userName = props.getProperty(getActiveServer() + SEPARATOR
				+ USERNAME_FOR_PROPERTY + SEPARATOR + person);

		if (userName == null) {
			userName = USERNAME_DEFAULT;
		}
		return userName;
	}
	
	/**
	 * Gets the password for the specified person on the active server.
	 *
	 * @param person
	 *            The person name that the request is for.
	 * @return Returns the password for the person on the active server, and
	 *         returns the default if it isn't set.
	 */
	public static String getPasswordForPerson(String person) {
		Properties props = loadTestDataProperties();

		String passwordForUser = props.getProperty(getActiveServer() + SEPARATOR
				+ PASSWORD_FOR_PROPERTY + SEPARATOR + person);

		if (passwordForUser == null) {
			passwordForUser = PASSWORD_DEFAULT;
		}
		return passwordForUser;
	}	
	
	/**
	 * Gets the grid browser name from system properties. If none was passed in
	 * it will use the one from the properties file.
	 *
	 * @return The grid browser name.
	 */
	public static String getGridBrowserName() {
		return getProperty(SYSTEM_PROPERTY_BROWSER_NAME,
				GRID_BROWSER_NAME_PROPERTY);
	}

	/**
	 * Gets the grid platform from system properties. If none was passed in it
	 * will use the one from the properties file.
	 *
	 * @return The grid platform.
	 */
	public static String getGridPlatform() {
		return getProperty(SYSTEM_PROPERTY_PLATFORM, GRID_PLATFORM_PROPERTY);
	}

	/**
	 * Gets the grid version from system properties. If none was passed in it
	 * will use the one from the properties file.
	 *
	 * @return The grid version.
	 */
	public static String getGridVersion() {
		return getProperty(SYSTEM_PROPERTY_VERSION, GRID_VERSION_PROPERTY);
	}

	/**
	 * Gets the max time out property.
	 *
	 * @return The max time out value.
	 */
	public static Integer getMaxTimeOut() {
		String timeoutValue = getProperty(SYSTEM_PROPERTY_MAX_TIMEOUT, MAX_TIMEOUT_PROPERTY);

		if (timeoutValue == null) {
			return null;
		}
		return Integer.parseInt(timeoutValue);
	}

	/**
	 * Returns the system property value if it was set, otherwise returns the
	 * value from the properties file.
	 *
	 * @param systemPropertyEntry
	 *            The name of the system property entry.
	 * @param propertiesFileEntry
	 *            The name of the properties file entry.
	 * @return The property value if returned from the system or the properties
	 *         file.
	 */
	private static String getProperty(String systemPropertyEntry,
			String propertiesFileEntry) {
		String propertyValue = System.getProperty(systemPropertyEntry);

		if (propertyValue == null) {
			Properties props = loadTestDataProperties();
			propertyValue = props.getProperty(propertiesFileEntry);
		}

		return propertyValue;
	}

	/**
	 * Loads the resource bundle to get the properties.
	 *
	 * @return The resource bundle loaded as @Properties.
	 */
	private static Properties loadTestDataProperties() {
		Properties props = new Properties();
		try {
			// Load as a resource bundle, to find it somewhere on the classpath
			ResourceBundle rb = ResourceBundle
					.getBundle(TESTDATA_RESOURCE_FILE);
			Enumeration<String> rbKeys = rb.getKeys();
			while (rbKeys.hasMoreElements()) {
				String prop = rbKeys.nextElement();
				props.put(prop, rb.getString(prop));
			}
		} catch (Throwable t) {
			System.out.println("Error loading resource bundle: "
					+ TESTDATA_RESOURCE_FILE);
			System.out.println("Exception: " + t);
		}
		return props;
	}
}