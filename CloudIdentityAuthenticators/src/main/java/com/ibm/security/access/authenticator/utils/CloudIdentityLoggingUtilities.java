package com.ibm.security.access.authenticator.utils;

import org.jboss.logging.Logger;

public class CloudIdentityLoggingUtilities {
	
    public static void print(Logger logger, String msg) {
        logger.info(msg);
    }

    public static void error(Logger logger, String msg) {
        logger.error(msg);
    }

    public static void error(Logger logger, String methodName, String errorMsg) {
        logger.errorf("ERROR [%s]", methodName, errorMsg);
    }

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
