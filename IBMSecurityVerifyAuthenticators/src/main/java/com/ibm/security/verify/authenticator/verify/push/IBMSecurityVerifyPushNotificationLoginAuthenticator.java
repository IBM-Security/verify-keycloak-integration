package com.ibm.security.verify.authenticator.verify.push;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import com.ibm.security.verify.authenticator.rest.FormUtilities;
import com.ibm.security.verify.authenticator.rest.IBMSecurityVerifyUtilities;
import com.ibm.security.verify.authenticator.rest.PushNotificationUtilities;
import com.ibm.security.verify.authenticator.rest.VerifyAppUtilities;
import com.ibm.security.verify.authenticator.rest.PushNotificationUtilities.Pair;
import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class IBMSecurityVerifyPushNotificationLoginAuthenticator implements Authenticator {

    private static final String PUSH_NOTIFICATION_LOGIN_TEMPLATE = "push-notification-login.ftl";
    private static final String PUSH_NOTIFICATION_LOGIN_RESEND_TEMPLATE = "push-notification-login-resend.ftl";

    private static final String ACTION_PARAM = "action";
    private static final String AUTHENTICATE_PARAM = "authenticate";
    private static final String RESEND_PARAM = "resend";
    
    private Logger logger = Logger.getLogger(IBMSecurityVerifyPushNotificationLoginAuthenticator.class);

    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.
                getHttpRequest().getDecodedFormParameters();
        String action= formParams.getFirst(ACTION_PARAM);

        UserModel user = context.getUser();
        if (user == null) {
            context.challenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsg2FARequired")));
            return;
        }
        
        if (RESEND_PARAM.equals(action)) {
            context.getAuthenticationSession().removeAuthNote(
                    PushNotificationUtilities.PUSH_NOTIFICATION_AUTHENTICATOR_ID_ATTR_NAME);
            context.getAuthenticationSession().removeAuthNote(
                    PushNotificationUtilities.PUSH_NOTIFICATION_TRANSACTION_ID_ATTR_NAME);
            initiatePushNotification(context);
            return;
        }
        
        String pushNotificationState = PushNotificationUtilities.getPushNotificationVerification(context);

        if (AUTHENTICATE_PARAM.equals(action) && 
                "VERIFY_SUCCESS".equals(pushNotificationState)) {
            context.success();
            return;
        } else if (AUTHENTICATE_PARAM.equals(action) && "PENDING".equals(pushNotificationState)) {
            Response challenge = context.form()
                    .createForm(PUSH_NOTIFICATION_LOGIN_TEMPLATE);
            context.challenge(challenge);
            return;
        } else if (AUTHENTICATE_PARAM.equals(action) && "TIMEOUT".equals(pushNotificationState)) {
            Response challenge = context.form()
                    .addError(new FormMessage("pushNotificationFormExpiredError"))
                    .createForm(PUSH_NOTIFICATION_LOGIN_RESEND_TEMPLATE);
            context.forceChallenge(challenge);
        } else {
            context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgAccessDenied")));
        }
    }

    public void authenticate(AuthenticationFlowContext context) {
        if (context.getAuthenticationSession().getAuthNote(
                PushNotificationUtilities.PUSH_NOTIFICATION_TRANSACTION_ID_ATTR_NAME) == null) {
            initiatePushNotification(context);
        }
    }
    
    private void initiatePushNotification(AuthenticationFlowContext context) {
        final String methodName = "initiatePushNotification";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);
        
        UserModel user = context.getUser();
        if (user != null) {
            // User is associated with the context
            String userId = IBMSecurityVerifyUtilities.getCIUserId(context, user);
            if (userId == null) {
                requireVerifyRegistration(context, methodName);
                return;
            } else {
                boolean isRegistered = VerifyAppUtilities.doesUserHaveVerifyRegistered(context, userId);
                if (!isRegistered) {
                    requireVerifyRegistration(context, methodName);
                    return;
                } else {          
                    Pair<String,String> result = PushNotificationUtilities.getSignatureEnrollmentAuthenticatorId(context, userId);
                    PushNotificationUtilities.sendPushNotification(context, result.key, result.value);
                    
                    Response challenge = context.form()
                            .createForm(PUSH_NOTIFICATION_LOGIN_TEMPLATE);
                    context.challenge(challenge);
                    
                    IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
                    return;
                }
            }
        } else {
            context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsg2FARequired")));
        }
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
    }
    
    private void requireVerifyRegistration(AuthenticationFlowContext context, String methodName) {
        context.form().addError(new FormMessage("verifyRegistrationRequired"));
        context.attempted();
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
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
        return true;
    }

    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No-op for the time being
    }
}
