package com.ibm.security.access.authenticator.verify.push;

import java.util.List;

import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class CloudIdentityPushNotificationLoginAuthenticatorFactory implements AuthenticatorFactory {

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Authenticator create(KeycloakSession arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void init(Scope arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void postInit(KeycloakSessionFactory arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getDisplayType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getReferenceCategory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Requirement[] getRequirementChoices() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isConfigurable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isUserSetupAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getHelpText() {
        // TODO Auto-generated method stub
        return null;
    }

}
