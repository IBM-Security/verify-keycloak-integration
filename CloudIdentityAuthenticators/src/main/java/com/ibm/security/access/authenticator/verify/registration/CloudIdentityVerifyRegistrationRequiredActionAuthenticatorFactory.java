package com.ibm.security.access.authenticator.verify.registration;

import org.jboss.logging.Logger;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;

import com.ibm.security.access.authenticator.AbstractCloudIdentityAuthenticatorFactory;
import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;

public class CloudIdentityVerifyRegistrationRequiredActionAuthenticatorFactory extends AbstractCloudIdentityAuthenticatorFactory {

	public static final String ID = "ci-verify-reg";
	private static final CloudIdentityVerifyRegistrationRequiredActionAuthenticator SINGLETON = new CloudIdentityVerifyRegistrationRequiredActionAuthenticator();
	
	private static final Requirement[] REQUIREMENT_CHOICES = {
			Requirement.REQUIRED
	};
	
	private Logger logger = Logger.getLogger(CloudIdentityVerifyRegistrationRequiredActionAuthenticatorFactory.class);
	
	public Authenticator create(KeycloakSession session) {
	    final String methodName = "create";
        CloudIdentityLoggingUtilities.entry(logger, methodName, session);
        return SINGLETON;
	}

	public String getDisplayType() {
		final String methodName = "getDisplayType";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		String displayType = "Cloud Identity IBM Verify Registration";
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, displayType);
		return displayType;
	}

	public String getHelpText() {
		final String methodName = "getHelpText";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		String helpText = "Register with IBM Verify. Requires an authenticated user in the current authentication context.";
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, helpText);
		return helpText;
	}

	public String getId() {
		final String methodName = "getId";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, ID);
		return ID;
	}

	public Requirement[] getRequirementChoices() {
		final String methodName = "getRequirementChoices";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, REQUIREMENT_CHOICES);
		return REQUIREMENT_CHOICES;
	}

}
