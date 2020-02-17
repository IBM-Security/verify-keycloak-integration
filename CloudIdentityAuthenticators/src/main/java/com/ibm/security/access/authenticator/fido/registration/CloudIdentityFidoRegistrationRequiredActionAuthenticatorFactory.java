package com.ibm.security.access.authenticator.fido.registration;

import org.jboss.logging.Logger;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;

import com.ibm.security.access.authenticator.AbstractCloudIdentityAuthenticatorFactory;
import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilites;

public class CloudIdentityFidoRegistrationRequiredActionAuthenticatorFactory extends AbstractCloudIdentityAuthenticatorFactory {

	private static final String ID = "ci-fido-reg";
	
	private static final Requirement[] REQUIREMENT_CHOICES = {
			Requirement.REQUIRED
	};
	
	private Logger logger = Logger.getLogger(CloudIdentityFidoRegistrationRequiredActionAuthenticatorFactory.class);
	
	public Authenticator create(KeycloakSession session) {
		final String methodName = "create";
		CloudIdentityLoggingUtilites.entry(logger, methodName, session);
		
		CloudIdentityFidoRegistrationRequiredActionAuthenticator instance = new CloudIdentityFidoRegistrationRequiredActionAuthenticator();
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, instance);
		return instance;
	}

	public String getDisplayType() {
		final String methodName = "getDisplayType";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		String displayName = "Cloud Identity FIDO Registration Required Action Authenticator";
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, displayName);
		return displayName;
	}

	public String getHelpText() {
		final String methodName = "getHelpText";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		String helpText = "Checks if the user (authenticated from a prior execution in the authentiation flow) already has a FIDO device registered. Prompts the user to register if necessary.";
		
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
