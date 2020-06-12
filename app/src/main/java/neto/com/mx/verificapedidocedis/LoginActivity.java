package neto.com.mx.verificapedidocedis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import neto.com.mx.verificapedidocedis.beans.UsuarioVO;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog;
import neto.com.mx.verificapedidocedis.providers.ProviderValidaUsuario;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.TiposAlert;

import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_VALIDAUSUARIO;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACEVALIDAUSUARIO;

public class LoginActivity extends AppCompatActivity {

    private String usuario;
    private String pass;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ImageView logoImage = (ImageView) findViewById(R.id.logoImage);
        logoImage.animate().setDuration(2000);
        logoImage.animate().alpha(1f);

        Button loginBoton = (Button) findViewById(R.id.loginBoton);
        loginBoton.animate().setDuration(2000);
        loginBoton.animate().alpha(1f);
    }

    public void iniciaLogueo(View view) {
        view.startAnimation(buttonClick);
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        EditText usuarioText = (EditText) findViewById(R.id.usuarioText);
        usuario = usuarioText.getText().toString();
//        usuario = "703021";

        EditText passwordText = (EditText) findViewById(R.id.passwordText);
        pass = passwordText.getText().toString();
//        pass = "703021";

        if(usuario.equals("")) {
            ViewDialog alert = new ViewDialog(LoginActivity.this);
            alert.showDialog(LoginActivity.this, "Debes introducir tu usuario: ", null, TiposAlert.ERROR);
            return;
        }

        if(pass.equals("")) {
            ViewDialog alert = new ViewDialog(LoginActivity.this);
            alert.showDialog(LoginActivity.this, "Debes introducir tu password", null, TiposAlert.ERROR);
            return;
        }

        validaLogin();
    }

    public void validaLogin() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Operaciones http
            try {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Validando usuario...");
                mDialog.setCancelable(false);
                mDialog.setInverseBackgroundForced(false);
                mDialog.show();


                SoapObject request = new SoapObject(NAMESPACEVALIDAUSUARIO,METHOD_NAME_VALIDAUSUARIO);

                request.addProperty("usuario", usuario);
                request.addProperty("password", convierteMD5(pass));
                request.addProperty("idApp", Constantes.ID_APP_VERIFICADOR);

                ProviderValidaUsuario.getInstance(this).getValidaUsuario(request, new ProviderValidaUsuario.interfaceValidaUsuario() {
                    @Override
                    public void resolver(UsuarioVO respuestaValidaUsuario) {

                        mDialog.dismiss();
                        System.out.println("*** 1 *** " + respuestaValidaUsuario);



                        if (respuestaValidaUsuario != null) {
                            if (respuestaValidaUsuario.isEmpleadoValido().equals("true")) {
                                Intent intent = new Intent(getApplicationContext(), CargaFolioPedidoActivity.class);
                                intent.putExtra("numeroEmpleado", usuario);
                                intent.putExtra("nombreEmpleado", respuestaValidaUsuario.getNombreEmpleado());
                                startActivity(intent);
                            } else {
                                ViewDialog alert = new ViewDialog(LoginActivity.this);
                                alert.showDialog(LoginActivity.this, respuestaValidaUsuario.getMensaje(), null, TiposAlert.ERROR);
                            }
                        } else {
                            mDialog.dismiss();
                            System.out.println("*** 2 ***");
                            //cuando el tiempo del servicio exedio el timeout
                            ViewDialog alert = new ViewDialog(LoginActivity.this);
                            alert.showDialog(LoginActivity.this, "Error al consumir el servicio que valida el usuario", null, TiposAlert.ERROR);
                        }

                    }
                });












                /*String url = Constantes.URL_STRING_LOGIN + "validaUsuario";


                StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                mDialog.dismiss();
                                System.out.println("*** 1 *** " + response);

                                UsuarioVO usuarioVO = new UsuarioVO();
                                usuarioVO.setNumeroEmpleado(Integer.parseInt(usuario));

                                generaRespuesta(response, usuarioVO);

                                if(usuarioVO.isEmpleadoValido()) {
                                    Intent intent = new Intent(getApplicationContext(), CargaFolioPedidoActivity.class);
                                    intent.putExtra("numeroEmpleado", usuario);
                                    intent.putExtra("nombreEmpleado", usuarioVO.getNombreEmpleado());
                                    startActivity(intent);
                                } else {
                                    ViewDialog alert = new ViewDialog(LoginActivity.this);
                                    alert.showDialog(LoginActivity.this, usuarioVO.getMensaje(), null, TiposAlert.ERROR);
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                mDialog.dismiss();
                                System.out.println("*** 2 ***");

                                ViewDialog alert = new ViewDialog(LoginActivity.this);
                                alert.showDialog(LoginActivity.this, "Error al validar el usuario: " + error.toString(), null, TiposAlert.ERROR);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("usuario", usuario);
                        params.put("password", convierteMD5(pass));
                        params.put("idApp", Constantes.ID_APP_VERIFICADOR);
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

            } catch(Exception me) {
                ViewDialog alert = new ViewDialog(LoginActivity.this);
                alert.showDialog(LoginActivity.this, "URL no disponible: " + me.getMessage(), null, TiposAlert.ERROR);
            }
        } else {
            // Mostrar errores
            ViewDialog alert = new ViewDialog(LoginActivity.this);
            alert.showDialog(LoginActivity.this, "No hay conexión HTTP", null, TiposAlert.ERROR);
        }
    }

    /*public void generaRespuesta(String response, UsuarioVO usuarioVO) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader(response) );

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("codigo")) {
                        eventType = xpp.next(); // advance to inner text
                        usuarioVO.setCodigo(Integer.parseInt(xpp.getText()));
                    }  else if (xpp.getName().equals("mensaje")) {
                        eventType = xpp.next(); // advance to inner text
                        usuarioVO.setMensaje(xpp.getText());
                    } else if (xpp.getName().equals("nombreUsuario")) {
                        eventType = xpp.next(); // advance to inner text
                        usuarioVO.setNombreEmpleado(xpp.getText());
                    } else if (xpp.getName().equals("esUsuarioValido")) {
                        eventType = xpp.next(); // advance to inner text
                        if(xpp.getText().equals("true")) {
                            usuarioVO.setEmpleadoValido(true);
                        } else {
                            usuarioVO.setEmpleadoValido(false);
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch(Exception e) {
            ViewDialog alert = new ViewDialog(LoginActivity.this);
            alert.showDialog(LoginActivity.this, "Error al leer el estatus del usuario del xml: " + e.getMessage(), null, TiposAlert.ERROR);
        }
    }*/

    public String convierteMD5(String pass) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(pass.getBytes());
            byte messageDigest[] = digest.digest();

            return bytesToHex(messageDigest);

            // Create Hex String
            /*StringBuffer hexString = new StringBuffer();
            for (int i=0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();*/

        } catch (NoSuchAlgorithmException e) {
            ViewDialog alert = new ViewDialog(LoginActivity.this);
            alert.showDialog(LoginActivity.this, "Imposible cifrar el parámetro de entrada", null, TiposAlert.ERROR);
        }
        return "";
    }

    private static String bytesToHex (byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    @Override
    public void onBackPressed() {

    }
}
