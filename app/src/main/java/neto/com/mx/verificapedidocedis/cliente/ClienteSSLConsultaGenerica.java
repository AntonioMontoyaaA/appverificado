package neto.com.mx.verificapedidocedis.cliente;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


import neto.com.mx.verificapedidocedis.R;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog;
import neto.com.mx.verificapedidocedis.mensajes.RespuestaDinamica;
import neto.com.mx.verificapedidocedis.mensajes.SolicitudServicio;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.GlobalShare;
import neto.com.mx.verificapedidocedis.utiles.TiposAlert;

/**
 * Created by dramirezr on 18/01/2018.
 */

public class ClienteSSLConsultaGenerica {
    private static final int TIMEOUT_REPUESTA_REST = 80000;
    private static final int MAX_NUM_REINTENTOS_REST = 1;
    private static RequestQueue reqQueue;
    private final Context contexto;
    private final Activity activity;
    private final String descOperacion;
    private final SolicitudServicio solicitud;
    private String generado;
    private final HandlerRespuestasVolley handler;
    private final String endPointConsulta;
    private ObjectMapper oMapper = new ObjectMapper();
    private boolean manejarCodigoError;
    private static SecretKeySpec secretKey;
    private ProgressDialog mDialog;

    private static int contSolicitudesPendientes = 0;

    private RequestQueue getQueue(Context _contexto){

        if( reqQueue == null || !contexto.equals(_contexto)){
            contSolicitudesPendientes = 0;
            final KeyStore ksBKS;
            try {
                ksBKS = KeyStore.getInstance("BKS");
                InputStream caInput = new BufferedInputStream(contexto.getResources().openRawResource( R.raw.certificado_prod));
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
                sslcontext.init(null, trustAllCerts, null);

                HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
                HttpsTrustManager.allowAllSSL(contexto);

                reqQueue = Volley.newRequestQueue( _contexto,
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


        }

        return reqQueue;
    }

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

    public ClienteSSLConsultaGenerica(
            String endPoint,
            Context contextoApp,
            AppCompatActivity activityApp,
            String descripcionOperacion,
            SolicitudServicio solicitudGenerica,
            HandlerRespuestasVolley handler)
    {
        this.endPointConsulta = endPoint;
        this.contexto = contextoApp;
        this.activity = activityApp;
        this.descOperacion = descripcionOperacion;
        this.solicitud = solicitudGenerica;
        this.handler = handler;
    }

    public ClienteSSLConsultaGenerica(
            String endPoint,
            Context contextoApp,
            AppCompatActivity activityApp,
            String descripcionOperacion,
            String solicitudG,
            HandlerRespuestasVolley handler)
    {
        this.manejarCodigoError = true;
        this.endPointConsulta = endPoint;
        this.contexto = contextoApp;
        this.activity = activityApp;
        this.descOperacion = descripcionOperacion;
        this.solicitud = null;
        generado = solicitudG;
        this.handler = handler;
    }

    boolean sinDialogos= false;
    public ClienteSSLConsultaGenerica(
            String endPoint,
            Context contextoApp,
            SolicitudServicio solicitud,
            HandlerRespuestasVolley handler)
    {
        sinDialogos = true;
        this.manejarCodigoError = true;
        this.endPointConsulta = endPoint;
        this.contexto = contextoApp;
        this.activity = null;
        this.descOperacion = null;
        this.solicitud = solicitud;
        generado = null;
        this.handler = handler;
    }


    public void setManejarCodigoError(boolean manejarCodigoError) {
        this.manejarCodigoError = manejarCodigoError;
    }

    /*private boolean haySolicitudesPendientes(){
        contSolicitudesPendientes = 0;
        reqQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                //request.getTag().equals(ClienteSSLConsultaGenerica.this.getClass().getName());
                contSolicitudesPendientes++;
                return false;
            }
        });
        return contSolicitudesPendientes > 0;
    }*/

    public void ejecutarConsultaWS() {
        ejecutarConsultaWS(0, 1);
    }
    public void ejecutarConsultaWS(final int idxCodigoError, final int idxMensaje) {
       // Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWS : Creando ConectivityManager...");
        try {
            ConnectivityManager connMgr = (ConnectivityManager)
                    contexto.getSystemService( Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            //Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWS : Verificando conexiones...");
            if (networkInfo != null && networkInfo.isConnected()) {
                mDialog = new ProgressDialog(contexto);

                    mDialog.setMessage(descOperacion);
                    mDialog.setCancelable(false);
                    mDialog.setInverseBackgroundForced(false);
                    mDialog.show();

                //////////
                final KeyStore ksBKS = KeyStore.getInstance("BKS");

                InputStream caInput = new BufferedInputStream(contexto.getResources().openRawResource( R.raw.certificado_prod));
                ksBKS.load(caInput, Constantes.LLAVE_BKS);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(ksBKS);

                // Create an SSLContext that uses our TrustManager
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, tmf.getTrustManagers(), null);
                //////////////

                Log.i(GlobalShare.logAplicaion, endPointConsulta);
                System.out.println( "endPointConsulta////////////// "+ endPointConsulta );
                /*RequestQueue reqQueue = Volley.newRequestQueue( contexto,
                        new HurlStack(null, sslcontext.getSocketFactory()));*/

                StringRequest strRequest = new StringRequest(
                        Request.Method.POST,
                        endPointConsulta,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String respuesta)
                            {
                                Log.d(GlobalShare.logAplicaion, "ejecutarConsultaWS : onResponse : respuesta > "+respuesta);

                                ViewDialog dialogo = new ViewDialog(contexto);

                                if (respuesta == null || respuesta.equals("") ) {
                                        dialogo.showDialog(activity,
                                                contexto.getString( R.string.request_sinrespuesta),
                                                null,
                                                TiposAlert.ERROR);
                                    return;
                                }

                                StringBuilder rastreo = new StringBuilder();
                                //RespuestaServicio respuestaObject = null;
                                try {
                                    rastreo.append(respuesta+"\n");
                                    RespuestaDinamica resDinamica = oMapper.readValue(respuesta, RespuestaDinamica.class);

                                    //Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWS : onResponse : "+
                                    //        " idxCodigoError: " + idxCodigoError + ", idxMensaje: " + idxMensaje );
                                    if (idxCodigoError > 0 && idxMensaje > 0)
                                    {
                                        rastreo.append("Validación de contenido... \n");
                                        if (!resDinamica.getStackTrace().isEmpty()) {
                                            rastreo.append("stackTrace: "+ resDinamica.getStackTrace());
                                            Log.e(GlobalShare.logAplicaion, "ejecutarConsultaWS : onResponse : WebService : "+resDinamica.getStackTrace());
                                                dialogo.showDialog(activity,
                                                        resDinamica.getStackTrace(),
                                                        null,
                                                        TiposAlert.ERROR);
                                            return;
                                        } else if (!resDinamica.getDatosSalida().get(idxCodigoError).equals("0"))
                                        {
                                            rastreo.append("stackTrace: "+ " idError: " + resDinamica.getDatosSalida().get(idxCodigoError)+
                                                    " Mensaje: " + resDinamica.getDatosSalida().get(idxMensaje)+"\n");
                                            Log.w(GlobalShare.logAplicaion, "ejecutarConsultaWS : onResponse : WebService : "+
                                                    " idError: " + resDinamica.getDatosSalida().get(idxCodigoError)+
                                                    " Mensaje: " + resDinamica.getDatosSalida().get(idxMensaje) );
                                            if (manejarCodigoError) {
                                                String mensaje = null;
                                                if (resDinamica.getDatosSalida().get(idxMensaje) != null &&
                                                    resDinamica.getDatosSalida().get(idxMensaje).indexOf('|') >= 0 )
                                                {
                                                    mensaje = resDinamica.getDatosSalida().get(idxMensaje).split( Pattern.quote("|"))[0];
                                                } else {
                                                    mensaje = resDinamica.getDatosSalida().get(idxMensaje);
                                                }
                                                if (mensaje != null) {
                                                    dialogo.showDialog(activity, mensaje, null, TiposAlert.ERROR);
                                                }else{
                                                    Log.d(GlobalShare.logAplicaion, "ejecutarConsultaWS : onResponse : WebService : "+
                                                            "mensaje NULO.");
                                                }
                                                return;
                                            } else {
                                                Log.w(GlobalShare.logAplicaion, "ejecutarConsultaWS : onResponse : solicitud > Código de error mandejado desde el invocador...");
                                            }
                                        }
                                    }

                                    rastreo.append("Liberado hacia el delegado...\n");
                                    handler.manejarExitoVolley(resDinamica);
                                } catch (Exception e) {
                                    Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWS : onResponse : "+e.getMessage(), e);
                                        dialogo.showDialog(activity,
                                                rastreo.toString()+"\n"+e.getLocalizedMessage(),
                                                null,
                                                TiposAlert.ERROR);
                                }finally {
                                    //if( !haySolicitudesPendientes() )
                                    mDialog.dismiss();
                                }

                            }
                        }, new Response.ErrorListener()
                        {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                getAllNetworkInfo();

                                String errorMostrar=null;
                                Log.e(GlobalShare.logAplicaion, "ejecutarConsultaWS : onErrorResponse : VolleyError : "+error );
                                if( error.getCause() instanceof ConnectException){
                                    error.getMessage().contains("Connection");
                                    errorMostrar = "No se tiene acceso al servidor.";
                                }else if( error.networkResponse != null ){
                                    errorMostrar = calcularErrorRed(error.networkResponse);
                                }else{
                                   if(error instanceof TimeoutError){ errorMostrar = "Se excedió el tiempo de espera para la respuesta."; }
                                   else if(error instanceof NoConnectionError){ errorMostrar = "Se produjo un error por no haber conexión."; }
                                   else if(error instanceof NetworkError){ errorMostrar = "Se produjo un error de red."; }
                                   else if(error instanceof AuthFailureError){ errorMostrar = "Se produjo un error de autenticación."; }
                                   else if(error instanceof ServerError){  errorMostrar = "Se produjo un error en el servidor.";  }
                                   else if(error instanceof ParseError){ errorMostrar = "Error de conversión de solicitud o respuesta."; }
                                   else errorMostrar = "Se presentó un error al procesar la solicitud.";
                                }
                                //if( !haySolicitudesPendientes() )
                                    mDialog.dismiss();

                                ViewDialog dialogo = new ViewDialog(contexto);
                                //dialogo.showDialog(activity, "VolleyError: "+errorMostrar, null, TiposAlert.ERROR);
                                dialogo.showDialog(activity, errorMostrar, null, TiposAlert.ERROR);

                                handler.manejarErrorVolley(error);
                            }
                        })
                {

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        byte[] body = null;
                        try {
                            String strSolicitud = oMapper.writeValueAsString(solicitud);
                            body = encrypt(strSolicitud, Constantes.CLAVE_CIFRADO).getBytes();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return body;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> parametros = new HashMap<String, String>();
                        parametros.put("Content-Encoding","UTF-8");
                        return parametros;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "text/plain";
                    }

                };

                HttpsTrustManager.allowAllSSL(contexto);
                strRequest.setRetryPolicy(new DefaultRetryPolicy(
                        TIMEOUT_REPUESTA_REST,
                        MAX_NUM_REINTENTOS_REST,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                strRequest.setTag(this.getClass().getName());
                reqQueue = getQueue(contexto);
                reqQueue.add(strRequest);
            } else {
                Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWS : networkInfo == null ó networkInfo no esta conectado...");
                    ViewDialog alert = new ViewDialog(contexto);
                    alert.showDialog(activity, "No hay conexión HTTP", null, TiposAlert.ERROR);
            }
        }catch(Exception e){
            Log.e(GlobalShare.logAplicaion, "ejecutarConsultaWS : "+e.getMessage(), e);
        }

    }

    public String ejecutarConsultaWSSinDialogos(final int idxCodigoError, final int idxMensaje) {
        //int MAX_NUM_REINTENTOS_CONS_RECARGA = 3;
        //Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWS : Creando ConectivityManager...");
        try {
            ConnectivityManager connMgr = (ConnectivityManager)
                    contexto.getSystemService( Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWS : Verificando conexiones...");
            if (networkInfo != null && networkInfo.isConnected()) {
                /*final KeyStore ksBKS = KeyStore.getInstance("BKS");

                InputStream caInput = new BufferedInputStream(contexto.getResources().openRawResource(R.raw.certificado_prod));
                ksBKS.load(caInput, Constantes.LLAVE_BKS);

                // Create a TrustManager that trusts the CAs in our KeyStore
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(ksBKS);

                // Create an SSLContext that uses our TrustManager
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, tmf.getTrustManagers(), null);*/
                //////////////

                Log.i(GlobalShare.logAplicaion, endPointConsulta);


                StringRequest strRequest = new StringRequest(
                        Request.Method.POST,
                        endPointConsulta,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String respuesta) {
                                Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : onResponse : respuesta > "+respuesta);


                                try {
                                    if (respuesta == null || respuesta.equals("") ) {
                                        throw new Exception("Respuesta vacia o nula...");
                                    }

                                    RespuestaDinamica resDinamica = oMapper.readValue(respuesta, RespuestaDinamica.class);

                                    Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : onResponse : "+
                                            " idxCodigoError: " + idxCodigoError + ", idxMensaje: " + idxMensaje );
                                    if (idxCodigoError > 0 && idxMensaje > 0)
                                    {

                                        if (!resDinamica.getStackTrace().isEmpty()) {
                                            Log.e(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : onResponse : WebService : "+resDinamica.getStackTrace());
                                            //return;
                                        } else if (!resDinamica.getDatosSalida().get(idxCodigoError).equals("0"))
                                        {
                                            Log.w(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : onResponse : WebService : "+
                                                    " idError: " + resDinamica.getDatosSalida().get(idxCodigoError)+
                                                    " Mensaje: " + resDinamica.getDatosSalida().get(idxMensaje) );
                                            if (manejarCodigoError)
                                            {
                                                String mensaje = null;
                                                if (resDinamica.getDatosSalida().get(idxMensaje) != null &&
                                                        resDinamica.getDatosSalida().get(idxMensaje).indexOf('|') >= 0 )
                                                {
                                                    mensaje = resDinamica.getDatosSalida().get(idxMensaje).split( Pattern.quote("|"))[0];
                                                } else {
                                                    mensaje = resDinamica.getDatosSalida().get(idxMensaje);
                                                }
                                                if (mensaje != null) {

                                                }else{
                                                    Log.d(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : onResponse : WebService : "+
                                                            "mensaje NULO.");
                                                }
                                                //return;
                                            } else {
                                                Log.w(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : onResponse : solicitud > Código de error mandejado desde el invocador...");
                                            }
                                        }
                                    }

                                    handler.manejarExitoVolley(resDinamica);
                                } catch (Exception e) {
                                    Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : onResponse : "+e.getMessage(), e);
                                    RespuestaDinamica resDina = new RespuestaDinamica();
                                    Map<Integer, String> datosSalida = new HashMap<>();
                                    datosSalida.put(idxCodigoError, "Excepcion al construir respuesta.");
                                    resDina.setDatosSalida(datosSalida);
                                    resDina.setStackTrace(e.getLocalizedMessage());
                                    handler.manejarExitoVolley(resDina);
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                getAllNetworkInfo();

                                String errorMostrar=null;
                                Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : onErrorResponse : VolleyError : "+error );
                                if( error.getCause() instanceof ConnectException){
                                    error.getMessage().contains("Connection");
                                    errorMostrar = "No se tiene acceso al servidor.";
                                }else if( error.networkResponse != null ){
                                    errorMostrar = calcularErrorRed(error.networkResponse);
                                }else{
                                    if(error instanceof TimeoutError){ errorMostrar = "Se excedió el tiempo de espera para la respuesta."; }
                                    else if(error instanceof NoConnectionError){ errorMostrar = "Se produjo un error de conexión."; }
                                    else if(error instanceof NetworkError){ errorMostrar = "Se produjo un error de red."; }
                                    else if(error instanceof AuthFailureError){ errorMostrar = "Se produjo un error de autenticación."; }
                                    else if(error instanceof ServerError){  errorMostrar = "Se produjo un error en el servidor.";  }
                                    else if(error instanceof ParseError){ errorMostrar = "Error de cenversión de solicitud o respuesta."; }

                                    errorMostrar = "Se presentó un error al procesar la solicitud.";
                                }

                                handler.manejarErrorVolley(error);
                            }
                        }
                ){
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                byte[] body = null;
                                try {
                                    String strSolicitud = oMapper.writeValueAsString(solicitud);
                                    body = encrypt(strSolicitud, Constantes.CLAVE_CIFRADO).getBytes();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return body;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> parametros = new HashMap<String, String>();
                                parametros.put("Content-Encoding","UTF-8");
                                return parametros;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "text/plain";
                            }
                };

                strRequest.setRetryPolicy(new DefaultRetryPolicy(
                        TIMEOUT_REPUESTA_REST,
                        MAX_NUM_REINTENTOS_REST,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                reqQueue = getQueue(contexto);
                strRequest.setTag(this.getClass().getName());
                reqQueue.add(strRequest);

            } else {
                Log.i(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : networkInfo == null ó networkInfo no esta conectado...");
                return "No hay conexión";
            }
        }catch(Exception e){
            Log.e(GlobalShare.logAplicaion, "ejecutarConsultaWSSinDialogos : "+e.getMessage(), e);
            return "Excepcion durante la construccion de llamada.";
        }
        return null;
    }

    public static String convierteMD5(String pass) throws NoSuchAlgorithmException {
        // Create MD5 Hash
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(pass.getBytes());
        byte messageDigest[] = digest.digest();

        return bytesToHex(messageDigest);
    }

    private static String bytesToHex (byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");
            strbuf.append( Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    private String calcularErrorRed(NetworkResponse response){
        String errorMostrar = "Se presentó un error al procesar la solicitud.";
        if( response != null ) {
            if (response.statusCode >= 300 && response.statusCode <= 399)
                errorMostrar = "Se presentó un error de redirección en la red. " +
                        "[" + response.statusCode + "] ";
            else if (response.statusCode >= 400 && response.statusCode <= 499)
                errorMostrar = "Se presentó un error en el cliente de red.";
            else if (response.statusCode >= 500 && response.statusCode <= 599)
                errorMostrar = "Se presentó un error en el servidor, favor de reportar este problema." +
                        "[" + response.statusCode + "] ";
        }
        return errorMostrar;
    }

    private void getAllNetworkInfo(){

        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService( Context.CONNECTIVITY_SERVICE);

        try {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            for (int i=0; i< info.length; i++)
            {
                Log.i(GlobalShare.logAplicaion, "getAllNetworkInfo : "+
                        info[i].toString());
                Log.i(GlobalShare.logAplicaion, "getAllNetworkInfo : "+
                        " getTypeName: "+info[i].getTypeName() +
                        " getSubtypeName: "+info[i].getSubtypeName()+
                        " getState: "+info[i].getState().toString() +
                        " getDetailedState: "+info[i].getDetailedState().toString() +
                        " getExtraInfo:"+info[i].getExtraInfo() );
            }

            /*Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);*/
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
    }
}

