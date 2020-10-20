package neto.com.mx.verificapedidocedis.cliente;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import neto.com.mx.verificapedidocedis.utiles.GlobalShare;


/**
 * Created by dramirezr on 07/03/2018.
 */

public class HttpsTrustManager implements X509TrustManager {
    private static Context AppContext;
    private static TrustManager[] trustManagers;
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};

    private X509TrustManager defaultTrustManager;
    private X509TrustManager localTrustManager;

    @Override
    public void checkClientTrusted(
            X509Certificate[] x509Certificates, String s)
            throws CertificateException
    {

    }

    @Override
    public void checkServerTrusted(
            X509Certificate[] x509Certificates, String s)
            throws CertificateException {

        try
        {
            defaultTrustManager.checkServerTrusted(x509Certificates, s);
        }catch(Exception e){
            localTrustManager.checkServerTrusted(x509Certificates, s);
        }
    }

    public boolean isClientTrusted(X509Certificate[] chain) {
        return true;
    }

    public boolean isServerTrusted(X509Certificate[] chain) {
        return true;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return _AcceptedIssuers;
    }

    public static void allowAllSSL(Context _context) {
        AppContext = _context;

        Log.i( GlobalShare.logAplicaion, "allowAllSSL : Permitir cualquier dominio...");
        HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });

        SSLContext context = null;
        if (trustManagers == null) {
            trustManagers = new TrustManager[]{new HttpsTrustManager()};
        }
        try {
        context = SSLContext.getInstance("TLS");
        context.init(null, trustManagers, null);
/*
        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            //InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
            InputStream caInput = AppContext.getResources().openRawResource(R.raw.certificado);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                Log.i(GlobalShare.logAplicaion, "allowAllSSL : ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }



            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, "p0JXRjVkbc06fyK".toCharArray());
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            //SSLContext
                    context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), new SecureRandom());*/
/*
            String[] ciphers = context.getSupportedSSLParameters().getCipherSuites();
            ArrayList<String> cipherList = new ArrayList<String>(Arrays.asList(ciphers));

            for (int ii = cipherList.size() - 1; ii >= 0; --ii ){
                Log.d(GlobalShare.logAplicaion, "cipher: "+cipherList.get(ii));
                if ( cipherList.get(ii).contains("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384") ||
                     cipherList.get(ii).contains("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384") ||
                     cipherList.get(ii).contains("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384") ||
                     cipherList.get(ii).contains("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384") ||
                     cipherList.get(ii).contains("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384") ||
                     cipherList.get(ii).contains("TLS_RSA_WITH_AES_256_GCM_SHA384"))
                    cipherList.remove(ii);
            }

            ciphers = cipherList.toArray(new String[cipherList.size()]);
            context.getSupportedSSLParameters().setCipherSuites(ciphers);

            for (int ii = cipherList.size() - 1; ii >= 0; --ii ){
                Log.d(GlobalShare.logAplicaion, ">> cipher: "+cipherList.get(ii));
            }*/



/*
            String[] protocols = context.getSupportedSSLParameters().getProtocols();
            ArrayList<String> protocolList = new ArrayList<String>(Arrays.asList(protocols));

            for (int ii = protocolList.size() - 1; ii >= 0; --ii )
            {
                Log.d(GlobalShare.logAplicaion, "Protocolos: "+protocolList.get(ii));
                if ((protocolList.get(ii).contains("TLSv1.1")) || (protocolList.get(ii).contains("TLSv1.2")))
                    protocolList.remove(ii);
            }

            protocols = protocolList.toArray(new String[protocolList.size()]);
            context.getSupportedSSLParameters().setProtocols(protocols);

            for (int ii = protocolList.size() - 1; ii >= 0; --ii )
            {
                Log.d(GlobalShare.logAplicaion, ">> Protocolos: "+protocolList.get(ii));
                if ((protocolList.get(ii).contains("TLSv1.1")) || (protocolList.get(ii).contains("TLSv1.2")))
                    protocolList.remove(ii);
            }*/

            /*KeyStore trustStore = loadTrustStore();
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);*/
        } catch (NoSuchAlgorithmException e) {
            Log.e( GlobalShare.logAplicaion, "allowAllSSL : NoSuchAlgorithmException : "+e.getLocalizedMessage(), e);
        }/* catch (KeyManagementException e) {
            Log.e(GlobalShare.logAplicaion, "allowAllSSL : KeyManagementException : "+e.getLocalizedMessage(), e);
        } catch(KeyStoreException e){
            Log.e(GlobalShare.logAplicaion, "allowAllSSL : KeyStoreException : "+e.getLocalizedMessage(), e);
        } catch (CertificateException e) {
            Log.e(GlobalShare.logAplicaion, "allowAllSSL : CertificateException : "+e.getLocalizedMessage(), e);
        } catch (IOException e) {
            Log.e(GlobalShare.logAplicaion, "allowAllSSL : IOException : "+e.getLocalizedMessage(), e);
        }*/catch (Exception e) {
            Log.e( GlobalShare.logAplicaion, "allowAllSSL : Exception : "+e.getLocalizedMessage(), e);
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    private static KeyStore loadTrustStore() {
        KeyStore localTrustStore = null;
        try {
            localTrustStore = KeyStore.getInstance("BKS");
            InputStream in = null;//AppContext.getResources().openRawResource(R.raw.des_base64);

            localTrustStore.load(in, "p0JXRjVkbc06fyK".toCharArray());

            //SchemeRegistry chemeRegistry = new SchemeRegistry ();


            //ClientConnectionManager cm;

        } catch (IOException e) {
            Log.e(GlobalShare.logAplicaion, "loadTrustStore : IOException : "+e.getLocalizedMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(GlobalShare.logAplicaion, "loadTrustStore : NoSuchAlgorithmException : "+e.getLocalizedMessage(), e);
        } catch (CertificateException e) {
            Log.e(GlobalShare.logAplicaion, "loadTrustStore : CertificateException : "+e.getLocalizedMessage(), e);
        } catch(KeyStoreException e){
            Log.e(GlobalShare.logAplicaion, "loadTrustStore : KeyStoreException : "+e.getLocalizedMessage(), e);
        }
        return localTrustStore;
    }

    private static KeyStore loadKeyStore() throws KeyStoreException {
        KeyStore localTrustStore = KeyStore.getInstance("BKS");
        return localTrustStore;
    }
}