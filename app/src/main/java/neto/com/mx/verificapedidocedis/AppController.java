package neto.com.mx.verificapedidocedis;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import neto.com.mx.verificapedidocedis.cliente.HttpsTrustManager;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.GlobalShare;
import neto.com.mx.verificapedidocedis.utiles.LruBitmapCache;


public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public RequestQueue getmRequestQueueSSL(){
        if (mRequestQueue != null) return mRequestQueue;
        final KeyStore ksBKS;
        try {
            ksBKS = KeyStore.getInstance("BKS");
            InputStream caInput = new BufferedInputStream(mInstance.getResources().openRawResource( R.raw.certificado_prod));
            ksBKS.load(caInput, Constantes.LLAVE_BKS);

            // Crea TrustManager para confiar en el CA en el KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(ksBKS);

            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException { }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException { }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[0];
                        }
                    }
            };

            // Crea un SSLContext que usa el nuevo TrustManager
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            //sslcontext.init(null, tmf.getTrustManagers(), null);
            sslcontext.init(null, trustAllCerts,  new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return arg0.equalsIgnoreCase(arg1.getPeerHost());
                }
            });
            HttpsTrustManager.allowAllSSL(mInstance);

            mRequestQueue = Volley.newRequestQueue( mInstance,
                    new HurlStack(null, sslcontext.getSocketFactory()));
        } catch (KeyStoreException e) {
            Log.e( GlobalShare.logAplicaion, "getQueue : KeyStoreException : "+e.getMessage(), e);
        } catch (CertificateException e) {
            Log.e(GlobalShare.logAplicaion, "getQueue : CertificateException : "+e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(GlobalShare.logAplicaion, "getQueue : NoSuchAlgorithmException : "+e.getMessage(), e);
        } catch (IOException e) {
            Log.e(GlobalShare.logAplicaion, "getQueue : IOException : "+e.getMessage(), e);
        } catch (KeyManagementException e) {
            Log.e(GlobalShare.logAplicaion, "getQueue : KeyManagementException : "+e.getMessage(), e);
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getmRequestQueueSSL().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}