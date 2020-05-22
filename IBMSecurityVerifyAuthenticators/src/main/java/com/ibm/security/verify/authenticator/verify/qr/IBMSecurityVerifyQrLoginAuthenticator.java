package com.ibm.security.verify.authenticator.verify.qr;

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
import com.ibm.security.verify.authenticator.rest.QrUtilities;
import com.ibm.security.verify.authenticator.rest.QrUtilities.QrLoginInitiationResponse;
import com.ibm.security.verify.authenticator.rest.QrUtilities.QrLoginResponse;
import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;
import com.ibm.security.verify.authenticator.verify.registration.IBMSecurityVerifyRegistrationRequiredActionAuthenticator;
import com.ibm.security.verify.authenticator.webauthn.registration.IBMSecurityVerifyFidoRegistrationRequiredActionAuthenticator;

public class IBMSecurityVerifyQrLoginAuthenticator implements Authenticator {

    private static final String QR_LOGIN_TEMPLATE = "qr-login.ftl";

    private static final String QR_CODE_ATTR_NAME = "qrCode";

    private static final String ACTION_PARAM = "action";
    private static final String VERIFY_REG_BTN = "hideBtn";
    private static final String AUTHENTICATE_PARAM = "authenticate";
    private static final String REGISTER_ACTION = "register";

    private static final String VERIFY_HIDE_REG_BUTTON = "verifyHideRegButton";

    private static Logger logger = Logger.getLogger(IBMSecurityVerifyQrLoginAuthenticator.class);

	public void action(AuthenticationFlowContext context) {
		MultivaluedMap<String, String> formParams = context.
		        getHttpRequest().getDecodedFormParameters();
        String action= formParams.getFirst(ACTION_PARAM);
        String verifyRegVerified = formParams.getFirst(VERIFY_REG_BTN);

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
		    UserModel user = IBMSecurityVerifyUtilities.matchCIUserIdToUserModel(context, qrResponse.userId);
            if (user != null) {
                context.setUser(user);
                context.success();
            } else {
                context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgUserDoesNotExist")));
                return;
            }
		} else if (AUTHENTICATE_PARAM.equals(action) && "FAILED".equals(qrResponse.state)) {
		    // Attempted but authentication failed (not registered with IBM Verify)
		    Response challenge = context.form()
                    .setAttribute(QR_CODE_ATTR_NAME, qrLoginImage)
                    .addError(new FormMessage("qrVerifyRegistrationRequiredError"))
                    .createForm(QR_LOGIN_TEMPLATE);
            context.challenge(challenge);
		} else if (AUTHENTICATE_PARAM.equals(action) && "TIMEOUT".equals(qrResponse.state)) {
		    context.form().addError(new FormMessage("qrFormLoginTimeOutError"));
		    authenticate(context);
		} else if (AUTHENTICATE_PARAM.equals(action) && "PENDING".equals(qrResponse.state)) {
			Response challenge = context.form()
					.setAttribute(QR_CODE_ATTR_NAME, qrLoginImage)
					.setAttribute(VERIFY_HIDE_REG_BUTTON, Boolean.parseBoolean(verifyRegVerified))
					.createForm(QR_LOGIN_TEMPLATE);
			context.challenge(challenge);
		} else {
		    // CANCELED
		    context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgLoginCanceled")));
            return;
		}
	}
	
	public void authenticate(AuthenticationFlowContext context) {
		// Initiate a QR Login and send the QR code back to the browser
		QrLoginInitiationResponse qrResponse = QrUtilities.initiateQrLogin(context);
		QrUtilities.setQrLoginId(context, qrResponse.id);
		QrUtilities.setQrLoginDsi(context, qrResponse.dsi);
		QrUtilities.setQrLoginImage(context, qrResponse.qrBase64Content);
		Response challenge = context.form()
				.setAttribute(QR_CODE_ATTR_NAME, qrResponse.qrBase64Content)
				.setAttribute(VERIFY_HIDE_REG_BUTTON, 
                        (context.getSession().getAttribute(
                                IBMSecurityVerifyRegistrationRequiredActionAuthenticator.VERIFY_REG_VERIFIED)) != null ? true : false)
				.createForm(QR_LOGIN_TEMPLATE);
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
