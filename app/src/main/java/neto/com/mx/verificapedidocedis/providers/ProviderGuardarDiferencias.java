package neto.com.mx.verificapedidocedis.providers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import neto.com.mx.verificapedidocedis.beans.CodigosGuardadosVO;
import neto.com.mx.verificapedidocedis.beans.RespuestaIncidenciasVO;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.Util;

import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GUARDARDIFERENCIASVERIFICADO;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACE;


public class ProviderGuardarDiferencias {
    final String TAG = "ProviderGuardaDiferencias";
    private static ProviderGuardarDiferencias instance;
    private Context context;
    Util u = new Util();
    public static ProviderGuardarDiferencias getInstance(Context context) {
        if (instance == null) {
            instance = new ProviderGuardarDiferencias();
        }
        instance.context = context;
        return instance;
    }

    public void getGuardarDiferencias(final SoapObject request, final interfaceGuardarDiferencias promise) {


        (new AsyncTask<Void, Void, RespuestaIncidenciasVO>() {
            RespuestaIncidenciasVO respuestaGuardaDiferencias = null;

            @Override
            protected RespuestaIncidenciasVO doInBackground(Void... voids) {
                try {
                    SoapSerializationEnvelope sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    sse.dotNet = true;
                    sse.setOutputSoapObject(request);

                    HttpTransportSE transportSE = new HttpTransportSE(Constantes.URL_STRING,60000);
                    transportSE.call(NAMESPACE + METHOD_NAME_GUARDARDIFERENCIASVERIFICADO, sse);


                    SoapObject response = (SoapObject) sse.getResponse();
                    Log.d(TAG, response.toString());
                    respuestaGuardaDiferencias = u.parseRespuestaGuardaDiferencias(response,context);
                    return respuestaGuardaDiferencias;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "error IOExepction");
                }
                return respuestaGuardaDiferencias;
            }


            @Override
            protected void onPostExecute(RespuestaIncidenciasVO respuestaGuardaDiferencias) {
                promise.resolver(respuestaGuardaDiferencias);
            }
        }).execute();
    }

    public interface interfaceGuardarDiferencias {
        void resolver(RespuestaIncidenciasVO respuestaGuardaDiferencias);
    }


}
