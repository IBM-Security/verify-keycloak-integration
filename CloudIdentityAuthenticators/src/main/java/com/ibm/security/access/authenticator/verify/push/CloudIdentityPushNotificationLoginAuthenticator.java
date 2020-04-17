package com.ibm.security.access.authenticator.verify.push;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import com.ibm.security.access.authenticator.rest.CloudIdentityUtilities;
import com.ibm.security.access.authenticator.rest.PushNotificationUtilities;
import com.ibm.security.access.authenticator.rest.PushNotificationUtilities.Pair;
import com.ibm.security.access.authenticator.rest.QrUtilities;
import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;

public class CloudIdentityPushNotificationLoginAuthenticator implements Authenticator {

    private static final String PUSH_NOTIFICATION_LOGIN_TEMPLATE = "push-notification-login.ftl";
    private static final String PUSH_NOTIFICATION_LOGIN_RESEND_TEMPLATE = "push-notification-login-resend.ftl";

    private static final String ACTION_PARAM = "action";
    private static final String AUTHENTICATE_PARAM = "authenticate";
    private static final String RESEND_PARAM = "resend";
    
    private Logger logger = Logger.getLogger(CloudIdentityPushNotificationLoginAuthenticator.class);

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.
                getHttpRequest().getDecodedFormParameters();
        String action= formParams.getFirst(ACTION_PARAM);

        UserModel user = context.getUser();
        if (user == null) {
            // TODO: Error in flow set up - push notification is always 2FA and
            // should require native keycloak username/pw auth before attempting 2FA
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
            context.challenge(challenge);
        } else {
            // TODO:
            // failed - reset/start over page?
            // context.failureChallenge();
        }
        
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        if (context.getAuthenticationSession().getAuthNote(
                PushNotificationUtilities.PUSH_NOTIFICATION_TRANSACTION_ID_ATTR_NAME) == null) {
            initiatePushNotification(context);
        }
    }
    
    private void initiatePushNotification(AuthenticationFlowContext context) {
        final String methodName = "initiatePushNotification";
        CloudIdentityLoggingUtilities.entry(logger, methodName, context);
        
        UserModel user = context.getUser();
        if (user != null) {
            // User is associated with the context
            String userId = CloudIdentityUtilities.getCIUserId(user);
            if (userId == null) {
                requireVerifyRegistration(context, methodName);
                return;
            } else {
                boolean isRegistered = QrUtilities.doesUserHaveVerifyRegistered(context, userId);
                if (!isRegistered) {
                    requireVerifyRegistration(context, methodName);
                    return;
                } else {          
                    Pair<String,String> result = PushNotificationUtilities.getSignatureEnrollmentAuthenticatorId(context, userId);
                    PushNotificationUtilities.sendPushNotification(context, result.key, result.value);
                    
                    Response challenge = context.form()
                            .createForm(PUSH_NOTIFICATION_LOGIN_TEMPLATE);
                    context.challenge(challenge);
                    
                    CloudIdentityLoggingUtilities.exit(logger, methodName);
                    return;
                }
            }
        } else {
            // TODO: Error in flow set up - push notification is always 2FA and
            // should require native keycloak username/pw auth before attempting 2FA
        }
        CloudIdentityLoggingUtilities.exit(logger, methodName);
    }
    
    private void requireVerifyRegistration(AuthenticationFlowContext context, String methodName) {
        context.form().addError(new FormMessage("verifyRegistrationRequired"));
        context.attempted();
        CloudIdentityLoggingUtilities.exit(logger, methodName);
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
