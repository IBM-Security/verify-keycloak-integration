package com.ibm.security.access.authenticator.otp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;
import com.ibm.security.access.authenticator.rest.CloudIdentityUtilities;

public class CloudIdentityOTPAuthenticator implements Authenticator {

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
	private static final String OTP_EMAIL_SUBMISSION_LABEL = "Enter your email OTP";
	private static final String OTP_SMS_SUBMISSION_LABEL = "Enter your SMS OTP";

	private static final String OTP_SMS_ATTR_NAME = "phone.number";
	/**
	 * Constants used in user session attribute storage
	 */
	private static final String OTP_CORRELATION_KEY = "otp.correlation";
	private static final String OTP_TRANSACTION_ID_KEY = "otp.transactionId";
	private static final String OTP_TYPE_KEY = "otp.type";

	private static final String OTP_TYPE_EMAIL = "email";
	private static final String OTP_TYPE_SMS = "sms";

	private Logger logger = Logger.getLogger(CloudIdentityOTPAuthenticator.class);

	public void action(AuthenticationFlowContext context) {
		final String methodName = "action";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);

		String incomingOtpType = getOtpType(context);
		MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
		if (OTP_TYPE_EMAIL.equals(incomingOtpType) || OTP_TYPE_SMS.equals(incomingOtpType)) {
			String correlation = getOtpCorrelation(context);
			String transactionId = getOtpTransactionId(context);
			String otpValue = formParams.getFirst(OTP_FORM_NAME_VALUE);

			boolean isOtpValid = false;
			String otpSubmissionLabel = null;
			if (OTP_TYPE_EMAIL.equals(incomingOtpType)) {
				isOtpValid = validateEmailOtp(context, transactionId, otpValue);
				otpSubmissionLabel = OTP_EMAIL_SUBMISSION_LABEL;
			} else {
				isOtpValid = validateSmsOtp(context, transactionId, otpValue);
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
				setOtpType(context, OTP_TYPE_EMAIL);
				TransientOtpResponse otpResponse = sendEmailOtp(context, email);
				if (otpResponse != null) {
					setOtpCorrelation(context, otpResponse.correlation);
					setOtpTransactionId(context, otpResponse.transactionId);
					Response challenge = context.form()
							.setAttribute(OTP_FORM_NAME_ATTR_NAME, OTP_FORM_NAME_VALUE)
							.setAttribute(OTP_CORRELATION_FORM_NAME_ATTR_NAME, OTP_CORRELATION_FORM_NAME_VALUE)
							.setAttribute(OTP_CORRELATION_FORM_VALUE_ATTR_NAME, otpResponse.correlation)
							.setAttribute(OTP_TYPE_LABEL_ATTR_NAME, OTP_EMAIL_SUBMISSION_LABEL)
							.createForm(OTP_SUBMISSION_TEMPLATE);
					context.challenge(challenge);
				}
			} else if (OTP_TYPE_SMS.equals(otpType)) {
				setOtpType(context, OTP_TYPE_SMS);
				String phoneNumber = phoneValues.get(0);
				TransientOtpResponse otpResponse = sendSmsOtp(context, phoneNumber );
				if (otpResponse != null) {
					setOtpCorrelation(context, otpResponse.correlation);
					setOtpTransactionId(context, otpResponse.transactionId);
					Response challenge = context.form()
							.setAttribute(OTP_FORM_NAME_ATTR_NAME, OTP_FORM_NAME_VALUE)
							.setAttribute(OTP_CORRELATION_FORM_NAME_ATTR_NAME, OTP_CORRELATION_FORM_NAME_VALUE)
							.setAttribute(OTP_CORRELATION_FORM_VALUE_ATTR_NAME, otpResponse.correlation)
							.setAttribute(OTP_TYPE_LABEL_ATTR_NAME, OTP_SMS_SUBMISSION_LABEL)
							.createForm(OTP_SUBMISSION_TEMPLATE);
					context.challenge(challenge);
				}
			}
		}

		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public void authenticate(AuthenticationFlowContext context) {
		final String methodName = "authenticate";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);

		String email = context.getUser().getEmail();
		List<String> phoneValues = context.getUser().getAttribute(OTP_SMS_ATTR_NAME);

		if (email != null && !phoneValues.isEmpty()) {
			// OTP Selection page
			Response challenge = context.form()
					.createForm(OTP_SELECTION_TEMPLATE);
			context.challenge(challenge);
		} else if (email != null) {
			setOtpType(context, OTP_TYPE_EMAIL);
			TransientOtpResponse otpResponse = sendEmailOtp(context, email);
			if (otpResponse != null) {
				setOtpCorrelation(context, otpResponse.correlation);
				setOtpTransactionId(context, otpResponse.transactionId);
				Response challenge = context.form()
						.setAttribute(OTP_FORM_NAME_ATTR_NAME, OTP_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_NAME_ATTR_NAME, OTP_CORRELATION_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_VALUE_ATTR_NAME, otpResponse.correlation)
						.setAttribute(OTP_TYPE_LABEL_ATTR_NAME, OTP_EMAIL_SUBMISSION_LABEL)
						.createForm(OTP_SUBMISSION_TEMPLATE);
				context.challenge(challenge);
			}
		} else if (!phoneValues.isEmpty()) {
			setOtpType(context, OTP_TYPE_SMS);
			String phoneNumber = phoneValues.get(0);
			TransientOtpResponse otpResponse = sendSmsOtp(context, phoneNumber );
			if (otpResponse != null) {
				setOtpCorrelation(context, otpResponse.correlation);
				setOtpTransactionId(context, otpResponse.transactionId);
				Response challenge = context.form()
						.setAttribute(OTP_FORM_NAME_ATTR_NAME, OTP_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_NAME_ATTR_NAME, OTP_CORRELATION_FORM_NAME_VALUE)
						.setAttribute(OTP_CORRELATION_FORM_VALUE_ATTR_NAME, otpResponse.correlation)
						.setAttribute(OTP_TYPE_LABEL_ATTR_NAME, OTP_SMS_SUBMISSION_LABEL)
						.createForm(OTP_SUBMISSION_TEMPLATE);
				context.challenge(challenge);
			}
		} else {
			// neither sms or email available
			context.success();
		}

		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	public void close() {
		final String methodName = "close";
		CloudIdentityLoggingUtilities.entry(logger, methodName);
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}


	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "configuredFor";
		CloudIdentityLoggingUtilities.entry(logger, methodName, session, realm, user);
		
		boolean result = true;
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, result);
		return result;
	}

	public boolean requiresUser() {
		final String methodName = "requiresUser";
		CloudIdentityLoggingUtilities.entry(logger, methodName);

		// Return true because this authenticator is used for a 2FA scenario
		boolean result = true;

		CloudIdentityLoggingUtilities.exit(logger, methodName, result);
		return result;
	}

	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		final String methodName = "setRequiredActions";
		CloudIdentityLoggingUtilities.entry(logger, methodName, session, realm, user);
		// no-op if we don't want to add any required actions to the login flow
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	private TransientOtpResponse sendEmailOtp(AuthenticationFlowContext context, String emailAddress) {
		final String methodName = "sendEmailOtp";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, emailAddress);

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		TransientOtpResponse transientOtpResponse = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v1.0/authnmethods/emailotp/transient/verification")
					.build();
			HttpPost post = new HttpPost(uri);
			post.setEntity(new StringEntity("{\"otpDeliveryEmailAddress\": \"" + emailAddress + "\"}"));
			post.addHeader("Authorization", "Bearer " + accessToken);
			post.addHeader("Content-Type", "application/json");
			post.addHeader("Accept", "application/json");
			CloseableHttpResponse response = httpClient.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			EntityUtils.consume(response.getEntity());
			if (statusCode == 202) {
				String correlation = null;
				Pattern correlationExtraction = Pattern.compile("\"correlation\":\"([a-zA-Z0-9]+)\"");
				Matcher matcher = correlationExtraction.matcher(responseBody);
				if (matcher.find()) {
					correlation = matcher.group(1);
				}
				String transactionId = null;
				Pattern transactionIdExtraction = Pattern.compile("\"id\":\"([a-zA-Z0-9\\-]+)\"");
				matcher = transactionIdExtraction.matcher(responseBody);
				if (matcher.find()) {
					transactionId = matcher.group(1);
				}
				if (correlation != null && transactionId != null) {
					transientOtpResponse = new TransientOtpResponse(correlation, transactionId);
				}
			} else {
                CloudIdentityLoggingUtilities.error(logger, methodName, String.format("%s: $s", statusCode, responseBody));
            }
			response.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		CloudIdentityLoggingUtilities.exit(logger, methodName, transientOtpResponse);
		return transientOtpResponse;
	}

	private TransientOtpResponse sendSmsOtp(AuthenticationFlowContext context, String phoneNumber) {
		final String methodName = "sendSmsOtp";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, phoneNumber);

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		TransientOtpResponse transientOtpResponse = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v1.0/authnmethods/smsotp/transient/verification")
					.build();
			HttpPost post = new HttpPost(uri);
			String normalizedPhoneNumber = "+" + phoneNumber.replaceAll("(?:\\-|\\+)", "");
			post.setEntity(new StringEntity("{\"otpDeliveryMobileNumber\": \"" + normalizedPhoneNumber + "\"}"));
			post.addHeader("Authorization", "Bearer " + accessToken);
			post.addHeader("Content-Type", "application/json");
			post.addHeader("Accept", "application/json");
			CloseableHttpResponse response = httpClient.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			EntityUtils.consume(response.getEntity());
			if (statusCode == 202) {
				String correlation = null;
				Pattern correlationExtraction = Pattern.compile("\"correlation\":\"([a-zA-Z0-9]+)\"");
				Matcher matcher = correlationExtraction.matcher(responseBody);
				if (matcher.find()) {
					correlation = matcher.group(1);
				}
				String transactionId = null;
				Pattern transactionIdExtraction = Pattern.compile("\"id\":\"([a-zA-Z0-9\\-]+)\"");
				matcher = transactionIdExtraction.matcher(responseBody);
				if (matcher.find()) {
					transactionId = matcher.group(1);
				}
				if (correlation != null && transactionId != null) {
					transientOtpResponse = new TransientOtpResponse(correlation, transactionId);
				}
			} else {
			    CloudIdentityLoggingUtilities.error(logger, methodName, String.format("%s: $s", statusCode, responseBody));
			}
			response.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		CloudIdentityLoggingUtilities.exit(logger, methodName, transientOtpResponse);
		return transientOtpResponse;
	}

	static class TransientOtpResponse {
		String correlation;
		String transactionId;

		TransientOtpResponse(String correlation, String transactionId) {
			this.correlation = correlation;
			this.transactionId = transactionId;
		}
	}

	private boolean validateEmailOtp(AuthenticationFlowContext context, String transactionId, String otp) {
		final String methodName = "validateEmailOtp";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, transactionId, otp);

		boolean result = false;

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v1.0/authnmethods/emailotp/transient/verification/" + transactionId)
					.build();
			HttpPost post = new HttpPost(uri);
			post.setEntity(new StringEntity("{\"otp\": \"" + otp + "\"}"));
			post.addHeader("Authorization", "Bearer " + accessToken);
			post.addHeader("Content-Type", "application/json");
			post.addHeader("Accept", "application/json");
			CloseableHttpResponse response = httpClient.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			EntityUtils.consume(response.getEntity());
			if (statusCode == 200) {
				result = true;
			} else {
			    CloudIdentityLoggingUtilities.error(logger, methodName, String.format("%s: $s", statusCode, responseBody));
			}
			response.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		CloudIdentityLoggingUtilities.exit(logger, methodName, result);
		return result;
	}

	private boolean validateSmsOtp(AuthenticationFlowContext context, String transactionId, String otp) {
		final String methodName = "validateSmsOtp";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, transactionId, otp);

		boolean result = false;

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v1.0/authnmethods/smsotp/transient/verification/" + transactionId)
					.build();
			HttpPost post = new HttpPost(uri);
			post.setEntity(new StringEntity("{\"otp\": \"" + otp + "\"}"));
			post.addHeader("Authorization", "Bearer " + accessToken);
			post.addHeader("Content-Type", "application/json");
			post.addHeader("Accept", "application/json");
			CloseableHttpResponse response = httpClient.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			EntityUtils.consume(response.getEntity());
			if (statusCode == 200) {
				result = true;
			} else {
                CloudIdentityLoggingUtilities.error(logger, methodName, String.format("%s: $s", statusCode, responseBody));
            }
			response.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		CloudIdentityLoggingUtilities.exit(logger, methodName, result);
		return result;
	}

	private String getOtpTransactionId(AuthenticationFlowContext context) {
		final String methodName = "getOtpTransactionId";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);

		String trxnId = context.getAuthenticationSession().getUserSessionNotes().get(OTP_TRANSACTION_ID_KEY);

		CloudIdentityLoggingUtilities.exit(logger, methodName, trxnId);
		return trxnId;
	}

	private void setOtpTransactionId(AuthenticationFlowContext context, String transactionId) {
		final String methodName = "setOtpTransactionId";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, transactionId);

		context.getAuthenticationSession().setUserSessionNote(OTP_TRANSACTION_ID_KEY, transactionId);

		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	private String getOtpCorrelation(AuthenticationFlowContext context) {
		final String methodName = "getOtpCorrelation";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);

		String otpCorrelation = context.getAuthenticationSession().getUserSessionNotes().get(OTP_CORRELATION_KEY);

		CloudIdentityLoggingUtilities.exit(logger, methodName, otpCorrelation);
		return otpCorrelation;
	}

	private void setOtpCorrelation(AuthenticationFlowContext context, String correlation) {
		final String methodName = "setOtpCorrelation";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, correlation);

		context.getAuthenticationSession().setUserSessionNote(OTP_CORRELATION_KEY, correlation);

		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

	private String getOtpType(AuthenticationFlowContext context) {
		final String methodName = "getOtpType";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);

		String otpType = context.getAuthenticationSession().getUserSessionNotes().get(OTP_TYPE_KEY);

		CloudIdentityLoggingUtilities.exit(logger, methodName, otpType);
		return otpType;
	}

	private void setOtpType(AuthenticationFlowContext context, String type) {
		final String methodName = "setOtpType";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, type);

		context.getAuthenticationSession().setUserSessionNote(OTP_TYPE_KEY, type.toString());

		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

}
