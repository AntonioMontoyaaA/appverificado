package neto.com.mx.verificapedidocedis.decarga_version;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


import neto.com.mx.verificapedidocedis.R;
import neto.com.mx.verificapedidocedis.cliente.ClienteSSLConsultaGenerica;
import neto.com.mx.verificapedidocedis.cliente.HandlerRespuestasVolley;
import neto.com.mx.verificapedidocedis.mensajes.ParametroCuerpo;
import neto.com.mx.verificapedidocedis.mensajes.RespuestaDinamica;
import neto.com.mx.verificapedidocedis.mensajes.SolicitudServicio;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.GlobalShare;
import neto.com.mx.verificapedidocedis.utiles.Identidad;


//import android.app.DownloadManager;


/**
 * Created by dramirezr on 16/02/2018.
 */
interface EscuchaEstatusProgreso {
    enum estatusProgreso {ERROR, SUCCESS}

    void notificaProgreso(int progreso);
    void notificaEstatus(estatusProgreso estatus, String detalleError);
}

public class DescargaUltimaVersionDialog_https extends Activity implements EscuchaEstatusProgreso {
    public static final int RESULT_ERROR = 1;
    public static final int RESULT_OK = 0;
    public static final int RESULT_ACCESO_DENEGADO = -1;
    public static final int MAX_INTENTOS_DESCARGA = 30;

    private static final int TIME_WAIT_CLOSE_ACTIVITY_SHORT = 2000;
    private static final int TIME_WAIT_CLOSE_ACTIVITY_MEDIUM = 3000;
    private static final int TIME_WAIT_CLOSE_ACTIVITY_LONG = 5000;
    private static final int TIEMPO_REVISAR_DESCARGA = 500;
    private static final int PRIMER_ELEMENTO = 0;
    private static final int SEGUNDO_ELEMENTO = 1;
    private String identidadDispositivo;

    @Override
    public void notificaProgreso(int progreso) {
        cambiaVistaYTexto( VistaActualizacion.DESCARGA, "Descargando versión " + versionDescargar + "  %" + progreso);
        progresoDesacarga.setProgress(progreso);
    }

    @Override
    public void notificaEstatus(estatusProgreso estatus, String detalleError) {
        switch (estatus) {
            case ERROR:
                cambiaVistaYTexto( VistaActualizacion.RESULTADO, "Se presentó un error durante la descarga." + detalleError);
                break;
            case SUCCESS:
                cambiaVistaYTexto( VistaActualizacion.RESULTADO, "Proceso de descarga realizado correctamente");

                Log.d( GlobalShare.logAplicaion, "Iniciando proceso de instalación.");
                File archivo = new File(ubicacionArchivoInstalar);

                if (!archivo.exists()) {
                    cambiaVistaYTexto( VistaActualizacion.RESULTADO, "Archivo descargado no econtrado.");
                    Log.d(GlobalShare.logAplicaion, "Archivo descargado no econtrado.");
                    return;
                } else {
                    cambiaVistaYTexto( VistaActualizacion.RESULTADO,
                            "Instalando nueva versión. No será posible usar esta aplicación sin la versión más reciente.");
                            /*detalleError+">>"+ubicacionArchivoInstalar
                                    + ", canRead: "+archivo.canRead()
                                    + ", getTotalSpace: "+archivo.getTotalSpace()
                                    + ", lastModified: "+archivo.lastModified()
                                    + ", canExecute: "+archivo.canExecute()
                                    + ", isHidden: "+archivo.isHidden());*/
                }


                Uri uriArchivoEncontrado = null;
                Intent intentInstaller = null;

                uriArchivoEncontrado = Uri.fromFile(archivo);
                intentInstaller = new Intent(Intent.ACTION_VIEW);
                /*uriArchivoEncontrado = FileProvider.getUriForFile(
                        getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", archivo);*/
                intentInstaller.setDataAndType(
                        uriArchivoEncontrado,
                        "application/vnd.android.package-archive");
                intentInstaller.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intentInstaller);

                break;

        }
    }

    private static class RespuestaCentral {
        public static final int ACCESO_DENEGADO_A_APP = -1;
        public static final int NO_ES_NECESARIO_ACTUALIZAR = 0;
        public static final int ACTUALIZAR_NUEVA_VERSION = 1;
        public static final int ACTUALIZAR_VERSION_ESTABLE = 2;
        public static final int INICIO_RESPUESTAS_ERROR = 3;
        public static final int APPLICACION_INACTIVA = 15;
    }


    class ParametrosDescarga {
        InputStream certificado;
        String urlHttps;
        String directorioDestino;
        String nombreApk;
        EscuchaEstatusProgreso escuchaEstatusProgreso;

        public ParametrosDescarga(
                InputStream certificado,
                String urlHttps,
                String directorioDestino,
                String nombreApk,
                EscuchaEstatusProgreso escuchaEstatusProgreso) {
            this.certificado = certificado;
            this.urlHttps = urlHttps;
            this.directorioDestino = directorioDestino;
            this.nombreApk = nombreApk;
            this.escuchaEstatusProgreso = escuchaEstatusProgreso;
        }

        public InputStream getCertificado() {
            return certificado;
        }

        public void setCertificado(InputStream certificado) {
            this.certificado = certificado;
        }

        public String getUrlHttps() {
            return urlHttps;
        }

        public void setUrlHttps(String urlHttps) {
            this.urlHttps = urlHttps;
        }

        public String getDirectorioDestino() {
            return directorioDestino;
        }

        public void setDirectorioDestino(String directorioDestino) {
            this.directorioDestino = directorioDestino;
        }

        public String getNombreApk() {
            return nombreApk;
        }

        public void setNombreApk(String nombreApk) {
            this.nombreApk = nombreApk;
        }

        public EscuchaEstatusProgreso getEscuchaEstatusProgreso() {
            return escuchaEstatusProgreso;
        }

        public void setEscuchaEstatusProgreso(EscuchaEstatusProgreso escuchaEstatusProgreso) {
            this.escuchaEstatusProgreso = escuchaEstatusProgreso;
        }
    }

    class ControlProgreso extends AsyncTask<ParametrosDescarga, Integer, Void> {
        private boolean esProcesoExitoso = false;
        private static final int MILISEGUNDOS_TIMEOUT_DESCARGA = 120 * 1000;
        private ParametrosDescarga descarga;
        private KeyStore ksBKS = null;
        private SSLContext sslcontext = null;
        private InputStream caInput = null;
        private boolean estaInicializado = false;
        private int ByteWritten = 0;
        private double bytesTotales = 0;

        public void inicializa(InputStream certificado) {
            bytesTotales = 0;
            if (!estaInicializado) {
                try {
                    caInput = new BufferedInputStream(certificado);
                    ksBKS = KeyStore.getInstance("BKS");
                    ksBKS.load(caInput, Constantes.LLAVE_BKS);

                    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager(){
                        public X509Certificate[] getAcceptedIssuers(){
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType){}

                        public void checkServerTrusted(X509Certificate[] certs, String authType){}
                    } };

                    // Crea TrustManager para confiar en el CA en el KeyStore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    tmf.init(ksBKS);



                    // Crea un SSLContext que usa el nuevo TrustManager
                    sslcontext = SSLContext.getInstance("TLS");
                    //sslcontext.init(null, tmf.getTrustManagers(), null);
                    sslcontext.init(null, trustAllCerts, null);
                } catch (KeyStoreException e) {
                    Log.e(GlobalShare.logAplicaion, "KeyStoreException: ", e);
                } catch (CertificateException e) {
                    Log.e(GlobalShare.logAplicaion, "CertificateException: ", e);
                } catch (NoSuchAlgorithmException e) {
                    Log.e(GlobalShare.logAplicaion, "NoSuchAlgorithmException: ", e);
                } catch (KeyManagementException e) {
                    Log.e(GlobalShare.logAplicaion, "KeyManagementException: ", e);
                } catch (IOException e) {
                    Log.e(GlobalShare.logAplicaion, "IOException: ", e);
                }
            }
        }

        private double tamanoSiguiente = 0;
        private double incremento = 0;

        private void calculaNotificaciones(int bytesTot) {
            bytesTotales = bytesTot;
            incremento = bytesTotales * 0.1D;
            tamanoSiguiente = incremento;
        }

        private boolean seDebeNotificarProgreso(double leidos) {
            if (leidos >= tamanoSiguiente) {
                tamanoSiguiente += tamanoSiguiente;
                return true;
            }
            return false;
        }


        @Override
        protected Void doInBackground(ParametrosDescarga... parametrosDescarga) {
            OutputStream outStream = null;
            InputStream is = null;
            URLConnection uCon;

            descarga = parametrosDescarga[PRIMER_ELEMENTO];
            try {
                inicializa(descarga.certificado);

                URL url;
                byte[] buf;
                int ByteRead;
                ByteWritten = 0;
                url = new URL(descarga.getUrlHttps());

                /*File flocal = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);//new File( descarga.getDirectorioDestino() );
                FileOutputStream fos = DescargaUltimaVersionDialog_https.this.openFileOutput( descarga.getNombreApk(), MODE_PRIVATE );*/
                outStream = new BufferedOutputStream(new FileOutputStream(
                        descarga.getDirectorioDestino() + "/" + descarga.getNombreApk()));

                try {
                    HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
                    /**/HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String arg0, SSLSession arg1) {
                            return true;
                        }
                    });

                    uCon = url.openConnection();
                    uCon.setReadTimeout(MILISEGUNDOS_TIMEOUT_DESCARGA);
                    is = uCon.getInputStream();
                } catch (Exception e) {
                    Log.e(GlobalShare.logAplicaion, "openConnection:", e);
                    //descarga.escuchaEstatusProgreso.notificaEstatus( EscuchaEstatusProgreso.estatusProgreso.ERROR, e.getMessage() );
                    return null;
                }

                calculaNotificaciones(uCon.getContentLength());

                Log.i(GlobalShare.logAplicaion, "Inicia descarga URL.");
                descarga.escuchaEstatusProgreso.notificaProgreso(0);

                buf = new byte[1024];
                while ((ByteRead = is.read(buf)) != -1) {
                    outStream.write(buf, 0, ByteRead);
                    ByteWritten += ByteRead;
                    if (seDebeNotificarProgreso(ByteWritten)) {
                        try {
                            Double progresoCalculado = (ByteWritten * 100D) / bytesTotales;
                            publishProgress(progresoCalculado.intValue());
                        } catch (Exception e) {
                            Log.e(GlobalShare.logAplicaion, "ByteWritten:", e);
                        }
                    }
                }

                is.close();
                outStream.flush();
                outStream.close();

                publishProgress(100);

                ubicacionArchivoInstalar = descarga.getDirectorioDestino() + "/" + descarga.getNombreApk();

                Log.i(GlobalShare.logAplicaion, "Downloaded Successfully in [" +
                        descarga.getDirectorioDestino() + "/" + descarga.getNombreApk() + "]");
                esProcesoExitoso = true;
            } catch (Exception e) {
                Log.e(GlobalShare.logAplicaion, "Descargando https:", e);
                //descarga.escuchaEstatusProgreso.notificaEstatus(estatusProgreso.ERROR, "Se produjo un error durante la descarga");//e.getMessage());
            } finally {
                try {
                    is.close();
                    outStream.close();
                } catch (Exception e) {
                    Log.e(GlobalShare.logAplicaion, "cerrando input stream.", e);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(final Integer... progreso) {
            descarga.escuchaEstatusProgreso.notificaProgreso(progreso[PRIMER_ELEMENTO]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            File archivo = new File(descarga.getDirectorioDestino() + "/" + descarga.getNombreApk());
            if (archivo != null && archivo.exists()) {
                if (bytesTotales != ByteWritten) {
                    esProcesoExitoso = false;
                    Log.i(GlobalShare.logAplicaion,
                            "El tamaño del archivo descargado no coincide con el proporcionado por el servidor por lo que se borrará.");
                }
            }

            if (esProcesoExitoso) {
                descarga.escuchaEstatusProgreso.notificaEstatus(estatusProgreso.SUCCESS, "");
            } else {
                descarga.escuchaEstatusProgreso.notificaEstatus(estatusProgreso.ERROR, "Se prentó un error durante la descarga.");
            }

            super.onPostExecute(aVoid);
        }
    }

    private String ubicacionArchivoInstalar;
    private String versionDescargar;
    private Handler handler = new Handler();

    LinearLayout principal;

    LinearLayout contDescarga;
    LinearLayout contInicial;

    ScrollView scroll;
    TextView txtAvisoFinal;
    TextView txtMensaje;
    TextView textMensajeDescarga;
    ProgressBar progresoDesacarga;
    String version;

    private void cerrarActivity(long miliSegs) {
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, miliSegs);
    }

    enum VistaActualizacion {INICIAL, DESCARGA, RESULTADO}

    VistaActualizacion vistaAnterior;

    protected void cambiaVistaYTexto(final VistaActualizacion vista, final String strMensaje) {
        runOnUiThread(new Runnable() {
            public void run() {
                txtMensaje.setText(strMensaje);
                textMensajeDescarga.setText(strMensaje);
                txtAvisoFinal.setText(strMensaje);
                if (vistaAnterior != vista) {
                    switch (vista) {
                        case INICIAL:
                            contInicial.setVisibility(View.VISIBLE);
                            contDescarga.setVisibility(View.GONE);
                            txtAvisoFinal.setVisibility(View.GONE);
                            break;
                        case DESCARGA:
                            contDescarga.setVisibility(View.VISIBLE);
                            contInicial.setVisibility(View.GONE);
                            txtAvisoFinal.setVisibility(View.GONE);
                            break;
                        case RESULTADO:
                            contInicial.setVisibility(View.GONE);
                            contDescarga.setVisibility(View.GONE);
                            txtAvisoFinal.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        });
    }

    private void crearVista() {
        principal = new LinearLayout(this);
        principal.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        principal.setBackgroundColor(Color.BLACK);
        principal.setGravity(Gravity.CENTER);
        principal.setOrientation(LinearLayout.VERTICAL);

        int dim9sp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 9, getResources().getDisplayMetrics());
        int dim10sp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());
        int dim40sp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
        int dim20sp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());

        principal.setPadding(dim20sp, dim20sp, dim20sp, dim20sp);

        contInicial = new LinearLayout(this);
        contInicial.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
        int widthProgressBar = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 100f, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams linearLayoutParamsProgress = new LinearLayout.LayoutParams(
                widthProgressBar, LinearLayout.LayoutParams.WRAP_CONTENT, 0.9f);

        progressBar.setLayoutParams(linearLayoutParamsProgress);

        contInicial.addView(progressBar);

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setHorizontalScrollBarEnabled(true);
        scroll.setVerticalScrollBarEnabled(true);

        contInicial.addView(scroll);

        txtMensaje = new TextView(this);
        txtMensaje.setText("Verificando versión de aplicación...");
        txtMensaje.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txtMensaje.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtMensaje.setTextSize(dim9sp);

        txtMensaje.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.9f
        ));
        //contInicial.addView(txtMensaje);
        scroll.addView(txtMensaje);

        principal.addView(contInicial);

        contDescarga = new LinearLayout(this);
        contDescarga.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        contDescarga.setOrientation(LinearLayout.VERTICAL);

        textMensajeDescarga = new TextView(this);
        textMensajeDescarga.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textMensajeDescarga.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        textMensajeDescarga.setTextColor(Color.WHITE);
        textMensajeDescarga.setTextSize(dim9sp);

        contDescarga.addView(textMensajeDescarga);
        int progressHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        progresoDesacarga = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progresoDesacarga.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, progressHeight
        ));
        progresoDesacarga.setMax(100);
        progresoDesacarga.setProgress(0);
        progresoDesacarga.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        contDescarga.addView(progresoDesacarga);
        contDescarga.setVisibility(View.GONE);

        principal.addView(contDescarga);

        txtAvisoFinal = new TextView(this);
        txtAvisoFinal.setTextSize(dim10sp);
        txtAvisoFinal.setText("No se podrá continuar usando la aplicación hasta que se haya instalado la nueva versión.");
        txtAvisoFinal.setTextColor(Color.YELLOW);
        txtAvisoFinal.setVisibility(View.GONE);
        txtAvisoFinal.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        principal.addView(txtAvisoFinal);


        setContentView(principal);
        setFinishOnTouchOutside(false);

        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ne) {
            Log.e(GlobalShare.logAplicaion, "Error al obtener la versión : " + ne.getMessage());
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        crearVista();
        setResult(Activity.RESULT_OK);

        verificarVersion();
    }

    public void onBackPressed() {
        return;
    }


    public void verificarVersion() {
        try {
            this.identidadDispositivo = Identidad.leerIdentificadorDispositivo(this);
        }catch (Exception e){}


        if (GlobalShare.getInstace().getVersionVerificado()) {
            setResult(RESULT_OK);
            Log.w(GlobalShare.logAplicaion, "Versión y acceso verificados...");
            cambiaVistaYTexto( VistaActualizacion.RESULTADO, "Versión y acceso verificados...");
            cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_SHORT);
            return;
        }

        String mensajeError = null;
        //TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String aplicacionId = getResources().getString( R.string.app_id);

        //String imeii = identidadDispositivo; // telephonyManager.getDeviceId();
        String imeii = Settings.Secure.getString( getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            mensajeError = e.getLocalizedMessage();
            Log.e(GlobalShare.logAplicaion, getClass().getName() + " : Obteniendo la vesion actual..", e);
        }
        final String versionActual = version;
        //Eliminar apks con version actual en su nombre

        boolean errorConf = true;
        if (aplicacionId == null || aplicacionId.isEmpty()) {
            mensajeError = "No se ha configurado un id para esta aplicación.";
        } else if (versionActual == null || versionActual.isEmpty()) {
            mensajeError = "No ha configurado la versión actual de esta aplicación.";
        } else if (imeii == null || imeii.isEmpty()) {
            mensajeError = "No fue posible leer el IDENTIFICADOR de este dispositivo.";
        } else {
            errorConf = false;
        }

        if (errorConf) {
            cambiaVistaYTexto( VistaActualizacion.RESULTADO, mensajeError);
            cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_LONG);
            return;
        }

        File archivos = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        Log.d(GlobalShare.logAplicaion, "Buscando al archivo que contenga: "+versionActual + ".apk");
        if ( archivos != null ) {
            File[] encontrados = archivos.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().contains(versionActual + ".apk");
                }
            });

            if (encontrados != null && encontrados.length > 0) {
                Log.d(GlobalShare.logAplicaion, "Archivo de versión actual encontrado, procediendo a elimnar...");
                for (File arcActual : encontrados) {
                    if( arcActual != null ){
                        Log.d(GlobalShare.logAplicaion, "Elimnando archivo: " + arcActual.getAbsolutePath());
                        try {
                            arcActual.delete();
                        } catch (Exception e) {
                            Log.e(GlobalShare.logAplicaion, getClass().getName() + " : Error al eliminar archivo...", e);
                            //mensajeError = e.getLocalizedMessage();
                        }
                    }
                }
            } else {
                Log.d(GlobalShare.logAplicaion, "Archivo de versión anterior NO encontrado...");
            }
        }
        //contInicial.setVisibility(View.VISIBLE);
        List<ParametroCuerpo> cuerpoPeticion = new ArrayList<>();

        final int idxVersion = 4,
                idxUrlDescarga = 5,
                idxIdError = 6,
                idxMensaje = 7;

        GlobalShare.getInstace().setVersionVerificado(false);

        cuerpoPeticion.add(new ParametroCuerpo(1, "String", imeii));//IMEII
        cuerpoPeticion.add(new ParametroCuerpo(2, "Long", aplicacionId));//IDAPP
        cuerpoPeticion.add(new ParametroCuerpo(3, "String", versionActual));//VERSIONACTUAL
        cuerpoPeticion.add(new ParametroCuerpo(idxVersion, ":String", ""));//Version por actualizar
        cuerpoPeticion.add(new ParametroCuerpo(idxUrlDescarga, ":String", ""));//URL Desacarga
        cuerpoPeticion.add(new ParametroCuerpo(idxIdError, "::Int", "0"));
        cuerpoPeticion.add(new ParametroCuerpo(idxMensaje, "::String", "0"));


        System.out.println( "////////////////////////////////////////////////imeii " + imeii+ " aplicacionId "+ aplicacionId + " versionActual " + versionActual + " idxVersion " +idxVersion+ " idxUrlDescarga " + idxUrlDescarga+
                " idxIdError "+idxIdError+ " idxMensaje " + idxMensaje );

        runOnUiThread(new Runnable() {
            public void run() {
                textMensajeDescarga.setText("Validando acceso de dispositivo...");
                txtMensaje.setText("Validando acceso de dispositivo...");
            }
        });

        SolicitudServicio solicitud = new SolicitudServicio("VERIFICAVERSION", cuerpoPeticion);
        ClienteSSLConsultaGenerica cliente = new ClienteSSLConsultaGenerica(
                Constantes.CADENA_CONEXION,
                this,
                solicitud,
                new HandlerRespuestasVolley() {
                    @Override
                    public void manejarExitoVolley(final RespuestaDinamica respuesta) {
                        try {

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    textMensajeDescarga.setText("Procesando respuesta de servicio...");
                                    txtMensaje.setText("Procesando respuesta de servicio...");
                                }
                            });

                            if (respuesta.getStackTrace() != null && !respuesta.getStackTrace().isEmpty()) {
                                setResult(RESULT_ERROR);
                                Log.d(GlobalShare.logAplicaion, getClass().getName() + " : verificarVersion : Respuesta indica error.");
                                cambiaVistaYTexto( VistaActualizacion.RESULTADO,
                                        "Se presentó un problema al validar la versión... " + respuesta.toString());
                                cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_MEDIUM);
                            } else if (respuesta == null
                                    || respuesta.getDatosSalida() == null
                                    || respuesta.getDatosSalida().size() == 0) {
                                setResult(RESULT_ERROR);
                                Log.d(GlobalShare.logAplicaion, getClass().getName() + " : verificarVersion : manejarExitoVolley :" +
                                        "Respuesta vacía");
                                cambiaVistaYTexto( VistaActualizacion.RESULTADO,
                                        "Respuesta de servicio vacía... " + respuesta.toString());

                                cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_MEDIUM);
                            }

                            int idOperacionRealizar = Integer.parseInt(respuesta.getDatosSalida().get(idxIdError));
                            String descripOpRealizar = respuesta.getDatosSalida().get(idxMensaje);
                            txtAvisoFinal.setText(descripOpRealizar);

                            //0 := No es necesario descargar versión
                            //1 := Descargar versión anterior
                            //2 := Descargar nueva versión

                            GlobalShare.getInstace().setAccesoVerificado(true);
                            if (idOperacionRealizar == RespuestaCentral.NO_ES_NECESARIO_ACTUALIZAR) {//ok
                                GlobalShare.getInstace().setVersionVerificado(true);
                                setResult(RESULT_OK);//Log.d(GlobalShare.logAplicaion, "No es necesario actualizar la versión");
                                cambiaVistaYTexto( VistaActualizacion.RESULTADO, "No es necesario actualizar la versión.");
                                cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_SHORT);
                                return;
                            } else if (idOperacionRealizar == RespuestaCentral.ACTUALIZAR_VERSION_ESTABLE) {//cambiaVistaYTexto(VistaActualizacion.INICIAL, "Se requiere instalar actualización");
                                Log.w(GlobalShare.logAplicaion, descripOpRealizar);
                                cambiaVistaYTexto( VistaActualizacion.INICIAL, getPrimerElementoPipeOTodoNoPipe(descripOpRealizar));
                            } else if (idOperacionRealizar == RespuestaCentral.ACTUALIZAR_NUEVA_VERSION) {//cambiaVistaYTexto(VistaActualizacion.INICIAL, "Se requiere hacer Downgrade...");
                                Log.w(GlobalShare.logAplicaion, descripOpRealizar);
                                cambiaVistaYTexto( VistaActualizacion.INICIAL, getPrimerElementoPipeOTodoNoPipe(descripOpRealizar));
                            } else if (idOperacionRealizar == RespuestaCentral.ACCESO_DENEGADO_A_APP ||
                                    idOperacionRealizar == RespuestaCentral.APPLICACION_INACTIVA) {
                                setResult(RESULT_ACCESO_DENEGADO);
                                GlobalShare.getInstace().setAccesoVerificado(false);
                                //cambiaVistaYTexto(VistaActualizacion.INICIAL, "Acceso denegado para este dispositivo...");
                                cambiaVistaYTexto( VistaActualizacion.RESULTADO, getPrimerElementoPipeOTodoNoPipe(descripOpRealizar));
                                cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_LONG);
                                return;
                            } else if (idOperacionRealizar >= RespuestaCentral.INICIO_RESPUESTAS_ERROR) {
                                setResult(RESULT_ERROR);
                                Log.w(GlobalShare.logAplicaion, descripOpRealizar);
                                cambiaVistaYTexto( VistaActualizacion.RESULTADO, getPrimerElementoPipeOTodoNoPipe(descripOpRealizar));
                                cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_LONG);
                                return;
                            } else {//Casos no contemplados pasan como [ OK ]
                                setResult(RESULT_OK);
                                Log.w(GlobalShare.logAplicaion, descripOpRealizar);
                                cambiaVistaYTexto( VistaActualizacion.INICIAL, getPrimerElementoPipeOTodoNoPipe(descripOpRealizar));
                                cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_SHORT);
                                return;
                            }

                            //cambiaVistaYTexto(VistaActualizacion.RESULTADO, "Acceso denegado para este dispositivo...");

                            versionDescargar = respuesta.getDatosSalida().get(idxVersion);
                            final String urlVerSig = respuesta.getDatosSalida().get(idxUrlDescarga);

                            try {
                                String[] nombre = urlVerSig.split(Matcher.quoteReplacement("/"));
                                String nombreAPK = nombre[nombre.length - 1];

                                Log.w(GlobalShare.logAplicaion, getClass().getName() +
                                        " : Buscar archivo por nombre : ["+nombreAPK+"]");

                                //Uri uriDescarga = Uri.parse(urlVerSig);

                                if( !isExternalStorageWritable() || !isExternalStorageReadable() ){
                                    setResult(RESULT_ERROR);
                                    Log.e(GlobalShare.logAplicaion, "No es posible leear o escribir en el ExternalStorage.");
                                    cambiaVistaYTexto( VistaActualizacion.RESULTADO, "No es posible leear o escribir en el ExternalStorage.");
                                    cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_SHORT);
                                    return;
                                }

                                File archivo = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), nombreAPK);

                                if (archivo != null && archivo.exists() && archivo.getTotalSpace() > 0 ){
                                    cambiaVistaYTexto( VistaActualizacion.RESULTADO, "Se instalará desde las descargas. No será posible usar esta aplicación sin la versión más actual.");
                                    Log.d(GlobalShare.logAplicaion, getClass().getName() +
                                            " : Archivo encontrado > se instalará desde las descargas :: "+archivo.getTotalSpace());

                                    Uri uriArchivoEncontrado = null;
                                    Intent intentInstaller = null;

                                    //Log.d(GlobalShare.logAplicaion, getClass().getName() + " : ELSE ...");
                                    uriArchivoEncontrado = Uri.fromFile(archivo);
                                    intentInstaller = new Intent(Intent.ACTION_VIEW);
                                    intentInstaller.setDataAndType( uriArchivoEncontrado,
                                            "application/vnd.android.package-archive");
                                    intentInstaller.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    Log.d(GlobalShare.logAplicaion, getClass().getSimpleName() +
                                            " : Iniciando la instalación de la ultima versión ...");

                                    startActivity(intentInstaller);
                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            txtAvisoFinal.setText("Iniciando la descarga...");
                                            txtMensaje.setText("Iniciando la descarga...");
                                        }
                                    });

                                    Log.d(GlobalShare.logAplicaion, getClass().getName() + " : Archivo no encontrado > se descargará...");
                                    fileUrl(urlVerSig, nombreAPK);
                                }
                            } catch (Exception e) {
                                setResult(RESULT_ERROR);
                                cambiaVistaYTexto( VistaActualizacion.RESULTADO, "Ocurrió un error durante el proceso de actualización..."+e.getMessage());
                                Log.e(GlobalShare.logAplicaion, e.getMessage(), e);
                            }
                        } catch (Exception e) {
                            setResult(RESULT_ERROR);
                            Log.e(GlobalShare.logAplicaion, getClass().getName() + " : verificarVersion : " + e.getMessage(), e);
                        }
                    }

                    @Override
                    public void manejarErrorVolley(final VolleyError error) {
                        String errorMostrar = "Se presentó un problema al intentar realizar la consulta.";
                        setResult(RESULT_ERROR);

                        if( error != null ) {
                            errorMostrar = "No se tiene acceso al servidor.";
                            if (error.networkResponse != null) {
                                if (error.networkResponse.statusCode >= 500) {
                                    errorMostrar = "[" + error.networkResponse.statusCode + ", " +
                                            error.networkResponse.toString() + "]  \nEl servidor presenta un problema al recibir conexiónes.";
                                } else if (error.networkResponse.statusCode >= 400 &&
                                        error.networkResponse.statusCode < 500) {
                                    errorMostrar = "[" + error.networkResponse.statusCode + ", " +
                                            error.networkResponse.data.toString() + "] \nSe presentó un problema al intentar conectarse con el servidor.";
                                } else if (error.networkResponse.statusCode >= 300 &&
                                        error.networkResponse.statusCode < 400) {
                                    errorMostrar = "[" + error.networkResponse.statusCode + ", " +
                                            error.networkResponse.toString() + "]  \nSe presentó un problema al enviar los datos al servidor.";
                                }else{
                                    errorMostrar = "[" + error.networkResponse.statusCode + ", " +
                                            error.networkResponse.toString() + "]  \nSe presentó un problema inesperado al enviar los datos al servidor.";
                                }
                            }
                        }

                        cambiaVistaYTexto( VistaActualizacion.RESULTADO, errorMostrar);


                        Log.e(GlobalShare.logAplicaion, getClass().getName() +
                                " : verificarVersion : manejarErrorVolley :" + error.getMessage(), error);

                        cerrarActivity(TIME_WAIT_CLOSE_ACTIVITY_SHORT);
                    }
                }
        );

        runOnUiThread(new Runnable() {
            public void run() {
                textMensajeDescarga.setText("Consultando info de servicio...");
            }
        });

        cliente.setManejarCodigoError(false);
        //final String respuesta =
        cliente.ejecutarConsultaWSSinDialogos(idxIdError, idxMensaje);
        /*if( respuesta != null && !respuesta.isEmpty())
        {
            runOnUiThread(new Runnable(){
                public void run() {
                    textMensajeDescarga.setText(respuesta);
                    txtMensaje.setText(respuesta);
                    txtAvisoFinal.setText(respuesta);
                }
            });
        }*/
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
       /* try {
            this.unregisterReceiver(broadcastReceiverDownload);
        } catch (Exception e) {
            Log.e(GlobalShare.logAplicaion, getClass().getName() + " : onDestroy : " + e.getMessage(), e);
        }*/
        super.onDestroy();
    }

    private String getPrimerElementoPipeOTodoNoPipe(String texto){
        /**/String res = texto.split(Pattern.quote("|"))[PRIMER_ELEMENTO];
        if( res != null && !res.isEmpty() )
            return res;
        else
            return texto;
        //return texto;
    }

    public void fileUrl(final String fAddress, final String localFileName ) {
        //List<String> archivos = new ArrayList<>();
        //Collections.addAll(archivos, fileList());
        File archivos = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        if( archivos != null ) {
            Log.d(GlobalShare.logAplicaion, "Buscando archivo ...");
            //int idxArchivoEncontrado = archivos.indexOf(localFileName + ".apk");
            File[] encontrados = archivos.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().contains(localFileName + ".apk");
                }
            });

            if (encontrados.length > 0) {
                //Log.d(GlobalShare.logAplicaion, "Archivo encontrado, procediendo a elimnar...");
                for (File arcActual : encontrados) {
                    Log.d(GlobalShare.logAplicaion, "Elimnando archivo : " + arcActual.getAbsolutePath());
                    try {
                        arcActual.delete();
                    } catch (Exception e) {
                        Log.e(GlobalShare.logAplicaion, getClass().getName() + " : Error al eliminar archivo...", e);
                    }
                }
            }
        }

        String destinationDir = // getFilesDir().getAbsolutePath();//archivos.getAbsolutePath();
                ubicacionArchivoInstalar = archivos.getAbsolutePath();//destinationDir + "/" + localFileName;

        ControlProgreso controlProgreso = new ControlProgreso();
        controlProgreso.execute(
                new ParametrosDescarga(
                        getResources().openRawResource(R.raw.certificado_prod),
                        fAddress,
                        destinationDir,
                        localFileName,
                        this
                )
        );
/*
        File archivos = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        Log.d(GlobalShare.logAplicaion, "Buscando archivo ...");
        File[] encontrados = archivos.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().contains(localFileName+".apk");
            }
        });

        if( encontrados != null && encontrados.length > 0) {
            Log.d(GlobalShare.logAplicaion, "Archivo encontrado, procediendo a elimnar...");
            for (File arcActual : encontrados) {
                Log.d(GlobalShare.logAplicaion, "Elimnando archivo : " + arcActual.getAbsolutePath());
                try {
                    arcActual.delete();
                } catch (Exception e) {
                    Log.e(GlobalShare.logAplicaion, getClass().getName() + " : Error al eliminar archivo...", e);
                }
            }
        }else{
            Log.d(GlobalShare.logAplicaion, "Archivo de versión anterior NO encontrado...");
        }

        OutputStream outStream = null;
        URLConnection uCon = null;

        InputStream is = null;
        String destinationDir = archivos.getAbsolutePath();
        KeyStore ksBKS= null;

        SSLContext sslcontext = null;

        final String msjResultado = "Se presentó un problema con la descarga de la nueva versión, se intentará descargar en el futuro.";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            ksBKS = KeyStore.getInstance("BKS");

            InputStream caInput = new BufferedInputStream( DescargaUltimaVersionDialog_https.this.getResources().openRawResource(R.raw.certificado_prod));
            ksBKS.load(caInput, Constantes.LLAVE_BKS);

            // Crea TrustManager para confiar en el CA en el KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(ksBKS);

            // Crea un SSLContext que usa el nuevo TrustManager
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tmf.getTrustManagers(), null);
        } catch (KeyStoreException e) {
            Log.e(GlobalShare.logAplicaion, "KeyStoreException.", e);
            cambiaVistaYTexto( VistaActualizacion.RESULTADO,msjResultado);
        } catch (CertificateException e) {
            Log.e(GlobalShare.logAplicaion, "CertificateException.", e);
            cambiaVistaYTexto( VistaActualizacion.RESULTADO,msjResultado);
        } catch (NoSuchAlgorithmException e) {
            Log.e(GlobalShare.logAplicaion, "NoSuchAlgorithmException.", e);
            cambiaVistaYTexto( VistaActualizacion.RESULTADO,msjResultado);
        } catch (KeyManagementException e) {
            Log.e(GlobalShare.logAplicaion, "KeyManagementException.", e);
            cambiaVistaYTexto( VistaActualizacion.RESULTADO,msjResultado);
        } catch (IOException e) {
            Log.e(GlobalShare.logAplicaion, "IOException.", e);
            cambiaVistaYTexto( VistaActualizacion.RESULTADO,msjResultado);
        }



        try {
            URL url;
            byte[] buf;
            int ByteRead, ByteWritten = 0, totalBytesRead=0;
            url = new URL(fAddress);
            outStream = new BufferedOutputStream(new FileOutputStream(
                    destinationDir + "/"+localFileName));

            try {
                HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
                uCon = url.openConnection();
                is = uCon.getInputStream();
            } catch (Exception e) {
                Log.e(GlobalShare.logAplicaion, "openConnection:", e);
                cambiaVistaYTexto( VistaActualizacion.RESULTADO,msjResultado);
                return ;
            }

            //totalBytesRead = is.available();

            Log.i(GlobalShare.logAplicaion, "Inicia descarga URL.");
            cambiaVistaYTexto( VistaActualizacion.DESCARGA,"Descargando versión " + versionDescargar + " %" +
                    String.format(new Locale("es", "MX"), "%3.0f", 0.0f));

            buf = new byte[1024];
            //float prog=0f, i=0.1f;
            while ((ByteRead = is.read(buf)) != -1) {
                outStream.write(buf, 0, ByteRead);
                ByteWritten += ByteRead;
            }

            cambiaVistaYTexto( VistaActualizacion.DESCARGA,"Descargando versión " + versionDescargar + " %" +
                    String.format(new Locale("es", "MX"), "%3.0f", 100.0f));
            progresoDesacarga.setProgress(100);

            Log.i(GlobalShare.logAplicaion, "Downloaded Successfully in ["+destinationDir +"/"+ localFileName+"]");
            Log.i(GlobalShare.logAplicaion, "File name: [" + localFileName + "], No ofbytes :" + ByteWritten);

        } catch (Exception e) {
            Log.e(GlobalShare.logAplicaion, "Descargando.", e);
            cambiaVistaYTexto( VistaActualizacion.RESULTADO,msjResultado);
        } finally {
            try {
                is.close();
                outStream.close();
            } catch (Exception e) {
                Log.e(GlobalShare.logAplicaion, "cerrando input stream.", e);
            }
        }

        try {
            cambiaVistaYTexto( VistaActualizacion.RESULTADO,"Inicia instalación de archivo descargado.");
            Log.d(GlobalShare.logAplicaion, "Iniciando proceso de instalación.");
            File archivo = new File(destinationDir + "/"+localFileName);

            if( !archivo.exists() ){
                cambiaVistaYTexto( VistaActualizacion.RESULTADO,"Archivo descargado no econtrado.");
                Log.d(GlobalShare.logAplicaion, "Archivo descargado no econtrado.");
                return ;
            }

            Uri uriArchivoEncontrado = null;
            Intent intentInstaller = null;

            uriArchivoEncontrado = Uri.fromFile(archivo);
            intentInstaller = new Intent(Intent.ACTION_VIEW);
            intentInstaller.setDataAndType(
                    uriArchivoEncontrado,
                    "application/vnd.android.package-archive");
            intentInstaller.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intentInstaller);
        }catch(Exception e){
            Log.e(GlobalShare.logAplicaion, "Instalando: ", e);
            cambiaVistaYTexto( VistaActualizacion.RESULTADO,msjResultado);
        }*/

    }
}




