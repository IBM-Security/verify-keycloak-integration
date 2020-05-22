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

package com.ibm.security.verify.authenticator.otp;

import java.util.List;

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
import com.ibm.security.verify.authenticator.rest.OtpUtilities;
import com.ibm.security.verify.authenticator.rest.OtpUtilities.TransientOtpResponse;
import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class IBMSecurityVerifyOtpLoginAuthenticator implements Authenticator {

	/**
	 * Constants used for the OTP submission template
	 */
	private static final String OTP_SELECTION_TEMPLATE = "otp-selection.ftl";
	private static final String OTP_SUBMISSION_TEMPLATE = "otp-validation.ftl";

	private static final String OTP_FORM_NAME_ATTR_NAME = "otpFormName";
	private static final String OTP_FORM_NAME_VALUE = "otp";

	private static final String OTP_CORRELATION_FORM_NAME_ATTR_NAME ="otpCorrelationFormName";
	private static final String OTP_CORRELATION_FORM_NAME_VALUE = "otpCorrelation";

	private static final String OTP_CORRELATION_FORM_VALUE_ATTR_NAME = "otpCorrelationValue";

	private static final String OTP_TYPE_LABEL_ATTR_NAME = "otpTypeLabel";
	private static final String OTP_EMAIL_HINT = "otpEmailHint";
	private static final String OTP_SMS_HINT = "otpSmsHint";
	private static final String OTP_EMAIL_SUBMISSION_LABEL = "Enter your email OTP";
	private static final String OTP_SMS_SUBMISSION_LABEL = "Enter your SMS OTP";

	private static final String OTP_SMS_ATTR_NAME = "phone.number";

	private static final String OTP_TYPE_EMAIL = "email";
	private static final String OTP_TYPE_SMS = "sms";

	private Logger logger = Logger.getLogger(IBMSecurityVerifyOtpLoginAuthenticator.class);

	public void action(AuthenticationFlowContext context) {
		final String methodName = "action";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);

		String incomingOtpType = OtpUtilities.getOtpType(context);
		MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
		if (OTP_TYPE_EMAIL.equals(incomingOtpType) || OTP_TYPE_SMS.equals(incomingOtpType)) {
			String correlation = OtpUtilities.getOtpCorrelation(context);
			String transactionId = OtpUtilities.getOtpTransactionId(context);
			String otpValue = formParams.getFirst(OTP_FORM_NAME_VALUE);

			boolean isOtpValid = false;
			String otpSubmissionLabel = null;
			if (OTP_TYPE_EMAIL.equals(incomingOtpType)) {
				isOtpValid = OtpUtilities.validateEmailOtp(context, transactionId, otpValue);
				otpSubmissionLabel = OTP_EMAIL_SUBMISSION_LABEL;
			} else {
				isOtpValid = OtpUtilities.validateSmsOtp(context, transactionId, otpValue);
				otpSubmissionLabel = OTP_SMS_SUBMISSION_LABEL;
			}
			if (isOtpValid) {
				context.success();
			} else {
				Response challenge = context.form()
						.setAttribute(OTP_FORM_NAME_ATTR_NAME, OTP_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_NAME_ATTR_NAME, OTP_CORRELATION_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_VALUE_ATTR_NAME, correlation)
						.setAttribute(OTP_TYPE_LABEL_ATTR_NAME, otpSubmissionLabel)
						.setError("OTP is invalid")
						.createForm(OTP_SUBMISSION_TEMPLATE);
				context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
			}
		} else {
			// OTP has not been sent yet, the choice has just been made
			String email = context.getUser().getEmail();
			List<String> phoneValues = context.getUser().getAttribute(OTP_SMS_ATTR_NAME);
			String otpType = formParams.getFirst("otpType");
			if (OTP_TYPE_EMAIL.equals(otpType)) {
			    String emailHint = " (" + email.substring(0,3) + "..." + email.substring(email.indexOf("@"), email.length()) + ")";
			    OtpUtilities.setOtpType(context, OTP_TYPE_EMAIL);
				TransientOtpResponse otpResponse = OtpUtilities.sendEmailOtp(context, email);
				if (otpResponse != null) {
				    OtpUtilities.setOtpCorrelation(context, otpResponse.correlation);
				    OtpUtilities.setOtpTransactionId(context, otpResponse.transactionId);
					Response challenge = context.form()
							.setAttribute(OTP_FORM_NAME_ATTR_NAME, OTP_FORM_NAME_VALUE)
							.setAttribute(OTP_CORRELATION_FORM_NAME_ATTR_NAME, OTP_CORRELATION_FORM_NAME_VALUE)
							.setAttribute(OTP_CORRELATION_FORM_VALUE_ATTR_NAME, otpResponse.correlation)
							.setAttribute(OTP_TYPE_LABEL_ATTR_NAME, OTP_EMAIL_SUBMISSION_LABEL + emailHint)
							.createForm(OTP_SUBMISSION_TEMPLATE);
					context.challenge(challenge);
				}
			} else if (OTP_TYPE_SMS.equals(otpType)) {
			    OtpUtilities.setOtpType(context, OTP_TYPE_SMS);
				String phoneNumber = phoneValues.get(0);
				String phoneHint = " (..." + phoneNumber.substring(phoneNumber.length() - 4) + ")";
				TransientOtpResponse otpResponse = OtpUtilities.sendSmsOtp(context, phoneNumber );
				if (otpResponse != null) {
				    OtpUtilities.setOtpCorrelation(context, otpResponse.correlation);
				    OtpUtilities.setOtpTransactionId(context, otpResponse.transactionId);
					Response challenge = context.form()
							.setAttribute(OTP_FORM_NAME_ATTR_NAME, OTP_FORM_NAME_VALUE)
							.setAttribute(OTP_CORRELATION_FORM_NAME_ATTR_NAME, OTP_CORRELATION_FORM_NAME_VALUE)
							.setAttribute(OTP_CORRELATION_FORM_VALUE_ATTR_NAME, otpResponse.correlation)
							.setAttribute(OTP_TYPE_LABEL_ATTR_NAME, OTP_SMS_SUBMISSION_LABEL + phoneHint)
							.createForm(OTP_SUBMISSION_TEMPLATE);
					context.challenge(challenge);
				}
			}
		}

		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

	public void authenticate(AuthenticationFlowContext context) {
		final String methodName = "authenticate";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);
		
		if (context.getUser() == null) {
		    context.challenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsg2FARequired")));
            return;
		}

		String email = context.getUser().getEmail();
		List<String> phoneValues = context.getUser().getAttribute(OTP_SMS_ATTR_NAME);

		if (email != null && !phoneValues.isEmpty()) {
			// Allow user to select OTP method
		    String emailHint = "(" + email.substring(0,3) + "..." + email.substring(email.indexOf("@"), email.length()) + ")";
	        String phoneHint = "(..." + phoneValues.get(0).substring(phoneValues.get(0).length() - 4) + ")";
			Response challenge = context.form()
			        .setAttribute(OTP_EMAIL_HINT, emailHint)
			        .setAttribute(OTP_SMS_HINT, phoneHint)
					.createForm(OTP_SELECTION_TEMPLATE);
			context.challenge(challenge);
		} else if (email != null) {
		    String emailHint = " (" + email.substring(0,3) + "..." + email.substring(email.indexOf("@"), email.length()) + ")";
		    OtpUtilities.setOtpType(context, OTP_TYPE_EMAIL);
			TransientOtpResponse otpResponse = OtpUtilities.sendEmailOtp(context, email);
			if (otpResponse != null) {
			    OtpUtilities.setOtpCorrelation(context, otpResponse.correlation);
			    OtpUtilities.setOtpTransactionId(context, otpResponse.transactionId);
				Response challenge = context.form()
						.setAttribute(OTP_FORM_NAME_ATTR_NAME, OTP_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_NAME_ATTR_NAME, OTP_CORRELATION_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_VALUE_ATTR_NAME, otpResponse.correlation)
						.setAttribute(OTP_TYPE_LABEL_ATTR_NAME, OTP_EMAIL_SUBMISSION_LABEL + emailHint)
						.createForm(OTP_SUBMISSION_TEMPLATE);
				context.challenge(challenge);
			}
		} else if (!phoneValues.isEmpty()) {
		    OtpUtilities.setOtpType(context, OTP_TYPE_SMS);
			String phoneNumber = phoneValues.get(0);
			String phoneHint = " (..." + phoneNumber.substring(phoneNumber.length() - 4) + ")";
			TransientOtpResponse otpResponse = OtpUtilities.sendSmsOtp(context, phoneNumber );
			if (otpResponse != null) {
			    OtpUtilities.setOtpCorrelation(context, otpResponse.correlation);
			    OtpUtilities.setOtpTransactionId(context, otpResponse.transactionId);
				Response challenge = context.form()
						.setAttribute(OTP_FORM_NAME_ATTR_NAME, OTP_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_NAME_ATTR_NAME, OTP_CORRELATION_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_VALUE_ATTR_NAME, otpResponse.correlation)
						.setAttribute(OTP_TYPE_LABEL_ATTR_NAME, OTP_SMS_SUBMISSION_LABEL + phoneHint)
						.createForm(OTP_SUBMISSION_TEMPLATE);
				context.challenge(challenge);
			}
		} else {
		    context.forceChallenge(FormUtilities.createErrorPage(context, new FormMessage("errorMsgMissingEmailAndPhoneNumber")));
            return;
		}

		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

	public void close() {
		final String methodName = "close";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}


	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "configuredFor";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, session, realm, user);
		
		boolean result = true;
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, result);
		return result;
	}

	public boolean requiresUser() {
		final String methodName = "requiresUser";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);

		// Return true because this authenticator is used for a 2FA scenario
		boolean result = true;

		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, result);
		return result;
	}

	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "setRequiredActions";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, session, realm, user);
		// no-op if we don't want to add any required actions to the login flow
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}
}
