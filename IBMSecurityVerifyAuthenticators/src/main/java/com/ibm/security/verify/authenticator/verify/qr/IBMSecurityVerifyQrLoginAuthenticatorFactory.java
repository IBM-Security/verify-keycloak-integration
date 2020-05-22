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

package com.ibm.security.verify.authenticator.verify.qr;

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

import com.ibm.security.verify.authenticator.rest.IBMSecurityVerifyUtilities;
import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class IBMSecurityVerifyQrLoginAuthenticatorFactory implements AuthenticatorFactory {
	
	public static final String ID = "qr-login-authenticator";
	private static final IBMSecurityVerifyQrLoginAuthenticator SINGLETON = new IBMSecurityVerifyQrLoginAuthenticator();
	
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

	private Logger logger = Logger.getLogger(IBMSecurityVerifyQrLoginAuthenticatorFactory.class);

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
		return "IBM Security Verify QR Login Authenticator";
	}

	public String getHelpText() {
		return "Login by scanning a QR code with your IBM Verify Mobile App";
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
