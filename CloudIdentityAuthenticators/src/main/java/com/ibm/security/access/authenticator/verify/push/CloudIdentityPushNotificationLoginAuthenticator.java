package com.ibm.security.access.authenticator.verify.push;

import java.util.List;

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
import com.ibm.security.access.authenticator.rest.PushNotificationUtilities.PushNotificationVerificationResponse;
import com.ibm.security.access.authenticator.rest.QrUtilities;
import com.ibm.security.access.authenticator.rest.QrUtilities.QrLoginInitiationResponse;
import com.ibm.security.access.authenticator.rest.QrUtilities.QrLoginResponse;
import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;
import com.ibm.security.access.authenticator.verify.registration.CloudIdentityVerifyRegistrationRequiredActionAuthenticator;

public class CloudIdentityPushNotificationLoginAuthenticator implements Authenticator {

    private static final String ACTION_PARAM = "action";
    private static final String AUTHENTICATE_PARAM = "authenticate";
    private static final String RESEND_PARAM = "resend";
//    private static final String REGISTER_ACTION = "register";
    
    private Logger logger = Logger.getLogger(CloudIdentityPushNotificationLoginAuthenticator.class);

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.
                getHttpRequest().getDecodedFormParameters();
        String action= formParams.getFirst(ACTION_PARAM);

        UserModel user = context.getUser();
        String ciUserId = null;
        if (user != null) {
            // User is associated with the context
            ciUserId = CloudIdentityUtilities.getCIUserId(user);
        } else {
            // TODO: Need to register first
            // Error Page
        }
        
        PushNotificationVerificationResponse response = PushNotificationUtilities.getPushNotificationVerification(context);

//        TODO:      
//        if (AUTHENTICATE_PARAM.equals(action) && 
//                "VERIFY_SUCCESS".equals(response.state)) {
//            context.success();
//        } else if (AUTHENTICATE_PARAM.equals(action) && "PENDING".equals(response.state)) {
//            return;
//        } else if (AUTHENTICATE_PARAM.equals(action) && "TIMEOUT".equals(response.state)) {
//            // Attempted but authentication timed out - resend page?
//            Response challenge = context.form()
//                    .setError("Timed out - resend?")
//                    .createForm("push-notification-login-resend.ftl");
//            context.challenge(challenge);
//        } else {
//            // failed - reset/start over page?
//        }
        
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        initiatePushNotification(context);
    }
    
    private void initiatePushNotification(AuthenticationFlowContext context) {
        final String methodName = "initiatePushNotification";
        CloudIdentityLoggingUtilities.entry(logger, methodName, context);
        
        UserModel user = context.getUser();
        if (user != null) {
            // User is associated with the context
            String userId = CloudIdentityUtilities.getCIUserId(user);
            if (userId == null) {
                // TODO: Error - must register with IBM Verify first
            } else {
                // User has a CI User ID
                boolean isRegistered = QrUtilities.doesUserHaveVerifyRegistered(context, userId);
                if (!isRegistered) {
                    // TODO: must register with IBM Verify first
                } else {          
                    String sigId = PushNotificationUtilities.getSignatureEnrollmentId(userId);
                    String authenticatorId = CloudIdentityUtilities.getAuthenticatorId(userId);
                    PushNotificationUtilities.sendPushNotification(context, authenticatorId, sigId);
                    
                    Response challenge = context.form()
                            .createForm("push-notification-login.ftl");
                    context.challenge(challenge);
                    
                    CloudIdentityLoggingUtilities.exit(logger, methodName);
                    return;
                }
            }
        } else {
            // TODO: Error in flow set up - push notification is always 2FA and
            // should require registration before coming to the authenticator
        }
        CloudIdentityLoggingUtilities.exit(logger, methodName);
    }

    @Override
    public boolean configuredFor(KeycloakSession arg0, RealmModel arg1,
            UserModel arg2) {
        return false;
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession arg0, RealmModel arg1,
            UserModel arg2) {
        
    }
    
    @Override
    public void close() {
      
    }
}
