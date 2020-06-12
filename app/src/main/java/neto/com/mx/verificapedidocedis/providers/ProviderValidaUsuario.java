package neto.com.mx.verificapedidocedis.providers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import neto.com.mx.verificapedidocedis.beans.UsuarioVO;
import neto.com.mx.verificapedidocedis.utiles.Util;

import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_VALIDAUSUARIO;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACEVALIDAUSUARIO;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.URL_STRING_LOGIN;

public class ProviderValidaUsuario {
    final String TAG = "ProviderValidaUsuario";
    private static ProviderValidaUsuario instance;
    private Context context;
    Util u = new Util();
    public static ProviderValidaUsuario getInstance(Context context) {
        if (instance == null) {
            instance = new ProviderValidaUsuario();
        }
        instance.context = context;
        return instance;
    }

    public void getValidaUsuario(final SoapObject request, final interfaceValidaUsuario promise) {


        (new AsyncTask<Void, Void, UsuarioVO>() {
            UsuarioVO respuestaValidaUsuario = null;

            @Override
            protected UsuarioVO doInBackground(Void... voids) {
                try {
                    SoapSerializationEnvelope sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    sse.dotNet = true;
                    sse.setOutputSoapObject(request);

                    HttpTransportSE transportSE = new HttpTransportSE(URL_STRING_LOGIN,60000);
                    transportSE.call(NAMESPACEVALIDAUSUARIO + METHOD_NAME_VALIDAUSUARIO, sse);


                    SoapObject response = (SoapObject) sse.getResponse();
                    System.out.println("//////////////////////////////////Response:"+response);
                    Log.d(TAG, response.toString());
                    respuestaValidaUsuario = u.parseRespuestaValidaUsuario(response,context);
                    return respuestaValidaUsuario;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "error IOExepction");
                }
                return respuestaValidaUsuario;
            }


            @Override
            protected void onPostExecute(UsuarioVO respuestaValidaUsuario) {
                promise.resolver(respuestaValidaUsuario);
            }
        }).execute();
    }

    public interface interfaceValidaUsuario {
        void resolver(UsuarioVO respuestaValidaUsuario);
    }


}
