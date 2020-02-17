package com.ibm.security.access.authenticator.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;

import com.ibm.security.access.authenticator.utils.CloudIdentityLoggingUtilites;

public class CloudIdentityUtilities {
	
	public static final String CONFIG_TENANT_FQDN = "tenant.fqdn";
	public static final String CONFIG_CLIENT_ID = "client.id";
	public static final String CONFIG_CLIENT_SECRET = "client.secret";

	private static Logger logger = Logger.getLogger(CloudIdentityUtilities.class);
	
	public static String getAccessToken(AuthenticationFlowContext context) {
		final String methodName = "getAccessToken";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);
		String accessToken = null;
		
		String tenantHostname = getTenantHostname(context);
		CloseableHttpClient httpClient = null;
		try {
			AuthenticatorConfigModel authenticatorConfigModel = context.getAuthenticatorConfig();
			if (authenticatorConfigModel != null) {
				Map<String, String> authenticatorConfig = authenticatorConfigModel.getConfig();
				if (authenticatorConfig != null) {
					// Load tenant configuration
					String clientId = authenticatorConfig.get(CloudIdentityUtilities.CONFIG_CLIENT_ID);
					String clientSecret = authenticatorConfig.get(CloudIdentityUtilities.CONFIG_CLIENT_SECRET);
					
					// Request for the access token
					httpClient = HttpClients.createDefault();
					URI uri = new URIBuilder()
							.setScheme("https")
							.setHost(tenantHostname)
							.setPath("/v1.0/endpoint/default/token")
							.build();
					HttpPost post = new HttpPost(uri);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("client_id", clientId));
					params.add(new BasicNameValuePair("client_secret", clientSecret));
					params.add(new BasicNameValuePair("grant_type", "client_credentials"));
					post.setEntity(new UrlEncodedFormEntity(params));
					CloseableHttpResponse response = httpClient.execute(post);
					int statusCode = response.getStatusLine().getStatusCode();
					String responseBody = EntityUtils.toString(response.getEntity());
					response.close();
					if (statusCode == 200) {
						Pattern accessTokenExtraction = Pattern.compile("\"access_token\":\"([a-zA-Z0-9]+)\"");
						Matcher matcher = accessTokenExtraction.matcher(responseBody);
						if (matcher.find()) {
							accessToken = matcher.group(1);
						}
					}
				}
			}
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
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, accessToken);
		return accessToken;
	}
	
	public static String getTenantHostname(AuthenticationFlowContext context) {
		final String methodName = "getTenantHostname";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);
		
		String tenantHostname = null;
		AuthenticatorConfigModel authenticatorConfigModel = context.getAuthenticatorConfig();
		if (authenticatorConfigModel != null) {
			Map<String, String> authenticatorConfig = authenticatorConfigModel.getConfig();
			if (authenticatorConfig != null) {
				// Load tenant configuration
				tenantHostname = authenticatorConfig.get(CloudIdentityUtilities.CONFIG_TENANT_FQDN);
			}
		}
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, tenantHostname);
		return tenantHostname;
	}
	
	public static UserModel matchCIUserIdToUserModel(AuthenticationFlowContext context, String userId) {
		final String methodName = "matchCIUserIdToUserModel";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context, userId);
		
		List<UserModel> users = context.getSession().users().getUsers(context.getRealm());
		UserModel matchingUser = null;
		for (int i = 0; i < users.size(); i++) {
			UserModel iterUser = users.get(i);
			List<String> cloudIdentityUserIdValues = iterUser.getAttribute("cloudIdentity.userId");
			if (!cloudIdentityUserIdValues.isEmpty()) {
				if (userId.equals(cloudIdentityUserIdValues.get(0))){
					matchingUser = iterUser;
					i = users.size();
				}
			}
		}
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, matchingUser);
		return matchingUser;
	}
	
	public static String getCIUserId(UserModel user) {
		final String methodName = "getCIUserId";
		CloudIdentityLoggingUtilites.entry(logger, methodName, user);
		
		String userId = null;
		List<String> cloudIdentityUserIdValues = user.getAttribute("cloudIdentity.userId");
		if (!cloudIdentityUserIdValues.isEmpty()) {
			userId = cloudIdentityUserIdValues.get(0);
		}
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, userId);
		return userId;
	}
	
	public static void setCIUserId(UserModel user, String ciUserId) {
		final String methodName = "setCIUserId";
		CloudIdentityLoggingUtilites.entry(logger, methodName, user, ciUserId);
		
		user.setSingleAttribute("cloudIdentity.userId", ciUserId);
		
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}
	
	public static boolean createCIShadowUser(AuthenticationFlowContext context, UserModel user) {
		final String methodName = "createCIShadowUser";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context, user);
		
		boolean result = false;
		String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
		String accessToken = CloudIdentityUtilities.getAccessToken(context);
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			URI uri = new URIBuilder()
					.setScheme("https")
					.setHost(tenantHostname)
					.setPath("/v2.0/Users")
					.build();
			HttpPost postRequest = new HttpPost(uri);
			postRequest.addHeader("Authorization", "Bearer " + accessToken);
			postRequest.addHeader("Accept", "application/scim+json");
			postRequest.addHeader("Content-type", "application/scim+json");
			String createUserPayload = String.format(
				"{\"userName\": \"%s\",\"urn:ietf:params:scim:schemas:extension:ibm:2.0:Notification\": {\"notifyType\": \"NONE\"}, \"externalId\": \"%s\", \"emails\": [{\"type\": \"work\", \"value\": \"%s\"}], \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:User\", \"urn:ietf:params:scim:schemas:extension:ibm:2.0:Notification\"]}",
				user.getId(),
				user.getId(),
				user.getEmail()
			);
			postRequest.setEntity(new StringEntity(createUserPayload));
			CloseableHttpResponse response = httpClient.execute(postRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			logger.logv(Level.TRACE, "{0}#{1} createUser response code [{2}]", CloudIdentityUtilities.class.getName(), methodName, statusCode);
			String responseBody = EntityUtils.toString(response.getEntity());
			logger.logv(Level.TRACE, "{0}#{1} createUser response [{2}]", CloudIdentityUtilities.class.getName(), methodName, responseBody);
			if (statusCode == 201) {
				Pattern idExtraction = Pattern.compile("\"id\":\\s*\"(\\w+)\"");
				Matcher matcher = idExtraction.matcher(responseBody);
				if (matcher.find()) {
					String ciUserId = matcher.group(1);
					if (ciUserId != null) {
						setCIUserId(user, ciUserId);
						result = true;
					}
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
	
	public static void setPromptedPasswordlessRegistration(AuthenticationFlowContext context) {
		final String methodName = "setPromptedPasswordlessRegistration";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);
		
		context.getAuthenticationSession().setAuthNote("prompt.passwordless.registration", Boolean.TRUE.toString());
		
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}
	
	public static void clearPromptedPasswordlessRegistration(AuthenticationFlowContext context) {
		final String methodName = "clearPromptedPasswordlessRegistration";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);
		
		context.getAuthenticationSession().setAuthNote("prompt.passwordless.registration", Boolean.FALSE.toString());
		
		CloudIdentityLoggingUtilites.exit(logger, methodName);
	}
	
	public static boolean hasPromptedPasswordlessRegistration(AuthenticationFlowContext context) {
		final String methodName = "hasPromptedPasswordlessRegistration";
		CloudIdentityLoggingUtilites.entry(logger, methodName, context);
		
		String authNote = context.getAuthenticationSession().getAuthNote("prompt.passwordless.registration");
		boolean hasPrompted = authNote == null ? false : Boolean.valueOf(authNote);
		
		CloudIdentityLoggingUtilites.exit(logger, methodName, hasPrompted);
		return hasPrompted;
	}
}
