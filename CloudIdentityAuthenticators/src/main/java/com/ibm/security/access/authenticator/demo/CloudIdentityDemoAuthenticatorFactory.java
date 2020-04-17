package com.ibm.security.access.authenticator.demo;

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

import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;
import com.ibm.security.access.authenticator.otp.CloudIdentityOTPAuthenticator;
import com.ibm.security.access.authenticator.rest.CloudIdentityUtilities;

public class CloudIdentityDemoAuthenticatorFactory implements AuthenticatorFactory {

	public static final String ID = "ci-demo-authenticator";
	private static final CloudIdentityDemoAuthenticator SINGLETON = new CloudIdentityDemoAuthenticator();

	private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<ProviderConfigProperty>();
	
	private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
			AuthenticationExecutionModel.Requirement.ALTERNATIVE,
			AuthenticationExecutionModel.Requirement.REQUIRED,
	};
	
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
	
	private Logger logger = Logger.getLogger(CloudIdentityDemoAuthenticatorFactory.class);

	public void close() {
		final String methodName = "close";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public Authenticator create(KeycloakSession session) {
	    final String methodName = "create";
        CloudIdentityLoggingUtilities.entry(logger, methodName, session);
        return SINGLETON;
	}

	public List<ProviderConfigProperty> getConfigProperties() {
		final String methodName = "getConfigProperties";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, CONFIG_PROPERTIES);
		return CONFIG_PROPERTIES;
	}

	public String getDisplayType() {
		final String methodName = "getDisplayType";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		String displayType = "Cloud Identity Demo Authenticator";
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, displayType);
		return displayType;
	}

	public String getHelpText() {
		final String methodName = "getHelpText";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		String helpText = "Cloud Identity Demo Authenticator help text";
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, helpText);
		return helpText;
	}

	public String getId() {
		final String methodName = "getId";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, ID);
		return ID;
	}

	public String getReferenceCategory() {
		final String methodName = "getReferenceCategory";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		String referenceCategory = null;
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, referenceCategory);
		return referenceCategory;
	}

	public Requirement[] getRequirementChoices() {
		final String methodName = "getRequirementChoices";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, REQUIREMENT_CHOICES);
		return REQUIREMENT_CHOICES;
	}

	public void init(Scope config) {
		final String methodName = "init";
		CloudIdentityLoggingUtilities.entry(logger, methodName, config);
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public boolean isConfigurable() {
		final String methodName = "isConfigurable";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		boolean isConfigurable = true;
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, isConfigurable);
		return isConfigurable;
	}

	public boolean isUserSetupAllowed() {
		final String methodName = "isUserSetupAllowed";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		boolean isUserSetupAllowed = false;
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, isUserSetupAllowed);
		return isUserSetupAllowed;
	}

	public void postInit(KeycloakSessionFactory factory) {
		final String methodName = "postInit";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}
}
