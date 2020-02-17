package com.ibm.security.access.authenticator.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;

import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilites;

public class FidoUtilities {

	public static final String AUTHN_TYPE_PARAM = "type";
	public static final String AUTHN_AUTHENTICATOR_DATA_PARAM = "authenticator-data";
	public static final String AUTHN_SIGNATURE_PARAM = "signature";
	public static final String AUTHN_RAW_ID_PARAM = "raw-id";
	public static final String AUTHN_ID_PARAM = "id";
	public static final String AUTHN_CLIENT_DATA_JSON_PARAM = "client-data-json";
	public static final String AUTHN_USER_HANDLE_PARAM = "user-handle";

	private static Logger logger = Logger.getLogger(FidoUtilities.class);

	public static String initiateFidoRegistration(AuthenticationFlowContext context, String userId) {
		final String methodName = "initiateFidoRegistration";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context, userId);

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		String relyingPartyId = FidoUtilities.getFidoRelyingPartyId(context);
		CloseableHttpClient httpClient = null;
		String fidoInitResponse = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v2.0/factors/fido2/relyingparties/" + relyingPartyId + "/attestation/options")
					.build();
			HttpPost postRequest = new HttpPost(uri);
			postRequest.addHeader("Authorization", "Bearer " + accessToken);
			postRequest.addHeader("Accept", "application/json");
			postRequest.addHeader("Content-type", "application/json");
			postRequest.setEntity(new StringEntity("{\"attestation\": \"none\", \"userId\": \"" + userId + "\", \"authenticatorSelection\": {\"requireResidentKey\": true, \"authenticatorAttachment\": \"cross-platform\", \"userVerification\": \"preferred\"}}"));
			CloseableHttpResponse response = httpClient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				fidoInitResponse = responseBody;
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

		CloudIdentityLoggingUtilites.exit(logger, methodName, fidoInitResponse);
		return fidoInitResponse;
	}

	public static boolean completeFidoRegistration(AuthenticationFlowContext context, String type, String id, String clientDataJSON, String attestationObject, String nickname) {
		final String methodName = "completeFidoRegistration";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context, type, id, clientDataJSON, attestationObject, nickname);

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		String relyingPartyId = FidoUtilities.getFidoRelyingPartyId(context);
		CloseableHttpClient httpClient = null;
		boolean result = false;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v2.0/factors/fido2/relyingparties/" + relyingPartyId + "/attestation/result")
					.build();
			HttpPost postRequest = new HttpPost(uri);
			postRequest.addHeader("Authorization", "Bearer " + accessToken);
			postRequest.addHeader("Accept", "application/json");
			postRequest.addHeader("Content-type", "application/json");
			String completeRegistrationPayload = "{\"type\": \"{TYPE}\", \"enabled\": true, \"id\": \"{ID}\", \"nickname\": \"{NICKNAME}\", \"rawId\": \"{RAW_ID}\", \"response\": {\"clientDataJSON\": \"{CLIENT_DATA_JSON}\", \"attestationObject\": \"{ATTESTATION_OBJECT}\"}}"
					.replace("{TYPE}", type)
					.replace("{ID}", id)
					.replace("{NICKNAME}", nickname)
					.replace("{RAW_ID}", id)
					.replace("{CLIENT_DATA_JSON}", clientDataJSON)
					.replace("{ATTESTATION_OBJECT}", attestationObject);
			postRequest.setEntity(
				new StringEntity(completeRegistrationPayload
						)
			);
			CloseableHttpResponse response = httpClient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				result = true;
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

		CloudIdentityLoggingUtilites.exit(logger, methodName, result);
		return result;
	}

	public static boolean completeFidoRegistration(AuthenticationFlowContext context) {
		final String methodName = "completeFidoRegistration";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);

		MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
		String type = formParams.getFirst("type");
		String id = formParams.getFirst("id");
		String clientDataJSON = formParams.getFirst("clientDataJSON");
		String attestationObject = formParams.getFirst("attestationObject");
		String nickname = formParams.getFirst("fidoDeviceNickname");

		boolean result = completeFidoRegistration(context, type, id, clientDataJSON, attestationObject, nickname);

		CloudIdentityLoggingUtilites.exit(logger, methodName, result);
		return result;
	}

	/**
	 * Pass null for userId if this is acting as a first factor authentication flow
	 * @param context
	 * @param userId
	 * @return
	 */
	public static String initiateFidoAuthn(AuthenticationFlowContext context, String userId) {
		final String methodName = "initiateFidoAuthn";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context, userId);

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		String relyingPartyId = FidoUtilities.getFidoRelyingPartyId(context);
		CloseableHttpClient httpClient = null;
		String fidoInitResponse = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v2.0/factors/fido2/relyingparties/" + relyingPartyId + "/assertion/options")
					.build();
			HttpPost postRequest = new HttpPost(uri);
			postRequest.addHeader("Authorization", "Bearer " + accessToken);
			postRequest.addHeader("Accept", "application/json");
			postRequest.addHeader("Content-type", "application/json");
			StringBuilder entity = new StringBuilder();
			entity.append("{")
			.append("\"userVerification\": \"preferred\"");
			if (userId != null) {
				entity.append(",")
					.append("\"userId\": \"" + userId + "\"");
			}
			entity.append("}");
			postRequest.setEntity(new StringEntity(entity.toString()));
			CloseableHttpResponse response = httpClient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				fidoInitResponse = responseBody;
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

		CloudIdentityLoggingUtilites.exit(logger, methodName, fidoInitResponse);
		return fidoInitResponse;
	}

	public static String completeFidoAuthentication(
		AuthenticationFlowContext context,
		String type,
		String id,
		String clientDataJSON,
		String authenticatorData,
		String userHandle,
		String signature
	) {
		final String methodName = "completeFidoAuthentication";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context, type, id, clientDataJSON, authenticatorData, userHandle, signature);

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		String relyingPartyId = FidoUtilities.getFidoRelyingPartyId(context);
		CloseableHttpClient httpClient = null;
		String result = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v2.0/factors/fido2/relyingparties/" + relyingPartyId + "/assertion/result")
					.build();
			HttpPost postRequest = new HttpPost(uri);
			postRequest.addHeader("Authorization", "Bearer " + accessToken);
			postRequest.addHeader("Accept", "application/json");
			postRequest.addHeader("Content-type", "application/json");
			String completeRegistrationPayload = "{\"type\": \"{TYPE}\", \"id\": \"{ID}\", \"rawId\": \"{RAW_ID}\", \"response\": {\"clientDataJSON\": \"{CLIENT_DATA_JSON}\", \"authenticatorData\": \"{AUTHENTICATOR_DATA}\", \"userHandle\": \"{USER_HANDLE}\", \"signature\": \"{SIGNATURE}\"}}"
					.replace("{TYPE}", type)
					.replace("{ID}", id)
					.replace("{RAW_ID}", id)
					.replace("{CLIENT_DATA_JSON}", clientDataJSON)
					.replace("{AUTHENTICATOR_DATA}", authenticatorData)
					.replace("{USER_HANDLE}", userHandle)
					.replace("{SIGNATURE}", signature);
			postRequest.setEntity(new StringEntity(completeRegistrationPayload));
			CloseableHttpResponse response = httpClient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				Pattern userIdExtraction = Pattern.compile("\"userId\":\\s*\"([a-zA-Z0-9]+)\"");
				Matcher matcher = userIdExtraction.matcher(responseBody);
				if (matcher.find()) {
					result = matcher.group(1);
				}
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

		CloudIdentityLoggingUtilites.exit(logger, methodName, result);
		return result;
	}

	public static String completeFidoAuthentication(AuthenticationFlowContext context) {
		final String methodName = "completeFidoAuthentication";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);

		MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
		String type = formParams.getFirst(AUTHN_TYPE_PARAM);
		String authenticatorData = formParams.getFirst(AUTHN_AUTHENTICATOR_DATA_PARAM);
		String signature = formParams.getFirst(AUTHN_SIGNATURE_PARAM);
		String id = formParams.getFirst(AUTHN_ID_PARAM);
		String clientDataJSON = formParams.getFirst(AUTHN_CLIENT_DATA_JSON_PARAM);
		String userHandle = formParams.getFirst(AUTHN_USER_HANDLE_PARAM);

		String result = completeFidoAuthentication(context, type, id, clientDataJSON, authenticatorData, userHandle, signature);

		CloudIdentityLoggingUtilites.exit(logger, methodName, result);
		return result;
	}

	public static String getFidoRelyingPartyId(AuthenticationFlowContext context) {
		final String methodName = "getFidoRelyingPartyId";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		String rpId = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/config/v2.0/factors/fido2/relyingparties")
					.build();
			HttpGet getRequest = new HttpGet(uri);
			getRequest.addHeader("Authorization", "Bearer " + accessToken);
			getRequest.addHeader("Accept", "application/json");
			CloseableHttpResponse response = httpClient.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				Pattern idExtraction = Pattern.compile("\"id\":\"([a-fA-F0-9\\-]+)\"");
				Matcher matcher = idExtraction.matcher(responseBody);
				if (matcher.find()) {
					rpId = matcher.group(1);
				}
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

		CloudIdentityLoggingUtilites.exit(logger, methodName, rpId);
		return rpId;
	}

	public static boolean doesUserHaveFidoRegistered(AuthenticationFlowContext context, String userId) {
		final String methodName = "doesUserHaveFidoRegistered";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context, userId);

		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		boolean result= false;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v2.0/factors/fido2/registrations")
					.setParameter("search", "userId=\"" + userId + "\"")
					.build();
			HttpGet getRequest = new HttpGet(uri);
			getRequest.addHeader("Authorization", "Bearer " + accessToken);
			getRequest.addHeader("Accept", "application/json");
			CloseableHttpResponse response = httpClient.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				Pattern idExtraction = Pattern.compile("\"id\":\"[a-fA-F0-9\\-]+\"");
				Matcher matcher = idExtraction.matcher(responseBody);
				if (matcher.find()) {
					result = true;
				}
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

		CloudIdentityLoggingUtilites.exit(logger, methodName, result);
		return result;
	}

	public static String getFidoInitAuthnPayload(AuthenticationFlowContext context) {
		final String methodName = "getFidoInitAuthnPayload";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);

		String initPayload = context.getAuthenticationSession().getUserSessionNotes().get("fido.init.authn");

		CloudIdentityLoggingUtilites.exit(logger, methodName, initPayload);
		return initPayload;
	}

	public static void setFidoInitAuthnPayload(AuthenticationFlowContext context, String initPayload) {
		final String methodName = "setFidoInitAuthnPayload";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context, initPayload);

		context.getAuthenticationSession().setUserSessionNote("fido.init.authn", initPayload);

		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}

}
