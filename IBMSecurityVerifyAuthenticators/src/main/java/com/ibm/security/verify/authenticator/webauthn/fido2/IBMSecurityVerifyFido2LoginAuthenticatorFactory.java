package com.ibm.security.verify.authenticator.webauthn.fido2;

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

public class IBMSecurityVerifyFido2LoginAuthenticatorFactory implements AuthenticatorFactory {
    
    public static final String ID = "fido2-login-authenticator";
    private static final IBMSecurityVerifyFido2LoginAuthenticator SINGLETON = new IBMSecurityVerifyFido2LoginAuthenticator();
    
    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<ProviderConfigProperty>();
    
    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE
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

    private Logger logger = Logger.getLogger(IBMSecurityVerifyOtpLoginAuthenticator.class);

    public void close() {
        // no-op
    }

    public Authenticator create(KeycloakSession session) {
        final String methodName = "create";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, session);
        return SINGLETON;
    }

    public List<ProviderConfigProperty> getConfigProperties() {
        // TODO Auto-generated method stub
        return CONFIG_PROPERTIES;
    }

    public String getDisplayType() {
        return "IBM Security Verify FIDO2 Login Authenticator";
    }

    public String getHelpText() {
        return "Login using your FIDO2 device";
    }

    public String getId() {
        return ID;
    }

    public String getReferenceCategory() {
        return null;
    }

    public Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    public void init(Scope config) {
        // no-op
    }

    public boolean isConfigurable() {
        return true;
    }

    public boolean isUserSetupAllowed() {
        return false;
    }

    public void postInit(KeycloakSessionFactory factory) {
        // no-op
    }

}
