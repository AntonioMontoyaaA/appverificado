package neto.com.mx.verificapedidocedis.providers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import neto.com.mx.verificapedidocedis.beans.RespuestaIncidenciasVO;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.Util;

import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GUARDARDIFERENCIASVERIFICADO;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACE;


public class ProviderGuardarDiferencias_CargaCodigosBarra {
    final String TAG = "ProvGuarDif_CargaCB";
    private static ProviderGuardarDiferencias_CargaCodigosBarra instance;
    private Context context;
    Util u = new Util();
    public static ProviderGuardarDiferencias_CargaCodigosBarra getInstance(Context context) {
        if (instance == null) {
            instance = new ProviderGuardarDiferencias_CargaCodigosBarra();
        }
        instance.context = context;
        return instance;
    }

    public void getGuardarDiferencias_CargaCodigosBarra(final SoapObject request, final interfaceGuardarDiferencias_CargaCodigosBarra promise) {


        (new AsyncTask<Void, Void, RespuestaIncidenciasVO>() {
            RespuestaIncidenciasVO respuestaGuardaDiferencias_CargaCodigosBarra = null;

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
                    respuestaGuardaDiferencias_CargaCodigosBarra = u.parseRespuestaGuardaDiferencias_CargaCodigosBarra(response,context);
                    return respuestaGuardaDiferencias_CargaCodigosBarra;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "error IOExepction");
                }
                return respuestaGuardaDiferencias_CargaCodigosBarra;
            }


            @Override
            protected void onPostExecute(RespuestaIncidenciasVO respuestaGuardaDiferencias_CargaCodigosBarra) {
                promise.resolver(respuestaGuardaDiferencias_CargaCodigosBarra);
            }
        }).execute();
    }

    public interface interfaceGuardarDiferencias_CargaCodigosBarra {
        void resolver(RespuestaIncidenciasVO respuestaGuardaDiferencias_CargaCodigosBarra);
    }


}
