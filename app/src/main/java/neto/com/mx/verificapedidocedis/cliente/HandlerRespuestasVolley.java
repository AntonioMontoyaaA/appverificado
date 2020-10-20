package neto.com.mx.verificapedidocedis.cliente;

import com.android.volley.VolleyError;



import neto.com.mx.verificapedidocedis.mensajes.RespuestaDinamica;


/**
 * Created by dramirezr on 18/01/2018.
 */

public interface HandlerRespuestasVolley {
    public void manejarExitoVolley(RespuestaDinamica respuesta);
    public void manejarErrorVolley(VolleyError error);
}
