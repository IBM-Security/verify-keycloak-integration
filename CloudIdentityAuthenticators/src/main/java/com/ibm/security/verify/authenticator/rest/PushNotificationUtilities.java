package com.ibm.security.verify.authenticator.rest;

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

import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class PushNotificationUtilities {
    
    private static Logger logger = Logger.getLogger(PushNotificationUtilities.class);
    final public static String PUSH_NOTIFICATION_AUTHENTICATOR_ID_ATTR_NAME = "push.notification.authenticator.id";
    final public static String PUSH_NOTIFICATION_TRANSACTION_ID_ATTR_NAME = "push.notification.transaction.id";
    
    
    public static Pair<String, String> getSignatureEnrollmentAuthenticatorId(AuthenticationFlowContext context, String ciUserId) {
        final String methodName = "getSignatureEnrollmentId";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, ciUserId);
        
        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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
            EntityUtils.consume(response.getEntity());
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
                IBMSecurityVerifyLoggingUtilities.error(logger, methodName, String.format("%s: $s", statusCode, responseBody));
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
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, (result != null) ? result.toString() : "null");
        return result;
    }
    
    public static String sendPushNotification(AuthenticationFlowContext context, String sigId, String authenticatorId) {
        final String methodName = "sendPushNotification";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, authenticatorId, sigId);
        
        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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
            EntityUtils.consume(response.getEntity());
            if (statusCode == 202) {
                Pattern transactionIdExtraction = Pattern.compile("\"authenticatorId\":.*\"id\":\\s*\"([a-fA-F0-9\\-]+)\"");
                Matcher matcher = transactionIdExtraction.matcher(responseBody);
                
                if (matcher.find()) {
                    transactionId = matcher.group(1);
                    context.getAuthenticationSession().setAuthNote(PUSH_NOTIFICATION_AUTHENTICATOR_ID_ATTR_NAME, authenticatorId);
                    context.getAuthenticationSession().setAuthNote(PUSH_NOTIFICATION_TRANSACTION_ID_ATTR_NAME, transactionId);
                }
            } else {
                IBMSecurityVerifyLoggingUtilities.error(logger, methodName, String.format("%s: $s", statusCode, responseBody));
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
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, transactionId);
        return transactionId;
    }
    
    public static String getPushNotificationVerification(AuthenticationFlowContext context) {
        final String methodName = "getPushNotificationVerification";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName);
        
        String authenticatorId = context.getAuthenticationSession().getAuthNote(PUSH_NOTIFICATION_AUTHENTICATOR_ID_ATTR_NAME);
        String transactionId = context.getAuthenticationSession().getAuthNote(PUSH_NOTIFICATION_TRANSACTION_ID_ATTR_NAME);
        if (authenticatorId == null || transactionId == null) {
            // TODO: Error!
        }
        
        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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
            EntityUtils.consume(response.getEntity());
            if (statusCode == 200) {
                Pattern idExtraction = Pattern.compile("\"state\":\\s*\"([a-zA-Z\\_]+)\"");
                Matcher matcher = idExtraction.matcher(responseBody);
                if (matcher.find()) {
                    pushNotificationState = matcher.group(1);
                }
            } else {
                IBMSecurityVerifyLoggingUtilities.error(logger, methodName, String.format("%s: $s", statusCode, responseBody));
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
        
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, pushNotificationState);
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
