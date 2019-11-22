package com.ibm.security.access.authenticator;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.ibm.security.access.util.CloudIdentityLoggingUtilites;
import com.ibm.security.access.util.CloudIdentityUtilities;
import com.ibm.security.access.util.FidoUtilities;

public class CloudIdentityFidoRegistrationRequiredActionAuthenticator implements Authenticator {
	
	private static final String ACTION_PARAM = "action";
	private static final String REGISTER_ACTION = "register";
	private static final String BYPASS_ACTION = "bypass";
	
	private Logger logger = Logger.getLogger(CloudIdentityFidoRegistrationRequiredActionAuthenticator.class);

	public void action(AuthenticationFlowContext context) {
		final String methodName = "action";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);
		
		MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
		String action= formParams.getFirst(ACTION_PARAM);
		if (REGISTER_ACTION.equals(action)) {
			boolean result = FidoUtilities.completeFidoRegistration(context);
			if (result) {
				context.success();
			}
		} else {
			// Bypassing the registration phase since it's optional
			context.success();
		}
		
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}

	public void authenticate(AuthenticationFlowContext context) {
		final String methodName = "authenticate";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);
		
		boolean hasPromptedRegistration = CloudIdentityUtilities.hasPromptedPasswordlessRegistration(context);
		if (hasPromptedRegistration) {
			context.success();
			CloudIdentityLoggingUtilites.exit(logger, methodName);
			return;
		}
		UserModel user = context.getUser();
		if (user != null) {
			// User is associated with the context
			String userId = CloudIdentityUtilities.getCIUserId(user);
			if (userId == null) {
				// User does not yet have a CI user record associated with them. Let's create it now
				boolean createdShadowUserSuccessfully = CloudIdentityUtilities.createCIShadowUser(context, user);
				if (createdShadowUserSuccessfully) {
					userId = CloudIdentityUtilities.getCIUserId(user);
				}
			}
			if (userId != null) {
				// User has a CI User ID
				boolean isRegistered = FidoUtilities.doesUserHaveFidoRegistered(context, userId);
				if (!isRegistered) {
					// User does not have a FIDO authn device registered
					String fidoInitRegResp = FidoUtilities.initiateFidoRegistration(context, userId);
					Response challenge = context.form()
							.setAttribute("fidoRegInit", fidoInitRegResp)
							.setAttribute("fidoAuthnInit", "{}")
							.createForm("fido-registration.ftl");
					context.challenge(challenge);
					CloudIdentityUtilities.setPromptedPasswordlessRegistration(context);
					CloudIdentityLoggingUtilites.exit(logger, methodName);
					return;
				}
			}
		}
		context.success();
		
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}

	public void close() {
		final String methodName = "close";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		// no-op
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}

	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "configuredFor";
		CloudIdentityLoggingUtilites.entry(logger, methodName, session, realm, user);
		
		boolean configuredFor = true;
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, configuredFor);
		return configuredFor;
	}

	public boolean requiresUser() {
		final String methodName = "requiresUser";
		CloudIdentityLoggingUtilites.entry(logger, methodName);
		
		boolean requiresUser = true;
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, requiresUser);
		return requiresUser;
	}

	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "setRequiredActions";
		CloudIdentityLoggingUtilites.entry(logger, methodName, session, realm, user);
		// no-op
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}

}
