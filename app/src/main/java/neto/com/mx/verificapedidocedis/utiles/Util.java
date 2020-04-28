package neto.com.mx.verificapedidocedis.utiles;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import neto.com.mx.verificapedidocedis.beans.CodigoBarraVO;
import neto.com.mx.verificapedidocedis.beans.CodigosGuardadosVO;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog;

/**
 * Created by yruizm on 21/10/16.
 */

public class Util {
    private final String TAG = "Util";

    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        ;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
        return properties.getProperty(key);

    }

    public CodigosGuardadosVO parseRespuestaGuardaArticulos(SoapObject servicio, Context context) {
        final String str = servicio.toString();
        Log.d(TAG, "parseRespuestaGuardaArticulos: " + str);
        CodigosGuardadosVO item = new CodigosGuardadosVO();
        ArrayList<CodigoBarraVO> listaCodigosFaltantes;
        int count = servicio.getPropertyCount();

        if (str.contains("Error") || servicio.equals(null)) {
            SoapPrimitive coidgo = (SoapPrimitive) servicio.getProperty("errorCode");
            SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
            item.setCodigo(Integer.valueOf((String) coidgo.getValue()));
            item.setMensaje((String) mensaje.getValue());

        } else {
            if (count > 0) {
                listaCodigosFaltantes = new ArrayList<>();
                try {
                    for (int i = 0; i < count; i++) {
                        CodigoBarraVO items = new CodigoBarraVO();
                        try {
                            if (!servicio.getPropertyAsString(i).contains("articulosConDiferencia")) {
                                SoapObject pojoSoap = null;
                                try {
                                    pojoSoap = (SoapObject) servicio.getProperty(i);

                                } catch (Exception e) {
                                    Log.d(TAG, "parseRespuestaGuardaArticulos: pojo es nulo");
                                }
                                if (pojoSoap != null) {
                                    SoapPrimitive articuloId = (SoapPrimitive) pojoSoap.getProperty("articuloId");
                                    SoapPrimitive codigobarras = (SoapPrimitive) pojoSoap.getProperty("codigobarras");
                                    SoapPrimitive nombre = (SoapPrimitive) pojoSoap.getProperty("nombre");
                                    SoapPrimitive cantidadAsignada = (SoapPrimitive) pojoSoap.getProperty("cantidadAsignada");
                                    SoapPrimitive cantidadVerificada = (SoapPrimitive) pojoSoap.getProperty("cantidadVerificada");

                                    items.setArticuloId(Long.valueOf((String) articuloId.getValue()));
                                    items.setCodigoBarras((String) codigobarras.getValue());
                                    items.setNombreArticulo((String) nombre.getValue());
                                    items.setCajasAsignadas(Integer.valueOf((String) cantidadAsignada.getValue()));
                                    items.setCajasVerificadas(Integer.valueOf((String) cantidadVerificada.getValue()));
                                    listaCodigosFaltantes.add(items);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    item.setArticulosDiferencias(listaCodigosFaltantes.toArray(new CodigoBarraVO[listaCodigosFaltantes.size()]));
                    SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
                    SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
                    SoapPrimitive articulosAsignados = (SoapPrimitive) servicio.getProperty("articulosAsignados");
                    SoapPrimitive articulosVerificados = (SoapPrimitive) servicio.getProperty("articulosVerificados");
                    SoapPrimitive estatusPedido = (SoapPrimitive) servicio.getProperty("estatusPedido");
                    SoapPrimitive totalCajasAsignadas = (SoapPrimitive) servicio.getProperty("totalCajasAsignadas");
                    SoapPrimitive totalCajasVerificadas = (SoapPrimitive) servicio.getProperty("totalCajasVerificadas");
                    try {
                        item.setCodigo(codigo.getValue() != null ? Integer.valueOf((String) codigo.getValue()) : 1);
                    } catch (Exception e) {
                        item.setCodigo(1);
                    }
                    try {
                        item.setMensaje(mensaje.getValue() != null ? (String) mensaje.getValue() : " ");
                    } catch (Exception e) {
                        item.setMensaje(" ");
                    }
                    try {
                        item.setTotalArticulosAsignados(articulosAsignados.getValue() != null ? Integer.valueOf((String) articulosAsignados.getValue()) : 0);
                    } catch (Exception e) {
                        item.setTotalArticulosAsignados(0);
                    }
                    try {
                        item.setTotalArticulosVerificados(articulosVerificados.getValue() != null ? Integer.valueOf((String) articulosVerificados.getValue()) : 0);
                    } catch (Exception e) {
                        item.setTotalArticulosVerificados(0);
                    }
                    try {
                        item.setEstatusPedido(estatusPedido.getValue().equals(null) ? " " : (String) estatusPedido.getValue());
                    } catch (Exception e) {
                        item.setEstatusPedido(" ");
                    }
                    try {
                        item.setTotalCajasAsignadas(totalCajasAsignadas.getValue() != null ? Integer.valueOf((String) totalCajasAsignadas.getValue()) : 0);
                    } catch (Exception e) {
                        item.setTotalCajasAsignadas(0);
                    }
                    try {
                        item.setTotalCajasVerificadas(totalCajasVerificadas.getValue() != null ? Integer.valueOf((String) totalCajasVerificadas.getValue()) : 0);
                    } catch (Exception e) {
                        item.setTotalCajasVerificadas(0);
                    }
                } catch (Exception e) {
                    ViewDialog alert = new ViewDialog(context);
                    alert.showDialog((Activity) context, e.getMessage(), null, TiposAlert.ALERT);
                }
            }
        }
        return item;
    }

}
