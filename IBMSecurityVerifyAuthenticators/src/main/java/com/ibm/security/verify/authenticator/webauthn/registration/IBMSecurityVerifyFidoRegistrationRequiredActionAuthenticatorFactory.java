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

package com.ibm.security.verify.authenticator.webauthn.registration;

import org.jboss.logging.Logger;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;

import com.ibm.security.verify.authenticator.AbstractIBMSecurityVerifyAuthenticatorFactory;
import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class IBMSecurityVerifyFidoRegistrationRequiredActionAuthenticatorFactory extends AbstractIBMSecurityVerifyAuthenticatorFactory {

	private static final String ID = "fido-reg";
	private static final IBMSecurityVerifyFidoRegistrationRequiredActionAuthenticator SINGLETON = new IBMSecurityVerifyFidoRegistrationRequiredActionAuthenticator();
	
	private static final Requirement[] REQUIREMENT_CHOICES = {
			Requirement.REQUIRED
	};
	
	private Logger logger = Logger.getLogger(IBMSecurityVerifyFidoRegistrationRequiredActionAuthenticatorFactory.class);
	
	public Authenticator create(KeycloakSession session) {
	    final String methodName = "create";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, session);
        return SINGLETON;
	}

	public String getDisplayType() {
		final String methodName = "getDisplayType";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		String displayName = "IBM Security Verify FIDO2 Registration";
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, displayName);
		return displayName;
	}

	public String getHelpText() {
		final String methodName = "getHelpText";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		String helpText = "Register your FIDO2 device. Requires an authenticated user in the current authentication context.";
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, helpText);
		return helpText;
	}

	public String getId() {
		final String methodName = "getId";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, ID);
		return ID;
	}

	public Requirement[] getRequirementChoices() {
		final String methodName = "getRequirementChoices";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, REQUIREMENT_CHOICES);
		return REQUIREMENT_CHOICES;
	}
}
