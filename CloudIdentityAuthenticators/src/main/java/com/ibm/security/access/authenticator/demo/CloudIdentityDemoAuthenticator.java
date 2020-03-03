package com.ibm.security.access.authenticator.demo;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;
import com.ibm.security.access.authenticator.rest.CloudIdentityUtilities;
import com.ibm.security.access.authenticator.rest.FidoUtilities;
import com.ibm.security.access.authenticator.rest.QrUtilities;
import com.ibm.security.access.authenticator.rest.QrUtilities.QrLoginInitiationResponse;
import com.ibm.security.access.authenticator.rest.QrUtilities.QrLoginResponse;

public class CloudIdentityDemoAuthenticator implements Authenticator {

	/**
	 * HTML Page template constants
	 */
	private static final String INITIAL_LOGIN_PAGE_TEMPLATE = "demo-login.ftl";

	/**
	 * HTML Page template injection macros
	 */
	private static final String FIDO_AUTHN_INIT_MACRO = "fidoAuthnInit";
	private static final String FIDO_REG_INIT_MACRO = "fidoRegInit";
	private static final String QR_AUTHN_INIT_MACRO = "qrAuthnInit";

	private static final String ACTION_TYPE = "action-type";
	private static final String ACTION_TYPE_BYPASS = "bypass";
	private static final String ACTION_TYPE_FIDO = "fido";
	private static final String ACTION_TYPE_QR = "qr";
	
	private Logger logger = Logger.getLogger(CloudIdentityDemoAuthenticator.class);

	public void action(AuthenticationFlowContext context) {
		final String methodName = "action";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
		MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
		String actionType = formParams.getFirst(ACTION_TYPE);
		if (ACTION_TYPE_FIDO.equals(actionType)) {
			String userId = FidoUtilities.completeFidoAuthentication(context);
			if (userId != null) {
				UserModel user = CloudIdentityUtilities.matchCIUserIdToUserModel(context, userId);
				if (user != null) {
					context.setUser(user);
					context.success();
				} else {
					// User ID doesn't match a user in RH-SSO
					context.failure(null);
				}
			} else {
				// User ID not obtained from FIDO response
				context.failure(null);
			}
		} else if (ACTION_TYPE_QR.equals(actionType)) {
			String qrLoginId = QrUtilities.getQrLoginId(context);
			String qrLoginDsi = QrUtilities.getQrLoginDsi(context);
			QrLoginResponse qrResponse = QrUtilities.pollQrLoginStatus(context, qrLoginId, qrLoginDsi);
			if ("SUCCESS".equals(qrResponse.state) && qrResponse.userId != null) {
				UserModel user = CloudIdentityUtilities.matchCIUserIdToUserModel(context, qrResponse.userId);
				if (user != null) {
					context.setUser(user);
					context.success();
				} else {
					// User ID doesn't match a user in RH-SSO
					context.failure(null);
				}
			} else {
				// Not successful, so this was just a polling interval. Restart the authn flow
				authenticate(context);
			}
		} else {
			// Handle bypass flow; treat any rogue input as bypass flow
			context.attempted();
		}
		
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public void authenticate(AuthenticationFlowContext context) {
		final String methodName = "authenticate";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		/**
		 * 0) Check session cache if the FIDO and QR authn has already been initiated. Only initiate on first access
		 * 1) Initiate QR login flow
		 * 2) Initiate FIDO login flow
		 * 3) Send both QR and FIDO login flow data to browser so the user can use either
		 * 4) Also render a button that will allow a user to do username/password login, bypassing either CIV option
		 */
		
		String fidoInitAuthnResponse = FidoUtilities.getFidoInitAuthnPayload(context);
		if (fidoInitAuthnResponse == null) {
			fidoInitAuthnResponse = FidoUtilities.initiateFidoAuthn(context, null);
			FidoUtilities.setFidoInitAuthnPayload(context, fidoInitAuthnResponse);
		}
		
		String qrInitAuthnResponseDsi = QrUtilities.getQrLoginDsi(context);
		String qrInitAuthnResponseId = QrUtilities.getQrLoginId(context);
		String qrInitAuthnResponseImage = QrUtilities.getQrLoginImage(context);
		if (qrInitAuthnResponseDsi == null || qrInitAuthnResponseId == null || qrInitAuthnResponseImage == null) {
			QrLoginInitiationResponse qrInitAuthnResponse = QrUtilities.initiateQrLogin(context);
			qrInitAuthnResponseDsi = qrInitAuthnResponse.dsi;
			qrInitAuthnResponseId = qrInitAuthnResponse.id;
			qrInitAuthnResponseImage = qrInitAuthnResponse.qrBase64Content;
			QrUtilities.setQrLoginDsi(context, qrInitAuthnResponseDsi);
			QrUtilities.setQrLoginId(context, qrInitAuthnResponseId);
			QrUtilities.setQrLoginImage(context, qrInitAuthnResponseImage);
		}
		
		Response challenge = context.form()
				.setAttribute(FIDO_REG_INIT_MACRO, "{}")
				.setAttribute(FIDO_AUTHN_INIT_MACRO, fidoInitAuthnResponse)
				.setAttribute(QR_AUTHN_INIT_MACRO, qrInitAuthnResponseImage)
				.createForm(INITIAL_LOGIN_PAGE_TEMPLATE);
		context.forceChallenge(challenge);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public void close() {
		final String methodName = "close";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		// No-op
		// Token(s) could be revoked here for better cleanup
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "configuredFor";
		CloudIdentityLoggingUtilities.entry(logger, methodName, session, realm, user);
		
		// Hardcode to true for the time being
		boolean configuredFor = true;
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, configuredFor);
		return configuredFor;
	}

	public boolean requiresUser() {
		final String methodName = "requiresUser";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		
		boolean requiresUser = false;
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, requiresUser);
		return requiresUser;
	}

	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "setRequiredActions";
		CloudIdentityLoggingUtilities.entry(logger, methodName, session, realm, user);
		// No-op for the time being
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

}
