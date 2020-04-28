package neto.com.mx.verificapedidocedis.providers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


import neto.com.mx.verificapedidocedis.beans.CodigosGuardadosVO;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.Util;

import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GUARDARARTSCONTADOSVERIFICADOR;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACE;


public class ProviderGuardarArticulos {
    final String TAG = "ProviderGuardaArticulos";
    private static ProviderGuardarArticulos instance;
    private Context context;
    Util u = new Util();
    public static ProviderGuardarArticulos getInstance(Context context) {
        if (instance == null) {
            instance = new ProviderGuardarArticulos();
        }
        instance.context = context;
        return instance;
    }

    public void getGuardarArticulos(final SoapObject request, final interfaceGuardarArticulos promise) {


        (new AsyncTask<Void, Void, CodigosGuardadosVO>() {
            CodigosGuardadosVO respuestaGuardaArticulos = null;

            @Override
            protected CodigosGuardadosVO doInBackground(Void... voids) {
                try {
                    SoapSerializationEnvelope sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    sse.dotNet = true;
                    sse.setOutputSoapObject(request);

                    HttpTransportSE transportSE = new HttpTransportSE(Constantes.URL_STRING,60000);
                    transportSE.call(NAMESPACE + METHOD_NAME_GUARDARARTSCONTADOSVERIFICADOR, sse);


                    SoapObject response = (SoapObject) sse.getResponse();
                    Log.d(TAG, response.toString());
                    respuestaGuardaArticulos = u.parseRespuestaGuardaArticulos(response,context);
                    return respuestaGuardaArticulos;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "error IOExepction");
                }
                return respuestaGuardaArticulos;
            }


            @Override
            protected void onPostExecute(CodigosGuardadosVO respuestaGuardaArticulos) {
                promise.resolver(respuestaGuardaArticulos);
            }
        }).execute();
    }

    public interface interfaceGuardarArticulos {
        void resolver(CodigosGuardadosVO respuestaGuardaArticulos);
    }


}
