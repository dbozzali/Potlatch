package org.coursera.androidcapstone.common.unsafe;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class UnsafeHttpsClient {
    /**
     * This is an example of an HTTP client that does not properly
     * validate SSL certificates that are used for HTTPS. You should
     * NEVER use a client like this in a production application. Self-signed
     * certificates are usually only OK for testing purposes, such as
     * this use case.
     *
     * @author jules
     */
    public static HttpClient createUnsafeClient() {
        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslSocketFactory =
                new SSLConnectionSocketFactory(builder.build());
            CloseableHttpClient httpclient =
                HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
            return httpclient;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HttpClient getHttpsClient() {
        try {
            SSLContext sslcontext = SSLContexts.custom().useSSL().build();
            sslcontext.init(null, new X509TrustManager[] { new HttpsTrustManager() }, new SecureRandom());
            SSLConnectionSocketFactory factory =
                new SSLConnectionSocketFactory(sslcontext,
                                               SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient =
                HttpClients.custom().setSSLSocketFactory(factory).build();
            return httpclient;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class HttpsTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[] { };
    }
}
