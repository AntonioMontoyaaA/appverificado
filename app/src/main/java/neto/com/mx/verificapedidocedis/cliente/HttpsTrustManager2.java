package neto.com.mx.verificapedidocedis.cliente;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by dramirezr on 14/03/2018.
 */

public class HttpsTrustManager2 implements X509TrustManager {

    private static TrustManager[] trustManagers;
    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{
            new X509Certificate() {
                @Override
                public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {

                }

                @Override
                public void checkValidity(Date date) throws CertificateExpiredException, CertificateNotYetValidException {

                }

                @Override
                public int getVersion() {
                    return 1;
                }

                @Override
                public BigInteger getSerialNumber() {
                    return new BigInteger("00");
                }

                @Override
                public Principal getIssuerDN() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return "10.81.12.46";
                        }
                    };
                }

                @Override
                public Principal getSubjectDN() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return "10.81.12.46";
                        }
                    };
                }

                @Override
                public Date getNotBefore() {
                    return new Date(1,1,2017);
                }

                @Override
                public Date getNotAfter() {
                    return new Date(1,1,2019);
                }

                @Override
                public byte[] getTBSCertificate() throws CertificateEncodingException {
                    return new byte[0];
                }

                @Override
                public byte[] getSignature() {
                    return new byte[0];
                }

                @Override
                public String getSigAlgName() {
                    return null;
                }

                @Override
                public String getSigAlgOID() {
                    return null;
                }

                @Override
                public byte[] getSigAlgParams() {
                    return new byte[0];
                }

                @Override
                public boolean[] getIssuerUniqueID() {
                    return new boolean[]{ false };
                }

                @Override
                public boolean[] getSubjectUniqueID() {
                    return new boolean[]{ false };
                }

                @Override
                public boolean[] getKeyUsage() {
                    return new boolean[]{ false };
                }

                @Override
                public int getBasicConstraints() {
                    return 0;
                }

                @Override
                public byte[] getEncoded() throws CertificateEncodingException {
                    return new byte[0];
                }

                @Override
                public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {

                }

                @Override
                public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {

                }

                @Override
                public String toString() {
                    return null;
                }

                @Override
                public PublicKey getPublicKey() {
                    return null;
                }

                @Override
                public boolean hasUnsupportedCriticalExtension() {
                    return false;
                }

                @Override
                public Set<String> getCriticalExtensionOIDs() {
                    return null;
                }

                @Override
                public Set<String> getNonCriticalExtensionOIDs() {
                    return null;
                }

                @Override
                public byte[] getExtensionValue(String oid) {
                    return new byte[0];
                }
            }
    };

    @Override
    public void checkClientTrusted(
            X509Certificate[] x509Certificates, String s)
            throws CertificateException {

    }

    @Override
    public void checkServerTrusted(
            X509Certificate[] x509Certificates, String s)
            throws CertificateException {

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

    public static void allowAllSSL() {
        HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }

        });

        SSLContext context = null;
        if (trustManagers == null) {
            trustManagers = new TrustManager[]{new HttpsTrustManager2()};
        }

        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(context
                .getSocketFactory());
    }

}
