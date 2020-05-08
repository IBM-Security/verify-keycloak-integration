package com.ibm.security.verify.authenticator.rest;

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

import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class VerifyAppUtilities {

    private static Logger logger = Logger.getLogger(VerifyAppUtilities.class);

    public static String getVerifyProfileId(AuthenticationFlowContext context) {
        final String methodName = "getVerifyProfileId";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);
        
        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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
            EntityUtils.consume(response.getEntity());
            if (statusCode == 200) {
                Pattern idExtraction = Pattern.compile("\"id\":\"([a-fA-F0-9\\-]+)\"");
                Matcher matcher = idExtraction.matcher(responseBody);
                if (matcher.find()) {
                    verifyProfileId = matcher.group(1);
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
        
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, verifyProfileId);
        return verifyProfileId;
    }

    public static String initiateVerifyAuthenticatorRegistration(AuthenticationFlowContext context, String userId, String friendlyName) {
        final String methodName = "initiateVerifyAuthenticatorRegistration";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, userId, friendlyName);
        
        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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
            EntityUtils.consume(response.getEntity());
            if (statusCode == 200) {
                Pattern qrExtraction = Pattern.compile("\"qrcode\":\\s*\"([^\"]+)\"");
                Matcher matcher = qrExtraction.matcher(responseBody);
                if (matcher.find()) {
                    qrCode = matcher.group(1);
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
        
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, qrCode);
        return qrCode;
    }

    public static boolean doesUserHaveVerifyRegistered(AuthenticationFlowContext context, String userId) {
        final String methodName = "doesUserHaveVerifyRegistered";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, userId);
        
        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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
            EntityUtils.consume(response.getEntity());
            if (statusCode == 200) {
                Pattern idExtraction = Pattern.compile("\"id\":\"[a-fA-F0-9\\-]+\"");
                Matcher matcher = idExtraction.matcher(responseBody);
                if (matcher.find()) {
                    result = true;
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
        
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, result);
        return result;
    }

    public static String getVerifyRegistrationQrCode(AuthenticationFlowContext context) {
        final String methodName = "getVerifyRegistrationQrCode";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);
        
        String result = context.getAuthenticationSession().getUserSessionNotes().get("verify.registration.qr");
        
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, result);
        return result;
    }

    public static void setVerifyRegistrationQrCode(AuthenticationFlowContext context, String qrCode) {
        final String methodName = "setVerifyRegistrationQrCode";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, qrCode);
        
        context.getAuthenticationSession().setUserSessionNote("verify.registration.qr", qrCode);
        
        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
    }
}
