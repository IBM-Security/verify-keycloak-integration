/*
 * Copyright 2020 IBM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.ibm.security.verify.authenticator;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import com.ibm.security.verify.authenticator.rest.IBMSecurityVerifyUtilities;
import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

abstract public class AbstractIBMSecurityVerifyAuthenticatorFactory implements AuthenticatorFactory {
	
	private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<ProviderConfigProperty>();
	
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
	
	private Logger logger = Logger.getLogger(AbstractIBMSecurityVerifyAuthenticatorFactory.class);
	
	public void close() {
		// no-op
		final String methodName = "close";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}
	
	abstract public Authenticator create(KeycloakSession session);
	
	public List<ProviderConfigProperty> getConfigProperties() {
		final String methodName = "getConfigProperties";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, CONFIG_PROPERTIES);
		return CONFIG_PROPERTIES;
	}
	
	abstract public String getDisplayType();
	
	abstract public String getHelpText();
	
	abstract public String getId();
	
	public String getReferenceCategory() {
		final String methodName = "getReferenceCategory";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		String referenceCategory = null;
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, referenceCategory);
		return referenceCategory;
	}
	
	abstract public Requirement[] getRequirementChoices();
	
	public void init(Scope config) {
		// no-op
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
		// no-op
		final String methodName = "postInit";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, factory);
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

}
