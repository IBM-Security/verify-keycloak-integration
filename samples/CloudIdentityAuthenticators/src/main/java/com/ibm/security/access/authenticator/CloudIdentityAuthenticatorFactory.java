package com.ibm.security.access.authenticator;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import com.ibm.security.access.util.CloudIdentityLoggingUtilites;
import com.ibm.security.access.util.CloudIdentityUtilities;

public class CloudIdentityAuthenticatorFactory implements AuthenticatorFactory {
	
	public static final String ID = "cloud-identity-authenticator";
	
	private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
			AuthenticationExecutionModel.Requirement.REQUIRED,
			AuthenticationExecutionModel.Requirement.DISABLED
	};
	
	private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<ProviderConfigProperty>();
	
	static {
		ProviderConfigProperty property;
		
		property = new ProviderConfigProperty();
		property.setName(CloudIdentityUtilities.CONFIG_TENANT_FQDN);
		property.setLabel("Tenant Fully Qualified Domain Name");
		property.setType(ProviderConfigProperty.STRING_TYPE);
		property.setHelpText("The FQDN of your Cloud Identity tenant");
		CONFIG_PROPERTIES.add(property);
		
		property = new ProviderConfigProperty();
		property.setName(CloudIdentityUtilities.CONFIG_CLIENT_ID);
		property.setLabel("API Client ID");
		property.setType(ProviderConfigProperty.STRING_TYPE);
		property.setHelpText("Client ID from your Cloud Identity API Client");
		CONFIG_PROPERTIES.add(property);
		
		property = new ProviderConfigProperty();
		property.setName(CloudIdentityUtilities.CONFIG_CLIENT_SECRET);
		property.setLabel("API Client Secret");
		property.setType(ProviderConfigProperty.STRING_TYPE);
		property.setHelpText("Client Secret from your Cloud Identity API Client");
		property.setSecret(true);
		CONFIG_PROPERTIES.add(property);
	}
	
	private Logger logger = Logger.getLogger(CloudIdentityAuthenticatorFactory.class);

	public void close() {
		final String methodName = "close";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}

	public Authenticator create(KeycloakSession session) {
		final String methodName = "create";
		CloudIdentityLoggingUtilites.entry(logger, methodName, session);
		
		CloudIdentityAuthenticator instance = new CloudIdentityAuthenticator();
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, instance);
		return instance;
	}
	
	public List<ProviderConfigProperty> getConfigProperties() {
		final String methodName = "getConfigProperties";
		CloudIdentityLoggingUtilites.entry(logger, methodName);

		CloudIdentityLoggingUtilites.exit(logger, methodName, CONFIG_PROPERTIES);
		return CONFIG_PROPERTIES;
	}
	
	public String getDisplayType() {
		final String methodName = "getDisplayType";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		logger.tracef("%s entry", methodName);
		
		String displayType = "Cloud Identity Authenticator";
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, displayType);
		return displayType;
	}
	
	public String getHelpText() {
		final String methodName = "getHelpText";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		String helpText = "Cloud Identity Authenticator help text";
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, helpText);
		return helpText;
	}

	public String getId() {
		final String methodName = "getId";
		CloudIdentityLoggingUtilites.entry(logger, methodName);

		CloudIdentityLoggingUtilites.exit(logger, methodName, ID);
		return ID;
	}

	public String getReferenceCategory() {
		final String methodName = "getReferenceCategory";
		CloudIdentityLoggingUtilites.entry(logger, methodName);

		String referenceCategory = null;
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, referenceCategory);
		return referenceCategory;
	}

	public Requirement[] getRequirementChoices() {
		final String methodName = "getRequirementChoices";
		CloudIdentityLoggingUtilites.entry(logger, methodName);

		CloudIdentityLoggingUtilites.exit(logger, methodName, REQUIREMENT_CHOICES);
		return REQUIREMENT_CHOICES;
	}
	
	public void init(Scope scope) {
		final String methodName = "init";
		CloudIdentityLoggingUtilites.entry(logger, methodName, scope);
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}

	public boolean isConfigurable() {
		final String methodName = "isConfigurable";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		boolean isConfigurable = true;
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, isConfigurable);
		return isConfigurable;
	}

	public boolean isUserSetupAllowed() {
		final String methodName = "isUserSetupAllowed";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		boolean isUserSetupAllowed = false;
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, isUserSetupAllowed);
		return isUserSetupAllowed;
	}

	public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
		final String methodName = "postInit";
		CloudIdentityLoggingUtilites.entry(logger, methodName, keycloakSessionFactory);
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}

}
