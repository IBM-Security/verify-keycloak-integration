package com.ibm.security.access.authenticator;

import java.util.List;

import javax.ws.rs.core.Response;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.ibm.security.access.util.QrUtilities;
import com.ibm.security.access.util.QrUtilities.QrLoginInitiationResponse;
import com.ibm.security.access.util.QrUtilities.QrLoginResponse;

public class CloudIdentityQrLoginAuthenticator implements Authenticator {

	public void action(AuthenticationFlowContext context) {
		// Poll for the QR login
		String qrLoginId = QrUtilities.getQrLoginId(context);
		String qrLoginDsi = QrUtilities.getQrLoginDsi(context);
		String qrLoginImage = QrUtilities.getQrLoginImage(context);
		QrLoginResponse qrResponse = QrUtilities.pollQrLoginStatus(context, qrLoginId, qrLoginDsi);
		if ("SUCCESS".equals(qrResponse.state) && qrResponse.userId != null) {
			List<UserModel> users = context.getSession().users().getUsers(context.getRealm());
			UserModel matchingUser = null;
			for (int i = 0; i < users.size(); i++) {
				UserModel iterUser = users.get(i);
				List<String> cloudIdentityUserIdValues = iterUser.getAttribute("cloudIdentity.userId");
				if (!cloudIdentityUserIdValues.isEmpty()) {
					if (qrResponse.userId.equals(cloudIdentityUserIdValues.get(0))){
						matchingUser = iterUser;
						i = users.size();
					}
				}
			}
			context.setUser(matchingUser);
			context.success();
		} else {
			Response challenge = context.form()
					.setAttribute("qrCode", qrLoginImage)
					.createForm("qr-login.ftl");
			context.challenge(challenge);
		}
	}
	
	public void authenticate(AuthenticationFlowContext context) {
		// Initiate a QR Login and send the QR code back to the browser
		QrLoginInitiationResponse qrResponse = QrUtilities.initiateQrLogin(context);
		QrUtilities.setQrLoginId(context, qrResponse.id);
		QrUtilities.setQrLoginDsi(context, qrResponse.dsi);
		QrUtilities.setQrLoginImage(context, qrResponse.qrBase64Content);
		Response challenge = context.form()
				.setAttribute("qrCode", qrResponse.qrBase64Content)
				.createForm("qr-login.ftl");
		context.challenge(challenge);
	}
	
	public void close() {
		// No-op
	}

	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		// Hardcode to true for the time being
		// Only users with verify configured should use this authenticator
		return true;
	}
	
	public boolean requiresUser() {
		// Doesn't require a user because the user will not yet have been authenticated
		return false;
	}

	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		// No-op for the time being
	}

}
