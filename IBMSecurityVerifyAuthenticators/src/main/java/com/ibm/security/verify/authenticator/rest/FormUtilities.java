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
