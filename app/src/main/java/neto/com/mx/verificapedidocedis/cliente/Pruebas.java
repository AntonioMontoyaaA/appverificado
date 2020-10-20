package neto.com.mx.verificapedidocedis.cliente;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog2;
import neto.com.mx.verificapedidocedis.utiles.GlobalShare;
import neto.com.mx.verificapedidocedis.utiles.TiposAlert;

/**
 * Created by dramirezr on 12/03/2018.
 */

public class Pruebas {
    private static SecretKeySpec secretKey;
    private static byte[] key;
    {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static void setKey(String myKey)
    {
        MessageDigest sha = null;
        try
        {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt, String secret) throws Exception
    {
        setKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, secretKey);

        return Base64.encodeToString(
                cipher.doFinal( strToEncrypt.getBytes("UTF-8")),
                Base64.DEFAULT );
    }

    public static String cifrar(byte[] valorCifrar, byte[] llave){

        String resultado=null;

        BlockCipher engine = new DESedeEngine();
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine);

        cipher.init(true, new KeyParameter(llave));
        byte[] cipherText = new byte[cipher.getOutputSize(valorCifrar.length)];

        int outputLen = cipher.processBytes(valorCifrar, 0, valorCifrar.length, cipherText, 0);
        try{
            cipher.doFinal(cipherText, outputLen);
        }catch(Exception e){
            e.printStackTrace();
        }

        return cipherText.toString();
    }

    public static String prueba_desarrollo(Context _context)
    {
        String error = "Inicia";
        //HttpsTrustManager2.allowAllSSL();
        URL url = null;
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = null;//new BufferedInputStream(_context.getResources().openRawResource(R.raw.prod_base64));
            Certificate ca = cf.generateCertificate(caInput);
            error = ""+((X509Certificate) ca).getSubjectDN();
            Log.d( GlobalShare.logAplicaion, "ca=" + ((X509Certificate) ca).getSubjectDN());

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            //keyStore.load(null, "p0JXRjVkbc06fyK".toCharArray());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tmf.getTrustManagers(), null);
/**/

            /*SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, null, null);*/

            /*String[] protocols = sslcontext.getSupportedSSLParameters().getProtocols();
            ArrayList<String> protocolList = new ArrayList<String>(Arrays.asList(protocols));

            for (int ii = protocolList.size() - 1; ii >= 0; --ii )
            {
                Log.d(GlobalShare.logAplicaion, "Protocolos: "+protocolList.get(ii));
                if ((protocolList.get(ii).contains("TLSv1.1")) || (protocolList.get(ii).contains("TLSv1.2")))
                    protocolList.remove(ii);
            }

            sslcontext.getSupportedSSLParameters().setProtocols(protocols);*/

            ///SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
            ///HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);

            //https://10.37.140.202:4443/WSSIONRest/ssl/servicio/consultaGenericaDinamica
            //https://10.37.140.202:4443/WSSION/services/ReporteAvisosService?wsdl
            //https://www.servicios.tiendasneto.com/WSSIANMoviles/index.html
            url = new URL("https://www.servicios.tiendasneto.com/WSSIANMoviles/services/WSAbastoMovil/validaPedidoSv");
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setSSLSocketFactory(sslcontext.getSocketFactory());

            //String parametros = "solicitud={\"propiedadSQL\":\"CONSRESUMENINVETARIO_ITDA\",\"cuerpoPeticion\":[{\"indice\":1,\"tipoDato\":\"long\",\"valor\":\"20180103636466\"},{\"indice\":2,\"tipoDato\":\"CUR_SALIDA\",\"valor\":\"0\"},{\"indice\":3,\"tipoDato\":\"::Int\",\"valor\":\"0\"},{\"indice\":4,\"tipoDato\":\"::String\",\"valor\":\"0\"}]}";
            /*String parametros =
                    "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://service.movil.abasto.neto\">\n" +
                    "   <soap:Header/>\n" +
                    "   <soap:Body>\n" +
                    "      <ser:validaPedidoSv>\n" +
                    "         <ser:pedidoIdStr>8zAhBpCysN+qnWAyE5/0yg==</ser:pedidoIdStr>\n" +
                    "         <ser:ipClienteStr>D/zVZSjEl+5j+oP1kaU6cg==</ser:ipClienteStr>\n" +
                    "      </ser:validaPedidoSv>\n" +
                    "   </soap:Body>\n" +
                    "</soap:Envelope>";*/


            conn.setRequestMethod("POST");
            //conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Encoding", "UTF-8");
            //conn.setRequestProperty("ACCEPT-LANGUAGE", "en-US, en;0.5");

            conn.setDoInput(true);
            conn.setDoOutput(true);
            //conn.connect();
            DataOutputStream dstream = new DataOutputStream(conn.getOutputStream());

            dstream.writeBytes("pedidoIdStr=8zAhBpCysN+qnWAyE5/0yg==\n" +
                    "&ipClienteStr=D/zVZSjEl+5j+oP1kaU6cg==\n" );
            dstream.flush();
            dstream.close();

            int response  = conn.getResponseCode();
            error += "response: "+ response + " - "+conn.getResponseMessage();
            //Log.d(GlobalShare.logAplicaion, "respuesta: " + response);

            BufferedReader br  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuffer strBuf = new StringBuffer();
            while((line = br.readLine()) != null){
                strBuf.append(line);
                //Log.d(GlobalShare.logAplicaion, line);
            }
            br.close();

            return strBuf.toString();
        } catch (MalformedURLException e) {
            //e.getCause().printStackTrace();
            //e.printStackTrace();
            error += " > "+e.getLocalizedMessage() + getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, ""+e.getMessage(), e);
        }catch (IOException e) {
            //e.getCause().printStackTrace();
            //e.printStackTrace();
            error += " > "+ e.getLocalizedMessage() + getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, ""+e.getMessage(), e);
        }catch (Exception e) {
            //e.getCause().printStackTrace();
            //e.printStackTrace();
            error += " > "+ e.getLocalizedMessage() + getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, ""+e.getMessage(), e);
        }

        return error;
    }

    public static String pruebaSSLRest(Context _context)
    {
        String error = "Inicia";
        //HttpsTrustManager2.allowAllSSL();
        URL url = null;
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = null;//new BufferedInputStream(_context.getResources().openRawResource(R.raw.des_base64));
            Certificate ca = cf.generateCertificate(caInput);
            error = ""+((X509Certificate) ca).getSubjectDN();
            Log.d( GlobalShare.logAplicaion, "ca=" + ((X509Certificate) ca).getSubjectDN());

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            List<TrustManager> tmL = new ArrayList<TrustManager>();///
            for( TrustManager t : tmf.getTrustManagers())///
                tmL.add(t);///

            tmL.add(new HttpsTrustManager2());

            // Create an SSLContext that uses our TrustManager
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tmL.toArray(new TrustManager[tmL.size()]), null);///
            //sslcontext.init(null, tmf.getTrustManagers(), null);///

            //url = new URL("https://10.81.12.46:4443/WSGenericoMovil/ssl/servicio/consultaGenericaDinamica");
            url = new URL("https://10.81.12.46:4443/WSGenericoMovil/ssl/servicio/consultaGenericaDinamica");
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();

            conn.setSSLSocketFactory(sslcontext.getSocketFactory());
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Encoding", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String parametros = "solicitud={\"propiedadSQL\":\"CONSRESUMENINVETARIO_ITDA\",\"cuerpoPeticion\":[{\"indice\":1,\"tipoDato\":\"long\",\"valor\":\"20180103636466\"},{\"indice\":2,\"tipoDato\":\"CUR_SALIDA\",\"valor\":\"0\"},{\"indice\":3,\"tipoDato\":\"::Int\",\"valor\":\"0\"},{\"indice\":4,\"tipoDato\":\"::String\",\"valor\":\"0\"}]}";

            conn.setDoInput(true);
            conn.setDoOutput(true);

            DataOutputStream dstream = new DataOutputStream(conn.getOutputStream());

            dstream.writeBytes(parametros );
            dstream.flush();
            dstream.close();

            int response  = conn.getResponseCode();
            error += "response: "+ response + " - "+conn.getResponseMessage();

            BufferedReader br  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuffer strBuf = new StringBuffer();
            while((line = br.readLine()) != null){
                strBuf.append(line);
            }
            br.close();

            return strBuf.toString();
        } catch (MalformedURLException e) {
            error += " > "+e.getLocalizedMessage() + " - "+getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, " - "+e.getMessage(), e);
        }catch (IOException e) {
            error += " > "+e.getLocalizedMessage() + " - "+getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, ""+e.getMessage(), e);
        }catch (Exception e) {
            error += " > "+e.getLocalizedMessage() + " - "+getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, ""+e.getMessage(), e);
        }

        return error;
    }

    public static String pruebaSSLRest_BKS(Context _context)
    {
        String error = "Inicia...\n";
        URL url = null;
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            final KeyStore ksBKS = KeyStore.getInstance("BKS");

            InputStream caInput = null;//new BufferedInputStream(_context.getResources().openRawResource(R.raw.certificado));
            ksBKS.load(caInput, "p0JXRjVkbc06fyK".toCharArray());

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(ksBKS);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tmf.getTrustManagers(), null);

             url = new URL( Constantes.CADENA_CONEXION);
            //url = new URL("https://10.81.12.46:4443/WSGenericoMovil/ssl/servicio/consultaGenericaDinamica");

            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();

            conn.setSSLSocketFactory(sslcontext.getSocketFactory());
            conn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Encoding", "UTF-8");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Type", "text/plain");

            String parametros = "{\"propiedadSQL\":\"CONSRESUMENINVETARIO_ITDA\",\"cuerpoPeticion\":[{\"indice\":1,\"tipoDato\":\"long\",\"valor\":\"20180103636466\"},{\"indice\":2,\"tipoDato\":\"CUR_SALIDA\",\"valor\":\"0\"},{\"indice\":3,\"tipoDato\":\"::Int\",\"valor\":\"0\"},{\"indice\":4,\"tipoDato\":\"::String\",\"valor\":\"0\"}]}";

            conn.setDoInput(true);
            conn.setDoOutput(true);

            DataOutputStream dstream = new DataOutputStream(conn.getOutputStream());

            String encriptado = encrypt(parametros, Constantes.CLAVE_CIFRADO);
            error += "solicitud: ["+encriptado+"]";
            dstream.writeBytes( encriptado );
            dstream.flush();
            dstream.close();

            int response  = conn.getResponseCode();
            error += "response: ["+ response + " - "+conn.getResponseMessage()+"]\n";

            BufferedReader br  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuffer strBuf = new StringBuffer();
            while((line = br.readLine()) != null){
                strBuf.append(line);
            }
            br.close();

            return strBuf.toString();
        } catch (MalformedURLException e) {
            error += " > "+e.getLocalizedMessage() + " - "+getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, " - "+e.getMessage(), e);
        }catch (IOException e) {
            error += " > "+e.getLocalizedMessage() + " - "+getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, ""+e.getMessage(), e);
        }catch (Exception e) {
            error += " > "+e.getLocalizedMessage() + " - "+getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, ""+e.getMessage(), e);
        }

        return error;
    }

    public static String prueba(Context _context)
    {
        String error = "Inicia";
        //HttpsTrustManager2.allowAllSSL();
        URL url = null;
        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = null;//new BufferedInputStream(_context.getResources().openRawResource(R.raw.prod_base64));
            Certificate ca = cf.generateCertificate(caInput);
            error = ""+((X509Certificate) ca).getSubjectDN();
            Log.d( GlobalShare.logAplicaion, "ca=" + ((X509Certificate) ca).getSubjectDN());

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tmf.getTrustManagers(), null);

            url = new URL("https://www.servicios.tiendasneto.com/WSSIANMoviles/services/WSAbastoMovil?wsdl");
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            conn.setSSLSocketFactory(sslcontext.getSocketFactory());

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Encoding", "UTF-8");

            conn.setRequestProperty("Content-type", "application/soap+xml; charset=utf-8");
            conn.setRequestProperty("SOAPAction",
                    "https://www.servicios.tiendasneto.com/WSSIANMoviles/services/WSAbastoMovil#validaPedidoSv");
             String parametros =
                    "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://service.movil.abasto.neto\">\n" +
                    "   <soap:Header/>\n" +
                    "   <soap:Body>\n" +
                    "      <ser:validaPedidoSv>\n" +
                    "         <ser:pedidoIdStr>8zAhBpCysN+qnWAyE5/0yg==</ser:pedidoIdStr>\n" +
                    "         <ser:ipClienteStr>D/zVZSjEl+5j+oP1kaU6cg==</ser:ipClienteStr>\n" +
                    "      </ser:validaPedidoSv>\n" +
                    "   </soap:Body>\n" +
                    "</soap:Envelope>";


            conn.setDoInput(true);
            conn.setDoOutput(true);
            //conn.connect();
            DataOutputStream dstream = new DataOutputStream(conn.getOutputStream());

            dstream.writeBytes(parametros );
            dstream.flush();
            dstream.close();

            int response  = conn.getResponseCode();
            error += "response: "+ response + " - "+conn.getResponseMessage();

            BufferedReader br  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuffer strBuf = new StringBuffer();
            while((line = br.readLine()) != null){
                strBuf.append(line);
            }
            br.close();

            return strBuf.toString();
        } catch (MalformedURLException e) {
            error += " > "+e.getLocalizedMessage() + " - "+getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, " - "+e.getMessage(), e);
        }catch (IOException e) {
            error += " > "+e.getLocalizedMessage() + " - "+getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, ""+e.getMessage(), e);
        }catch (Exception e) {
            error += " > "+e.getLocalizedMessage() + " - "+getStackTrace(e);
            Log.d(GlobalShare.logAplicaion, ""+e.getMessage(), e);
        }

        return error;
    }


    public static String getStackTrace(Throwable aThrowable) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }


    static String dato1 = null;
    static String dato2 = null;
    public static void callVolley_SSL(final Activity _context)
    throws Exception
    {
        ViewDialog2 dialog = new ViewDialog2(_context);

        try {
            String key = "RecibePedidosV1";
            //String input = "{\"propiedadSQL\":\"CONSRESUMENINVETARIO_ITDA\",\"cuerpoPeticion\":[{\"indice\":1,\"tipoDato\":\"long\",\"valor\":\"20180103636466\"},{\"indice\":2,\"tipoDato\":\"CUR_SALIDA\",\"valor\":\"0\"},{\"indice\":3,\"tipoDato\":\"::Int\",\"valor\":\"0\"},{\"indice\":4,\"tipoDato\":\"::String\",\"valor\":\"0\"}]}";
            String input1 = "201803131639934";
            String input2 = "10.81.12.46";
            setKey(key);
            dato1 = encrypt(input1, key);
            dato2 = encrypt(input2, key);

            dialog.showDialog(_context,
                    "dato1: " + dato1 + ", dato2: " + dato2 + ", IMEI: ",
                    //android.telephony.TelephonyManager.(),
                    null, TiposAlert.ALERT);
        }catch (Exception e){
            dialog.showDialog(_context, "error: " + e.getMessage(), null, TiposAlert.ALERT);
        }

        final ViewDialog2 dialogo = new ViewDialog2(_context);
        RequestQueue reqQueue = Volley.newRequestQueue(
                _context,
                new HurlStack(null,
                        mySSLSocketFactory(_context)));
        //HttpsTrustManager2.allowAllSSL();
        try {
            /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);*/

            StringRequest req = new StringRequest(
                    Method.POST,
                    "https://www.servicios.tiendasneto.com/WSSIANMoviles/services/WSAbastoMovil/validaPedidoSv",
                    //"http://10.37.140.202:7777/WSIndicadores/services/WSAbastoMovil/validaPedidoSv",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            dialogo.showDialog( _context, response, null, TiposAlert.CORRECTO);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialogo.showDialog( _context, getStackTrace(error), null, TiposAlert.ERROR);
                        }
                    }
            ) {
                /*@Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Encoding", "UTF-8");
                    headers.put("Content-type", "application/soap+xml; charset=utf-8");
                    headers.put("SOAPAction",
                            "https://www.servicios.tiendasneto.com/WSSIANMoviles/services/WSAbastoMovil#validaPedidoSv");
                            //"https://www.servicios.tiendasneto.com/WSSIANMoviles/services/WSAbastoMovil#validaPedidoSv");
                    return super.getHeaders();
                }*/

                @Override
                protected Map<String, String> getParams() {
                    Security.addProvider(new BouncyCastleProvider());
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("pedidoIdStr", dato1);
                    params.put("ipClienteStr", dato2);

                    return params;
                }



                /*@Override
                public byte[] getBody() throws AuthFailureError {
                    String sol = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://service.movil.abasto.neto\">\n" +
                                    "   <soap:Header/>\n" +
                                    "   <soap:Body>\n" +
                                    "      <ser:validaPedidoSv>\n" +
                                    "         <ser:pedidoIdStr>8zAhBpCysN+qnWAyE5/0yg==</ser:pedidoIdStr>\n" +
                                    "         <ser:ipClienteStr>D/zVZSjEl+5j+oP1kaU6cg==</ser:ipClienteStr>\n" +
                                    "      </ser:validaPedidoSv>\n" +
                                    "   </soap:Body>\n" +
                                    "</soap:Envelope>";

                    return sol.getBytes();
                }*/
            };
            req.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            //AppController.getInstance().addToRequestQueue(req, "pruebas");
            reqQueue.add(req);

        }catch (Exception e){
            dialogo.showDialog( _context, getStackTrace(e), null, TiposAlert.CORRECTO);
        }

    }

    public static SSLSocketFactory mySSLSocketFactory(Context _context) throws Exception {
        String error = null;

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream caInput = null;//new BufferedInputStream(_context.getResources().openRawResource(R.raw.prod_base64));
        Certificate ca = cf.generateCertificate(caInput);
        error = ""+((X509Certificate) ca).getSubjectDN();
        Log.d( GlobalShare.logAplicaion, "ca=" + ((X509Certificate) ca).getSubjectDN());

        // Create a KeyStore contenedor de nuestro trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Crear un TrustManager que confie en el CAs en nuestro KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Crea un SSLContext que use nuestro TrustManager
        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, tmf.getTrustManagers(), null);

        return sslcontext.getSocketFactory();
    }


}

class NoSSLv3SocketFactory extends SSLSocketFactory {
    private final SSLSocketFactory delegate;

    public NoSSLv3SocketFactory() {
        this.delegate = HttpsURLConnection.getDefaultSSLSocketFactory();
    }

    public NoSSLv3SocketFactory(SSLSocketFactory delegate) {
        this.delegate = delegate;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    private Socket makeSocketSafe(Socket socket) {
        if (socket instanceof SSLSocket) {
            socket = new NoSSLv3SSLSocket((SSLSocket) socket);
        }
        return socket;
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return makeSocketSafe(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return makeSocketSafe(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return makeSocketSafe(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return makeSocketSafe(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return makeSocketSafe(delegate.createSocket(address, port, localAddress, localPort));
    }

    private class NoSSLv3SSLSocket extends DelegateSSLSocket {

        private NoSSLv3SSLSocket(SSLSocket delegate) {
            super(delegate);

        }

        @Override
        public void setEnabledProtocols(String[] protocols) {
            if (protocols != null && protocols.length == 1 && "SSLv3".equals(protocols[0])) {

                List<String> enabledProtocols = new ArrayList<String>( Arrays.asList(delegate.getEnabledProtocols()));
                if (enabledProtocols.size() > 1) {
                    enabledProtocols.remove("SSLv3");
                    System.out.println("Removed SSLv3 from enabled protocols");
                } else {
                    System.out.println("SSL stuck with protocol available for " + String.valueOf(enabledProtocols));
                }
                protocols = enabledProtocols.toArray(new String[enabledProtocols.size()]);
            }

            super.setEnabledProtocols(protocols);
        }
    }

    public class DelegateSSLSocket extends SSLSocket {

        protected final SSLSocket delegate;

        DelegateSSLSocket(SSLSocket delegate) {
            this.delegate = delegate;
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public String[] getEnabledCipherSuites() {
            return delegate.getEnabledCipherSuites();
        }

        @Override
        public void setEnabledCipherSuites(String[] suites) {
            delegate.setEnabledCipherSuites(suites);
        }

        @Override
        public String[] getSupportedProtocols() {
            return delegate.getSupportedProtocols();
        }

        @Override
        public String[] getEnabledProtocols() {
            return delegate.getEnabledProtocols();
        }

        @Override
        public void setEnabledProtocols(String[] protocols) {
            delegate.setEnabledProtocols(protocols);
        }

        @Override
        public SSLSession getSession() {
            return delegate.getSession();
        }

        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener listener) {
            delegate.addHandshakeCompletedListener(listener);
        }

        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener listener) {
            delegate.removeHandshakeCompletedListener(listener);
        }

        @Override
        public void startHandshake() throws IOException {
            delegate.startHandshake();
        }

        @Override
        public void setUseClientMode(boolean mode) {
            delegate.setUseClientMode(mode);
        }

        @Override
        public boolean getUseClientMode() {
            return delegate.getUseClientMode();
        }

        @Override
        public void setNeedClientAuth(boolean need) {
            delegate.setNeedClientAuth(need);
        }

        @Override
        public void setWantClientAuth(boolean want) {
            delegate.setWantClientAuth(want);
        }

        @Override
        public boolean getNeedClientAuth() {
            return delegate.getNeedClientAuth();
        }

        @Override
        public boolean getWantClientAuth() {
            return delegate.getWantClientAuth();
        }

        @Override
        public void setEnableSessionCreation(boolean flag) {
            delegate.setEnableSessionCreation(flag);
        }

        @Override
        public boolean getEnableSessionCreation() {
            return delegate.getEnableSessionCreation();
        }

        @Override
        public void bind(SocketAddress localAddr) throws IOException {
            delegate.bind(localAddr);
        }

        @Override
        public synchronized void close() throws IOException {
            delegate.close();
        }

        @Override
        public void connect(SocketAddress remoteAddr) throws IOException {
            delegate.connect(remoteAddr);
        }

        @Override
        public void connect(SocketAddress remoteAddr, int timeout) throws IOException {
            delegate.connect(remoteAddr, timeout);
        }

        @Override
        public SocketChannel getChannel() {
            return delegate.getChannel();
        }

        @Override
        public InetAddress getInetAddress() {
            return delegate.getInetAddress();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return delegate.getInputStream();
        }

        @Override
        public boolean getKeepAlive() throws SocketException {
            return delegate.getKeepAlive();
        }

        @Override
        public InetAddress getLocalAddress() {
            return delegate.getLocalAddress();
        }

        @Override
        public int getLocalPort() {
            return delegate.getLocalPort();
        }

        @Override
        public SocketAddress getLocalSocketAddress() {
            return delegate.getLocalSocketAddress();
        }

        @Override
        public boolean getOOBInline() throws SocketException {
            return delegate.getOOBInline();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return delegate.getOutputStream();
        }

        @Override
        public int getPort() {
            return delegate.getPort();
        }

        @Override
        public synchronized int getReceiveBufferSize() throws SocketException {
            return delegate.getReceiveBufferSize();
        }

        @Override
        public SocketAddress getRemoteSocketAddress() {
            return delegate.getRemoteSocketAddress();
        }

        @Override
        public boolean getReuseAddress() throws SocketException {
            return delegate.getReuseAddress();
        }

        @Override
        public synchronized int getSendBufferSize() throws SocketException {
            return delegate.getSendBufferSize();
        }

        @Override
        public int getSoLinger() throws SocketException {
            return delegate.getSoLinger();
        }

        @Override
        public synchronized int getSoTimeout() throws SocketException {
            return delegate.getSoTimeout();
        }

        @Override
        public boolean getTcpNoDelay() throws SocketException {
            return delegate.getTcpNoDelay();
        }

        @Override
        public int getTrafficClass() throws SocketException {
            return delegate.getTrafficClass();
        }

        @Override
        public boolean isBound() {
            return delegate.isBound();
        }

        @Override
        public boolean isClosed() {
            return delegate.isClosed();
        }

        @Override
        public boolean isConnected() {
            return delegate.isConnected();
        }

        @Override
        public boolean isInputShutdown() {
            return delegate.isInputShutdown();
        }

        @Override
        public boolean isOutputShutdown() {
            return delegate.isOutputShutdown();
        }

        @Override
        public void sendUrgentData(int value) throws IOException {
            delegate.sendUrgentData(value);
        }

        @Override
        public void setKeepAlive(boolean keepAlive) throws SocketException {
            delegate.setKeepAlive(keepAlive);
        }

        @Override
        public void setOOBInline(boolean oobinline) throws SocketException {
            delegate.setOOBInline(oobinline);
        }

        @Override
        public void setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
            delegate.setPerformancePreferences(connectionTime, latency, bandwidth);
        }

        @Override
        public synchronized void setReceiveBufferSize(int size) throws SocketException {
            delegate.setReceiveBufferSize(size);
        }

        @Override
        public void setReuseAddress(boolean reuse) throws SocketException {
            delegate.setReuseAddress(reuse);
        }

        @Override
        public synchronized void setSendBufferSize(int size) throws SocketException {
            delegate.setSendBufferSize(size);
        }

        @Override
        public void setSoLinger(boolean on, int timeout) throws SocketException {
            delegate.setSoLinger(on, timeout);
        }

        @Override
        public synchronized void setSoTimeout(int timeout) throws SocketException {
            delegate.setSoTimeout(timeout);
        }

        @Override
        public void setTcpNoDelay(boolean on) throws SocketException {
            delegate.setTcpNoDelay(on);
        }

        @Override
        public void setTrafficClass(int value) throws SocketException {
            delegate.setTrafficClass(value);
        }

        @Override
        public void shutdownInput() throws IOException {
            delegate.shutdownInput();
        }

        @Override
        public void shutdownOutput() throws IOException {
            delegate.shutdownOutput();
        }

        @Override
        public String toString() {
            return delegate.toString();
        }

        @Override
        public boolean equals(Object o) {
            return delegate.equals(o);
        }
    }


}


