package com.ccc.mail.ssl.truststores;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * DummyTrustManager - NOT SECURE
 * This is used when connecting to a development server
 * or a server used with a certificate you know you can trust 
 * immediately.
 */
public class DummyTrustManager implements X509TrustManager {

    public void checkClientTrusted(X509Certificate[] cert, String authType) {
    // everything is trusted
    }

    public void checkServerTrusted(X509Certificate[] cert, String authType) {
    // everything is trusted
    }

    public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[0];
    }
}