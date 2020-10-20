package neto.com.mx.verificapedidocedis.cliente;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by dramirezr on 23/03/2018.
 */

class HttpsTrustManagerBKS implements X509TrustManager {
    protected List<X509TrustManager> x509TrustManagers = new ArrayList<>();


    protected HttpsTrustManagerBKS(KeyStore... keystores){
        List<TrustManagerFactory> trustManagerFactories = new ArrayList<>();
        TrustManagerFactory original = null;
        try {
            original = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm());
            original.init((KeyStore) null);
            trustManagerFactories.add(original);

            for (KeyStore ks : keystores){
                final TrustManagerFactory cert = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm());
                cert.init(ks);
                trustManagerFactories.add(cert);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        //Generar los x509
        for (TrustManagerFactory tmf : trustManagerFactories ) {
            for (TrustManager tm : tmf.getTrustManagers()) {
                if( tm instanceof X509TrustManager){
                    x509TrustManagers.add((X509TrustManager) tm);
                }
            }
        }

        if( trustManagerFactories.size() == 0 )
            throw new RuntimeException("No se pudieron encontrar X509managers.");
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        x509TrustManagers.get(0).checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        for (X509TrustManager tm:  x509TrustManagers) {
            try {
                tm.checkServerTrusted(chain, authType);
                return;
            }catch(CertificateException c){}
        }
        throw new CertificateException();
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return x509TrustManagers.toArray(new X509Certificate[x509TrustManagers.size()]);
    }
}

public class HttpsSSLSocketFactoryBKS extends SSLSocketFactory {
    protected SSLContext sslContext = null;

    public HttpsSSLSocketFactoryBKS(KeyStore keyStore)
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
    {
        //super(null, null, null, null, null, null);
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{ new HttpsTrustManagerBKS( keyStore ) },null);
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return sslContext.getDefaultSSLParameters().getCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return sslContext.getDefaultSSLParameters().getCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return super.createSocket();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return null;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return null;
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return sslContext.getSocketFactory().createSocket(s, host, port, autoClose);
    }
}