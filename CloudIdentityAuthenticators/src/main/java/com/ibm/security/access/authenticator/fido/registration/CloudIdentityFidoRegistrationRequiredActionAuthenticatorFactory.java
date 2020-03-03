package com.ibm.security.access.authenticator.fido.registration;

import org.jboss.logging.Logger;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;

import com.ibm.security.access.authenticator.AbstractCloudIdentityAuthenticatorFactory;
import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;

public class CloudIdentityFidoRegistrationRequiredActionAuthenticatorFactory extends AbstractCloudIdentityAuthenticatorFactory {

	private static final String ID = "ci-fido-reg";
	
	private static final Requirement[] REQUIREMENT_CHOICES = {
			Requirement.REQUIRED
	};
	
	private Logger logger = Logger.getLogger(CloudIdentityFidoRegistrationRequiredActionAuthenticatorFactory.class);
	
	public Authenticator create(KeycloakSession session) {
		final String methodName = "create";
		CloudIdentityLoggingUtilities.entry(logger, methodName, session);
		
		CloudIdentityFidoRegistrationRequiredActionAuthenticator instance = new CloudIdentityFidoRegistrationRequiredActionAuthenticator();
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, instance);
		return instance;
	}

	public String getDisplayType() {
		final String methodName = "getDisplayType";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		String displayName = "Cloud Identity FIDO Registration Required Action Authenticator";
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, displayName);
		return displayName;
	}

	public String getHelpText() {
		final String methodName = "getHelpText";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		String helpText = "Checks if the user (authenticated from a prior execution in the authentiation flow) already has a FIDO device registered. Prompts the user to register if necessary.";
		
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
