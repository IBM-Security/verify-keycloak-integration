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

package com.ibm.security.verify.authenticator.webauthn.registration;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import com.ibm.security.verify.authenticator.rest.FormUtilities;
import com.ibm.security.verify.authenticator.rest.IBMSecurityVerifyUtilities;
import com.ibm.security.verify.authenticator.rest.FidoUtilities;
import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class IBMSecurityVerifyFidoRegistrationRequiredActionAuthenticator implements Authenticator {
    private static final String FIDO_REGISTRATION_TEMPLATE = "fido-registration.ftl";

	private static final String ACTION_PARAM = "action";
	private static final String REGISTER_ACTION = "register";

	private static final String FIDO_REG_INIT_ATTR_NAME = "fidoRegInit";
	private static final String FIDO_AUTHN_INIT_ATTR_NAME = "fidoAuthnInit";

	public static final String FIDO_REG_VERIFIED = "fido.registration.verified";

	private Logger logger = Logger.getLogger(IBMSecurityVerifyFidoRegistrationRequiredActionAuthenticator.class);

	public void action(AuthenticationFlowContext context) {
		final String methodName = "action";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);
		
		MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
		String action= formParams.getFirst(ACTION_PARAM);
		if (REGISTER_ACTION.equals(action)) {
			boolean result = FidoUtilities.completeFidoRegistration(context);
			if (result) {
				context.form().addSuccess(new FormMessage("fidoRegistrationVerified"));
                context.resetFlow();
                return;
			} else {
			    context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgDeviceRegistrationFailed")));
			}
		} else {
			// No-op
		    context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgDeviceRegistrationFailed")));
		}
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

	public void authenticate(AuthenticationFlowContext context) {
		final String methodName = "authenticate";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);

		UserModel user = context.getUser();
		if (user != null) {
			// User is associated with the context
			String userId = IBMSecurityVerifyUtilities.getCIUserId(context, user);
			if (userId == null) {
				// User does not yet have a CI user record associated with them. Let's create it now
				boolean createdShadowUserSuccessfully = IBMSecurityVerifyUtilities.createCIShadowUser(context, user);
				if (createdShadowUserSuccessfully) {
					userId = IBMSecurityVerifyUtilities.getCIUserId(context, user);
				} else {
				    context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgUserRegistrationFailed")));
                    return;
				}
			}
			if (userId != null) {
				// User has a CI User ID
				boolean isRegistered = FidoUtilities.doesUserHaveFidoRegistered(context, userId);
				if (!isRegistered) {
					// User does not have a FIDO authn device registered
					String fidoInitRegResp = FidoUtilities.initiateFidoRegistration(context, userId);
					Response challenge = context.form()
							.setAttribute(FIDO_REG_INIT_ATTR_NAME, fidoInitRegResp)
							.setAttribute(FIDO_AUTHN_INIT_ATTR_NAME, "{}")
							.createForm(FIDO_REGISTRATION_TEMPLATE);
					context.challenge(challenge);

					IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
					return;
				} else {
				    context.form().addSuccess(new FormMessage("fidoRegistrationVerified"));
				    context.getSession().setAttribute(FIDO_REG_VERIFIED, true);
                    context.resetFlow();
                    return;
				}
			}
		} else {
		    context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgMissingEmailAndPhoneNumber")));
            return;
		}
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

	public void close() {
		final String methodName = "close";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		// no-op
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "configuredFor";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, session, realm, user);
		
		boolean configuredFor = true;
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, configuredFor);
		return configuredFor;
	}

	public boolean requiresUser() {
		final String methodName = "requiresUser";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		
		boolean requiresUser = true;
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, requiresUser);
		return requiresUser;
	}

	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "setRequiredActions";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, session, realm, user);
		// no-op
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

}
