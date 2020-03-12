package com.ibm.security.access.authenticator.fido.fido2;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.ibm.security.access.authenticator.rest.CloudIdentityUtilities;
import com.ibm.security.access.authenticator.rest.FidoUtilities;
import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;

public class CloudIdentityFido2LoginAuthenticator implements Authenticator {

    private static final String INITIAL_LOGIN_PAGE_TEMPLATE = "fido-login.ftl";

    private static final String ACTION_PARAM = "action";
    private static final String AUTHENTICATE_PARAM = "authenticate";
    private static final String REGISTER_ACTION = "register";

    private static final String FIDO_REG_INIT_MACRO = "fidoRegInit";
    private static final String FIDO_AUTHN_INIT_MACRO = "fidoAuthnInit";

    private Logger logger = Logger.getLogger(CloudIdentityFido2LoginAuthenticator.class);

    public void action(AuthenticationFlowContext context) {
        final String methodName = "action";
        CloudIdentityLoggingUtilities.entry(logger, methodName, context);

        MultivaluedMap<String, String> formParams = context.
                getHttpRequest().getDecodedFormParameters();
        String action= formParams.getFirst(ACTION_PARAM);

        if (REGISTER_ACTION.equals(action)) {
            // Redirect user to FIDO2 Registration (or next flow)
            context.attempted();
            return;
        }

        if (AUTHENTICATE_PARAM.equals(action)) {
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
        } else {
            context.failure(null);
        }
        CloudIdentityLoggingUtilities.exit(logger, methodName);
    }
    
    public void authenticate(AuthenticationFlowContext context) {
        final String methodName = "authenticate";
        CloudIdentityLoggingUtilities.entry(logger, methodName, context);

        String fidoInitAuthnResponse = FidoUtilities.getFidoInitAuthnPayload(context);
        if (fidoInitAuthnResponse == null) {
            fidoInitAuthnResponse = FidoUtilities.initiateFidoAuthn(context, null);
            FidoUtilities.setFidoInitAuthnPayload(context, fidoInitAuthnResponse);
        }

        Response challenge = context.form()
                .setAttribute(FIDO_REG_INIT_MACRO, "{}")
                .setAttribute(FIDO_AUTHN_INIT_MACRO, fidoInitAuthnResponse)
                .createForm(INITIAL_LOGIN_PAGE_TEMPLATE);
        context.forceChallenge(challenge);
        
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
        // Doesn't require a user because the user will not yet have been authenticated
        return false;
    }

    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No-op for the time being
    }

}
