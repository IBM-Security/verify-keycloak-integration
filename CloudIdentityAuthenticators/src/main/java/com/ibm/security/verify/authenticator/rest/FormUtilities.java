package com.ibm.security.verify.authenticator.rest;

import javax.ws.rs.core.Response;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.utils.FormMessage;

public class FormUtilities {
    public static final String ERROR_PAGE_TEMPLATE = "error-page.ftl";

    public static Response createErrorPage(AuthenticationFlowContext context, FormMessage msg) {
        return context.form()
                .addError(msg)
                .createForm(ERROR_PAGE_TEMPLATE);
    }

}
