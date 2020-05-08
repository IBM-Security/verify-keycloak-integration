package com.ibm.security.verify.authenticator.demo;

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

import com.ibm.security.verify.authenticator.otp.IBMSecurityVerifyOtpLoginAuthenticator;
import com.ibm.security.verify.authenticator.rest.IBMSecurityVerifyUtilities;
import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class IBMSecurityVerifyDemoLoginAuthenticatorFactory implements AuthenticatorFactory {

	public static final String ID = "demo-authenticator";
	private static final IBMSecurityVerifyDemoLoginAuthenticator SINGLETON = new IBMSecurityVerifyDemoLoginAuthenticator();

	private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<ProviderConfigProperty>();
	
	private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
			AuthenticationExecutionModel.Requirement.ALTERNATIVE,
			AuthenticationExecutionModel.Requirement.REQUIRED,
	};
	
	static {
	    ProviderConfigProperty property;

        property = new ProviderConfigProperty();
        property.setName(IBMSecurityVerifyUtilities.CONFIG_TENANT_FQDN);
        property.setLabel("Tenant Fully Qualified Domain Name");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("The FQDN of your IBM Security Verify tenant");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(IBMSecurityVerifyUtilities.CONFIG_CLIENT_ID);
        property.setLabel("API Client ID");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Client ID from your IBM Security Verify API Client");
        CONFIG_PROPERTIES.add(property);

        property = new ProviderConfigProperty();
        property.setName(IBMSecurityVerifyUtilities.CONFIG_CLIENT_SECRET);
        property.setLabel("API Client Secret");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Client Secret from your IBM Security Verifyl API Client");
        property.setSecret(true);
        CONFIG_PROPERTIES.add(property);
    }
	
	private Logger logger = Logger.getLogger(IBMSecurityVerifyDemoLoginAuthenticatorFactory.class);

	public void close() {
		final String methodName = "close";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

	public Authenticator create(KeycloakSession session) {
	    final String methodName = "create";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, session);
        return SINGLETON;
	}

	public List<ProviderConfigProperty> getConfigProperties() {
		final String methodName = "getConfigProperties";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, CONFIG_PROPERTIES);
		return CONFIG_PROPERTIES;
	}

	public String getDisplayType() {
		final String methodName = "getDisplayType";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		String displayType = "IBM Security Verify Demo Authenticator";
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, displayType);
		return displayType;
	}

	public String getHelpText() {
		final String methodName = "getHelpText";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		String helpText = "IBM Security Verify Demo Authenticator help text";
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, helpText);
		return helpText;
	}

	public String getId() {
		final String methodName = "getId";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, ID);
		return ID;
	}

	public String getReferenceCategory() {
		final String methodName = "getReferenceCategory";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		String referenceCategory = null;
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, referenceCategory);
		return referenceCategory;
	}

	public Requirement[] getRequirementChoices() {
		final String methodName = "getRequirementChoices";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, REQUIREMENT_CHOICES);
		return REQUIREMENT_CHOICES;
	}

	public void init(Scope config) {
		final String methodName = "init";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, config);
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

	public boolean isConfigurable() {
		final String methodName = "isConfigurable";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		boolean isConfigurable = true;
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, isConfigurable);
		return isConfigurable;
	}

	public boolean isUserSetupAllowed() {
		final String methodName = "isUserSetupAllowed";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		boolean isUserSetupAllowed = false;
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, isUserSetupAllowed);
		return isUserSetupAllowed;
	}

	public void postInit(KeycloakSessionFactory factory) {
		final String methodName = "postInit";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}
}
