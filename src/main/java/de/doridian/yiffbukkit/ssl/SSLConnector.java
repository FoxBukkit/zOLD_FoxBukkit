package de.doridian.yiffbukkit.ssl;

import de.doridian.yiffbukkit.util.Configuration;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;

public class SSLConnector {
    public static SSLServerSocketFactory allTrustingSocketFactory;

    static {
		char[] keystorePW = Configuration.getValue("server-ssl-keystore-password","SECRET").toCharArray();
		char[] keyPW = Configuration.getValue("server-ssl-key-password","SECRET").toCharArray();

        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {

                    }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {

                    }
                }
            };

			KeyStore kStore  = KeyStore.getInstance(KeyStore.getDefaultType());
			kStore.load(new FileInputStream(Configuration.getValue("server-ssl-keystore", "server.keystore")), keystorePW);

			KeyManagerFactory kmfac = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmfac.init(kStore, keyPW);

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(kmfac.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
            allTrustingSocketFactory = sc.getServerSocketFactory();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
