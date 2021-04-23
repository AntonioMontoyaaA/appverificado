package neto.com.mx.verificapedidocedis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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
import neto.com.mx.verificapedidocedis.beans.IncidenciaVO;
import neto.com.mx.verificapedidocedis.beans.RespuestaIncidenciasVO;
import neto.com.mx.verificapedidocedis.dialogos.DiferenciaAclaradaDialog;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialogoGenerico;
import neto.com.mx.verificapedidocedis.providers.ProviderGuardarArticulos;
import neto.com.mx.verificapedidocedis.providers.ProviderGuardarDiferencias;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.TiposAlert;

import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GUARDARARTSCONTADOSVERIFICADOR;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GUARDARDIFERENCIASVERIFICADO;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACE;

public class ConteoDiferenciasActivity extends AppCompatActivity {

    EditText editTextCodigos = null;

    private String codigoBarras = "";
    private long articuloIdBusqueda = 0;
    private HashMap<Long, ArticuloVO> mapaCatalogo = new HashMap<Long, ArticuloVO>();
    private String folioPedido = "";
    private int ACCION_GUARDA = 0;
    private boolean esGuardadoPorCodigos = false;

    ScrollView mScrollView = null;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

    private String nombreTienda = "";
    private String nombreZona = "";
    private int idZona = 0;
    String indicadorProceso="1";

    //Variables para Dialogo de confirmación
    boolean existeCodigo = false;
    String descripcionCodigoBarras = "";
    int cantidadCapturadaDialogo = 0;
    int cantidadEmbarcadaDialogo = 0;
    String descripcionNormaEmpaqueDialogo = "";
    String descripcionNormaEmpaqueWSDialogo = "";

    private String folio = "";
    private String numeroEmpleado = "";
    private String nombreEmpleado = "";
    private String version = "";
    public static RespuestaIncidenciasVO respuestaIncidencias;
    int tipoPermiso;
    int totalCajasSurtidas;
    int incidencia=0;
    int estatusIncidencia=0;
    String codigoActual;
    String metodo="";
    boolean bloqueo_finalizado= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteo_diferencias);
        getSupportActionBar().hide();

        mapaCatalogo = (HashMap<Long, ArticuloVO>)this.getIntent().getSerializableExtra("mapaCat");
        folio = new String(this.getIntent().getStringExtra("folio"));
        nombreEmpleado = new String(this.getIntent().getStringExtra("nombreEmpleado").trim());
        numeroEmpleado = new String(this.getIntent().getStringExtra("numeroEmpleado").trim());
        nombreTienda = new String(this.getIntent().getStringExtra("nombreTienda").trim());
        nombreZona = new String(this.getIntent().getStringExtra("nombreZona").trim());
        idZona = this.getIntent().getIntExtra("idZona", 0);
        tipoPermiso = this.getIntent().getIntExtra("tipoPermiso", 0);
        totalCajasSurtidas = this.getIntent().getIntExtra("totalCajasSurtidas", 0);

        editTextCodigos = (EditText) findViewById(R.id.codigoBarraText);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            editTextCodigos.setCursorVisible(false);
        }else{
            editTextCodigos.setInputType(InputType.TYPE_NULL);
        }
        editTextCodigos.requestFocus();
        editTextCodigos.setOnEditorActionListener(codigosListener);

        imprimeDiferencias();

        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch(PackageManager.NameNotFoundException ne) {
            Log.e("CARGA_FOLIO_TAG", "Error al obtener la versión: " + ne.getMessage());
        }
    }

    private int totalCajasRecibidas = 0;

    TextView.OnEditorActionListener codigosListener = new TextView.OnEditorActionListener(){
        public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
            try {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    existeCodigo = false;

                    //EditText editText = (EditText) findViewById(R.id.codigoBarraText);
                    codigoBarras =  editTextCodigos.getText().toString().trim();
                    descripcionCodigoBarras = codigoBarras;

                    cantidadCapturadaDialogo = 0;
                    cantidadEmbarcadaDialogo = 0;

                    if(!codigoBarras.equals("")) {

                        //Busca código de barras en el catálogo
                        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                            if(entry.getValue().getCodigos().contains(codigoBarras)) {

                                articuloIdBusqueda = entry.getKey();
                                descripcionCodigoBarras = entry.getValue().getNombreArticulo();
                                entry.getValue().setTotalCajasVerificadas(entry.getValue().getTotalCajasVerificadas() + 1);
                                cantidadCapturadaDialogo = entry.getValue().getTotalCajasVerificadas();
                                cantidadEmbarcadaDialogo = entry.getValue().getTotalCajasAsignadas();
                                existeCodigo = true;
                                entry.getValue().setEsCapturado(true);
                                if(entry.getValue().getTotalCajasVerificadas() != entry.getValue().getTotalCajasAsignadas()) {
                                    entry.getValue().setEsBuscadoDiferencias(true);
                                }

                                if(cantidadCapturadaDialogo <= cantidadEmbarcadaDialogo) {
                                    guardaAvancePorCodigos();
                                    DiferenciaAclaradaDialog diferenciaAclaradaDialog = new DiferenciaAclaradaDialog(ConteoDiferenciasActivity.this);
                                    diferenciaAclaradaDialog.showDialog(ConteoDiferenciasActivity.this, entry.getValue().getNombreArticulo(),
                                            entry.getValue().getTotalCajasVerificadas(),entry.getValue().getTotalCajasAsignadas());
                                }

                            } else {
                                entry.getValue().setEsBuscadoDiferencias(false);
                                entry.getValue().setEsCapturado(false);
                            }
                        }

                        if(cantidadCapturadaDialogo > cantidadEmbarcadaDialogo) {

                            ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                            alert.showDialog(ConteoDiferenciasActivity.this, "No puedes contar más cajas de las asignadas", null, TiposAlert.ERROR);

                            for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                                if (entry.getValue().getCodigos().contains(codigoBarras)) {
                                    entry.getValue().setTotalCajasVerificadas(entry.getValue().getTotalCajasAsignadas());
                                    entry.getValue().setEsCapturado(false);
                                }
                            }
                            ingresaCodigoBarras();


                            /*ViewDialogoConfirma confirma = new ViewDialogoConfirma(ConteoDiferenciasActivity.this);
                            confirma.showDialog(ConteoDiferenciasActivity.this, cantidadCapturadaDialogo, cantidadEmbarcadaDialogo);
                            confirma.setViewDialogoConfirmaListener(new ViewDialogoConfirma.ViewDialogoConfirmaListener() {
                                @Override
                                public void onIncrementaContador() {
                                    ingresaCodigoBarras();
                                }
                                @Override
                                public void onLimpiaCampo() {

                                    for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                                        if (entry.getValue().getCodigos().contains(codigoBarras)) {
                                            entry.getValue().setTotalCajasVerificadas(entry.getValue().getTotalCajasVerificadas() - 1);
                                            entry.getValue().setEsCapturado(false);
                                        }
                                    }
                                }
                            });*/
                        } else {
                            ingresaCodigoBarras();
                        }
                        editTextCodigos.setText("");
                    }



                }
            } catch(Exception e) {
                //Toast.makeText(CargaCodigosBarraActivity.this, "Error al leer el código de barras: " + e.getMessage(), Toast.LENGTH_LONG).show();
                ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                alert.showDialog(ConteoDiferenciasActivity.this, "Error al leer el código de barras: " + e.getMessage(), null, TiposAlert.ERROR);
            }
            return true;
        }
    };

    public void imprimeDiferencias() {

        List<CodigoBarraVO> listaCodigos = new ArrayList<CodigoBarraVO>();

        //Coloca el elemento escaneado al inicio
        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            if(entry.getValue().isEsBuscadoDiferencias()) {

                System.out.println("*** EN CONTEO DE DIFERENCIAS NOMBRE ARTICULO: " + entry.getValue().getNombreArticulo());

                if(entry.getValue().getTotalCajasVerificadas() != entry.getValue().getTotalCajasAsignadas()) {
                    CodigoBarraVO codigo = new CodigoBarraVO();
                    codigo.setArticuloId(entry.getKey());
                    codigo.setNombreArticulo(entry.getValue().getNombreArticulo());
                    codigo.setCajasAsignadas(entry.getValue().getTotalCajasAsignadas());
                    codigo.setCajasVerificadas(entry.getValue().getTotalCajasVerificadas());
                    listaCodigos.add(codigo);
                    break;
                }
            }
        }

        //Coloca los otros elementos
        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            if(entry.getValue().getTotalCajasVerificadas() != entry.getValue().getTotalCajasAsignadas() && !entry.getValue().isEsBuscadoDiferencias()) {
                CodigoBarraVO codigo = new CodigoBarraVO();
                codigo.setArticuloId(entry.getKey());
                codigo.setNombreArticulo(entry.getValue().getNombreArticulo());
                codigo.setCajasAsignadas(entry.getValue().getTotalCajasAsignadas());
                codigo.setCajasVerificadas(entry.getValue().getTotalCajasVerificadas());
                listaCodigos.add(codigo);
            }
        }

        if(listaCodigos.size() > 0) {
            dibujaDiferencias(listaCodigos.toArray(new CodigoBarraVO[listaCodigos.size()]));
        }
//        else {
//            finalizaConteo();
//        }
    }

    public void ingresaCodigoBarras() {
        ACCION_GUARDA = 0;
        guardaAvancePorCodigos();
        if(!existeCodigo) {
            ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
            alert.showDialog(ConteoDiferenciasActivity.this, "Artículo no encontrado - " + codigoBarras, null, TiposAlert.ERROR);

            //EditText editText = (EditText) findViewById(R.id.codigoBarraText);
            editTextCodigos.setText("");

        } else {
            limpiarTabla();
            imprimeDiferencias();
        }
    }

    public void limpiarTabla() {
        int count = ((TableLayout) findViewById(R.id.tabla_diferencias_view)).getChildCount();
        for (int i = 0; i < count; i++) {
            View child = ((TableLayout) findViewById(R.id.tabla_diferencias_view)).getChildAt(i);
            if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
        }
    }

    public void dibujaDiferencias(CodigoBarraVO[] codigos) {
        int width_Ancho = this.getResources().getConfiguration().screenWidthDp;
        int height_Largo = this.getResources().getConfiguration().screenHeightDp;
        TableLayout ll = (TableLayout) findViewById(R.id.tabla_diferencias_view);
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER_HORIZONTAL);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        TextView tArticuloHeader = new TextView(this);
        int anchoArticulo = 400;
        int anchoTextoArticulo = 130;
        //System.out.println("/////////////////////////////// Ancho: "+ width_Ancho + "//////////////largo: "+ height_Largo);
        if (width_Ancho > 350 && height_Largo > 740){
            anchoArticulo = 700;
            anchoTextoArticulo = 180;
        }
        tArticuloHeader.setWidth(anchoArticulo);
        tArticuloHeader.setTextSize(25);

        TextView tCantidadHeader = new TextView(this);
        tCantidadHeader.setWidth(200);
        tCantidadHeader.setTextSize(25);

        //This generates the caption row
        tArticuloHeader.setText("Artículo");
        //tArticuloHeader.setPadding(20, 3, 3, 3);
        if (width_Ancho <= 320 ){
            tArticuloHeader.setPadding(85, 0, 0, 0);
        }else{
            tArticuloHeader.setPadding(0, 0, 0, 0);
        }
        tArticuloHeader.setTextColor(getResources().getColor(R.color.colorFuenteActivo));

        tCantidadHeader.setText("Cant.");
        //tCantidadHeader.setPadding(5, 3, 3, 3);
        if (width_Ancho <= 320){
            tCantidadHeader.setPadding(-100, 0, 0, 0);
        }else{
            tCantidadHeader.setPadding(0, 0, 0, 0);
        }
        tCantidadHeader.setTextColor(getResources().getColor(R.color.colorFuenteActivo));
        tCantidadHeader.setGravity(Gravity.CENTER);

        row.addView(tArticuloHeader);
        row.addView(tCantidadHeader);
        ll.addView(row,0);

        for (int i = 1; i <= codigos.length; i++) {
            row = new TableRow(this);
            row.setGravity(Gravity.CENTER_HORIZONTAL);
            lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            lp.height = anchoTextoArticulo;
            row.setLayoutParams(lp);

            TextView tArticulo = new TextView(this);
            tArticulo.setWidth(400);
            tArticulo.setHeight(80);
            tArticulo.setText("*" + codigos[i-1].getNombreArticulo());
            tArticulo.setTextColor(getResources().getColor(R.color.colorFuenteAzul));
            if (width_Ancho <= 320){
                tArticulo.setPadding(85, 20, 0, 0);
            }else{
                tArticulo.setPadding(0, 20, 0, 0);
            }
            tArticulo.setLayoutParams(lp);

            TextView tCantidad = new TextView(this);
            tCantidad.setWidth(150);
            tCantidad.setHeight(80);
            tCantidad.setText(codigos[i-1].getCajasVerificadas()+"");
            tCantidad.setTextColor(getResources().getColor(R.color.colorFuenteAzul));
            if (width_Ancho <= 320){
                tCantidad.setPadding(-100, 3, 3, 3);
            }else{
                tCantidad.setPadding(0, 3, 3, 3);
            }
            tCantidad.setTextSize(25);
            tCantidad.setLayoutParams(lp);
            tCantidad.setGravity(Gravity.CENTER);

            row.addView(tArticulo);
            row.addView(tCantidad);
            ll.addView(row,i);
        }
    }

    public void guardaAvancePorCodigos() {
        ACCION_GUARDA = 0;
        metodo = "ConteoDiferencias - guardaAvancePorCodigos";
        esGuardadoPorCodigos = true;
        ejecutaWS();
    }

    public void ejecutaWS() {
        if(!bloqueo_finalizado) {
            if (ACCION_GUARDA == 1) {//si finaliza
                bloqueo_finalizado = true; //bloquea el metodo de guardado para que solo se ejecute una vez
                System.out.println("SE BLOQUEA FINALIZADO");
            }
            // Do something in response to button
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Operaciones http
                System.out.println("tipo Guardado: " + String.valueOf(ACCION_GUARDA));
                System.out.println("ENTRA A METODO DE GUARDADO 3" + metodo);

                try {
                    //String url = Constantes.URL_STRING + "guardarArtsContadosVerificador";
                    int contadorOcurrencias = 0;
                    final ProgressDialog mDialog = new ProgressDialog(this);
                    mDialog.setMessage("Guardando códigos de barra...");
                    mDialog.setCancelable(false);
                    mDialog.setInverseBackgroundForced(false);
                    if (!esGuardadoPorCodigos) {
                        mDialog.show();
                    }

                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GUARDARARTSCONTADOSVERIFICADOR);
                    request.addProperty("folio", folio);
                    request.addProperty("idZona",String.valueOf(idZona));
                    request.addProperty("articulosArray",obtieneCadenaArticulos());
                    request.addProperty("cantidadesArray",obtieneCadenaCajas());
                    request.addProperty("tipoGuardado",String.valueOf(ACCION_GUARDA));
                    request.addProperty("numeroSerie",Build.SERIAL);
                    request.addProperty("version",version);
                    request.addProperty("usuario",numeroEmpleado);
                    request.addProperty("metodo",metodo);

                    ProviderGuardarArticulos.getInstance(this).getGuardarArticulos(request, new ProviderGuardarArticulos.interfaceGuardarArticulos() {
                        @Override
                        public void resolver(CodigosGuardadosVO respuestaGuardaArticulos) {
                            mDialog.dismiss();
                            System.out.println("tipo Guardado: " + String.valueOf(ACCION_GUARDA));
                            System.out.println("*** 1 *** response: " + respuestaGuardaArticulos);

                            if (respuestaGuardaArticulos.getCodigo() == 0) {
                                if (ACCION_GUARDA == 1) {
                                    Intent intent = new Intent(ConteoDiferenciasActivity.this, DiferenciasRecibidasActivity.class);
                                    intent.putExtra("CodigosGuardados", respuestaGuardaArticulos);
                                    intent.putExtra("folio", folio);
                                    intent.putExtra("numeroEmpleado", numeroEmpleado);
                                    intent.putExtra("nombreEmpleado", nombreEmpleado);
                                    intent.putExtra("estatusPedido", respuestaGuardaArticulos.getEstatusPedido());
                                    intent.putExtra("nombreTienda", nombreTienda);
                                    intent.putExtra("nombreZona", nombreZona);
                                    intent.putExtra("idZona", idZona);
                                    startActivity(intent);
                                }
                            } else {
                                Intent intent = new Intent(ConteoDiferenciasActivity.this, CargaFolioPedidoActivity.class);
                                intent.putExtra("numeroEmpleado", numeroEmpleado);
                                intent.putExtra("nombreEmpleado", nombreEmpleado);
                                ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                                alert.showDialog(ConteoDiferenciasActivity.this, "Error: " + respuestaGuardaArticulos.getMensaje(), intent, TiposAlert.ERROR);

                            }
                            ACCION_GUARDA = 0;
                            bloqueo_finalizado = false;
                        }
                        });
                } catch (Exception me) {
                    bloqueo_finalizado = false;
                    if (!esGuardadoPorCodigos) {
                        ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                        alert.showDialog(ConteoDiferenciasActivity.this, "URL no disponible: favor de validar la conexión a Internet" + me.getMessage(), null, TiposAlert.ERROR);
                    }
                }
            } else {
                bloqueo_finalizado= false;
                // Mostrar errores
                if (!esGuardadoPorCodigos) {
                    ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                    alert.showDialog(ConteoDiferenciasActivity.this, "No hay conexión HTTP", null, TiposAlert.ERROR);
                }
            }
        }else{
            System.out.println("SE OMITE PETICION MIENTRAS TRABAJA FINALIZADO");
        }
    }

    public String obtieneCadenaArticulos() {
        StringBuffer cadena = new StringBuffer();
        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            if(ACCION_GUARDA == 0 && esGuardadoPorCodigos && entry.getValue().isEsCapturado()) {
                cadena.append(entry.getKey());
                cadena.append("|");
            } else if(ACCION_GUARDA == 1) {
                cadena.append(entry.getKey());
                cadena.append("|");
            }
        }
        cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "" );

        return cadena.toString();
    }

    public String obtieneCadenaCajas() {
        StringBuffer cadena = new StringBuffer();
        for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
            if(ACCION_GUARDA == 0 && esGuardadoPorCodigos && entry.getValue().isEsCapturado()) {
                cadena.append(entry.getValue().getTotalCajasVerificadas());
                cadena.append("|");
            } else if(ACCION_GUARDA == 1) {
                cadena.append(entry.getValue().getTotalCajasVerificadas());
                cadena.append("|");
            }
        }
        cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "" );

        return cadena.toString();
    }

    public void generaFaltantes(String response, CodigosGuardadosVO codigos) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader(response) );

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
                        listaCodigosFaltantes.add(codigoBarraVO);
                    }
                }
                eventType = xpp.next();
            }
            codigos.setArticulosDiferencias(listaCodigosFaltantes.toArray(new CodigoBarraVO[listaCodigosFaltantes.size()]));

            int contador = 0;
            for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                if(entry.getValue().isEsCapturado()) {
                    contador++;
                }
            }
        } catch(Exception e) {
            ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
            alert.showDialog(ConteoDiferenciasActivity.this, "Error al formar las diferencias en xml: " + e.getMessage(), null, TiposAlert.ERROR);
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void finalizarMenuFront(View view) {
        cuentaCajasRecibidas();
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        if(totalCajasRecibidas < totalCajasSurtidas) {
            if(tipoPermiso == 0){
                indicadorProceso = "2";
                if(obtieneArticulosIncidencia(true,0)!="") {
                    consultaIncidencias(true, 0, 4);
                }else{
                    finalizaConteo();
                }
            }else{
                indicadorProceso = "1";
                consultaIncidencias(false, 0, 5);
            }

        } else {
            finalizaConteo();
        }
    }

    public void regresarMenuFront(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        regresaMenu();
    }

    public void salirMenuFront(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        salirMenu();
    }

    public void scrollPaginado(View view) {
        view.startAnimation(buttonClick);
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        mScrollView = (ScrollView)findViewById(R.id.container_scroll_view);
        mScrollView.post(new Runnable() {
            public void run() {
                mScrollView.smoothScrollBy(0,300);
            }
        });
    }

    public void cuentaCajasRecibidas() {
        totalCajasRecibidas = 0;
        for (ArticuloVO entry : mapaCatalogo.values()) {
            totalCajasRecibidas += entry.getTotalCajasVerificadas();
        }
    }

    public void finalizaConteo() {
        ACCION_GUARDA = 1;
        esGuardadoPorCodigos = false;
        metodo = "ConteoDiferencias - finalizaConteo";
        ejecutaWS();
    }

    public void regresaMenu() {
        Intent intent = new Intent(ConteoDiferenciasActivity.this, CargaCodigosBarraActivity.class);
        intent.putExtra("folio", folio);
        intent.putExtra("numeroEmpleado", numeroEmpleado);
        intent.putExtra("nombreEmpleado", nombreEmpleado);
        intent.putExtra("nombreTienda", nombreTienda);
        intent.putExtra("descargaCatalogo", true);
        intent.putExtra("nombreZona", nombreZona);
        intent.putExtra("idZona", idZona);
        startActivity(intent);
    }

    public void salirMenu() {
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("LOGOUT", true);
        startActivity(intent);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }


    public String obtieneArticulosIncidencia(boolean xTodos, long articuloId) {
        StringBuffer cadena = new StringBuffer();
        if(!xTodos && articuloId == 0){
            System.out.println("consultando**");
            return "";
        }else{
            for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                int cantidadCapturadaDialogo = entry.getValue().getTotalCajasVerificadas();
                int cantidadEmbarcadaDialogo = entry.getValue().getTotalCajasAsignadas();
                if (cantidadCapturadaDialogo < cantidadEmbarcadaDialogo) {
                    if (xTodos) {
                        boolean encontrado = false;

                        if(respuestaIncidencias != null){
                            for (IncidenciaVO lista : respuestaIncidencias.getListaIncidencias()) {
                                if (lista.getArticuloId() == entry.getKey()) {
                                    if (lista.getEstatusDiferencia() == 1) {
                                        System.out.println("impresion omite "+entry.getKey());
                                        encontrado = true;
                                    }
                                }
                            }
                        }
                        if (!encontrado) {
                            cadena.append(entry.getKey());
                            cadena.append("|");
                        }
                    }else if (!xTodos && articuloId != 0) {
                        if (entry.getKey() == articuloId) {
                            cadena.append(entry.getKey());
                            cadena.append("|");
                        }
                    }
                }
            }
            if(cadena.length()==0){
                return "";
            }else{
                cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "" );
                System.out.println("***Articulos con incidencia: "+cadena);
                return cadena.toString();
            }
        }
    }
    public String obtieneCajasIncidencia(boolean xTodos, long articuloId) {
        StringBuffer cadena = new StringBuffer();
        if(!xTodos && articuloId == 0){
            return "";
        }else{
            for (Map.Entry<Long, ArticuloVO> entry : mapaCatalogo.entrySet()) {
                int cantidadCapturadaDialogo = entry.getValue().getTotalCajasVerificadas();
                int cantidadEmbarcadaDialogo = entry.getValue().getTotalCajasAsignadas();
                if (cantidadCapturadaDialogo < cantidadEmbarcadaDialogo) {
                    if (xTodos) {
                        boolean encontrado = false;
                        if(respuestaIncidencias != null) {
                            for (IncidenciaVO lista : respuestaIncidencias.getListaIncidencias()) {
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
            cadena.replace(cadena.lastIndexOf("|"), cadena.lastIndexOf("|") + 1, "" );
            System.out.println("***Cajas con incidencia: "+cadena);
            return cadena.toString();
        }
    }
    public void consultaIncidencias(final boolean xTodos, final long articuloId,final int opcion) {// true -> todos los articulos  // false -> por artículo
        final ProgressDialog mDialog = new ProgressDialog(this);
        if(indicadorProceso!="1"){
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME_GUARDARDIFERENCIASVERIFICADO);
                request.addProperty("folio", folio);
                request.addProperty("numeroSerie", Build.SERIAL);
                request.addProperty("version", version);
                request.addProperty("usuarioVerificaId", numeroEmpleado);
                request.addProperty("usuarioAutorizaId","0");
                request.addProperty("tipoAutorizacion", String.valueOf(tipoPermiso));
                request.addProperty("indicadorProceso", indicadorProceso);
                request.addProperty("articulosArray", obtieneArticulosIncidencia(xTodos,articuloId));
                request.addProperty("cantidadesArray", obtieneCajasIncidencia(xTodos, articuloId));
                System.out.println("/////////////////////////////REQUEST_DATOSENVIADOS_ConteoDiferenciasActivity:"+request);

                ProviderGuardarDiferencias.getInstance(this).getGuardarDiferencias(request, new ProviderGuardarDiferencias.interfaceGuardarDiferencias() {
                    @Override
                    public void resolver(RespuestaIncidenciasVO respuestaGuardaDiferencias) {

                        mDialog.dismiss();
                        System.out.println("*** 1 *** respuestaGuardaDiferencias: " + respuestaGuardaDiferencias);
                        if (respuestaGuardaDiferencias != null) {

                            // ----------------------------------
                            if (opcion == 1) {
                                if (respuestaIncidencias.getCodigo() == 0) {
                                    incidencia = 0;
                                    estatusIncidencia = 1;
//                                        banderaIncidencia = 0;
                                    for (IncidenciaVO inc : respuestaIncidencias.getListaIncidencias()) {
                                        incidencia = 1;
                                        if (inc.getEstatusDiferencia() == 0) {
                                            estatusIncidencia = 0;
//                                                banderaIncidencia = 1;
                                        }
                                    }
                                    if (estatusIncidencia == 0) {
                                        ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                                        alert.showDialog(ConteoDiferenciasActivity.this, "Existen incidencias sin autorizar, comunícate con el encargado de almacén para continuar", null, TiposAlert.ERROR);
                                    } else {
                                        codigoActual = codigoBarras;
//                                            actualizaValores(true);
                                    }
                                }
                            }
                            // ----------------------------------
                            else if (opcion == 2) {
                                if (respuestaIncidencias.getCodigo() == 0) {
                                    Toast toast1 = Toast.makeText(getApplicationContext(),
                                            "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                    toast1.show();
//                                        actualizaValores(true);
                                } else {
                                    codigoBarras = codigoActual;
                                }
                            }
                            // ----------------------------------
                            else if (opcion == 3) {
                                if (respuestaIncidencias.getCodigo() == 0) {
                                    incidencia = 0;
                                    estatusIncidencia = 1;
//                                        banderaIncidencia = 0;
                                    for (IncidenciaVO inc : respuestaIncidencias.getListaIncidencias()) {
                                        if (inc.getArticuloId() == articuloId) {
                                            incidencia = 1;
                                            if (inc.getEstatusDiferencia() == 0) {
                                                estatusIncidencia = 0;
//                                                    banderaIncidencia = 1;
                                            }
                                        }
                                    }

                                    if (incidencia == 0) {
                                        final ViewDialogoGenerico dialogo = new ViewDialogoGenerico(ConteoDiferenciasActivity.this);
                                        dialogo.showDialog(ConteoDiferenciasActivity.this, "Faltan cajas por verificar del artículo " + descripcionCodigoBarras + ". ¿Desea generar una incidencia de faltante?", "Aceptar", "Regresar", "", true);
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
                                            ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                                            alert.showDialog(ConteoDiferenciasActivity.this, "Solicita la autorización de la incidencia al encargado de almacén para continuar", null, TiposAlert.ERROR);
                                        } else {
                                            codigoActual = codigoBarras;
//                                                actualizaValores(true);
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
                                    for (IncidenciaVO inc : respuestaIncidencias.getListaIncidencias()) {
                                        incidencia = 1;
                                        if (inc.getEstatusDiferencia() == 0) {
                                            estatusIncidencia = 0;
                                        }
                                    }

                                    if (incidencia == 1) {
                                        if (estatusIncidencia == 0) {
                                            ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                                            alert.showDialog(ConteoDiferenciasActivity.this, "Existen incidencias sin autorizar, comunícate con el encargado de almacén para continuar", null, TiposAlert.ERROR);
                                        } else {
                                            indicadorProceso = "2";
                                            if (obtieneArticulosIncidencia(true, 0) != "") {
                                                consultaIncidencias(true, 0, 6);
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
//                                        banderaIncidencia = 1;
                                    Toast toast1 = Toast.makeText(getApplicationContext(),
                                            "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                    toast1.show();

                                    ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                                    alert.showDialog(ConteoDiferenciasActivity.this, "Solicita la autorización de la incidencia al encargado de almacén para continuar", null, TiposAlert.ERROR);
                                }
                            } else if (opcion == 7) {
                                if (respuestaIncidencias.getCodigo() == 0) {
//                                        banderaIncidencia = 1;
                                    Toast toast1 = Toast.makeText(getApplicationContext(),
                                            "Incidencias generadas con éxito", Toast.LENGTH_SHORT);
                                    toast1.show();


                                    ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                                    alert.showDialog(ConteoDiferenciasActivity.this, "Solicita la autorización de la incidencia al encargado de almacén para continuar", null, TiposAlert.ERROR);
                                }
                            }
                            // ----------------------------------
                            System.out.println("******GENERAR INCIDENCIAS" + respuestaGuardaDiferencias);
                            if (respuestaIncidencias.getCodigo() != 0) {
                                if (indicadorProceso == "1") {
                                    ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                                    alert.showDialog(ConteoDiferenciasActivity.this, "Ocurrió un error al consultar incidencias", null, TiposAlert.ERROR);
                                }
                                if (indicadorProceso == "2") {
                                    ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                                    alert.showDialog(ConteoDiferenciasActivity.this, "Ocurrió un error al generar incidencias", null, TiposAlert.ERROR);
                                }
                            }
                        }else {
                            System.out.println("*** 2 ***");
                            //cuando el tiempo del servicio exedio el timeout
                            ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                            alert.showDialog(ConteoDiferenciasActivity.this, "Ocurrió un problema al generar las incidencias", null, TiposAlert.ERROR);
                        }
                    }
                });

            } catch(Exception me) {
                mDialog.dismiss();
//                System.out.println("*** No existe comunicación ***");
                ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
                alert.showDialog(ConteoDiferenciasActivity.this, "URL no disponible: favor de validar la conexión a Internet" + me.getMessage(), null, TiposAlert.ERROR);

            }
        } else {
            mDialog.dismiss();
//            System.out.println("*** No existe comunicación ***");
            ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
            alert.showDialog(ConteoDiferenciasActivity.this, "URL no disponible: favor de validar la conexión a Internet", null, TiposAlert.ERROR);
        }
    }

    public void generaListaIncidencias(String response) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader(response) );

            int eventType = xpp.getEventType();
            respuestaIncidencias = new RespuestaIncidenciasVO();  //nueva respuesta
            List<IncidenciaVO> listaTemp = new ArrayList<>(); //nueva lista temporal de tipo IncidenciaVO (subclase)
           IncidenciaVO incidencia = new IncidenciaVO(); //nuevo objeto de tipo IncidenciaVO (subclase)

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("errorCode")) {
                        eventType = xpp.next(); // advance to inner text
                        respuestaIncidencias.setCodigo(Integer.parseInt(xpp.getText()));
                    }
                    else if (xpp.getName().equals("errorDesc")) {
                        eventType = xpp.next(); // advance to inner text
                        respuestaIncidencias.setMensaje(xpp.getText().toString());
                    }
                    else if (xpp.getName().equals("incidencia")) {
                        incidencia = new IncidenciaVO();
                    }
                    else if (xpp.getName().equals("articuloId")) {
                        eventType = xpp.next(); // advance to inner text
                        incidencia.setArticuloId(Long.parseLong(xpp.getText()));
                    }
                    else if (xpp.getName().equals("cantidadDiferencia")) {
                        eventType = xpp.next(); // advance to inner text
                        incidencia.setCantidadDiferencia(Integer.parseInt(xpp.getText()));
                    }
                    else if (xpp.getName().equals("estatusDiferencia")) {
                        eventType = xpp.next(); // advance to inner text
                        incidencia.setEstatusDiferencia(Integer.parseInt(xpp.getText()));
                    }
                    else if (xpp.getName().equals("incidenciaId")) {
                        eventType = xpp.next(); // advance to inner text
                        incidencia.setIncidenciaId(Long.parseLong(xpp.getText()));
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    if(xpp.getName().equals("incidencia")) {
                        listaTemp.add(incidencia);
                    }
                }
                eventType = xpp.next();
            }
            respuestaIncidencias.setListaIncidencias(listaTemp);
        } catch(Exception e) {
            ViewDialog alert = new ViewDialog(ConteoDiferenciasActivity.this);
            alert.showDialog(ConteoDiferenciasActivity.this, "Error al formar las diferencias del xml: " + e.getMessage(), null, TiposAlert.ERROR);
        }
    }

}
