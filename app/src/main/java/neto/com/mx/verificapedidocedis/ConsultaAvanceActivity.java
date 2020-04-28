package neto.com.mx.verificapedidocedis;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neto.com.mx.verificapedidocedis.beans.ArticuloVO;
import neto.com.mx.verificapedidocedis.beans.CodigoBarraVO;
import neto.com.mx.verificapedidocedis.beans.CodigosGuardadosVO;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.TiposAlert;

public class ConsultaAvanceActivity extends AppCompatActivity {

    private int ACCION_GUARDA = 0;
    private HashMap<Long, ArticuloVO> mapaCatalogo = null;
    private HashMap<String, Integer> mapaCodigosNoRem = null;
    private String folio = "";
    private String numeroEmpleado = "";
    private String nombreEmpleado = "";
    private String nombreZona = "";
    private int idZona = 0;
    String version = "";
    String metodo="";
    boolean bloqueo_finalizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_avance);

        getSupportActionBar().hide();

        mapaCatalogo = (HashMap<Long, ArticuloVO>)this.getIntent().getSerializableExtra("mapaCat");
        mapaCodigosNoRem = (HashMap<String, Integer>)this.getIntent().getSerializableExtra("mapaCodNoRem");
        folio = new String(this.getIntent().getStringExtra("folio"));
        nombreEmpleado = new String(this.getIntent().getStringExtra("nombreEmpleado").trim());
        numeroEmpleado = new String(this.getIntent().getStringExtra("numeroEmpleado").trim());
        nombreZona = new String(this.getIntent().getStringExtra("nombreZona").trim());
        idZona = this.getIntent().getIntExtra("idZona", 0);

        guardaAvance();

        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch(PackageManager.NameNotFoundException ne) {
            Log.e("CARGA_FOLIO_TAG", "Error al obtener la versión: " + ne.getMessage());
        }
    }

    public void regresarMenuFront(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        regresar();
    }

    public void guardaAvance() {
        ACCION_GUARDA = 0;
        metodo= "ConsultaAvanceActivity - guardaAvance";
        ejecutaWS();
    }

    public void ejecutaWS() {
        if(!bloqueo_finalizado) {
            if (ACCION_GUARDA == 1) {//si finaliza
                bloqueo_finalizado = true; //bloquea el metodo de guardado para que solo se ejecute una vez
                System.out.println("SE BLOQUEA FINALIZADO HASTA FINALIZAR");
            }
            // Do something in response to button
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Operaciones http
                System.out.println("ENTRA A METODO DE GUARDADO 4" + metodo);

                try {
                    String url = Constantes.URL_STRING + "guardarArtsContadosVerificador";

                    final ProgressDialog mDialog = new ProgressDialog(this);
                    mDialog.setMessage("Consultando avance...");
                    mDialog.setCancelable(false);
                    mDialog.setInverseBackgroundForced(false);
                    mDialog.show();

                    StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    mDialog.dismiss();
                                    System.out.println("tipo Guardado: " + String.valueOf(ACCION_GUARDA));
                                    System.out.println("*** 1 *** response: " + response);

                                    CodigosGuardadosVO codigosFaltantes = new CodigosGuardadosVO();
                                    generaFaltantes(response, codigosFaltantes);

                                    if (codigosFaltantes.getCodigo() == 0) {
                                        TextView artSurtidosText = (TextView) findViewById(R.id.artPickeados);
//                                    artSurtidosText.setText(String.valueOf(codigosFaltantes.getTotalArticulosAsignados()));

                                        TextView artContadosText = (TextView) findViewById(R.id.artVerificados);
                                        artContadosText.setText(String.valueOf(codigosFaltantes.getTotalArticulosVerificados()));

                                        TextView cajasSurtidosText = (TextView) findViewById(R.id.cajasPickeadas);
//                                    cajasSurtidosText.setText(String.valueOf(codigosFaltantes.getTotalCajasAsignadas()));

                                        TextView cajasContadosText = (TextView) findViewById(R.id.cajasVerificadas);
                                        cajasContadosText.setText(String.valueOf(codigosFaltantes.getTotalCajasVerificadas()));

                                        TextView porcentajeArticulosText = (TextView) findViewById(R.id.porcentajeArticulos);
                                        if (codigosFaltantes.getTotalArticulosAsignados() != 0) {
                                            int porcentajeArticulos = (int) ((codigosFaltantes.getTotalArticulosVerificados() * 100) / codigosFaltantes.getTotalArticulosAsignados());
                                            porcentajeArticulosText.setText(String.valueOf(porcentajeArticulos + "%"));

                                            ProgressBar progressBarArticulos = (ProgressBar) findViewById(R.id.progressBarArticulos);
                                            ObjectAnimator animation = ObjectAnimator.ofInt(progressBarArticulos, "progress", 0, porcentajeArticulos); // see this max value coming back here, we animale towards that value
                                            animation.setDuration(2000); //in milliseconds
                                            animation.setInterpolator(new DecelerateInterpolator());
                                            animation.start();
                                        } else {
                                            porcentajeArticulosText.setText("0%");
                                        }

                                        TextView porcentajeCajasText = (TextView) findViewById(R.id.porcentajeCajas);
                                        if (codigosFaltantes.getTotalCajasAsignadas() != 0) {
                                            int porcentajeCajas = (int) ((codigosFaltantes.getTotalCajasVerificadas() * 100) / codigosFaltantes.getTotalCajasAsignadas());
                                            porcentajeCajasText.setText(String.valueOf(porcentajeCajas + "%"));

                                            ProgressBar progressBarCajas = (ProgressBar) findViewById(R.id.progressBarCajas);
                                            ObjectAnimator animationCajas = ObjectAnimator.ofInt(progressBarCajas, "progress", 0, porcentajeCajas); // see this max value coming back here, we animale towards that value
                                            animationCajas.setDuration(2000); //in milliseconds
                                            animationCajas.setInterpolator(new DecelerateInterpolator());
                                            animationCajas.start();
                                        } else {
                                            porcentajeCajasText.setText("0%");
                                        }
                                    } else {
                                        ViewDialog alert = new ViewDialog(ConsultaAvanceActivity.this);
                                        alert.showDialog(ConsultaAvanceActivity.this, "Error en el servicio que guarda los códigos: " + codigosFaltantes.getMensaje(), null, TiposAlert.ERROR);
                                    }
                                    bloqueo_finalizado = false;
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    bloqueo_finalizado = false;
                                    mDialog.dismiss();
                                    //Toast.makeText(getApplicationContext(), "Error en el WS que guarda los códigos: " + error.toString(), Toast.LENGTH_SHORT).show();
                                    ViewDialog alert = new ViewDialog(ConsultaAvanceActivity.this);
                                    alert.showDialog(ConsultaAvanceActivity.this, "Error en el servicio que guarda los códigos: favor de validar la comunicación del dispositivo", null, TiposAlert.ERROR);
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            System.out.println(obtieneCadenaArticulos());
                            System.out.println(obtieneCadenaCajas());

                            params.put("folio", folio);
                            params.put("idZona", String.valueOf(idZona));
                            params.put("articulosArray", obtieneCadenaArticulos());
                            params.put("cantidadesArray", obtieneCadenaCajas());
                            params.put("tipoGuardado", String.valueOf(ACCION_GUARDA));
                            params.put("usuario", numeroEmpleado);
                            params.put("numeroSerie", Build.SERIAL);
                            params.put("version", version);
                            params.put("metodo", metodo);

                            return params;
                        }
                    };
                    strRequest.setRetryPolicy(new DefaultRetryPolicy(
                            20000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    AppController.getInstance().addToRequestQueue(strRequest, "tag");

                } catch (Exception me) {
                    bloqueo_finalizado = false;
                    ViewDialog alert = new ViewDialog(ConsultaAvanceActivity.this);
                    alert.showDialog(ConsultaAvanceActivity.this, "URL no disponible: favor de validar la conexión a Internet", null, TiposAlert.ERROR);
                }
            } else {
                bloqueo_finalizado = false;
                // Mostrar errores
                ViewDialog alert = new ViewDialog(ConsultaAvanceActivity.this);
                alert.showDialog(ConsultaAvanceActivity.this, "No hay conexión HTTP", null, TiposAlert.ERROR);
            }
        }else{
            System.out.println("SE OMITE PETICION MIENTRAS TRABAJA FINALIZADO");
        }
    }

    public void generaFaltantes(String response, CodigosGuardadosVO codigos) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader(response) );

            int eventType = xpp.getEventType();
            List<CodigoBarraVO> listaCodigos = new ArrayList<CodigoBarraVO>();

            CodigoBarraVO codigoBarraVO = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("totalCajasVerificadas")) {
                        eventType = xpp.next(); // advance to inner text
                        codigos.setTotalCajasVerificadas(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("totalCajasAsignadas")) {
                        eventType = xpp.next(); // advance to inner text
                        codigos.setTotalCajasAsignadas(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("articulosVerificados")) {
                        eventType = xpp.next(); // advance to inner text
                        codigos.setTotalArticulosVerificados(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("articulosAsignados")) {
                        eventType = xpp.next(); // advance to inner text
                        codigos.setTotalArticulosAsignados(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("errorCode")) {
                        eventType = xpp.next(); // advance to inner text
                        codigos.setCodigo(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("errorDesc")) {
                        eventType = xpp.next(); // advance to inner text
                        codigos.setMensaje(xpp.getText());
                    } else if(xpp.getName().equals("articulosConDiferencia")) {
                        codigoBarraVO = new CodigoBarraVO();
                    } else if(xpp.getName().equals("articuloId")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setArticuloId(Long.parseLong(xpp.getText()));
                    } else if(xpp.getName().equals("cantidadVerificada")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setCajasVerificadas(Integer.parseInt(xpp.getText()));
                    } else if(xpp.getName().equals("cantidadAsignada")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setCajasAsignadas(Integer.parseInt(xpp.getText()));
                    } else if(xpp.getName().equals("codigobarras")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setCodigoBarras(xpp.getText());
                    } else if(xpp.getName().equals("nombre")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setNombreArticulo(xpp.getText());
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    if(xpp.getName().equals("articulosConDiferencia")) {
                        listaCodigos.add(codigoBarraVO);
                    }
                }
                eventType = xpp.next();
            }
            codigos.setArticulosDiferencias(listaCodigos.toArray(new CodigoBarraVO[listaCodigos.size()]));

            int contador = 0;
            for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                if(entry.getValue().isEsCapturado()) {
                    contador++;
                }
            }
        } catch(Exception e) {
            ViewDialog alert = new ViewDialog(ConsultaAvanceActivity.this);
            alert.showDialog(ConsultaAvanceActivity.this, "Error al formar las diferencias del xml: " + e.getMessage(), null, TiposAlert.ERROR);
        }
    }

    public String obtieneCadenaArticulos() {
        StringBuffer cadena = new StringBuffer();
        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            cadena.append(entry.getKey());
            cadena.append("|");
        }
        cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "" );
        System.out.println(":::: Articulos = " + cadena);
        return cadena.toString();
    }

    public String obtieneCadenaCajas() {
        StringBuffer cadena = new StringBuffer();
        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            cadena.append(entry.getValue().getTotalCajasVerificadas());
            cadena.append("|");
        }
        cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "" );
        System.out.println(":::: Cajas = " + cadena);
        return cadena.toString();
    }

    public void regresar() {
        onBackPressed();
    }
}
