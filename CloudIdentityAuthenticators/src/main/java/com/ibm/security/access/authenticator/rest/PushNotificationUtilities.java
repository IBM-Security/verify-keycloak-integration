package com.ibm.security.access.authenticator.rest;

import java.io.IOException;
import java.net.InetAddress;
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

public class PushNotificationUtilities {
    
    private static Logger logger = Logger.getLogger(PushNotificationUtilities.class);
    final public static String PUSH_NOTIFICATION_AUTHENTICATOR_ID = "push.notification.authenticator.id";
    final public static String PUSH_NOTIFICATION_TRANSACTION_ID = "push.notification.transaction.id";
    
    
    public static Pair<String, String> getSignatureEnrollmentAuthenticatorId(AuthenticationFlowContext context, String ciUserId) {
        // https://{{tenant}}/v1.0/authnmethods/signatures?search=owner="ciUserId"
        final String methodName = "getSignatureEnrollmentId";
        CloudIdentityLoggingUtilities.entry(logger, methodName, ciUserId);
        
        String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
        String accessToken = CloudIdentityUtilities.getAccessToken(context);
        CloseableHttpClient httpClient = null;
        String sigId = null;
        String authId = null;

        try {
            httpClient = HttpClients.createDefault();
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(tenantHostname)
                    .setPath("/v1.0/authnmethods/signatures")
                    .setParameter("search", "owner=\"" + ciUserId + "\"")
                    .build();
            HttpGet getRequest = new HttpGet(uri);
            getRequest.addHeader("Authorization", "Bearer " + accessToken);
            getRequest.addHeader("Accept", "application/json");
            getRequest.addHeader("Content-type", "application/json");
            CloseableHttpResponse response = httpClient.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode == 200) {
                Pattern sigIdExtraction = Pattern.compile("\"id\":\\s*\"([a-fA-F0-9\\-]+)\"");
                Pattern authenticatorIdExtraction = Pattern.compile("\"authenticatorId\":\\s*\"([a-fA-F0-9\\-]+)\"");
                Matcher sigMatcher = sigIdExtraction.matcher(responseBody);
                Matcher authMatcher = authenticatorIdExtraction.matcher(responseBody);
                if (sigMatcher.find()) {
                    sigId = sigMatcher.group(1);
                }
                if (authMatcher.find()) {
                    authId = authMatcher.group(1);
                }
            } else {
                // TODO: Log error response
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
        
        Pair<String,String> result = new Pair<String,String>(sigId, authId);
        CloudIdentityLoggingUtilities.exit(logger, methodName, (result != null) ? result.toString() : "null");
        return result;
    }
    
    public static String sendPushNotification(AuthenticationFlowContext context, String sigId, String authenticatorId) {
        // TODO:
        // https://{{tenant}}/v1.0/authenticators/authenticatorId/verifications
        // get transactionId
        // store transactionId in session auth note
        // context.getAuthenticationSession().setAuthNote(push.notification.transaction.id, transactionId);
        
        final String methodName = "sendPushNotification";
        CloudIdentityLoggingUtilities.entry(logger, methodName, context, authenticatorId, sigId);
        
        String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
        String accessToken = CloudIdentityUtilities.getAccessToken(context);
        CloseableHttpClient httpClient = null;
        String transactionId = null;
        try {
            httpClient = HttpClients.createDefault();
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(tenantHostname)
                    .setPath("/v1.0/authenticators/"+ authenticatorId + "/verifications")
                    .build();
            HttpPost postRequest = new HttpPost(uri);
            postRequest.addHeader("Authorization", "Bearer " + accessToken);
            postRequest.addHeader("Accept", "application/json");
            postRequest.addHeader("Content-type", "application/json");
            
            String originIpAddress = InetAddress.getLocalHost().getHostAddress();
            // TODO: Get user browser information
            String originUserAgent = "Mozilla Firefox 11";
            
            postRequest.setEntity(
                    new StringEntity(PushNotificationPostRequestBody.generate(sigId, originIpAddress, originUserAgent)));
            CloseableHttpResponse response = httpClient.execute(postRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode == 202) {
                Pattern transactionIdExtraction = Pattern.compile("\"authenticatorId\":.*\"id\":\\s*\"([a-fA-F0-9\\-]+)\"");
                Matcher matcher = transactionIdExtraction.matcher(responseBody);
                
                if (matcher.find()) {
                    transactionId = matcher.group(1);
                    context.getAuthenticationSession().setAuthNote(PUSH_NOTIFICATION_AUTHENTICATOR_ID, authenticatorId);
                    context.getAuthenticationSession().setAuthNote(PUSH_NOTIFICATION_TRANSACTION_ID, transactionId);
                }
            } else {
                // TODO: Log error response
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
        CloudIdentityLoggingUtilities.exit(logger, methodName, transactionId);
        return transactionId;
    }
    
    public static String getPushNotificationVerification(AuthenticationFlowContext context) {
        // TODO:
        // get authenticatorId and transactionId from auth note
        // https://{{tenant}}/v1.0/authenticators/a93d0c87-01bb-4a04-be77-6b5494bb201e/verifications?search=id="3483cd27-abb0-48c7-a7b5-b23459cb34d7"
        final String methodName = "getPushNotificationVerification";
        CloudIdentityLoggingUtilities.entry(logger, methodName);
        
        String authenticatorId = context.getAuthenticationSession().getAuthNote(PUSH_NOTIFICATION_AUTHENTICATOR_ID);
        String transactionId = context.getAuthenticationSession().getAuthNote(PUSH_NOTIFICATION_TRANSACTION_ID);
        if (authenticatorId == null || transactionId == null) {
            // TODO: Error!
        }
        
        String tenantHostname = CloudIdentityUtilities.getTenantHostname(context);
        String accessToken = CloudIdentityUtilities.getAccessToken(context);
        CloseableHttpClient httpClient = null;
        String pushNotificationState = null; 

        try {
            httpClient = HttpClients.createDefault();
            URI uri = new URIBuilder()
                    .setScheme("https")
                    .setHost(tenantHostname)
                    .setPath("/v1.0/authenticators/" + authenticatorId + "/verifications")
                    .setParameter("search", "id=\"" + transactionId + "\"")
                    .build();
            HttpGet getRequest = new HttpGet(uri);
            getRequest.addHeader("Authorization", "Bearer " + accessToken);
            getRequest.addHeader("Accept", "application/json");
            getRequest.addHeader("Content-type", "application/json");
            CloseableHttpResponse response = httpClient.execute(getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            if (statusCode == 200) {
                Pattern idExtraction = Pattern.compile("\"state\":\\s*\"([a-zA-Z\\_]+)\"");
                Matcher matcher = idExtraction.matcher(responseBody);
                if (matcher.find()) {
                    pushNotificationState = matcher.group(1);
                }
            } else {
                // TODO: Log error response
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
        
        CloudIdentityLoggingUtilities.exit(logger, methodName, pushNotificationState);
        return pushNotificationState;
    }
    
    public static class PushNotificationPostRequestBody {
        public static String generate(String sigId, String originIpAddress, String originUserAgent) {
            return String.format(
                    "{\"expiresIn\": 120, \"pushNotification\": " + 
                    "{\"sound\": \"default\", \"message\": \"You have a pending authentication challenge\", " + 
                    "\"useDevCreds\": true, \"send\": true, \"title\": \"IBM Verify\"}, " + 
                    "\"authenticationMethods\": [{\"methodType\": \"signature\", " + 
                    "\"id\": \"%s\"}], \"logic\": \"OR\", " + 
                    "\"transactionData\": {\"message\": \"You have a pending authentication challenge\", " + 
                    "\"originIpAddress\": \"%s\", \"originUserAgent\": \"%s\"}}", sigId, originIpAddress, originUserAgent);
        }
    }
    
    public static class Pair<K, V> {         
        public final K key;
        public final V value;

        public Pair(K key, V value) {         
            this.key = key;
            this.value = value;
        }
        
        public String toString() {
            return key.toString() + ":" + value.toString();
        }
    }
}
