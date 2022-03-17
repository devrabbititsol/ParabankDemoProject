package com.utilities;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class OAuthUtilities {
	
	private static HttpClient unsafeHttpClient;

    static {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            unsafeHttpClient = HttpClients.custom().setSSLContext(sslContext)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public static HttpClient getClient() {
        return unsafeHttpClient;
    }
    
	public static String getAccessTokenFromOAuthData(String url, String ClientId, String ClientSecret) throws UnirestException {
		HttpClient creepyClient = getClient();
        Unirest.setHttpClient(creepyClient);
		HttpResponse<String> response = Unirest.post(url)
				  .header("content-type", "application/x-www-form-urlencoded")
				  .body("grant_type=client_credentials&client_id=" + ClientId + "&client_secret=" + ClientSecret )
				  .asString();
		return response.getBody().toString();
	}

}
