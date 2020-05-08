package com.ibm.security.verify.authenticator.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import com.ibm.security.verify.authenticator.utils.IBMSecurityVerifyLoggingUtilities;

public class OtpUtilities {
    private static Logger logger = Logger.getLogger(OtpUtilities.class);
    private static final String OTP_CORRELATION_KEY = "otp.correlation";
    private static final String OTP_TRANSACTION_ID_KEY = "otp.transactionId";
    private static final String OTP_TYPE_KEY = "otp.type";

    public static TransientOtpResponse sendEmailOtp(AuthenticationFlowContext context, String emailAddress) {
        final String methodName = "sendEmailOtp";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, emailAddress);

        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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

        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, transientOtpResponse);
        return transientOtpResponse;
    }

    public static TransientOtpResponse sendSmsOtp(AuthenticationFlowContext context, String phoneNumber) {
        final String methodName = "sendSmsOtp";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, phoneNumber);

        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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

        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, transientOtpResponse);
        return transientOtpResponse;
    }

    public static class TransientOtpResponse {
        public String correlation;
        public String transactionId;

        TransientOtpResponse(String correlation, String transactionId) {
            this.correlation = correlation;
            this.transactionId = transactionId;
        }
    }

    public static boolean validateEmailOtp(AuthenticationFlowContext context, String transactionId, String otp) {
        final String methodName = "validateEmailOtp";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, transactionId, otp);

        boolean result = false;

        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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

    public static boolean validateSmsOtp(AuthenticationFlowContext context, String transactionId, String otp) {
        final String methodName = "validateSmsOtp";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, transactionId, otp);

        boolean result = false;

        String tenantHostname = IBMSecurityVerifyUtilities.getTenantHostname(context);
        String accessToken = IBMSecurityVerifyUtilities.getAccessToken(context);
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

    public static String getOtpTransactionId(AuthenticationFlowContext context) {
        final String methodName = "getOtpTransactionId";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);

        String trxnId = context.getAuthenticationSession().getUserSessionNotes().get(OTP_TRANSACTION_ID_KEY);

        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, trxnId);
        return trxnId;
    }

    public static void setOtpTransactionId(AuthenticationFlowContext context, String transactionId) {
        final String methodName = "setOtpTransactionId";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, transactionId);

        context.getAuthenticationSession().setUserSessionNote(OTP_TRANSACTION_ID_KEY, transactionId);

        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
    }

    public static String getOtpCorrelation(AuthenticationFlowContext context) {
        final String methodName = "getOtpCorrelation";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);

        String otpCorrelation = context.getAuthenticationSession().getUserSessionNotes().get(OTP_CORRELATION_KEY);

        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, otpCorrelation);
        return otpCorrelation;
    }

    public static void setOtpCorrelation(AuthenticationFlowContext context, String correlation) {
        final String methodName = "setOtpCorrelation";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, correlation);

        context.getAuthenticationSession().setUserSessionNote(OTP_CORRELATION_KEY, correlation);

        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
    }

    public static String getOtpType(AuthenticationFlowContext context) {
        final String methodName = "getOtpType";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context);

        String otpType = context.getAuthenticationSession().getUserSessionNotes().get(OTP_TYPE_KEY);

        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName, otpType);
        return otpType;
    }

    public static void setOtpType(AuthenticationFlowContext context, String type) {
        final String methodName = "setOtpType";
        IBMSecurityVerifyLoggingUtilities.entry(logger, methodName, context, type);

        context.getAuthenticationSession().setUserSessionNote(OTP_TYPE_KEY, type.toString());

        IBMSecurityVerifyLoggingUtilities.exit(logger, methodName);
    }

}
