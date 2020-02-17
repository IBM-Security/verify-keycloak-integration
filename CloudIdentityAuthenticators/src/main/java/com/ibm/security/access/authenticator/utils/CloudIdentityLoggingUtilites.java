package com.ibm.security.access.authenticator.utils;

import org.jboss.logging.Logger;

public class CloudIdentityLoggingUtilites {
	
	public static void entry(Logger logger, String methodName, Object... arguments) {
		if (logger.isTraceEnabled()) {
			logger.tracef("%s entry", methodName, arguments);
		}
	}
	
	public static void exit(Logger logger, String methodName) {
		exit(logger, methodName, null);
	}
	
	public static void exit(Logger logger, String methodName, Object returnValue) {
		if (logger.isTraceEnabled()) {
			if (returnValue != null) {
				logger.tracef("%s exit [%s]", methodName, returnValue);
			} else {
				logger.tracef("%s exit", methodName);
			}
		}
	}
}
