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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;

import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class QrUtilities {
	
	private static Logger logger = Logger.getLogger(QrUtilities.class);

	public static QrLoginInitiationResponse initiateQrLogin(AuthenticationFlowContext context) {
		final String methodName = "initiateQrLogin";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);
		
		String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
		String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
		String verifyProfileId = VerifyAppUtilities.getVerifyProfileId(context);
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
			EntityUtils.consume(response.getEntity());
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
			} else {
                IBMSecurityVerifyLoggingUtilities.error(logger, methodName, String.format("%s: %s", statusCode, responseBody));
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
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, qrResponse);
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
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, qrLoginId, qrLoginDsi);
		
		String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
		String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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
			EntityUtils.consume(response.getEntity());
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
			} else {
                IBMSecurityVerifyLoggingUtilities.error(logger, methodName, String.format("%s: %s", statusCode, responseBody));
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
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, qrResponse);
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

	public static String getQrLoginId(AuthenticationFlowContext context) {
		final String methodName = "getQrLoginId";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);
		
		String result = context.getAuthenticationSession().getUserSessionNotes().get("qr.login.id");
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, result);
		return result;
	}

	public static void setQrLoginId(AuthenticationFlowContext context, String qrLoginId) {
		final String methodName = "setQrLoginId";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, qrLoginId);
		
		context.getAuthenticationSession().setUserSessionNote("qr.login.id", qrLoginId);
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

	public static String getQrLoginDsi(AuthenticationFlowContext context) {
		final String methodName = "getQrLoginDsi";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);
		
		String result = context.getAuthenticationSession().getUserSessionNotes().get("qr.login.dsi");
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, result);
		return result;
	}

	public static void setQrLoginDsi(AuthenticationFlowContext context, String qrLoginDsi) {
		final String methodName = "setQrLoginDsi";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, qrLoginDsi);
		
		context.getAuthenticationSession().setUserSessionNote("qr.login.dsi", qrLoginDsi);
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}

	public static String getQrLoginImage(AuthenticationFlowContext context) {
		final String methodName = "getQrLoginImage";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);
		
		String result = context.getAuthenticationSession().getUserSessionNotes().get("qr.login.image");
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, result);
		return result;
	}

	public static void setQrLoginImage(AuthenticationFlowContext context, String qrLoginImage) {
		final String methodName = "setQrLoginImage";
		IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, qrLoginImage);
		
		context.getAuthenticationSession().setUserSessionNote("qr.login.image", qrLoginImage);
		
		IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
	}
}
