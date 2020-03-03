package com.ibm.security.access.authenticator.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilities;

public class QrUtilities {
	
	private static Logger logger = Logger.getLogger(QrUtilities.class);
	
	public static String getVerifyProfileId(AuthenticationFlowContext context) {
		final String methodName = "getVerifyProfileId";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		String verifyProfileId = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v1.0/authenticators/clients")
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
					verifyProfileId = matcher.group(1);
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
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, verifyProfileId);
		return verifyProfileId;
	}
	
	public static String initiateVerifyAuthenticatorRegistration(AuthenticationFlowContext context, String userId, String friendlyName) {
		final String methodName = "initiateVerifyAuthenticatorRegistration";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, userId, friendlyName);
		
		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		String verifyProfileId = getVerifyProfileId(context);
		CloseableHttpClient httpClient = null;
		String qrCode = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v1.0/authenticators/initiation")
					.setParameter("qrcodeInResponse", "true")
					.build();
			HttpPost postRequest = new HttpPost(uri);
			postRequest.addHeader("Authorization", "Bearer " + accessToken);
			postRequest.addHeader("Accept", "application/json");
			postRequest.addHeader("Content-type", "application/json");
			postRequest.setEntity(new StringEntity("{\"clientId\": \"" + verifyProfileId + "\", \"owner\": \"" + userId + "\", \"accountName\": \"" + friendlyName + "\"}"));
			CloseableHttpResponse response = httpClient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				Pattern qrExtraction = Pattern.compile("\"qrcode\":\\s*\"([^\"]+)\"");
				Matcher matcher = qrExtraction.matcher(responseBody);
				if (matcher.find()) {
					qrCode = matcher.group(1);
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
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, qrCode);
		return qrCode;
	}
	
	public static QrLoginInitiationResponse initiateQrLogin(AuthenticationFlowContext context) {
		final String methodName = "initiateQrLogin";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		String verifyProfileId = getVerifyProfileId(context);
		CloseableHttpClient httpClient = null;
		QrLoginInitiationResponse qrResponse = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v2.0/factors/qr/authenticate")
					.setParameter("profileId", verifyProfileId)
					.build();
			HttpGet getRequest = new HttpGet(uri);
			getRequest.addHeader("Authorization", "Bearer " + accessToken);
			getRequest.addHeader("Accept", "application/json");
			CloseableHttpResponse response = httpClient.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				String qrCode = null;
				Pattern qrExtraction = Pattern.compile("\"qrCode\":\\s*\"([^\"]+)\"");
				Matcher matcher = qrExtraction.matcher(responseBody);
				if (matcher.find()) {
					qrCode = matcher.group(1);
				}
				String id = null;
				Pattern idExtraction = Pattern.compile("\"id\":\\s*\"([a-fA-F0-9\\-]+)\"");
				matcher = idExtraction.matcher(responseBody);
				if (matcher.find()) {
					id = matcher.group(1);
				}
				String dsi = null;
				Pattern dsiExtraction = Pattern.compile("\"dsi\":\\s*\"([a-zA-Z0-9]+)\"");
				matcher = dsiExtraction.matcher(responseBody);
				if (matcher.find()) {
					dsi = matcher.group(1);
				}
				qrResponse = new QrLoginInitiationResponse(qrCode, id, dsi);
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
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, qrResponse);
		return qrResponse;
	}
	
	public static class QrLoginInitiationResponse {
		public String qrBase64Content;
		public String id;
		public String dsi;
		
		QrLoginInitiationResponse(String qrContent, String id, String dsi) {
			this.qrBase64Content = qrContent;
			this.id = id;
			this.dsi = dsi;
		}
		
		public String toString() {
			return "id=[" + id + "] dsi=[" + dsi + "] qrBase64Content=[" + qrBase64Content + "]";
		}
	}
	
	public static QrLoginResponse pollQrLoginStatus(AuthenticationFlowContext context, String qrLoginId, String qrLoginDsi) {
		final String methodName = "pollQrLoginStatus";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, qrLoginId, qrLoginDsi);
		
		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		CloseableHttpClient httpClient = null;
		QrLoginResponse qrResponse = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v2.0/factors/qr/authenticate/" + qrLoginId)
					.setParameter("dsi", qrLoginDsi)
					.build();
			HttpGet getRequest = new HttpGet(uri);
			getRequest.addHeader("Authorization", "Bearer " + accessToken);
			getRequest.addHeader("Accept", "application/json");
			CloseableHttpResponse response = httpClient.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(response.getEntity());
			if (statusCode == 200) {
				String state = null;
				Pattern stateExtraction = Pattern.compile("\"state\":\\s*\"([a-zA-Z]+)\"");
				Matcher matcher = stateExtraction.matcher(responseBody);
				if (matcher.find()) {
					state = matcher.group(1);
				}
				String userId = null;
				Pattern userIdExtraction = Pattern.compile("\"userId\":\\s*\"([a-zA-Z0-9]+)\"");
				matcher = userIdExtraction.matcher(responseBody);
				if (matcher.find()) {
					userId = matcher.group(1);
				}
				qrResponse = new QrLoginResponse(state, userId);
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
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, qrResponse);
		return qrResponse;
	}
	
	public static class QrLoginResponse {
		public String state;
		public String userId;
		
		QrLoginResponse(String state, String userId) {
			this.state = state;
			this.userId = userId;
		}
	}
	
	public static boolean doesUserHaveVerifyRegistered(AuthenticationFlowContext context, String userId) {
		final String methodName = "doesUserHaveVerifyRegistered";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, userId);
		
		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		boolean result= false;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v1.0/authenticators")
					.setParameter("search", "owner=\"" + userId + "\"")
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
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, result);
		return result;
	}
	
	public static String getQrLoginId(AuthenticationFlowContext context) {
		final String methodName = "getQrLoginId";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
		String result = context.getAuthenticationSession().getUserSessionNotes().get("qr.login.id");
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, result);
		return result;
	}
	
	public static void setQrLoginId(AuthenticationFlowContext context, String qrLoginId) {
		final String methodName = "setQrLoginId";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, qrLoginId);
		
		context.getAuthenticationSession().setUserSessionNote("qr.login.id", qrLoginId);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}
	
	public static String getQrLoginDsi(AuthenticationFlowContext context) {
		final String methodName = "getQrLoginDsi";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
		String result = context.getAuthenticationSession().getUserSessionNotes().get("qr.login.dsi");
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, result);
		return result;
	}
	
	public static void setQrLoginDsi(AuthenticationFlowContext context, String qrLoginDsi) {
		final String methodName = "setQrLoginDsi";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, qrLoginDsi);
		
		context.getAuthenticationSession().setUserSessionNote("qr.login.dsi", qrLoginDsi);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}
	
	public static String getQrLoginImage(AuthenticationFlowContext context) {
		final String methodName = "getQrLoginImage";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
		String result = context.getAuthenticationSession().getUserSessionNotes().get("qr.login.image");
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, result);
		return result;
	}
	
	public static void setQrLoginImage(AuthenticationFlowContext context, String qrLoginImage) {
		final String methodName = "setQrLoginImage";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, qrLoginImage);
		
		context.getAuthenticationSession().setUserSessionNote("qr.login.image", qrLoginImage);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}
	
	public static String getVerifyRegistrationQrCode(AuthenticationFlowContext context) {
		final String methodName = "getVerifyRegistrationQrCode";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context);
		
		String result = context.getAuthenticationSession().getUserSessionNotes().get("verify.registration.qr");
		
		CloudIdentityLoggingUtilities.exit(logger, methodName, result);
		return result;
	}
	
	public static void setVerifyRegistrationQrCode(AuthenticationFlowContext context, String qrCode) {
		final String methodName = "setVerifyRegistrationQrCode";
		CloudIdentityLoggingUtilities.entry(logger, methodName, context, qrCode);
		
		context.getAuthenticationSession().setUserSessionNote("verify.registration.qr", qrCode);
		
		CloudIdentityLoggingUtilities.exit(logger, methodName);
	}

}
