/*
    Copyright 2020 IBM
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
      http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.ibm.security.verify.authenticator.webauthn.fido2;

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
import com.ibm.security.verify.authenticator.rest.FidoUtilities;
import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;
import com.ibm.security.verify.authenticator.webauthn.registration.IBMSecurityVerifyFidoRegistrationRequiredActionAuthenticator;

public class IBMSecurityVerifyFido2LoginAuthenticator implements Authenticator {

    private static final String FIDO_LOGIN_PAGE_TEMPLATE = "fido-login.ftl";

    private static final String ACTION_PARAM = "action";
    private static final String AUTHENTICATE_PARAM = "authenticate";
    private static final String REGISTER_ACTION = "register";

    private static final String FIDO_REG_INIT_MACRO = "fidoRegInit";
    private static final String FIDO_AUTHN_INIT_MACRO = "fidoAuthnInit";

    private static final String FIDO_HIDE_REG_BUTTON = "fidoHideRegButton";

    private Logger logger = Logger.getLogger(IBMSecurityVerifyFido2LoginAuthenticator.class);

    public void action(AuthenticationFlowContext context) {
        final String methodName = "action";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);

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
                UserModel user = IBMSecurityVerifyUtilities.matchCIUserIdToUserModel(context, userId);
                if (user != null) {
                    context.setUser(user);
                    context.success();
                } else {
                    context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgUserDoesNotExist")));
                    return;
                }
            } else {
                context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgUserDoesNotExist")));
                return;
            }
        } else {
            context.failure(null);
        }
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
    }
    
    public void authenticate(AuthenticationFlowContext context) {
        final String methodName = "authenticate";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);

        String fidoInitAuthnResponse = FidoUtilities.getFidoInitAuthnPayload(context);
        if (fidoInitAuthnResponse == null) {
            fidoInitAuthnResponse = FidoUtilities.initiateFidoAuthn(context, null);
            FidoUtilities.setFidoInitAuthnPayload(context, fidoInitAuthnResponse);
        }

        Response challenge = context.form()
                .setAttribute(FIDO_REG_INIT_MACRO, "{}")
                .setAttribute(FIDO_AUTHN_INIT_MACRO, fidoInitAuthnResponse)
                .setAttribute(FIDO_HIDE_REG_BUTTON, 
                        (context.getSession().getAttribute(
                                IBMSecurityVerifyFidoRegistrationRequiredActionAuthenticator.FIDO_REG_VERIFIED)) != null ? true : false)
                .createForm(FIDO_LOGIN_PAGE_TEMPLATE);
        context.forceChallenge(challenge);
        
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
        // Doesn't require a user because the user will not yet have been authenticated
        return false;
    }

    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No-op for the time being
    }

}
