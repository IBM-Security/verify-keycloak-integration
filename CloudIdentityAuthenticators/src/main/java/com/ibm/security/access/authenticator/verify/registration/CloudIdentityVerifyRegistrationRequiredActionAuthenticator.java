package com.ibm.security.access.authenticator.verify.registration;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;
import com.ibm.security.access.authenticator.rest.CloudIdentityUtilities;
import com.ibm.security.access.authenticator.rest.QrUtilities;

public class CloudIdentityVerifyRegistrationRequiredActionAuthenticator implements Authenticator {

    private static final String VERIFY_REGISTRATION_TEMPLATE = "verify-registration.ftl";
    private static final String QR_CODE_ATTR_NAME = "qrCode";
    private static final String VERIFY_REGISTRATION_FRIENDLY_NAME = "Keycloak SSO";

	private static final String ACTION_PARAM = "action";
	private static final String REGISTER_ACTION = "register";
	
	private Logger logger = Logger.getLogger(CloudIdentityVerifyRegistrationRequiredActionAuthenticator.class);
	
	public void action(AuthenticationFlowContext context) {
		final String methodName = "action";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
		MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
		String action= formParams.getFirst(ACTION_PARAM);
		if (REGISTER_ACTION.equals(action)) {
			// User has not yet cancelled the registration attempt. Let's poll for registration status
			initiateAndPoll(context);
		} else {
			// Bypassing the registration phase since it's optional
			context.success();
		}
		
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public void authenticate(AuthenticationFlowContext context) {
		final String methodName = "authenticate";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
//		boolean hasPromptedRegistration = CloudIdentityUtilities.hasPromptedPasswordlessRegistration(context);
//		if (hasPromptedRegistration) {
//			context.success();
//			
//			CloudIdentityLoggingUtilities.exit(logger, methodName);
//			return;
//		}
		initiateAndPoll(context);
		CloudIdentityUtilities.setPromptedPasswordlessRegistration(context);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}
	
	private void initiateAndPoll(AuthenticationFlowContext context) {
		final String methodName = "initiateAndPoll";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
		UserModel user = context.getUser();
		if (user != null) {
			// User is associated with the context
			String userId = CloudIdentityUtilities.getCIUserId(user);
			if (userId == null) {
				// User does not yet have a CI user record associated with them. Let's create it now
				boolean createdShadowUserSuccessfully = CloudIdentityUtilities.createCIShadowUser(context, user);
				if (createdShadowUserSuccessfully) {
					userId = CloudIdentityUtilities.getCIUserId(user);
				} else {
				    // TODO: Error page
				}
			}
			if (userId != null) {
				// User has a CI User ID
				boolean isRegistered = QrUtilities.doesUserHaveVerifyRegistered(context, userId);
				if (!isRegistered) {
					// User does not have IBM Verify registered
					// Check to see if we've already initiated the verify registration
					String qrCode = QrUtilities.getVerifyRegistrationQrCode(context);
					if (qrCode == null) {
						// No verify registration initiated yet, let's start it up
						qrCode = QrUtilities.initiateVerifyAuthenticatorRegistration(context, userId, VERIFY_REGISTRATION_FRIENDLY_NAME);
						QrUtilities.setVerifyRegistrationQrCode(context, qrCode);
					}
					Response challenge = context.form()
							.setAttribute(QR_CODE_ATTR_NAME, qrCode)
							.createForm(VERIFY_REGISTRATION_TEMPLATE);
					context.challenge(challenge);
					
					CloudIdentityLoggingUtilities.exit(logger, methodName);
					return;
				} else {
				    context.form().addSuccess(new FormMessage("verifyRegistrationVerified"));
				    context.resetFlow();
				    return;
				}
			}
		}
		context.success();
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public void close() {
		final String methodName = "close";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		// no-op
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "configuredFor";
		CloudIdentityLoggingUtilities.entry(logger, methodName, session, realm, user);
		
		boolean configuredFor = true;
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, configuredFor);
		return configuredFor;
	}

	public boolean requiresUser() {
		final String methodName = "requiresUser";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		boolean requiresUser = true;
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, requiresUser);
		return requiresUser;
	}

	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "setRequiredActions";
		CloudIdentityLoggingUtilities.entry(logger, methodName, session, realm, user);
		// no-op
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

}
