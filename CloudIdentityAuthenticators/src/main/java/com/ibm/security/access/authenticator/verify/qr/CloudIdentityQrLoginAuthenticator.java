package com.ibm.security.access.authenticator.verify.qr;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.ibm.security.access.authenticator.rest.CloudIdentityUtilities;
import com.ibm.security.access.authenticator.rest.QrUtilities;
import com.ibm.security.access.authenticator.rest.QrUtilities.QrLoginInitiationResponse;
import com.ibm.security.access.authenticator.rest.QrUtilities.QrLoginResponse;

public class CloudIdentityQrLoginAuthenticator implements Authenticator {

    private static final String ACTION_PARAM = "action";
    private static final String AUTHENTICATE_PARAM = "authenticate";
    private static final String REGISTER_ACTION = "register";

	public void action(AuthenticationFlowContext context) {
		MultivaluedMap<String, String> formParams = context.
		        getHttpRequest().getDecodedFormParameters();
        String action= formParams.getFirst(ACTION_PARAM);

		if (REGISTER_ACTION.equals(action)) {
            // Redirect user to IBM Verify Registration (or next flow)
            context.attempted();
            return;
		}
		
		// Poll for the QR login
        String qrLoginId = QrUtilities.getQrLoginId(context);
        String qrLoginDsi = QrUtilities.getQrLoginDsi(context);
        String qrLoginImage = QrUtilities.getQrLoginImage(context);
		QrLoginResponse qrResponse = QrUtilities.
		        pollQrLoginStatus(context, qrLoginId, qrLoginDsi);

		if (AUTHENTICATE_PARAM.equals(action) && 
		        "SUCCESS".equals(qrResponse.state) && qrResponse.userId != null) {
			List<UserModel> users = 
			        context.getSession().users().getUsers(context.getRealm());
			UserModel matchingUser = null;
			for (int i = 0; i < users.size(); i++) {
				UserModel iterUser = users.get(i);
				List<String> cloudIdentityUserIdValues = 
				        iterUser.getAttribute(CloudIdentityUtilities.CI_USER_ID_ATTR_NAME);
				if (!cloudIdentityUserIdValues.isEmpty()) {
					if (qrResponse.userId.equals(cloudIdentityUserIdValues.get(0))) {
						matchingUser = iterUser;
						i = users.size();
					}
				}
			}
			context.setUser(matchingUser);
			context.success();
		} else if (AUTHENTICATE_PARAM.equals(action) && "FAILED".equals(qrResponse.state)) {
		    // Attempted but authentication failed (not registered with IBM Verify)
		    Response challenge = context.form()
                    .setAttribute("qrCode", qrLoginImage)
                    .setError("QR Code Authentication Unsuccessful. Please register with IBM Verify to enable passwordless authentication.")
                    .createForm("qr-login.ftl");
            context.challenge(challenge);
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
