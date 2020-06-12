package neto.com.mx.verificapedidocedis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.ksoap2.serialization.SoapObject;
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
import neto.com.mx.verificapedidocedis.beans.RespuestaIncidenciasVO;
import neto.com.mx.verificapedidocedis.dialogos.PantallaInicioDialogo;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialogoGenerico;
import neto.com.mx.verificapedidocedis.providers.ProviderGeneraCatalogo;
import neto.com.mx.verificapedidocedis.providers.ProviderGuardarArticulos;
import neto.com.mx.verificapedidocedis.providers.ProviderGuardarDiferencias;
import neto.com.mx.verificapedidocedis.providers.ProviderGuardarDiferencias_CargaCodigosBarra;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.TiposAlert;

import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GETCATALOGOARTICULOSVERIFICADORGENERAL;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GUARDARARTSCONTADOSVERIFICADOR;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GUARDARDIFERENCIASVERIFICADO;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACE;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACEVALIDAUSUARIO;

public class CargaCodigosBarraActivity extends AppCompatActivity {

    EditText editTextCodigos = null;

    private String codigoBarras = "";
    private String nombreTienda = "";
    private long articuloIdBusqueda = 0;
    public static HashMap<Long, ArticuloVO> mapaCatalogo = new HashMap<Long, ArticuloVO>();
    private HashMap<String, Integer> mapaCodigosNoRem = new HashMap<String, Integer>();
    private String folio = "";
    private String numeroEmpleado = "";
    private String nombreEmpleado = "";
    private String version = "";
    private String nombreZona = "";
    private int idZona = 0;
    private boolean descargaCatalogoFlag = false;
    private List<String> listaCodigos = null;
    private int ACCION_GUARDA = 0;
    private boolean esGuardadoPorCodigos = false;
    public static int banderaIncidencia;
    public static int tipoPermiso;
    long incidencia = 0;
    int estatusIncidencia = 0;
    int pallet = 0;
    String indicadorProceso = "1";
    String codigoActual = "";
    public static RespuestaIncidenciasVO respuestaIncidencias;
    String metodo = "";
    boolean bloqueo_finalizado = false; //para asegurar que solo se guarde una vez por peticion

    //Variables para Dialogo de confirmación
    public static boolean existeCodigo = false;
    String descripcionCodigoBarras = "";
    int cantidadCapturadaDialogo = 0;
    int cantidadEmbarcadaDialogo = 0;
    String descripcionNormaEmpaqueDialogo = "";
    String descripcionNormaEmpaqueWSDialogo = "";

    TextView textView;
    TextView textView30;
    TextView textView32;
    Button btnSuma;
    Button btnResta;

    String TAG = "CargaCodigosBarraActivity";
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

    StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("entra a oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_codigos_barra);
        textView = (TextView) findViewById(R.id.numeroSerieLabel);
        textView30 = (TextView) findViewById(R.id.textView311);
        textView32 = (TextView) findViewById(R.id.textView32);
        btnSuma = (Button) findViewById(R.id.cargaCajasBoton);
        btnResta = (Button) findViewById(R.id.disminuyeCajasBoton);

        getSupportActionBar().hide();

        folio = new String(this.getIntent().getStringExtra("folio").trim());
        nombreEmpleado = new String(this.getIntent().getStringExtra("nombreEmpleado").trim());
        numeroEmpleado = new String(this.getIntent().getStringExtra("numeroEmpleado").trim());
        nombreTienda = new String(this.getIntent().getStringExtra("nombreTienda").trim());
//        nombreZona = new String(this.getIntent().getStringExtra("nombreZona").trim());
//        idZona = this.getIntent().getIntExtra("idZona", 0);
        descargaCatalogoFlag = this.getIntent().getBooleanExtra("descargaCatalogo", false);

        if (descargaCatalogoFlag) {
            listaCodigos = new ArrayList<String>();
            mapaCatalogo.clear();
            mapaCodigosNoRem.clear();
            descargaCatalogoArticulos();
        }

        editTextCodigos = (EditText) findViewById(R.id.codigoBarraText);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            editTextCodigos.setCursorVisible(false);
        }else{
            editTextCodigos.setInputType(InputType.TYPE_NULL);
        }
        editTextCodigos.requestFocus();
        editTextCodigos.setOnEditorActionListener(codigosListener);

        TextView tiendaTextView = (TextView) findViewById(R.id.tiendaTextView);
        String tiendaText = "";
        if (nombreTienda.length() > 20) {
            tiendaText = nombreTienda.substring(0, 20) + "... ";
        } else {
            tiendaText = nombreTienda;
        }
        tiendaTextView.setText("Tienda: " + tiendaText);

        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ne) {
            Log.e("CARGA_FOLIO_TAG", "Error al obtener la versión: " + ne.getMessage());
        }
    }

    public void descargaCatalogoArticulos() {
        System.out.println("entra a descargaCatalogo");

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Operaciones http
            try {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Descargando catálogo de artículos...");
                mDialog.setCancelable(false);
                mDialog.setInverseBackgroundForced(false);
                mDialog.show();

                //String url = Constantes.URL_STRING + "getCatalogoArticulosVerificadorGeneral";

                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME_GETCATALOGOARTICULOSVERIFICADORGENERAL);

                request.addProperty("folio", folio);
                request.addProperty("numeroSerie", Build.SERIAL);
                request.addProperty("version", version);
                request.addProperty("usuario", numeroEmpleado);

                System.out.println("///////////////////////////REQUEST DescargaCatalogosArticulos"+request);

                ProviderGeneraCatalogo.getInstance(this).getGeneraCatalogo(request, new ProviderGeneraCatalogo.interfaceGeneraCatalogo() {
                    @Override
                    public void resolver(ArticuloVO respuestaGeneraCatalogo) {

                        mDialog.dismiss();
                        System.out.println("*** 1 *** RESPONSE_CargaCodigosBarraActivity-GeneraCatalogo://////////////////////////////////" + respuestaGeneraCatalogo);
                        if (respuestaGeneraCatalogo != null) {
                            cuentaCajasRecibidas();
                            PantallaInicioDialogo pantallaInicioDialogo = new PantallaInicioDialogo(CargaCodigosBarraActivity.this);
                            pantallaInicioDialogo.showDialog(CargaCodigosBarraActivity.this, new String(nombreTienda), new String(folio), totalCajasSurtidas - totalCajasRecibidas);
                        }else {
                            mDialog.dismiss();
                            System.out.println("*** 2 ***");
                            //cuando el tiempo del servicio exedio el timeout
                            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                            alert.showDialog(CargaCodigosBarraActivity.this, "Error al cargar catálogo de artículos", null, TiposAlert.ERROR);
                        }

                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getHeaders();
                    }
                });






                /*StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                mDialog.dismiss();
                                System.out.println("*** 1 *** RESPONSE://////////////////////////////////" + response);
                                generaCatalogoV2(response);
//                                System.out.println("*** TIPO PERMISO: " + tipoPermiso);
//                                System.out.println("*** TOTAL DE ARTÍCULOS: " + mapaCatalogo.size());

                                cuentaCajasRecibidas();
                                PantallaInicioDialogo pantallaInicioDialogo = new PantallaInicioDialogo(CargaCodigosBarraActivity.this);
                                pantallaInicioDialogo.showDialog(CargaCodigosBarraActivity.this, new String(nombreTienda), new String(folio), totalCajasSurtidas - totalCajasRecibidas);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                mDialog.dismiss();
//                                System.out.println("*** 2 ***");
                                //Toast.makeText(getApplicationContext(), "Error en el WS que guarda los códigos: " + error.toString(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CargaCodigosBarraActivity.this, CargaFolioPedidoActivity.class);
                                intent.putExtra("numeroEmpleado", numeroEmpleado);
                                intent.putExtra("nombreEmpleado", nombreEmpleado);
                                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                alert.showDialog(CargaCodigosBarraActivity.this, "Error al cargar catálogo de artículos: " + error.toString(), intent, TiposAlert.ERROR);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();

                        params.put("folio", folio);
                        params.put("numeroSerie", Build.SERIAL);
                        params.put("version", version);
                        params.put("usuario", numeroEmpleado);
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return super.getHeaders();
                    }
                };

                strRequest.setRetryPolicy(new DefaultRetryPolicy(
                        50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                AppController.getInstance().addToRequestQueue(strRequest, "tag");*/


            } catch (Exception me) {
                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                alert.showDialog(CargaCodigosBarraActivity.this, "URL no disponible: " + me.getMessage(), null, TiposAlert.ERROR);
            }
        } else {
            // Mostrar errores
            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
            alert.showDialog(CargaCodigosBarraActivity.this, "No hay conexión HTTP", null, TiposAlert.ERROR);
        }
    }

    public static int totalCajasSurtidas = 0;
    public static int totalCajasRecibidas = 0;

    public void generaCatalogoV2(String response) {
        System.out.println("entra a generaCatalogo");

        totalCajasSurtidas = 0;
        totalCajasRecibidas = 0;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(response));

            int eventType = xpp.getEventType();
            ArticuloVO articuloVO = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("tipoPermiso")) {
                        eventType = xpp.next(); // advance to inner text
                        tipoPermiso = (Integer.parseInt(xpp.getText()));
                        if (tipoPermiso == 1) {
                            banderaIncidencia = 1;
                        }
                    } else if (xpp.getName().equals("articulos")) {
                        articuloVO = new ArticuloVO();
                    } else if (xpp.getName().equals("articuloId")) {
                        eventType = xpp.next(); // advance to inner text
                        articuloVO.setArticuloId(Long.parseLong(xpp.getText()));
//                        System.out.println(xpp.getText() + "nombre de art");
                    } else if (xpp.getName().equals("codigosBarraArr")) {
                        eventType = xpp.next(); // advance to inner text
                        articuloVO.getCodigos().add(xpp.getText());
                        System.out.println(xpp.getText() + " barras de art");
                    } else if (xpp.getName().equals("nombre")) {
                        eventType = xpp.next(); // advance to inner text
                        articuloVO.setNombreArticulo(xpp.getText());
                        System.out.println(articuloVO.getNombreArticulo());
                    } else if (xpp.getName().equals("normaEmpaque")) {
                        eventType = xpp.next(); // advance to inner text
                        articuloVO.setNormaEmpaque(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("unidadMedida")) {
                        eventType = xpp.next(); // advance to inner text
                        articuloVO.setUnidadMedida(xpp.getText());
                    } else if (xpp.getName().equals("unidadMedidaId")) {
                        eventType = xpp.next(); // advance to inner text
                        articuloVO.setUnidadMedidaId(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("cantidadVerificada")) {
                        eventType = xpp.next(); // advance to inner text
                        articuloVO.setTotalCajasVerificadas(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("cantidadAsignada")) {
                        eventType = xpp.next(); // advance to inner text
                        articuloVO.setTotalCajasAsignadas(Integer.parseInt(xpp.getText()));
                        totalCajasSurtidas += articuloVO.getTotalCajasAsignadas();
                    } else if (xpp.getName().equals("normaPallet")) {
                        eventType = xpp.next(); // advance to inner text
                        articuloVO.setNormaPallet(Integer.parseInt(xpp.getText()));
                    }

                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equals("articulos")) {
                        mapaCatalogo.put(articuloVO.getArticuloId(), articuloVO);
                    }
                }
                eventType = xpp.next();
            }
            existeCodigo = false;
            long articuloID = 0;

        } catch (Exception e) {
            //Toast.makeText(this, "Error al formar las diferencias del xml: " + e.getMessage(), Toast.LENGTH_LONG).show();
            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
            alert.showDialog(CargaCodigosBarraActivity.this, "Error al formar el catálogo en xml: " + e.getMessage(), null, TiposAlert.ERROR);
        }
    }


    TextView.OnEditorActionListener codigosListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
            System.out.println("entra a escaneo");

            try {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    codigoBarras = editTextCodigos.getText().toString().trim();
                    editTextCodigos.setText("");
                    editTextCodigos.getText().clear();

                    if(!codigoBarras.equals("")) {
                        if (codigoBarras.contains("\n")) {
                            String[] codigoBarras1 = codigoBarras.split("\n");
                            Log.d(TAG, "onEditorAction: " + codigoBarras1.length);
                            for (String a : codigoBarras1) {
                                Log.d(TAG, "onEditorAction: " + a);
                            }
                            codigoBarras = codigoBarras1[(codigoBarras1.length - 1)];
                            Log.d(TAG, "onEditorAction: " + codigoBarras);
                        }


                        long nuevoArticulo = existencia(codigoBarras);

                        if (codigoActual == "") {
                            System.out.println("***** PRIMER CODIGO");
                            if (tipoPermiso == 0) {
                                codigoActual = codigoBarras;
                                actualizaValores(true);

                            } else {
                                indicadorProceso = "1";
                                consultaIncidencias(false, 0, 1); //opcion1
                            }

                        } else {
                            System.out.println("***** SEGUNDO CODIGO: bandera Incidencia : " + banderaIncidencia + "nuevoArticulo:" + nuevoArticulo + " articuloIdBusqueda: " + articuloIdBusqueda);
                            if ((nuevoArticulo == articuloIdBusqueda) && (banderaIncidencia == 0)) { // SI ES EL MISMO CÓDIGO
                                actualizaValores(false);
                            } else { //SI ES UN CODIGO DIFERENTE
                                if (existencia(codigoBarras) != 0) { //SI EL NUEVO CODIGO ESCANEADO EXISTE
                                    final long articuloId = articuloIdBusqueda; // articuloId = ultimo código antes del escaneado (se iguala al nvo cod)
                                    if (cantidadCapturadaDialogo < cantidadEmbarcadaDialogo) {
                                        if (tipoPermiso == 0) {// SI BANDERA BLOQUEO ESTA DESACTIVADA
                                            final ViewDialogoGenerico dialogo = new ViewDialogoGenerico(CargaCodigosBarraActivity.this);
                                            dialogo.showDialog(CargaCodigosBarraActivity.this, "Faltan cajas por verificar del artículo " + descripcionCodigoBarras + ". ¿Desea generar una incidencia de faltante?", "Aceptar", "Regresar", "", true);
                                            dialogo.setViewDialogoGenericoListener(new ViewDialogoGenerico.ViewDialogoGenericoListener() {
                                                @Override
                                                public void onVerde() {
                                                    indicadorProceso = "2";
                                                    if (obtieneArticulosIncidencia(false, articuloId) != "") {
                                                        consultaIncidencias(false, articuloId, 2); //opcion2
                                                    }
                                                }

                                                @Override
                                                public void onRojo() {
                                                    codigoBarras = codigoActual;
                                                }

                                                @Override
                                                public void onExtra() {

                                                }
                                            });
                                        } else { // SI BANDERA BLOQUEO ESTA ACTIVADA
                                            indicadorProceso = "1";
                                            consultaIncidencias(false, articuloId, 3); //opcion3
                                        }
                                    } else {
                                        codigoActual = codigoBarras;
                                        actualizaValores(true);
                                    }
                                } else {// codigo inexistente
//                                System.out.println("***NO EXISTE***");
                                    ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                    alert.showDialog(CargaCodigosBarraActivity.this, "Artículo no encontrado - " + codigoBarras, null, TiposAlert.ERROR);
                                    editTextCodigos.setText("");
                                }
                            }

                        }
                    }
                }
            } catch (Exception e) {
                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                alert.showDialog(CargaCodigosBarraActivity.this, "Error al leer el código de barras: " + e.getMessage(), null, TiposAlert.ERROR);
                //EditText editText = (EditText) findViewById(R.id.codigoBarraText);
                editTextCodigos.setText("");
            } finally {
                System.out.println("entro finally, limpia lector");
                editTextCodigos.getText().clear();

            }
            editTextCodigos.setText("");
            return true;
        }
    };

    public long existencia(String codigo) {
        System.out.println("entra a existencia");

        existeCodigo = false;
        long articuloID = 0;

        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            if (entry.getValue().getCodigos().contains(codigo)) {
                existeCodigo = true;
                articuloID = entry.getKey();
            }
        }
        if (existeCodigo == true) {
            editTextCodigos.setText("");
            return articuloID;
        } else {
            editTextCodigos.setText("");
            return 0;
        }
    }

    public void actualizaValores(boolean guardar) {
        System.out.println("entra a actualizaValores");

        existeCodigo = false;
        descripcionCodigoBarras = codigoBarras;
//        System.out.println("** VALOR codigoBarras: *" + codigoBarras + "*");

        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            if (entry.getValue().getCodigos().contains(codigoBarras)) {
                articuloIdBusqueda = entry.getKey();
                descripcionCodigoBarras = entry.getValue().getNombreArticulo();
                entry.getValue().setTotalCajasVerificadas(entry.getValue().getTotalCajasVerificadas() + 1);
                cantidadCapturadaDialogo = entry.getValue().getTotalCajasVerificadas();
                cantidadEmbarcadaDialogo = entry.getValue().getTotalCajasAsignadas();
                descripcionNormaEmpaqueWSDialogo = " con " + entry.getValue().getNormaEmpaque() + " piezas";
                existeCodigo = true;
                pallet = entry.getValue().getNormaPallet();
                entry.getValue().setEsCapturado(true);

//                System.out.println("CODIGO VALIDO");
            }
        }
        if (!existeCodigo) {
//            System.out.println("***NO EXISTE***");
            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
            alert.showDialog(CargaCodigosBarraActivity.this, "Artículo no encontrado - " + codigoBarras, null, TiposAlert.ERROR);
            editTextCodigos.setText("");
        } else if (cantidadCapturadaDialogo > cantidadEmbarcadaDialogo) {
//            System.out.println("***SI EXISTE***");
            editTextCodigos.setText("");
            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
            alert.showDialog(CargaCodigosBarraActivity.this, "No puedes contar más cajas de las asignadas", null, TiposAlert.ERROR);

            for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                if (entry.getValue().getCodigos().contains(codigoBarras)) {
                    entry.getValue().setTotalCajasVerificadas(entry.getValue().getTotalCajasAsignadas());
                    //entry.getValue().setEsCapturado(false);
                    metodo = "CargaCodigosBarraActivity - ejecutaWSHilo";
                    estableceNombreArticulo();
                    ejecutaWSHilo();
                }
            }
        } else {
            ingresaCodigoBarras(guardar);
        }
    }

    public void ingresaCodigoBarras(boolean guardar) {
        System.out.println("entra a ingresacodigoBarras");

//        System.out.println("***INGRESA CODIGO BARRAS***");
        ACCION_GUARDA = 0;
//        ejecutaWSHilo();
        if (!existeCodigo) {
//            System.out.println("***NO VALIDO DE NUEVO***");
            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
            alert.showDialog(CargaCodigosBarraActivity.this, "Artículo no encontrado - " + codigoBarras, null, TiposAlert.ERROR);

            //EditText editText = (EditText) findViewById(R.id.codigoBarraText);
            editTextCodigos.setText("");

        } else {
            listaCodigos.add(codigoBarras);
            estableceNombreArticulo();

            if ((listaCodigos.size() % Constantes.CONTADOR_GUARDA_AVANCE == (Constantes.CONTADOR_GUARDA_AVANCE - 1))) {
//                guardaAvancePorCodigos();
                ejecutaWSHilo();
            } else if (guardar) {
                ejecutaWSHilo();
            }
        }
    }

    public void estableceNombreArticulo() {
        System.out.println("actualiza pantalla (estableceNombreArticulo");

        //EditText editText = (EditText) findViewById(R.id.codigoBarraText);
        if (cantidadCapturadaDialogo == 1) {
            textView30.setText("1");
            textView32.setText(String.valueOf(mapaCatalogo.get(articuloIdBusqueda).getTotalCajasAsignadas()));
        } else if (cantidadCapturadaDialogo == 0) {
            descripcionNormaEmpaqueDialogo = "por primera vez";
        } else {
            textView30.setText(String.valueOf(mapaCatalogo.get(articuloIdBusqueda).getTotalCajasVerificadas()));
            textView32.setText(String.valueOf(mapaCatalogo.get(articuloIdBusqueda).getTotalCajasAsignadas()));
        }
        textView.setText(descripcionCodigoBarras);
        btnSuma.setText(String.valueOf(pallet));
        btnResta.setText(String.valueOf("-" + pallet));
        editTextCodigos.setText("");
    }

    public void sumaCajas(View view) {
        Log.d(TAG, "sumaCajas: SUMO UNA CAJA");
        view.startAnimation(buttonClick);
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        if (articuloIdBusqueda != 0) {
//            final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.mensaje_ok);
//            mp.start();

//            CountDownTimer timer = new CountDownTimer(1800, 1800) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    // Nothing to do
//                }
//
//                @Override
//                public void onFinish() {
//                    if (mp.isPlaying()) {
//                        mp.stop();
//                        mp.release();
//                    }
//                }
//            };
//            timer.start();

            cantidadCapturadaDialogo = mapaCatalogo.get(articuloIdBusqueda).getTotalCajasVerificadas();
            cantidadEmbarcadaDialogo = mapaCatalogo.get(articuloIdBusqueda).getTotalCajasAsignadas();

            int num = cantidadCapturadaDialogo;
            int nearest = pallet;

            if (cantidadCapturadaDialogo % pallet == 0) { // SI MULTIPLO DE PALLET
                System.out.println("cantidadCapturadaDialogo1/////////////////////////////////////////////////////" + cantidadCapturadaDialogo);
                System.out.println("pallet1/////////////////////////////////////////////////////" + pallet);
                System.out.println("canidadEmbarcadaDialogo1/////////////////////////////////////////////////////" + cantidadEmbarcadaDialogo);
                if ((cantidadCapturadaDialogo + pallet) > cantidadEmbarcadaDialogo) {
                    // NO HACER NADAA
                    editTextCodigos.getText().clear();
                    ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                    alert.showDialog(CargaCodigosBarraActivity.this, "No puedes contar más cajas de las asignadas", null, TiposAlert.ERROR);
                    /*System.out.println("cantidadCapturadaDialogo2/////////////////////////////////////////////////////" + cantidadCapturadaDialogo);
                    System.out.println("pallet2/////////////////////////////////////////////////////" + pallet);
                    System.out.println("canidadEmbarcadaDialogo2/////////////////////////////////////////////////////" + cantidadEmbarcadaDialogo);*/
                } else {
                    cantidadCapturadaDialogo = cantidadCapturadaDialogo + pallet;
                    /*System.out.println("cantidadCapturadaDialogo3/////////////////////////////////////////////////////" + cantidadCapturadaDialogo);
                    System.out.println("pallet3/////////////////////////////////////////////////////" + pallet);
                    System.out.println("canidadEmbarcadaDialogo3/////////////////////////////////////////////////////" + cantidadEmbarcadaDialogo);*/
                }
            } else if (cantidadCapturadaDialogo < pallet){
                num = num + ((num % nearest - nearest) * -1);
                cantidadCapturadaDialogo = num;
                /*System.out.println("cantidadCapturadaDialogo4/////////////////////////////////////////////////////" + cantidadCapturadaDialogo);
                System.out.println("pallet4/////////////////////////////////////////////////////" + pallet);
                System.out.println("canidadEmbarcadaDialogo4/////////////////////////////////////////////////////" + cantidadEmbarcadaDialogo);*/
            }else {
                // NO HACER NADAA
                System.out.println("Dentro de error por mayor a pallet");
                editTextCodigos.getText().clear();
                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                alert.showDialog(CargaCodigosBarraActivity.this, "No puedes contar más cajas de las asignadas", null, TiposAlert.ERROR);
                /*System.out.println("cantidadCapturadaDialogo2/////////////////////////////////////////////////////" + cantidadCapturadaDialogo);
                System.out.println("pallet2/////////////////////////////////////////////////////" + pallet);
                System.out.println("canidadEmbarcadaDialogo2/////////////////////////////////////////////////////" + cantidadEmbarcadaDialogo);*/
            }
            mapaCatalogo.get(articuloIdBusqueda).setTotalCajasVerificadas(cantidadCapturadaDialogo);
            textView30.setText(String.valueOf(cantidadCapturadaDialogo));
        }
    }

    public void restaCaja(View view) {
        Log.d(TAG, "restaCaja: se restó una caja por click");
        view.startAnimation(buttonClick);
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        if (articuloIdBusqueda != 0) {
            cantidadCapturadaDialogo = mapaCatalogo.get(articuloIdBusqueda).getTotalCajasVerificadas();
            cantidadEmbarcadaDialogo = mapaCatalogo.get(articuloIdBusqueda).getTotalCajasAsignadas();

            int num = cantidadCapturadaDialogo;
            int nearest = pallet;

            if (cantidadCapturadaDialogo % pallet == 0) { // SI MULTIPLO DE PALLET
                if ((cantidadCapturadaDialogo - pallet) <= 0) {
                    cantidadCapturadaDialogo = 0;
                } else {
                    cantidadCapturadaDialogo = cantidadCapturadaDialogo - pallet;
//                    System.out.println(cantidadCapturadaDialogo +  " RESULTADOOO ");
                }
            } else {
                num = num - (num % nearest);
                cantidadCapturadaDialogo = num;
            }
            mapaCatalogo.get(articuloIdBusqueda).setTotalCajasVerificadas(cantidadCapturadaDialogo);
            textView30.setText(String.valueOf(cantidadCapturadaDialogo));
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void cuentaCajasRecibidas() {
        totalCajasRecibidas = 0;
        for (ArticuloVO entry : mapaCatalogo.values()) {
            totalCajasRecibidas += entry.getTotalCajasVerificadas();
        }
    }

    public void conteoDiferencias() {
        Intent intent = new Intent(getApplicationContext(), ConteoDiferenciasActivity.class);
        intent.putExtra("mapaCat", mapaCatalogo);
        intent.putExtra("folio", folio);
        intent.putExtra("nombreEmpleado", nombreEmpleado);
        intent.putExtra("numeroEmpleado", numeroEmpleado);
        intent.putExtra("nombreTienda", nombreTienda);
        intent.putExtra("nombreZona", nombreZona);
        intent.putExtra("idZona", idZona);
        intent.putExtra("tipoPermiso", tipoPermiso);
        intent.putExtra("totalCajasSurtidas", totalCajasSurtidas);

        startActivity(intent);
    }

    public void guardarMenuFront(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        if (banderaIncidencia == 1) {
            consultaIncidencias(false, 0, 1); //opcion1
        } else {
            guardaAvance();
        }


    }

    public void finalizarMenuFront(View view) {
        cuentaCajasRecibidas();
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        if (totalCajasRecibidas < totalCajasSurtidas && obtieneArticulosIncidencia(true, 0) != "") {
            final ViewDialogoGenerico dialogoFin = new ViewDialogoGenerico(CargaCodigosBarraActivity.this);
            dialogoFin.showDialog(CargaCodigosBarraActivity.this,
                    "Faltan artículos por escanear. ¿Desea finalizar el pedido y generar una incidencia de faltante?",
                    "Finalizar",
                    "Ver detalle",
                    "",
                    true);
            dialogoFin.setViewDialogoGenericoListener(new ViewDialogoGenerico.ViewDialogoGenericoListener() {
                @Override
                public void onVerde() {
                    Log.d(TAG, "onVerde: " + " entro al click boton verde");
                    if (totalCajasRecibidas < totalCajasSurtidas) {
                        if (tipoPermiso == 0) {
                            indicadorProceso = "2";
                            if (obtieneArticulosIncidencia(true, 0) != "") {
                                consultaIncidencias(true, 0, 4); //finalizarMenu opcion 4
                            } else {
                                finalizaConteo();
                            }
                        } else {
                            indicadorProceso = "1";
                            consultaIncidencias(false, 0, 5); //finalizarMenu opcion 5
                        }

                    } else {
                        finalizaConteo();
                    }
                }

                @Override
                public void onRojo() {
                    conteoDiferencias();
                }

                @Override
                public void onExtra() {
                }
            });
        } else {
            finalizaConteo();
        }
    }

    public void consultarAvanceMenuFront(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        consultaAvance();
    }

    public void salirMenuFront(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        metodo = "CargaCodigosBarraActivity-salirMenuFront";
        ACCION_GUARDA = 0;
        ejecutaWSHilo();
        salirMenu();
    }

    public void finalizaConteo() {
        ACCION_GUARDA = 1;
        esGuardadoPorCodigos = false;
        metodo = "CargaCodigosBarraActivity-finalizaConteo";
        ejecutaWS();
    }

    public void guardaAvance() {
        ACCION_GUARDA = 0;
        esGuardadoPorCodigos = false;
        metodo = "CargaCodigosBarraActivity-guardaAvance";
        ejecutaWS();
    }

    public void guardaAvancePorCodigos() {
        ACCION_GUARDA = 0;
        esGuardadoPorCodigos = true;
        metodo = "CargaCodigosBarraActivity-guardaAvancePorCodigos codigo:" + articuloIdBusqueda;
        ejecutaWS();
    }

    public void consultaAvance() {
        Intent intent = new Intent(getApplicationContext(), ConsultaAvanceActivity.class);
        intent.putExtra("mapaCat", mapaCatalogo);
        intent.putExtra("mapaCodNoRem", mapaCodigosNoRem);
        intent.putExtra("folio", folio);
        intent.putExtra("nombreEmpleado", nombreEmpleado);
        intent.putExtra("numeroEmpleado", numeroEmpleado);
        intent.putExtra("nombreZona", nombreZona);
        intent.putExtra("idZona", idZona);

        startActivity(intent);
    }

    public void regresaMenu() {
        Intent intent = new Intent(getApplicationContext(), CargaFolioPedidoActivity.class);
        intent.putExtra("numeroEmpleado", numeroEmpleado);
        intent.putExtra("nombreEmpleado", nombreEmpleado);
        startActivity(intent);
    }

    public void salirMenu() {
        Intent intent = new Intent(getApplicationContext(), CargaFolioPedidoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("LOGOUT", true);
        intent.putExtra("nombreEmpleado", nombreEmpleado);
        intent.putExtra("numeroEmpleado", numeroEmpleado);
        startActivity(intent);
    }

    public void ejecutaWS() {

        if (!bloqueo_finalizado) {
            if (ACCION_GUARDA == 1) {//si finaliza
                bloqueo_finalizado = true; //bloquea el metodo de guardado para que solo se ejecute una vez
                System.out.println("SE BLOQUEA FINALIZADO");
            }
            // Do something in response to button
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Operaciones http
                System.out.println("tipo Guardado: " + String.valueOf(ACCION_GUARDA));
                System.out.println("ENTRA A METODO DE GUARDADO 1" + metodo);
                try {
                    String url = Constantes.URL_STRING + "guardarArtsContadosVerificador";
                    int contadorOcurrencias = 0;

                    if (ACCION_GUARDA == 0) {
                        contadorOcurrencias = cuentaOcurrencias();
                    }

                    if (ACCION_GUARDA == 1 || (ACCION_GUARDA == 0 && contadorOcurrencias > 0)) {
                        final ProgressDialog mDialog = new ProgressDialog(this);
                        mDialog.setMessage("Guardando códigos de barra...");
                        mDialog.setCancelable(false);
                        mDialog.setInverseBackgroundForced(false);
                        if (!esGuardadoPorCodigos) {
                            mDialog.show();
                        }

                        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GUARDARARTSCONTADOSVERIFICADOR);
                        request.addProperty("folio", folio);
                        request.addProperty("idZona", String.valueOf(idZona));
                        request.addProperty("articulosArray", obtieneCadenaArticulos());
                        request.addProperty("cantidadesArray", obtieneCadenaCajas());
                        request.addProperty("tipoGuardado", String.valueOf(ACCION_GUARDA));
                        request.addProperty("numeroSerie", Build.SERIAL);
                        request.addProperty("version", version);
                        request.addProperty("usuario", numeroEmpleado);
                        request.addProperty("metodo", metodo);

                        ProviderGuardarArticulos.getInstance(this).getGuardarArticulos(request, new ProviderGuardarArticulos.interfaceGuardarArticulos() {
                            @Override
                            public void resolver(CodigosGuardadosVO respuestaGuardaArticulos) {

                                System.out.println("GUARDADO 1 REPONSE " + respuestaGuardaArticulos);

                                mDialog.dismiss();
                                if (respuestaGuardaArticulos != null) {
                                    if (respuestaGuardaArticulos.getCodigo() == 0) {
                                        if (ACCION_GUARDA == 1) {
                                            Intent intent = new Intent(CargaCodigosBarraActivity.this, DiferenciasRecibidasActivity.class);
                                            intent.putExtra("folio", folio);
                                            intent.putExtra("CodigosGuardados", respuestaGuardaArticulos);
                                            intent.putExtra("nombreEmpleado", nombreEmpleado);
                                            intent.putExtra("numeroEmpleado", numeroEmpleado);
                                            intent.putExtra("estatusPedido", respuestaGuardaArticulos.getEstatusPedido());
                                            intent.putExtra("nombreTienda", nombreTienda);
                                            intent.putExtra("nombreZona", nombreZona);
                                            intent.putExtra("idZona", idZona);
                                            startActivity(intent);
                                        } else if (ACCION_GUARDA == 0) {
                                            if (!esGuardadoPorCodigos) {
                                                Intent intent = new Intent(CargaCodigosBarraActivity.this, CargaFolioPedidoActivity.class);
                                                intent.putExtra("numeroEmpleado", numeroEmpleado);
                                                intent.putExtra("nombreEmpleado", nombreEmpleado);
                                                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                                alert.showDialog(CargaCodigosBarraActivity.this, "Se han guardado correctamente los cambios. Recuerda que debes completar tu pedido", null, TiposAlert.CORRECTO);
                                            }
                                        }
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), CargaFolioPedidoActivity.class);
                                        intent.putExtra("numeroEmpleado", numeroEmpleado);
                                        intent.putExtra("nombreEmpleado", nombreEmpleado);
                                        ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                        alert.showDialog(CargaCodigosBarraActivity.this, "Error: " + respuestaGuardaArticulos.getMensaje(), intent, TiposAlert.ERROR);


                                    }
                                } else {
                                    //cuando el tiempo del servicio exedio el timeout
                                    bloqueo_finalizado = false;
                                    ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                    alert.showDialog(CargaCodigosBarraActivity.this, "Ocurrió un problema al guardar, valida el avance", null, TiposAlert.ERROR);
                                }

                                ACCION_GUARDA = 0;
                                bloqueo_finalizado = false;
                            }
                        });

                    } else {
                        bloqueo_finalizado = false;
                        ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                        alert.showDialog(CargaCodigosBarraActivity.this, "Debes capturar por lo menos un código de barras", null, TiposAlert.ALERT);
                    }
                } catch (Exception me) {
                    bloqueo_finalizado = false;
                    if (!esGuardadoPorCodigos) {
                        ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                        alert.showDialog(CargaCodigosBarraActivity.this, "URL no disponible: favor de validar la conexión a Internet" + me.getMessage(), null, TiposAlert.ERROR);
                    }
                }
            } else {
                // Mostrar errores
                bloqueo_finalizado = false;
                if (!esGuardadoPorCodigos) {
                    ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                    alert.showDialog(CargaCodigosBarraActivity.this, "No hay conexión HTTP", null, TiposAlert.ERROR);
                }
            }
        } else {
            System.out.println("SE OMITE PETICION MIENTRAS TRABAJA FINALIZADO");
        }
    }

    public void ejecutaWSHilo() {
        if (!bloqueo_finalizado) {
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
                try {
                    System.out.println("ENTRA A METODO DE GUARDADO 2 " + metodo);
                    //String url = Constantes.URL_STRING + "guardarArtsContadosVerificador";
                    int contadorOcurrencias = 0;

                    SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME_GUARDARARTSCONTADOSVERIFICADOR);

                    request.addProperty("folio", folio);
                    request.addProperty("idZona", String.valueOf(idZona));
                    request.addProperty("articulosArray", obtieneCadenaArticulos());
                    request.addProperty("cantidadesArray", obtieneCadenaCajas());
                    request.addProperty("tipoGuardado", String.valueOf(ACCION_GUARDA));
                    request.addProperty("numeroSerie", Build.SERIAL);
                    request.addProperty("version", version);
                    request.addProperty("usuario", numeroEmpleado);
                    request.addProperty("metodo", metodo);
                    System.out.println("///////////////////////////REQUEST_EJECUTAWSHILO_CargaCodigosBarraActivity"+request);

                    ProviderGuardarArticulos.getInstance(this).getGuardarArticulos(request, new ProviderGuardarArticulos.interfaceGuardarArticulos() {
                        @Override
                        public void resolver(CodigosGuardadosVO respuestaGuardaArticulos) {
                            if (respuestaGuardaArticulos != null) {

                                System.out.println("*** GUARDADO 2 - hilo *** response: " + respuestaGuardaArticulos);
//                                System.out.println("*** Pedido = " + folio);
                                bloqueo_finalizado = false;
                            }else {
                                System.out.println("*** 2 ***");
                                //cuando el tiempo del servicio exedio el timeout
                                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                alert.showDialog(CargaCodigosBarraActivity.this, "Error al consumir el servicio que guarda los códigos", null, TiposAlert.ERROR);
                            }

                        }
                    });






                    /*StringRequest strRequest = null;


                    strRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    System.out.println("*** GUARDADO 2 - hilo *** response: " + response);
//                                System.out.println("*** Pedido = " + folio);
                                    bloqueo_finalizado = false;
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    bloqueo_finalizado = false;
//                                System.out.println("*** Error al guardar los códigos *** response: " + error.getMessage());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("folio", folio);
                            params.put("idZona", String.valueOf(idZona));
                            params.put("articulosArray", obtieneCadenaArticulos());
                            params.put("cantidadesArray", obtieneCadenaCajas());
                            params.put("tipoGuardado", String.valueOf(ACCION_GUARDA));
                            params.put("numeroSerie", Build.SERIAL);
                            params.put("version", version);
                            params.put("usuario", numeroEmpleado);
                            params.put("metodo", metodo);
                            return params;
                        }
                    };

                    strRequest.setRetryPolicy(new DefaultRetryPolicy(
                            50000,
                            0,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    AppController.getInstance().addToRequestQueue(strRequest, "tag");*/


                } catch (Exception me) {
                    bloqueo_finalizado = false;
//                System.out.println("*** No existe comunicación ***");
                }
            } else {
                bloqueo_finalizado = false;
//            System.out.println("*** No existe comunicación ***");
            }
        } else {
            System.out.println("SE OMITE PETICION MIENTRAS TRABAJA FINALIZADO");
        }
    }

    public String obtieneCadenaArticulos() {
        StringBuffer cadena = new StringBuffer();
        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            if (ACCION_GUARDA == 0 && esGuardadoPorCodigos) {
                cadena.append(entry.getKey());
                cadena.append("|");
            } else if (ACCION_GUARDA == 0 && entry.getValue().isEsCapturado()) {
                cadena.append(entry.getKey());
                cadena.append("|");
            } else if (ACCION_GUARDA == 1) {
                cadena.append(entry.getKey());
                cadena.append("|");
            }
        }
        cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "");

//        System.out.println(cadena.toString());
        return cadena.toString();
    }

    public String obtieneCadenaCajas() {
        StringBuffer cadena = new StringBuffer();
        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            if (ACCION_GUARDA == 0 && esGuardadoPorCodigos) {
                cadena.append(entry.getValue().getTotalCajasVerificadas());
                cadena.append("|");
            } else if (ACCION_GUARDA == 0 && entry.getValue().isEsCapturado()) {
                cadena.append(entry.getValue().getTotalCajasVerificadas());
                cadena.append("|");
            } else if (ACCION_GUARDA == 1) {
                cadena.append(entry.getValue().getTotalCajasVerificadas());
                cadena.append("|");
            }
        }
        cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "");
        return cadena.toString();
    }

    public int cuentaOcurrencias() {
        int cuenta = 0;
        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            if (entry.getValue().isEsCapturado()) {
                cuenta++;
            }
        }
        return cuenta;
    }

    public void generaFaltantes(String response, CodigosGuardadosVO codigos) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(response));

            int eventType = xpp.getEventType();
            List<CodigoBarraVO> listaCodigosFaltantes = new ArrayList<CodigoBarraVO>();

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
                    } else if (xpp.getName().equals("estatusPedido")) {
                        eventType = xpp.next(); // advance to inner text
                        codigos.setEstatusPedido(xpp.getText());
                    } else if (xpp.getName().equals("articulosConDiferencia")) {
                        codigoBarraVO = new CodigoBarraVO();
                    } else if (xpp.getName().equals("articuloId")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setArticuloId(Long.parseLong(xpp.getText()));
                    } else if (xpp.getName().equals("cantidadVerificada")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setCajasVerificadas(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("cantidadAsignada")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setCajasAsignadas(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("codigobarras")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setCodigoBarras(xpp.getText());
                    } else if (xpp.getName().equals("nombre")) {
                        eventType = xpp.next(); // advance to inner text
                        codigoBarraVO.setNombreArticulo(xpp.getText());
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equals("articulosConDiferencia")) {
                        listaCodigosFaltantes.add(codigoBarraVO);
                    }
                }
                eventType = xpp.next();
            }
            codigos.setArticulosDiferencias(listaCodigosFaltantes.toArray(new CodigoBarraVO[listaCodigosFaltantes.size()]));

            int contador = 0;
            for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                if (entry.getValue().isEsCapturado()) {
                    contador++;
                }
            }
        } catch (Exception e) {
            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
            alert.showDialog(CargaCodigosBarraActivity.this, "Error al formar las diferencias en xml: " + e.getMessage(), null, TiposAlert.ERROR);
            Log.d(TAG, "generaFaltantes: " + e.getMessage());
        }
    }

    public String obtieneArticulosIncidencia(boolean xTodos, long articuloId) {
        StringBuffer cadena = new StringBuffer();
        System.out.println("obtieneArticulosIncidencia**");

        if (!xTodos && articuloId == 0) {
            return "";
        } else {
            for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                int cantidadCapturadaDialogo = entry.getValue().getTotalCajasVerificadas();
                int cantidadEmbarcadaDialogo = entry.getValue().getTotalCajasAsignadas();
                if (cantidadCapturadaDialogo < cantidadEmbarcadaDialogo) {
                    if (xTodos) {
                        boolean encontrado = false;

                        if (respuestaIncidencias != null) {
                            for (RespuestaIncidenciasVO.IncidenciaVO lista : respuestaIncidencias.getListaIncidencias()) {
                                if (lista.getArticuloId() == entry.getKey()) {
                                    if (lista.getEstatusDiferencia() == 1) {
                                        System.out.println("impresion omite " + entry.getKey());
                                        encontrado = true;
                                    }
                                }
                            }
                        }
                        if (!encontrado) {
                            cadena.append(entry.getKey());
                            cadena.append("|");
                        }
                    } else if (!xTodos && articuloId != 0) {
                        if (entry.getKey() == articuloId) {
                            cadena.append(entry.getKey());
                            cadena.append("|");
                        }
                    }
                }
            }
            if (cadena.length() == 0) {
                return "";
            } else {
                cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "");
                System.out.println("***Articulos con incidencia: " + cadena);
                return cadena.toString();
            }
        }
    }

    public String obtieneCajasIncidencia(boolean xTodos, long articuloId) {
        StringBuffer cadena = new StringBuffer();
        System.out.println("obtieneCajasIncidencia**");

        if (!xTodos && articuloId == 0) {
            return "";
        } else {
            for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                int cantidadCapturadaDialogo = entry.getValue().getTotalCajasVerificadas();
                int cantidadEmbarcadaDialogo = entry.getValue().getTotalCajasAsignadas();
                if (cantidadCapturadaDialogo < cantidadEmbarcadaDialogo) {
                    if (xTodos) {
                        boolean encontrado = false;
                        if (respuestaIncidencias != null) {
                            for (RespuestaIncidenciasVO.IncidenciaVO lista : respuestaIncidencias.getListaIncidencias()) {
                                if (lista.getArticuloId() == entry.getKey()) {
                                    if (lista.getEstatusDiferencia() == 1) {
                                        encontrado = true;
                                    }
                                }
                            }
                        }
                        if (!encontrado) {
                            cadena.append(cantidadEmbarcadaDialogo - cantidadCapturadaDialogo);
                            cadena.append("|");
                        }
                    }
                    if (!xTodos && articuloId != 0) {
                        if (entry.getKey() == articuloId) {
                            cadena.append(cantidadEmbarcadaDialogo - cantidadCapturadaDialogo);
                            cadena.append("|");
                        }
                    }
                    if (!xTodos && articuloId == 0) {
                        cadena.append("");
                    }
                }
            }
            cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "");
            System.out.println("***Cajas con incidencia: " + cadena);
            return cadena.toString();
        }
    }

    public void consultaIncidencias(final boolean xTodos, final long articuloId, final int opcion) {// true -> todos los articulos  // false -> por artículo
        final ProgressDialog mDialog = new ProgressDialog(this);
        if (indicadorProceso != "1") {
            mDialog.setMessage("Generando incidencias...");
            mDialog.setCancelable(false);
            mDialog.setInverseBackgroundForced(false);
            mDialog.show();
        }

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                //String url = Constantes.URL_STRING + "guardarDiferenciasVerificado";
                SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME_GUARDARDIFERENCIASVERIFICADO);


                request.addProperty("folio", folio);
                request.addProperty("numeroSerie", Build.SERIAL);
                request.addProperty("version", version);
                request.addProperty("usuarioVerificaId", numeroEmpleado);
                request.addProperty("usuarioAutorizaId", "0");
                request.addProperty("tipoAutorizacion", String.valueOf(tipoPermiso));
                request.addProperty("indicadorProceso", indicadorProceso);
                request.addProperty("articulosArray", obtieneArticulosIncidencia(xTodos, articuloId));
                request.addProperty("cantidadesArray", obtieneCajasIncidencia(xTodos, articuloId));


                System.out.println("/////////////////////////////REQUEST_consultaIncidencias_CargaCodigosBarraActivity:"+request);

                ProviderGuardarDiferencias_CargaCodigosBarra.getInstance(this).getGuardarDiferencias_CargaCodigosBarra(request, new ProviderGuardarDiferencias_CargaCodigosBarra.interfaceGuardarDiferencias_CargaCodigosBarra() {
                    @Override
                    public void resolver(RespuestaIncidenciasVO respuestaGuardaDiferencias_CargaCodigosBarra) {
                        System.out.println("****** INCIDENCIAS RESPONSE" + respuestaGuardaDiferencias_CargaCodigosBarra);

                        mDialog.dismiss();

                        if (respuestaGuardaDiferencias_CargaCodigosBarra != null) {
                            // ----------------------------------
                            if (opcion == 1) {
                                Log.d(TAG, "onResponse: " + "respuesta opcion1");

                                if (respuestaIncidencias.getCodigo() == 0) {
                                    incidencia = 0;
                                    estatusIncidencia = 1;
                                    banderaIncidencia = 0;
                                    for (RespuestaIncidenciasVO.IncidenciaVO inc : respuestaIncidencias.getListaIncidencias()) {
                                        incidencia = 1;
                                        if (inc.getEstatusDiferencia() == 0) {
                                            estatusIncidencia = 0;
                                            banderaIncidencia = 1;
                                        }
                                    }
                                    if (estatusIncidencia == 0) {
                                        ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                        alert.showDialog(CargaCodigosBarraActivity.this, "Existen incidencias sin autorizar, comunícate con el encargado de almacén para continuar", null, TiposAlert.ERROR);
                                    } else {
                                        codigoActual = codigoBarras;
                                        actualizaValores(true);
                                    }
                                }
                            }
                            // ----------------------------------
                            else if (opcion == 2) {
                                if (respuestaIncidencias.getCodigo() == 0) {
                                    Toast toast1 = Toast.makeText(getApplicationContext(),
                                            "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                    toast1.show();
                                    actualizaValores(true);
                                } else {
                                    codigoBarras = codigoActual;
                                }
                            }
                            // ----------------------------------
                            else if (opcion == 3) {
                                if (respuestaIncidencias.getCodigo() == 0) {
                                    incidencia = 0;
                                    estatusIncidencia = 1;
                                    banderaIncidencia = 0;
                                    for (RespuestaIncidenciasVO.IncidenciaVO inc : respuestaIncidencias.getListaIncidencias()) {
                                        if (inc.getArticuloId() == articuloId) {
                                            incidencia = 1;
                                            if (inc.getEstatusDiferencia() == 0) {
                                                estatusIncidencia = 0;
                                                banderaIncidencia = 1;
                                            }
                                        }
                                    }

                                    if (incidencia == 0) {
                                        final ViewDialogoGenerico dialogo = new ViewDialogoGenerico(CargaCodigosBarraActivity.this);
                                        dialogo.showDialog(CargaCodigosBarraActivity.this, "Faltan cajas por verificar del artículo " + descripcionCodigoBarras + ". ¿Desea generar una incidencia de faltante?", "Aceptar", "Regresar", "", true);
                                        dialogo.setViewDialogoGenericoListener(new ViewDialogoGenerico.ViewDialogoGenericoListener() {
                                            @Override
                                            public void onVerde() {
                                                indicadorProceso = "2";
                                                if (obtieneArticulosIncidencia(false, articuloId) != "") {
                                                    consultaIncidencias(false, articuloId, 7);
                                                }
                                            }

                                            @Override
                                            public void onRojo() {
                                                codigoBarras = codigoActual;
                                            }

                                            @Override
                                            public void onExtra() {

                                            }
                                        });
                                    } else {
                                        if (estatusIncidencia == 0) {
                                            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                            alert.showDialog(CargaCodigosBarraActivity.this, "Solicita la autorización de la incidencia al encargado de almacén para continuar", null, TiposAlert.ERROR);
                                        } else {
                                            codigoActual = codigoBarras;
                                            actualizaValores(true);
                                        }
                                    }
                                }
                            }
                            // ----------------------------------
                            else if (opcion == 4) {
                                if (respuestaIncidencias.getCodigo() == 0) {
                                    Toast toast1 = Toast.makeText(getApplicationContext(),
                                            "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                    toast1.show();
                                    finalizaConteo();
                                }
                            }
                            // ----------------------------------
                            else if (opcion == 5) {
                                if (respuestaIncidencias.getCodigo() == 0) {
                                    incidencia = 0;
                                    estatusIncidencia = 1;
                                    for (RespuestaIncidenciasVO.IncidenciaVO inc : respuestaIncidencias.getListaIncidencias()) {
                                        incidencia = 1;
                                        if (inc.getEstatusDiferencia() == 0) {
                                            estatusIncidencia = 0;
                                        }
                                    }

                                    if (incidencia == 1) {
                                        if (estatusIncidencia == 0) {
                                            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                            alert.showDialog(CargaCodigosBarraActivity.this, "Existen incidencias sin autorizar, comunícate con el encargado de almacén para continuar", null, TiposAlert.ERROR);
                                        } else {
                                            indicadorProceso = "2";
                                            if (obtieneArticulosIncidencia(true, 0) != "") {//finalizar-obtieneIncidencias
                                                consultaIncidencias(true, 0, 6);//genera Incidenacias - finalizado - opcion 6
                                            } else {
                                                finalizaConteo();
                                            }
                                        }
                                    } else {
                                        indicadorProceso = "2";
                                        if (obtieneArticulosIncidencia(true, 0) != "") {
                                            consultaIncidencias(true, 0, 6);
                                        } else {
                                            finalizaConteo();
                                        }
                                    }
                                }
                            } else if (opcion == 6) {

                                if (respuestaIncidencias.getCodigo() == 0) {
                                    banderaIncidencia = 1;
                                    Toast toast1 = Toast.makeText(getApplicationContext(),
                                            "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                    toast1.show();

                                    ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                    alert.showDialog(CargaCodigosBarraActivity.this, "Solicita la autorización de la incidencia al encargado de almacén para continuar", null, TiposAlert.ERROR);
                                }
                            } else if (opcion == 7) {
                                if (respuestaIncidencias.getCodigo() == 0) {
                                    banderaIncidencia = 1;
                                    Toast toast1 = Toast.makeText(getApplicationContext(),
                                            "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                    toast1.show();


                                    ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                    alert.showDialog(CargaCodigosBarraActivity.this, "Solicita la autorización de la incidencia al encargado de almacén para continuar", null, TiposAlert.ERROR);
                                }
                            }
                            // ----------------------------------

                            if (respuestaIncidencias.getCodigo() != 0) {
                                if (indicadorProceso == "1") {
                                    ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                    alert.showDialog(CargaCodigosBarraActivity.this, "Ocurrió un error al consultar incidencias", null, TiposAlert.ERROR);
                                }
                                if (indicadorProceso == "2") {
                                    ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                    alert.showDialog(CargaCodigosBarraActivity.this, "Ocurrió un error al generar incidencias", null, TiposAlert.ERROR);
                                }
                            }
                        }else {
                            System.out.println("*** 2 ***");
                            //cuando el tiempo del servicio exedio el timeout
                            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                            alert.showDialog(CargaCodigosBarraActivity.this, "Ocurrió un problema al generar las incidencias", null, TiposAlert.ERROR);
                        }
                    }
                });










                /*StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("****** INCIDENCIAS RESPONSE" + response);

                                generaListaIncidencias(response);
                                mDialog.dismiss();
                                // ----------------------------------
                                if (opcion == 1) {
                                    Log.d(TAG, "onResponse: " + "respuesta opcion1");

                                    if (respuestaIncidencias.getCodigo() == 0) {
                                        incidencia = 0;
                                        estatusIncidencia = 1;
                                        banderaIncidencia = 0;
                                        for (RespuestaIncidenciasVO.IncidenciaVO inc : respuestaIncidencias.getListaIncidencias()) {
                                            incidencia = 1;
                                            if (inc.getEstatusDiferencia() == 0) {
                                                estatusIncidencia = 0;
                                                banderaIncidencia = 1;
                                            }
                                        }
                                        if (estatusIncidencia == 0) {
                                            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                            alert.showDialog(CargaCodigosBarraActivity.this, "Existen incidencias sin autorizar, comunícate con el encargado de almacén para continuar", null, TiposAlert.ERROR);
                                        } else {
                                            codigoActual = codigoBarras;
                                            actualizaValores(true);
                                        }
                                    }
                                }
                                // ----------------------------------
                                else if (opcion == 2) {
                                    if (respuestaIncidencias.getCodigo() == 0) {
                                        Toast toast1 = Toast.makeText(getApplicationContext(),
                                                "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                        toast1.show();
                                        actualizaValores(true);
                                    } else {
                                        codigoBarras = codigoActual;
                                    }
                                }
                                // ----------------------------------
                                else if (opcion == 3) {
                                    if (respuestaIncidencias.getCodigo() == 0) {
                                        incidencia = 0;
                                        estatusIncidencia = 1;
                                        banderaIncidencia = 0;
                                        for (RespuestaIncidenciasVO.IncidenciaVO inc : respuestaIncidencias.getListaIncidencias()) {
                                            if (inc.getArticuloId() == articuloId) {
                                                incidencia = 1;
                                                if (inc.getEstatusDiferencia() == 0) {
                                                    estatusIncidencia = 0;
                                                    banderaIncidencia = 1;
                                                }
                                            }
                                        }

                                        if (incidencia == 0) {
                                            final ViewDialogoGenerico dialogo = new ViewDialogoGenerico(CargaCodigosBarraActivity.this);
                                            dialogo.showDialog(CargaCodigosBarraActivity.this, "Faltan cajas por verificar del artículo " + descripcionCodigoBarras + ". ¿Desea generar una incidencia de faltante?", "Aceptar", "Regresar", "", true);
                                            dialogo.setViewDialogoGenericoListener(new ViewDialogoGenerico.ViewDialogoGenericoListener() {
                                                @Override
                                                public void onVerde() {
                                                    indicadorProceso = "2";
                                                    if (obtieneArticulosIncidencia(false, articuloId) != "") {
                                                        consultaIncidencias(false, articuloId, 7);
                                                    }
                                                }

                                                @Override
                                                public void onRojo() {
                                                    codigoBarras = codigoActual;
                                                }

                                                @Override
                                                public void onExtra() {

                                                }
                                            });
                                        } else {
                                            if (estatusIncidencia == 0) {
                                                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                                alert.showDialog(CargaCodigosBarraActivity.this, "Solicita la autorización de la incidencia al encargado de almacén para continuar", null, TiposAlert.ERROR);
                                            } else {
                                                codigoActual = codigoBarras;
                                                actualizaValores(true);
                                            }
                                        }
                                    }
                                }
                                // ----------------------------------
                                else if (opcion == 4) {
                                    if (respuestaIncidencias.getCodigo() == 0) {
                                        Toast toast1 = Toast.makeText(getApplicationContext(),
                                                "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                        toast1.show();
                                        finalizaConteo();
                                    }
                                }
                                // ----------------------------------
                                else if (opcion == 5) {
                                    if (respuestaIncidencias.getCodigo() == 0) {
                                        incidencia = 0;
                                        estatusIncidencia = 1;
                                        for (RespuestaIncidenciasVO.IncidenciaVO inc : respuestaIncidencias.getListaIncidencias()) {
                                            incidencia = 1;
                                            if (inc.getEstatusDiferencia() == 0) {
                                                estatusIncidencia = 0;
                                            }
                                        }

                                        if (incidencia == 1) {
                                            if (estatusIncidencia == 0) {
                                                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                                alert.showDialog(CargaCodigosBarraActivity.this, "Existen incidencias sin autorizar, comunícate con el encargado de almacén para continuar", null, TiposAlert.ERROR);
                                            } else {
                                                indicadorProceso = "2";
                                                if (obtieneArticulosIncidencia(true, 0) != "") {//finalizar-obtieneIncidencias
                                                    consultaIncidencias(true, 0, 6);//genera Incidenacias - finalizado - opcion 6
                                                } else {
                                                    finalizaConteo();
                                                }
                                            }
                                        } else {
                                            indicadorProceso = "2";
                                            if (obtieneArticulosIncidencia(true, 0) != "") {
                                                consultaIncidencias(true, 0, 6);
                                            } else {
                                                finalizaConteo();
                                            }
                                        }
                                    }
                                } else if (opcion == 6) {

                                    if (respuestaIncidencias.getCodigo() == 0) {
                                        banderaIncidencia = 1;
                                        Toast toast1 = Toast.makeText(getApplicationContext(),
                                                "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                        toast1.show();

                                        ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                        alert.showDialog(CargaCodigosBarraActivity.this, "Solicita la autorización de la incidencia al encargado de almacén para continuar", null, TiposAlert.ERROR);
                                    }
                                } else if (opcion == 7) {
                                    if (respuestaIncidencias.getCodigo() == 0) {
                                        banderaIncidencia = 1;
                                        Toast toast1 = Toast.makeText(getApplicationContext(),
                                                "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                        toast1.show();


                                        ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                        alert.showDialog(CargaCodigosBarraActivity.this, "Solicita la autorización de la incidencia al encargado de almacén para continuar", null, TiposAlert.ERROR);
                                    }
                                }
                                // ----------------------------------

                                if (respuestaIncidencias.getCodigo() != 0) {
                                    if (indicadorProceso == "1") {
                                        ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                        alert.showDialog(CargaCodigosBarraActivity.this, "Ocurrió un error al consultar incidencias", null, TiposAlert.ERROR);
                                    }
                                    if (indicadorProceso == "2") {
                                        ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                        alert.showDialog(CargaCodigosBarraActivity.this, "Ocurrió un error al generar incidencias", null, TiposAlert.ERROR);
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mDialog.dismiss();
                                System.out.println("*** Error al guardar los códigos *** response: " + error.getMessage());
                                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                                alert.showDialog(
                                        CargaCodigosBarraActivity.this,
                                        "Ocurrió un problema al generar las incidencias" + error.getMessage(),
                                        null, TiposAlert.ERROR);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("folio", folio);
                        params.put("numeroSerie", Build.SERIAL);
                        params.put("version", version);
                        params.put("usuarioVerificaId", numeroEmpleado);
                        params.put("usuarioAutorizaId", "0");
                        params.put("tipoAutorizacion", String.valueOf(tipoPermiso));
                        params.put("indicadorProceso", indicadorProceso);
                        params.put("articulosArray", obtieneArticulosIncidencia(xTodos, articuloId));
                        params.put("cantidadesArray", obtieneCajasIncidencia(xTodos, articuloId));

                        return params;
                    }
                };
                strRequest.setRetryPolicy(new DefaultRetryPolicy(
                        20000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(strRequest, "tag");*/
            } catch (Exception me) {
                mDialog.dismiss();
//                System.out.println("*** No existe comunicación ***");
                ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
                alert.showDialog(CargaCodigosBarraActivity.this, "URL no disponible: favor de validar la conexión a Internet" + me.getMessage(), null, TiposAlert.ERROR);

            }
        } else {
            mDialog.dismiss();
//            System.out.println("*** No existe comunicación ***");
            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
            alert.showDialog(CargaCodigosBarraActivity.this, "URL no disponible: favor de validar la conexión a Internet", null, TiposAlert.ERROR);
        }
    }

    public void generaListaIncidencias(String response) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(response));

            int eventType = xpp.getEventType();
            respuestaIncidencias = new RespuestaIncidenciasVO();  //nueva respuesta
            List<RespuestaIncidenciasVO.IncidenciaVO> listaTemp = new ArrayList<>(); //nueva lista temporal de tipo IncidenciaVO (subclase)
            RespuestaIncidenciasVO.IncidenciaVO incidencia = respuestaIncidencias.new IncidenciaVO(); //nuevo objeto de tipo IncidenciaVO (subclase)

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("errorCode")) {
                        eventType = xpp.next(); // advance to inner text
                        respuestaIncidencias.setCodigo(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("errorDesc")) {
                        eventType = xpp.next(); // advance to inner text
                        respuestaIncidencias.setMensaje(xpp.getText().toString());
                    } else if (xpp.getName().equals("incidencia")) {
                        incidencia = respuestaIncidencias.new IncidenciaVO();
                    } else if (xpp.getName().equals("articuloId")) {
                        eventType = xpp.next(); // advance to inner text
                        incidencia.setArticuloId(Long.parseLong(xpp.getText()));
                    } else if (xpp.getName().equals("cantidadDiferencia")) {
                        eventType = xpp.next(); // advance to inner text
                        incidencia.setCantidadDiferencia(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("estatusDiferencia")) {
                        eventType = xpp.next(); // advance to inner text
                        incidencia.setEstatusDiferencia(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("incidenciaId")) {
                        eventType = xpp.next(); // advance to inner text
                        incidencia.setIncidenciaId(Long.parseLong(xpp.getText()));
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equals("incidencia")) {
                        listaTemp.add(incidencia);
                    }
                }
                eventType = xpp.next();
            }
            respuestaIncidencias.setListaIncidencias(listaTemp);
        } catch (Exception e) {
            ViewDialog alert = new ViewDialog(CargaCodigosBarraActivity.this);
            alert.showDialog(CargaCodigosBarraActivity.this, "Error al formar las diferencias del xml: " + e.getMessage(), null, TiposAlert.ERROR);
        }
    }

    public void codigoBarraText(View view) {
        //doNothin
        Log.d(TAG, "codigoBarraText: ENTRO CODIGO BARRAS");
    }
}
