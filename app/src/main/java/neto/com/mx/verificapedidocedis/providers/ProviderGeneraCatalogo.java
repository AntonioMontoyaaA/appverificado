package neto.com.mx.verificapedidocedis.providers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.List;
import java.util.Map;

import neto.com.mx.verificapedidocedis.beans.ArticuloVO;
import neto.com.mx.verificapedidocedis.beans.CatalogoArticulosVO;
import neto.com.mx.verificapedidocedis.utiles.Constantes;
import neto.com.mx.verificapedidocedis.utiles.Util;

import static neto.com.mx.verificapedidocedis.utiles.Constantes.METHOD_NAME_GETCATALOGOARTICULOSVERIFICADORGENERAL;
import static neto.com.mx.verificapedidocedis.utiles.Constantes.NAMESPACE;

public class ProviderGeneraCatalogo {
    final String TAG = "ProviderGeneraCatalogo";
    private static ProviderGeneraCatalogo instance;
    private Context context;
    Util u = new Util();
    public static ProviderGeneraCatalogo getInstance(Context context) {
        if (instance == null) {
            instance = new ProviderGeneraCatalogo();
        }
        instance.context = context;
        return instance;
    }

    public void getGeneraCatalogo(final SoapObject request, final interfaceGeneraCatalogo promise ) {


        (new AsyncTask<Void, Void, CatalogoArticulosVO>() {
            CatalogoArticulosVO respuestaGeneraCatalogo = null;

            @Override
            protected CatalogoArticulosVO doInBackground(Void... voids) {
                try {
                    SoapSerializationEnvelope sse = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    sse.dotNet = true;
                    sse.setOutputSoapObject(request);

                    HttpTransportSE transportSE = new HttpTransportSE(Constantes.URL_STRING,60000);
                    transportSE.call(NAMESPACE + METHOD_NAME_GETCATALOGOARTICULOSVERIFICADORGENERAL, sse);


                    SoapObject response = (SoapObject) sse.getResponse();
                    System.out.println("//////////////////////////////////Response_ProviderGeneraCatalogo: "+response);
                    Log.d(TAG, response.toString());
                    respuestaGeneraCatalogo = u.parseRespuestaGeneraCatalogo(response, context);
                    return respuestaGeneraCatalogo;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "error IOExepction");
                }
                return respuestaGeneraCatalogo;
            }


            @Override
            protected void onPostExecute(CatalogoArticulosVO respuestaGeneraCatalogo) {
                promise.resolver(respuestaGeneraCatalogo);
            }
        }).execute();
    }

    public interface interfaceGeneraCatalogo {
        void resolver(CatalogoArticulosVO respuestaGeneraCatalogo);

        Map<String, String> getHeaders() throws AuthFailureError;
    }


}
