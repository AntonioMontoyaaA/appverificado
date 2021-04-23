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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neto.com.mx.verificapedidocedis.beans.ValidaPedidoVO;
import neto.com.mx.verificapedidocedis.beans.ZonaPickeoVO;
import neto.com.mx.verificapedidocedis.beans.ZonaVerificadoVO;
import neto.com.mx.verificapedidocedis.dialogos.BienvenidaDialog;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog;
import neto.com.mx.verificapedidocedis.providers.ProviderValidaPedido;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.TiposAlert;

import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GETVALIDAPEDIDOVERIFICADOR;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACE;

public class CargaFolioPedidoActivity extends AppCompatActivity {

    EditText editTextFolio = null;

    private String folioPedido = "";
    Context context;
    Intent intentGlobal;
    String version = "";
    private String numeroEmpleado = "";
    private String nombreEmpleado = "";

    boolean bandera = true;
    String numeroSerie = Build.SERIAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_folio_pedido);
        context = this.getApplicationContext();
        getSupportActionBar().hide();

        editTextFolio = (EditText) findViewById(R.id.folioPedidoText);
        editTextFolio.setText("");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            editTextFolio.setCursorVisible(false);
        }else{
            editTextFolio.setInputType(InputType.TYPE_NULL);
        }
        editTextFolio.requestFocus();
        editTextFolio.setOnEditorActionListener(escaneaListener);

        intentGlobal = new Intent(this, CargaCodigosBarraActivity.class);

        numeroEmpleado = new String(this.getIntent().getStringExtra("numeroEmpleado").trim());
        nombreEmpleado = new String(this.getIntent().getStringExtra("nombreEmpleado").trim());

        BienvenidaDialog alert = new BienvenidaDialog(CargaFolioPedidoActivity.this);
        alert.showDialog(CargaFolioPedidoActivity.this, nombreEmpleado);


        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch(PackageManager.NameNotFoundException ne) {
            Log.e("CARGA_FOLIO_TAG", "Error al obtener la versión: " + ne.getMessage());
        }

    }

    public void pedidoPruebaTogo(View view){
        editTextFolio.setText("202009211813803");
        ejecutaWS();
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

    public void regresaMenu() {
        Intent intent = new Intent(getApplicationContext(), CargaFolioPedidoActivity.class);
        startActivity(intent);
    }

    public void salirMenu() {
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("LOGOUT", true);
        startActivity(intent);
    }

    TextView.OnEditorActionListener escaneaListener = new TextView.OnEditorActionListener(){
        public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                ejecutaWS();
            }
            return true;
        }
    };

    public void ejecutaWS() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Operaciones http
            try {
                //EditText editText = (EditText) findViewById(R.id.folioPedidoText);
                if(!editTextFolio.getText().toString().equals("")) {
                    final ProgressDialog mDialog = new ProgressDialog(this);
                    mDialog.setMessage("Buscando folio del pedido...");
                    mDialog.setCancelable(false);
                    mDialog.setInverseBackgroundForced(false);
                    mDialog.show();


                    //String url = Constantes.URL_STRING + "getValidaPedidoVerificador";
                    SoapObject request = new SoapObject(NAMESPACE,METHOD_NAME_GETVALIDAPEDIDOVERIFICADOR);
                    request.addProperty("folio", editTextFolio.getText().toString().trim());
                    request.addProperty("numeroSerie", numeroSerie);
                    request.addProperty("version", version);
                    request.addProperty("usuario", numeroEmpleado);
                    System.out.println(" reuqest ejecutaWS:////////////////////////"+ request);

                    if (numeroSerie.contains("EF500")) {

                        ProviderValidaPedido.getInstance(this).getValidaPedido(request, new ProviderValidaPedido.interfaceValidaPedido() {
                            @Override
                            public void resolver(ValidaPedidoVO respuestaValidaPedido) {
                                System.out.println("Valida 1 RESPONSE: " + respuestaValidaPedido);
                                mDialog.dismiss();

                                if (respuestaValidaPedido != null && bandera == true) {

                                    if (respuestaValidaPedido.isPedidoValido().equals("true")) {
                                        System.out.println("///////////////////////////////////////////Entra a correr ZonasDisponibles////////////////////////");
                                        Intent intent = new Intent(context, CargaCodigosBarraActivity.class);
                                        intent.putExtra("folio", editTextFolio.getText().toString());
                                        intent.putExtra("listaZonasVerificado", (Serializable) respuestaValidaPedido.getListaZonasVerificado());
                                        intent.putExtra("descargaCatalogo", true);
                                        intent.putExtra("nombreEmpleado", nombreEmpleado);
                                        intent.putExtra("numeroEmpleado", numeroEmpleado);
                                        intent.putExtra("nombreTienda", respuestaValidaPedido.getTiendaId() + " " + respuestaValidaPedido.getNombreTienda());
                                        editTextFolio.setText("");
                                        startActivity(intent);
                                    } else if (respuestaValidaPedido.isPedidoValido() != "true" && (respuestaValidaPedido.getListaZonas() != null && respuestaValidaPedido.getListaZonas().length > 0)) {
                                        StringBuffer mensaje = new StringBuffer();
                                        mensaje.append("Pedido no válido:");
                                        mensaje.append("\n");

                                        if (respuestaValidaPedido.getListaZonas().length == 1 && respuestaValidaPedido.getListaZonas()[0].getEstatus().equals("NA")) {
                                            mensaje.append("\n");
                                            mensaje.append(respuestaValidaPedido.getMensaje());
                                        } else {
                                            for (int i = 0; i < respuestaValidaPedido.getListaZonas().length; i++) {
                                                mensaje.append("* " + respuestaValidaPedido.getListaZonas()[i].getZona());
                                                mensaje.append(" - " + respuestaValidaPedido.getListaZonas()[i].getEstatus());
                                                mensaje.append(" - " + respuestaValidaPedido.getListaZonas()[i].getUsuario());
                                                mensaje.append("\n");
                                            }
                                        }

                                        ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                                        alert.showDialog(CargaFolioPedidoActivity.this, mensaje.toString(), null,
                                                TiposAlert.CORRECTO);
                                    } else {
                                        if (bandera == true) {
                                            ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);

                                            alert.showDialog(CargaFolioPedidoActivity.this, "Pedido no válido: " +
                                                            respuestaValidaPedido.getMensaje() + "\n\n* Para mayor información puedes preguntar a tu CD o a soporte técnico de neto", null,
                                                    TiposAlert.CORRECTO);
                                        }
                                    }
                                    bandera = false;
                                    editTextFolio.setText("");
                                } else {
                                    if (bandera == true) {
                                        mDialog.dismiss();
                                        editTextFolio.setText("");
                                        System.out.println("*** 2 ***");
                                        System.out.println("////////////////////////////////////////////Error al consumir servicio//////////////////////////////////////////////");
                                        //cuando el tiempo del servicio exedio el timeout
                                        ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                                        alert.showDialog(CargaFolioPedidoActivity.this, "Error al consumir el servicio que valida el folio del pedido", null, TiposAlert.ERROR);
                                    }
                                    bandera = true;
                                }
                            }
                        });
                    }else{
                        ProviderValidaPedido.getInstance(this).getValidaPedido(request, new ProviderValidaPedido.interfaceValidaPedido() {
                            @Override
                            public void resolver(ValidaPedidoVO respuestaValidaPedido) {
                                System.out.println("Valida 1 RESPONSE: " + respuestaValidaPedido);

                                mDialog.dismiss();

                                if (respuestaValidaPedido != null && bandera == true) {
                                    bandera= false;

                                    if (respuestaValidaPedido.isPedidoValido().equals("true")) {
                                        System.out.println("///////////////////////////////////////////Entra a correr ZonasDisponibles////////////////////////");
                                        Intent intent = new Intent(context, CargaCodigosBarraActivity.class);
                                        intent.putExtra("folio", editTextFolio.getText().toString());
                                        intent.putExtra("listaZonasVerificado", (Serializable) respuestaValidaPedido.getListaZonasVerificado());
                                        intent.putExtra("descargaCatalogo", true);
                                        intent.putExtra("nombreEmpleado", nombreEmpleado);
                                        intent.putExtra("numeroEmpleado", numeroEmpleado);
                                        intent.putExtra("nombreTienda", respuestaValidaPedido.getTiendaId() + " " + respuestaValidaPedido.getNombreTienda());
                                        editTextFolio.setText("");
                                        startActivity(intent);
                                    } else if (respuestaValidaPedido.isPedidoValido() != "true" && (respuestaValidaPedido.getListaZonas() != null && respuestaValidaPedido.getListaZonas().length > 0)) {
                                        StringBuffer mensaje = new StringBuffer();
                                        mensaje.append("Pedido no válido:");
                                        mensaje.append("\n");

                                        if (respuestaValidaPedido.getListaZonas().length == 1 && respuestaValidaPedido.getListaZonas()[0].getEstatus().equals("NA")) {
                                            mensaje.append("\n");
                                            mensaje.append(respuestaValidaPedido.getMensaje());
                                        } else {
                                            for (int i = 0; i < respuestaValidaPedido.getListaZonas().length; i++) {
                                                mensaje.append("* " + respuestaValidaPedido.getListaZonas()[i].getZona());
                                                mensaje.append(" - " + respuestaValidaPedido.getListaZonas()[i].getEstatus());
                                                mensaje.append(" - " + respuestaValidaPedido.getListaZonas()[i].getUsuario());
                                                mensaje.append("\n");
                                            }
                                        }

                                        ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                                        alert.showDialog(CargaFolioPedidoActivity.this, mensaje.toString(), null,
                                                TiposAlert.CORRECTO);
                                    } else {
                                        ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                                        bandera=true;
                                        alert.showDialog(CargaFolioPedidoActivity.this, "Pedido no válido: " +
                                                        respuestaValidaPedido.getMensaje() + "\n\n* Para mayor información puedes preguntar a tu CD o a soporte técnico de neto", null,
                                                TiposAlert.CORRECTO);
                                    }
                                    editTextFolio.setText("");
                                } else {

                                    mDialog.dismiss();
                                    editTextFolio.setText("");
                                    System.out.println("*** 2 ***");
                                    System.out.println("////////////////////////////////////////////Error al consumir servicio//////////////////////////////////////////////");
                                    //cuando el tiempo del servicio exedio el timeout
                                    ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                                    alert.showDialog(CargaFolioPedidoActivity.this, "Error al consumir el servicio que valida el folio del pedido", null, TiposAlert.ERROR);
                                    bandera = true;
                                }
                            }
                        });
                    }














                    /*String url = Constantes.URL_STRING + "getValidaPedidoVerificador";

                    StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    mDialog.dismiss();
                                    //EditText editText = (EditText) findViewById(R.id.folioPedidoText);
                                    System.out.println("*** 1 *** response: " + response);

                                    ValidaPedidoVO validaPedidoVO = new ValidaPedidoVO();
                                    generaRespuesta(response, validaPedidoVO);

                                    if(validaPedidoVO.isPedidoValido()) {
                                        Intent intent = new Intent(context, CargaCodigosBarraActivity.class);
                                        intent.putExtra("folio", editTextFolio.getText().toString());
                                        intent.putExtra("listaZonasVerificado", (Serializable) validaPedidoVO.getListaZonasVerificado());
                                        intent.putExtra("descargaCatalogo", true);
                                        intent.putExtra("nombreEmpleado", nombreEmpleado);
                                        intent.putExtra("numeroEmpleado", numeroEmpleado);
                                        intent.putExtra("nombreTienda", validaPedidoVO.getTiendaId() + " " + validaPedidoVO.getNombreTienda());
//                                        intent.putExtra("idZona", 1);
//                                        intent.putExtra("nombreZona", "Todas las zonas");
                                        editTextFolio.setText("");
                                        startActivity(intent);
                                    } else if(!validaPedidoVO.isPedidoValido() && (validaPedidoVO.getListaZonas() != null && validaPedidoVO.getListaZonas().length > 0)) {
                                        StringBuffer mensaje = new StringBuffer();
                                        mensaje.append("Pedido no válido:");
                                        mensaje.append("\n");

                                        if(validaPedidoVO.getListaZonas().length == 1 && validaPedidoVO.getListaZonas()[0].getEstatus().equals("NA")) {
                                            mensaje.append("\n");
                                            mensaje.append(validaPedidoVO.getMensaje());
                                        } else {
                                            for(int i = 0; i < validaPedidoVO.getListaZonas().length; i++) {
                                                mensaje.append("* " + validaPedidoVO.getListaZonas()[i].getZona());
                                                mensaje.append(" - " + validaPedidoVO.getListaZonas()[i].getEstatus());
                                                mensaje.append(" - " + validaPedidoVO.getListaZonas()[i].getUsuario());
                                                mensaje.append("\n");
                                            }
                                        }

                                        ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                                        alert.showDialog(CargaFolioPedidoActivity.this, mensaje.toString(), null,
                                                TiposAlert.CORRECTO);
                                    } else {
                                        ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                                        alert.showDialog(CargaFolioPedidoActivity.this, "Pedido no válido: " +
                                                        validaPedidoVO.getMensaje() + "\n\n* Para mayor información puedes preguntar a tu CD o a soporte técnico de neto", null,
                                                TiposAlert.CORRECTO);
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    mDialog.dismiss();
                                    //EditText editText = (EditText) findViewById(R.id.folioPedidoText);
                                    editTextFolio.setText("");
                                    System.out.println("*** 2 ***");
                                    ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                                    alert.showDialog(CargaFolioPedidoActivity.this, "Error al consumir el servicio que valida el folio del pedido ", null, TiposAlert.ERROR);
                                    editTextFolio.setText("");
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            //EditText editText = (EditText) findViewById(R.id.folioPedidoText);

                            String serieEquipo = Build.SERIAL;

                            Map<String, String> params = new HashMap<String, String>();
                            params.put("folio", editTextFolio.getText().toString().trim());
                            params.put("numeroSerie", serieEquipo);
                            params.put("version", version);
                            params.put("usuario", numeroEmpleado);

                            return params;
                        }
                    };

                    strRequest.setRetryPolicy(new DefaultRetryPolicy(
                            50000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    AppController.getInstance().addToRequestQueue(strRequest, "tag");*/
                } else {
                    ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                    alert.showDialog(CargaFolioPedidoActivity.this, "Escanea un folio de pedido", null, TiposAlert.ALERT);
                }
            } catch(Exception me) {
                ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
                alert.showDialog(CargaFolioPedidoActivity.this, "URL no disponible: favor de validar la conexión a Internet", null, TiposAlert.ERROR);
            }
        } else {
            // Mostrar errores
            //EditText editText = (EditText) findViewById(R.id.folioPedidoText);
//            editText.setInputType(InputType.TYPE_NULL);
            editTextFolio.requestFocus();
            editTextFolio.setText("");
            ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
            alert.showDialog(CargaFolioPedidoActivity.this, "No hay conexión HTTP", null, TiposAlert.ERROR);
        }
    }

    public long generaLlave(ValidaPedidoVO validaPedidoVO) {
        //EditText editText = (EditText) findViewById(R.id.folioPedidoText);
        return  Long.parseLong(editTextFolio.getText().toString().trim()) + validaPedidoVO.getTiendaId() + validaPedidoVO.getCedisId();
    }

    /*public void generaRespuesta(String response, ValidaPedidoVO validaPedidoVO) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader(response) );

            int eventType = xpp.getEventType();
            List<ZonaVerificadoVO> listaZonasVerificado = new ArrayList<ZonaVerificadoVO>();
            ZonaVerificadoVO zonaVerificadoVO = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("errorCode")) {
                        eventType = xpp.next(); // advance to inner text
                        validaPedidoVO.setCodigo(Integer.parseInt(xpp.getText()));
                    }  else if (xpp.getName().equals("errorDesc")) {
                        eventType = xpp.next(); // advance to inner text
                        validaPedidoVO.setMensaje(xpp.getText());
                    } else if (xpp.getName().equals("cedis")) {
                        eventType = xpp.next(); // advance to inner text
                        validaPedidoVO.setNombreCedis(xpp.getText());
                    } else if (xpp.getName().equals("cedisId")) {
                        eventType = xpp.next(); // advance to inner text
                        validaPedidoVO.setCedisId(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("llave")) {
                        eventType = xpp.next(); // advance to inner text
                        validaPedidoVO.setLlave(xpp.getText());
                    } else if (xpp.getName().equals("pedidoValido")) {
                        eventType = xpp.next(); // advance to inner text
                        if(xpp.getText().equals("true")) {
                            validaPedidoVO.setPedidoValido(true);
                        } else {
                            validaPedidoVO.setPedidoValido(false);
                        }
                    } else if (xpp.getName().equals("requiereLlave")) {
                        eventType = xpp.next(); // advance to inner text
                        if(xpp.getText().equals("true")) {
                            validaPedidoVO.setRequiereLlave(true);
                        } else {
                            validaPedidoVO.setRequiereLlave(false);
                        }
                    } else if (xpp.getName().equals("tienda")) {
                        eventType = xpp.next(); // advance to inner text
                        validaPedidoVO.setNombreTienda(xpp.getText());
                    } else if (xpp.getName().equals("tiendaId")) {
                        eventType = xpp.next(); // advance to inner text
                        validaPedidoVO.setTiendaId(Integer.parseInt(xpp.getText()));
                    } else if (xpp.getName().equals("listaZonasVerificado")) {
                        zonaVerificadoVO = new ZonaVerificadoVO();
                    } else if(xpp.getName().equals("zonaId")) {
                        eventType = xpp.next(); // advance to inner text
                        zonaVerificadoVO.setIdZona(Integer.parseInt(xpp.getText()));
                    } else if(xpp.getName().equals("descripcionZona")) {
                        eventType = xpp.next(); // advance to inner text
                        zonaVerificadoVO.setDescripcionZona(xpp.getText());
                    } else if(xpp.getName().equals("esZonaValida")) {
                        eventType = xpp.next(); // advance to inner text
                        zonaVerificadoVO.setZonaValida(Integer.parseInt(xpp.getText()));
                    } else if(xpp.getName().equals("usuarioConteo")) {
                        eventType = xpp.next(); // advance to inner text
                        zonaVerificadoVO.setNombreUsuario(xpp.getText());
                    } else if(xpp.getName().equals("estatusConteoTransferenciaId")) {
                        eventType = xpp.next(); // advance to inner text
                        zonaVerificadoVO.setEstatusZona(Integer.parseInt(xpp.getText()));
                    } else if(xpp.getName().equals("porcentajeMinimoVerificado")) {
                        eventType = xpp.next(); // advance to inner text
                        zonaVerificadoVO.setPorcentaje(Integer.parseInt(xpp.getText()));
                    } else if(xpp.getName().equals("nombreCorto")) {
                        eventType = xpp.next(); // advance to inner text
                        zonaVerificadoVO.setNombreCorto(xpp.getText());
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    if(xpp.getName().equals("listaZonasVerificado")) {
                        listaZonasVerificado.add(zonaVerificadoVO);
                    }
                }
                eventType = xpp.next();
            }
            validaPedidoVO.setListaZonasVerificado(listaZonasVerificado);
        } catch(Exception e) {
            ViewDialog alert = new ViewDialog(CargaFolioPedidoActivity.this);
            alert.showDialog(CargaFolioPedidoActivity.this, "Error al formar las diferencias del xml: " + e.getMessage(), null, TiposAlert.ERROR);
        }
    }*/

    @Override
    public void onBackPressed() {
    }
}
