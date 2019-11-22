package com.ibm.security.access.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;

import com.ibm.security.access.util.CloudIdentityLoggingUtilites;

public class CloudIdentityVerifyRegistrationRequiredActionAuthenticatorFactory extends AbstractCloudIdentityAuthenticatorFactory {

	private static final String ID = "ci-verify-reg";
	
	private static final Requirement[] REQUIREMENT_CHOICES = {
			Requirement.REQUIRED
	};
	
	private Logger logger = Logger.getLogger(CloudIdentityVerifyRegistrationRequiredActionAuthenticatorFactory.class);
	
	public Authenticator create(KeycloakSession session) {
		final String methodName = "create";
		CloudIdentityLoggingUtilites.entry(logger, methodName, session);
		
		CloudIdentityVerifyRegistrationRequiredActionAuthenticator instance = new CloudIdentityVerifyRegistrationRequiredActionAuthenticator();
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, instance);
		return instance;
	}

	public String getDisplayType() {
		final String methodName = "getDisplayType";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		String displayType = "Cloud Identity IBM Verify Registration Required Action Authenticator";
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, displayType);
		return displayType;
	}

	public String getHelpText() {
		final String methodName = "getHelpText";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		String helpText = "Checks if the user (authenticated from a prior execution in the authentiation flow) already has IBM Verify registered. Prompts the user to register if necessary.";
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, helpText);
		return helpText;
	}

	public String getId() {
		final String methodName = "getId";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, ID);
		return ID;
	}

	public Requirement[] getRequirementChoices() {
		final String methodName = "getRequirementChoices";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, REQUIREMENT_CHOICES);
		return REQUIREMENT_CHOICES;
	}

}
