/*
    Copyright 2020 IBM
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
      http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.ibm.security.verify.authenticator.utils;

import org.jboss.logging.Logger;

public class IBMSecurityVerifyLoggingUtilities {
	
    public static void print(Logger logger, String msg) {
        logger.info(msg);
    }

    public static void error(Logger logger, String msg) {
        logger.error(msg);
    }

    public static void error(Logger logger, String methodName, String errorMsg) {
        logger.errorf("ERROR [%s] %s", methodName, errorMsg);
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
