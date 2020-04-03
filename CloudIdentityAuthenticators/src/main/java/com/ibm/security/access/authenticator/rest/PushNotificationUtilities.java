package com.ibm.security.access.authenticator.rest;

import org.keycloak.authentication.AuthenticationFlowContext;

public class PushNotificationUtilities {
    
    public static String getSignatureEnrollmentId(String ciUserId) {
        // https://{{tenant}}/v1.0/authnmethods/signatures?search=owner="ciUserId"
        return "";
    }
    
    public static void sendPushNotification(AuthenticationFlowContext context, String authenticatorId, String sigId) {
        // TODO:
        // https://{{tenant}}/v1.0/authenticators/authenticatorId/verifications
        // get transactionId
        // store transactionId in session auth note
        // context.getAuthenticationSession().setAuthNote(push.notification.transaction.id, transactionId);
    }
    
    public static PushNotificationVerificationResponse getPushNotificationVerification(AuthenticationFlowContext context) {
        // TODO:
        // get authenticatorId and transactionId from auth note
        // https://{{tenant}}/v1.0/authenticators/a93d0c87-01bb-4a04-be77-6b5494bb201e/verifications?search=id="3483cd27-abb0-48c7-a7b5-b23459cb34d7"
        return null;
    }
    
    public static class PushNotificationVerificationResponse {
        public String state;
        public String userId;
        
        PushNotificationVerificationResponse(String state, String userId) {
            this.state = state;
            this.userId = userId;
        }
    }

}
