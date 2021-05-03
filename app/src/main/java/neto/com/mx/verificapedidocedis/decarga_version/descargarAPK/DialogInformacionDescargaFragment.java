package neto.com.mx.verificapedidocedis.decarga_version.descargarAPK;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import neto.com.mx.verificapedidocedis.R;
import neto.com.mx.verificapedidocedis.cliente.ClienteConsultaGenericaPrueba2;
import neto.com.mx.verificapedidocedis.cliente.ClienteSSLConsultaGenerica;
import neto.com.mx.verificapedidocedis.cliente.HandlerRespuestasVolley;
import neto.com.mx.verificapedidocedis.decarga_version.descargarAPK.object.ParametrosDescarga;
import neto.com.mx.verificapedidocedis.mensajes.ParametroCuerpo;
import neto.com.mx.verificapedidocedis.mensajes.RespuestaDinamica;
import neto.com.mx.verificapedidocedis.mensajes.SolicitudServicio;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.FileManager;
import neto.com.mx.verificapedidocedis.utiles.GlobalShare;
import neto.com.mx.verificapedidocedis.utiles.LocalProperties;
import neto.com.mx.verificapedidocedis.utiles.exception.LogException;

public class DialogInformacionDescargaFragment extends DialogFragment {

    private static final int ACCESO_DENEGADO_A_APP = -1;
    private static final int NO_ES_NECESARIO_ACTUALIZAR = 0;
    private static final int ACTUALIZAR_NUEVA_VERSION = 1;
    private static final int ACTUALIZAR_VERSION_ESTABLE = 2;
    private static final int INICIO_RESPUESTAS_ERROR = 3;
    private static final int APPLICACION_INACTIVA = 15;

    final int idxVersion = 4,
            idxUrlDescarga = 5,
            idxIdError = 6,
            idxMensaje = 7;

    private Activity context;

    TextView textoInformativo;
    ProgressBar progressBar;
    Button descargaBotonCerrar;

    //Variables DownloadManager

    private long downloadId = 0;
    private DownloadManager downloadManager;
    private boolean descargaTerminada = false;
    private Timer timer = null;
    private int intentosDescargaRealizados = 0;

    //Verificar si usuario selecciono instalar.
    private boolean usuarioInstala = false;
    private int counterResume = 0;


    private void setContext(Activity context) {
        this.context = context;
    }

    public static DialogInformacionDescargaFragment newInstance(Activity context) {
        DialogInformacionDescargaFragment dialogInformacionDescargaFragment = new DialogInformacionDescargaFragment();
        dialogInformacionDescargaFragment.setContext(context);
        return dialogInformacionDescargaFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Constantes.DOWNLOAD_APK_WITH_MANAGER){
            IntentFilter filtroDescargaCompleta = new IntentFilter( DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.registerReceiver(broadcastReceiverDownload, filtroDescargaCompleta);
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(Constantes.DOWNLOAD_APK_WITH_MANAGER) context.unregisterReceiver(broadcastReceiverDownload);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(counterResume++ >=1 && !usuarioInstala){
            textoInformativo.setText("Para continuar, debe seleccionar la opcion de configurar o Instalar, reinicie la aplicación para volverlo a intentar.");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_descarga_apk, container, false);
        textoInformativo = v.findViewById(R.id.descargaTextInformativo);
        progressBar = v.findViewById(R.id.descargaProgressBar);
        setCancelable(false);

        descargaBotonCerrar = v.findViewById(R.id.descargaBotonCerrar);

        descargaBotonCerrar.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);


        final DescargaCallback descargaCallback = new DescargaCallback() {
            @Override
            public void updateProgress(int progreso) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progreso);
                textoInformativo.setText("Descargando actualización " + progreso + " %");
            }

            @Override
            public void updateTextinfo(String text) {
                textoInformativo.setText(text);
            }

            @Override
            public void errorDownloading(String error) {
                textoInformativo.setText(error);
                //Muestra y Habilita boton
                //descargaBotonCerrar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void finishedDownloading(String u) {
                //descargaBotonCerrar.setVisibility(View.VISIBLE);
                textoInformativo.setText("Iniciando instalación...");

                try {
                    if(FileManager.isLocalInstallationOlder(context,Uri.parse(u))){
                        startActivity(FileManager.getIntentForApk(context,new File(u),null));
                    }else{
                        textoInformativo.setText("No se pudo instalar la Actualización Error: 0x776");
                        progressBar.setVisibility(View.GONE);
                        //descargaBotonCerrar.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    textoInformativo.setText("Ocurrio un error al verificar el archivo Descargado: 0x778");
                    e.printStackTrace();
                }

            }

            @Override
            public void apkUpToDate(String text) {
                //descargaBotonCerrar.setVisibility(View.VISIBLE);
                textoInformativo.setText(text);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                },1500);
            }
        };

        descargaBotonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProcesoDescarga(descargaCallback);
            }
        });
        initProcesoDescarga(descargaCallback);
        return v;

    }

    private void initProcesoDescarga(final DescargaCallback descargaCallback) {
        descargaBotonCerrar.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        String aplicacionId = getResources().getString(R.string.app_id);
        String imeii = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        final String versionActual = LogException.getDeviceVersionName(context);

        if (aplicacionId == null || aplicacionId.isEmpty()) {
            descargaCallback.errorDownloading("No se ha configurado un id para esta aplicación.");
            return;
        }
        if (versionActual == null || versionActual.isEmpty()) {
            descargaCallback.errorDownloading("No ha configurado la versión actual de esta aplicación.");
            return;
        }
        if (imeii == null || imeii.isEmpty()) {
            descargaCallback.errorDownloading("No fue posible leer el IDENTIFICADOR de este dispositivo.");
            return;
        }

        descargaCallback.updateTextinfo("Buscando actualizaciones descargadas en el dispositivo...");
        File directorioDescargas = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

        if (directorioDescargas != null) {
            File[] encontrados = directorioDescargas.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().contains(versionActual + ".apk");
                }
            });

            if (encontrados != null && encontrados.length > 0) {
                descargaCallback.updateTextinfo("Eliminando descargas anteriores...");
                for (File arcActual : encontrados) {
                    if (arcActual != null) {
                        Log.d(GlobalShare.logAplicaion, "Elimnando archivo: " + arcActual.getAbsolutePath());
                        try {
                            arcActual.delete();
                        } catch (Exception e) {
                            Log.e(GlobalShare.logAplicaion, getClass().getName() + " : Error al eliminar archivo...", e);
                        }
                    }
                }
            } else {
                descargaCallback.updateTextinfo("No se encontraron actualizaciones descargadas...");
            }
        }
        List<ParametroCuerpo> cuerpoPeticion = new ArrayList<>();

        GlobalShare.getInstace().setVersionVerificado(false);

        cuerpoPeticion.add(new ParametroCuerpo(1, "String", imeii));//IMEII
        cuerpoPeticion.add(new ParametroCuerpo(2, "Long", aplicacionId));//IDAPP
        cuerpoPeticion.add(new ParametroCuerpo(3, "String", versionActual));//VERSIONACTUAL
        cuerpoPeticion.add(new ParametroCuerpo(idxVersion, ":String", ""));//Version por actualizar
        cuerpoPeticion.add(new ParametroCuerpo(idxUrlDescarga, ":String", ""));//URL Descarga
        cuerpoPeticion.add(new ParametroCuerpo(idxIdError, "::Int", "0"));
        cuerpoPeticion.add(new ParametroCuerpo(idxMensaje, "::String", "0"));

        descargaCallback.updateTextinfo("Validando Información del dispositivo...");

        SolicitudServicio solicitud = new SolicitudServicio("VERIFICAVERSION", cuerpoPeticion);
        if (Constantes.AMBIENTE_APP >= Constantes.AMBIENTE_PROD) {
            ClienteConsultaGenericaPrueba2 cliente = new ClienteConsultaGenericaPrueba2(
                    Constantes.CADENA_CONEXION,
                    context,
                    solicitud,
                    getHandlerRespuestasVolley(descargaCallback)
            );
            cliente.setManejarCodigoError(false);
            cliente.ejecutarConsultaWSSinDialogos(idxIdError, idxMensaje);
        } else {
            ClienteSSLConsultaGenerica cliente = new ClienteSSLConsultaGenerica(
                    Constantes.CADENA_CONEXION,
                    context,
                    solicitud,
                    getHandlerRespuestasVolley(descargaCallback)
            );
            cliente.setManejarCodigoError(false);
            cliente.ejecutarConsultaWSSinDialogos(idxIdError, idxMensaje);
        }


    }

    static class DescargaApkTask extends AsyncTask<ParametrosDescarga, Integer, String> {
        private final DescargaCallback descargaCallback;
        private ParametrosDescarga parametrosDescarga;
        private boolean isSuccessful = false;

        public DescargaApkTask(DescargaCallback descargaCallback, Context context) {
            this.descargaCallback = descargaCallback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            descargaCallback.updateProgress(0); //Inicia Proceso
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            descargaCallback.updateProgress(values[0]); //Notifica Progreso
        }

        @Override
        protected String doInBackground(ParametrosDescarga... params) {
            OutputStream outStream;
            InputStream inputStream;
            URLConnection urlConnection;
            String error = "";
            this.parametrosDescarga = params[0];
            try {
                URL url;
                byte[] buffer;
                int byteLeido;
                int byteEscrito = 0;

                url = new URL(parametrosDescarga.getUrlHttps());
                outStream = new BufferedOutputStream(new FileOutputStream(
                        parametrosDescarga.getDirectorioDestino() + "/" + parametrosDescarga.getNombreApk()));

                try {
                    // Crea un SSLContext que usa el nuevo TrustManager
                    //certificate
                    SSLContext sslcontext = SSLContext.getInstance("TLS");
                    //sslcontext.init(null, tmf.getTrustManagers(), null);
                    sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        }
                    }}, new java.security.SecureRandom());

                    HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
                    /**/
                    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String requestedHost, SSLSession remoteServerSession) {
                            return requestedHost.equalsIgnoreCase(remoteServerSession.getPeerHost());
                        }
                    });

                    urlConnection = url.openConnection();
                    urlConnection.setReadTimeout(12000);
                    inputStream = urlConnection.getInputStream();
                    //apk
                    double bytesTotales = urlConnection.getContentLength();

                    buffer = new byte[2048];
                    while ((byteLeido = inputStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, byteLeido);
                        byteEscrito += byteLeido;
                        double progresoCalculado = (byteEscrito * 100) / bytesTotales;
                        publishProgress((int) progresoCalculado);
                    }

                    inputStream.close();
                    outStream.flush();
                    outStream.close();
                    if (bytesTotales != byteEscrito) {
                        isSuccessful = false;
                        return "El tamaño del archivo descargado no coincide con el proporcionado por el servidor por lo que se borrará.";
                    }
                    isSuccessful = true;

                } catch (Exception e) {
                    String LOG = "DIAG_ASYNCTASK";
                    Log.e(LOG, "openConnection:", e);
                    return "Ocurrio un error durante la descarga " + e.getMessage() + " " + e.getCause();
                }
            } catch (MalformedURLException | FileNotFoundException e) {
                e.printStackTrace();
                return "No se pudo descargar el apk " + e.getMessage() + " " + e.getCause();
            }

            return error;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            File archivo = new File(parametrosDescarga.getDirectorioDestino() + "/" + parametrosDescarga.getNombreApk());
            if (archivo.exists() && isSuccessful) {
                descargaCallback.updateTextinfo("Archivo descargado correctamente.");
                descargaCallback.finishedDownloading(parametrosDescarga.getDirectorioDestino() + "/" + parametrosDescarga.getNombreApk());
            } else {
                descargaCallback.errorDownloading(result);
            }
        }
    }

    HandlerRespuestasVolley getHandlerRespuestasVolley(final DescargaCallback descargaCallback) {

        return new HandlerRespuestasVolley() {
            @Override
            public void manejarExitoVolley(final RespuestaDinamica respuesta) {
                try {
                    if (respuesta.getStackTrace() != null && !respuesta.getStackTrace().isEmpty()) {
                        descargaCallback.errorDownloading("Se presentó un problema al validar la versión:  "
                                + respuesta.toString());
                        return;
                    }
                    if (respuesta == null
                            || respuesta.getDatosSalida() == null
                            || respuesta.getDatosSalida().size() == 0) {
                        descargaCallback.errorDownloading("Se obtuvo una respuesta vacía del servicio.");
                        return;
                    }

                    int idOperacionRealizar = Integer.parseInt(respuesta.getDatosSalida().get(idxIdError));
                    String descripOpRealizar = respuesta.getDatosSalida().get(idxMensaje);
                    descargaCallback.updateTextinfo(descripOpRealizar);

                    switch (idOperacionRealizar) {
                        case NO_ES_NECESARIO_ACTUALIZAR:
                            LocalProperties.setUsuarioBloqueado(false);
                            descargaCallback.apkUpToDate("No es necesario actualizar la versión.");
                            return;
                        case ACTUALIZAR_VERSION_ESTABLE:
                            descargaCallback.updateTextinfo("Se Requiere instalar una versión anterior: " + descripOpRealizar);
                            LocalProperties.setUsuarioBloqueado(false);
                            break;
                        case ACTUALIZAR_NUEVA_VERSION:
                            descargaCallback.updateTextinfo("Se Requiere instalar una nueva versión: " + descripOpRealizar);
                            LocalProperties.setUsuarioBloqueado(false);
                            break;
                        case ACCESO_DENEGADO_A_APP:
                        case APPLICACION_INACTIVA:
                            GlobalShare.getInstace().setAccesoVerificado(false);
                            LocalProperties.setUsuarioBloqueado(true);
                            descargaCallback.updateTextinfo("Acceso denegado para este dispositivo. " + descripOpRealizar);
                            return;
                        case INICIO_RESPUESTAS_ERROR:
                        default:
                            LocalProperties.setUsuarioBloqueado(false);
                            descargaCallback.updateTextinfo("Ocurrio un error. " + descripOpRealizar);
                            return;
                    }
                    try {
                        //final String versionDescargar = respuesta.getDatosSalida().get(idxVersion);
                        final String urlVerSig = respuesta.getDatosSalida().get(idxUrlDescarga);
                        String[] nombre = urlVerSig.split(Matcher.quoteReplacement("/"));
                        String nombreAPK = nombre[nombre.length - 1];

                        File archivo = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), nombreAPK);

                        if (archivo != null && archivo.exists() && archivo.getTotalSpace() > 0) {
                            descargaCallback.updateTextinfo("Instalando aplicación...");

                            if(FileManager.isLocalInstallationOlder(context,Uri.parse(archivo.getAbsolutePath()))){
                                startActivity(FileManager.getIntentForApk(context,archivo,null));
                            }else{
                                textoInformativo.setText("No se pudo instalar la Actualización Error: 0x776");
                                progressBar.setVisibility(View.GONE);
                                //descargaBotonCerrar.setVisibility(View.VISIBLE);
                            }
                        } else {
                            descargaCallback.updateTextinfo("Iniciando descarga...");

                            if (Constantes.DOWNLOAD_APK_WITH_MANAGER) {
                                downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                DownloadManager.Request req = new DownloadManager.Request(Uri.parse(urlVerSig));
                                req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                req.setAllowedOverRoaming(false);
                                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                                //req.setVisibleInDownloadsUi(false);
                                req.setTitle("Actualización");
                                req.setDescription("Descargando nueva versión...");
                                req.setDestinationInExternalFilesDir(
                                        context,
                                        Environment.DIRECTORY_DOWNLOADS,
                                        nombreAPK
                                );
                                intentosDescargaRealizados = 0;
                                downloadId =  downloadManager.enqueue(req);
                                runDownloadTimer();

                            } else {
                                DescargaApkTask descargaApkTask = new DescargaApkTask(descargaCallback, context);
                                descargaApkTask.execute(new ParametrosDescarga(
                                        urlVerSig,
                                        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
                                        nombreAPK
                                ));
                            }
                        }
                    } catch (Exception e) {
                        Log.e(GlobalShare.logAplicaion, e.getMessage(), e);
                        descargaCallback.errorDownloading("Ocurrió un error durante el proceso de actualización: " + e.getMessage());
                    }
                } catch (Exception e) {
                    Log.e(GlobalShare.logAplicaion, getClass().getName() + " : verificarVersion : " + e.getMessage(), e);
                    descargaCallback.errorDownloading("Ocurrió un error durante la verificación de la aplicación.");
                }
            }

            @Override
            public void manejarErrorVolley(final VolleyError error) {
                String errorMostrar = "Se presentó un problema al intentar realizar la consulta.";

                if (error != null) {
                    errorMostrar = "No se tiene acceso al servidor.";
                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode >= 500) {
                            errorMostrar = "[" + error.networkResponse.statusCode + ", " +
                                    error.networkResponse.toString() + "]  \nEl servidor presenta un problema al recibir conexiónes. ¿Se encuentra arriba la instancia?";
                        } else if (error.networkResponse.statusCode >= 400 &&
                                error.networkResponse.statusCode < 500) {
                            errorMostrar = "[" + error.networkResponse.statusCode + ", " +
                                    error.networkResponse.data.toString() + "] \nSe presentó un problema al intentar conectarse con el servidor.";
                        } else if (error.networkResponse.statusCode >= 300 &&
                                error.networkResponse.statusCode < 400) {
                            errorMostrar = "[" + error.networkResponse.statusCode + ", " +
                                    error.networkResponse.toString() + "]  \nSe presentó un problema al enviar los datos al servidor.";
                        } else {
                            errorMostrar = "[" + error.networkResponse.statusCode + ", " +
                                    error.networkResponse.toString() + "]  \nSe presentó un problema inesperado al enviar los datos al servidor.";
                        }
                    }
                }
                descargaCallback.errorDownloading(errorMostrar);
                Log.e(GlobalShare.logAplicaion, getClass().getName() +
                        " : verificarVersion : manejarErrorVolley :" + error.getMessage(), error);
            }
        };
    }

  private void runDownloadTimer(){
      if(timer==null){
          timer = new Timer();
      }else{
          timer.cancel();
          timer = new Timer();
      }
      timer.schedule(new TimerTask() {
          @Override
          public void run() {
              Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
              if (cursor != null && cursor.moveToNext()) {
                  int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                  final long downloadIDActual = cursor.getLong(cursor.getColumnIndex( DownloadManager.COLUMN_ID));
                  final long bytes_total = cursor.getLong(cursor.getColumnIndex( DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                  final long bytes_so_far = cursor.getLong(cursor.getColumnIndex( DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                  final int razonFalla = cursor.getInt(cursor.getColumnIndex( DownloadManager.COLUMN_REASON));
                  cursor.close();

                  if(downloadId != downloadIDActual)
                      return;
                  if (intentosDescargaRealizados++ >= 5) {
                      if (status == DownloadManager.STATUS_PAUSED &&
                              razonFalla == DownloadManager.PAUSED_WAITING_TO_RETRY) {
                          context.runOnUiThread(new Runnable() {
                              @Override
                              public void run() {
                                  textoInformativo.setText("Descarga Fallida, reinicie la aplicación para intentar actualizar nuevamente.");
                                  downloadManager.remove(downloadIDActual);
                                  descargaTerminada = true;
                              }
                          });
                          return;
                      }
                  }
                  if (status == DownloadManager.STATUS_FAILED) {
                      // do something when failed

                      context.runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              String razon;
                              switch (razonFalla) {
                                  case DownloadManager.ERROR_CANNOT_RESUME:
                                      razon = "No se pudo resumir la descarga.";
                                      break;
                                  case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                                      razon = "No se encontró el dispositivo";
                                      break;
                                  case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                                      razon = "El archivo ya se encuentra en el dispositivo";
                                      break;
                                  case DownloadManager.ERROR_FILE_ERROR:
                                      razon = "Error en el archivo descargado";
                                      break;
                                  case DownloadManager.ERROR_HTTP_DATA_ERROR:
                                      razon = "Error en datos HTTP";
                                      break;
                                  case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                                      razon = "No hay suficiente espacio para la descarga";
                                      break;
                                  case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                                      razon = "Conexión sobrepasó las redirecciones";
                                      break;
                                  case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                                      razon = "Código HTTP desconocido";
                                      break;
                                  case DownloadManager.ERROR_UNKNOWN:
                                      razon = "Error desconocido";
                                      break;
                                  default:
                                      razon = "Unknown";
                              }
                              textoInformativo.setText("Descarga Fallida, reinicie la aplicación para intentar actualizar nuevamente. Motivo: " + razon);
                              //Muestra y Habilita boton
                              //descargaBotonCerrar.setVisibility(View.VISIBLE);
                              progressBar.setVisibility(View.GONE);
                              descargaTerminada = true;
                          }
                      });

                  }
                  else if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_PAUSED) {
                      // do something pending or paused

                      context.runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              String razon;
                              switch (razonFalla) {
                                  case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                                      razon = "Esperando por red WIFI";
                                      break;
                                  case DownloadManager.PAUSED_UNKNOWN:
                                      razon = "Esperando...";
                                      break;
                                  case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                                      razon = "Esperando por red celular";
                                      break;
                                  case DownloadManager.PAUSED_WAITING_TO_RETRY:
                                      razon = "Esperando para reintentar...";
                                      break;
                                  default:
                                      razon = "Unknown";
                              }
                              textoInformativo.setText("Descarga en espera..., Motivo: " + razon);
                          }
                      });

                      //Muestra y Habilita boton
                  }
                  else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                      // do something when successful
                  }
                  else if (status == DownloadManager.STATUS_RUNNING) {
                      // do something when running
                      context.runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              final Float progreso = bytes_so_far == 0L ? 0f : ((bytes_so_far * 1f / bytes_total * 1f) * 100f);
                              progressBar.setVisibility(View.VISIBLE);
                              progressBar.setProgress(progreso.intValue());
                              textoInformativo.setText("Descargando actualización " + progreso.intValue() + " %");
                          }
                      });


                  }
              }
              if(!descargaTerminada){
                  runDownloadTimer();
              }
          }
      }, 300);
  }
    private final BroadcastReceiver broadcastReceiverDownload = new BroadcastReceiver() {
        public void onReceive(Context contexto, Intent intent) {
            long idReferencia = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Uri uriURLDescarga = null;
            if (downloadId == idReferencia) {
                uriURLDescarga = downloadManager.getUriForDownloadedFile(downloadId);
                if (uriURLDescarga != null) {
                    progressBar.setProgress(100);
                    descargaTerminada = true;
                    textoInformativo.setText("actualización descargada al" + 100 + "% Iniciando instalación...");
                    //Muestra y Habilita boton


                    IntentFilter ifilter = new IntentFilter();
                    ifilter.addAction(Intent.ACTION_PACKAGE_ADDED);
                    ifilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
                    ifilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
                    ifilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
                    ifilter.addDataScheme("package");

                    contexto.registerReceiver(installReceiver, ifilter);

                    try {
                        if(FileManager.isLocalInstallationOlder(context,uriURLDescarga)){
                            startActivity(FileManager.getIntentForApk(context,null,uriURLDescarga));
                        }else{
                            textoInformativo.setText("No se pudo instalar la Actualización Error: 0x776");
                            progressBar.setVisibility(View.GONE);
                            //descargaBotonCerrar.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        textoInformativo.setText("Ocurrio un error al verificar el archivo Descargado: 0x778");
                        e.printStackTrace();
                    }

                } else {
                    textoInformativo.setText("No es posible descargar la actualización, es posible que los archivos ya no esten disponibles en la ubicación registrada.");
                    //Muestra y Habilita boton
                    //descargaBotonCerrar.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    descargaTerminada = true;
                }
            }
        }
    };

    private final BroadcastReceiver installReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            usuarioInstala = true;
        }
    };

    interface DescargaCallback {
        void updateProgress(int progreso);

        void updateTextinfo(String text);

        void errorDownloading(String error);

        void finishedDownloading(String path);

        void apkUpToDate(String text);
    }
}
