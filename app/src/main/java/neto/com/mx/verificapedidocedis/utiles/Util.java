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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import neto.com.mx.verificapedidocedis.CargaCodigosBarraActivity;
import neto.com.mx.verificapedidocedis.ConteoDiferenciasActivity;
import neto.com.mx.verificapedidocedis.beans.ArticuloVO;
import neto.com.mx.verificapedidocedis.beans.CodigoBarraVO;
import neto.com.mx.verificapedidocedis.beans.CodigosGuardadosVO;
import neto.com.mx.verificapedidocedis.beans.RespuestaIncidenciasVO;
import neto.com.mx.verificapedidocedis.beans.UsuarioVO;
import neto.com.mx.verificapedidocedis.beans.ValidaPedidoVO;
import neto.com.mx.verificapedidocedis.beans.ZonaVerificadoVO;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog;

import static neto.com.mx.verificapedidocedis.ConteoDiferenciasActivity.respuestaIncidencias;

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

    }/////////////////////////////////////////////ValidaUsuario//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public UsuarioVO parseRespuestaValidaUsuario(SoapObject servicio, Context context) {
        final String str = servicio.toString();
        Log.d(TAG, "parseRespuestaValidaUsuario: " + str);
        UsuarioVO item = new UsuarioVO();

        int count = servicio.getPropertyCount();

        if (count > 0) {
            try {
                SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("codigo");
                SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("mensaje");
                SoapPrimitive nombreUsuario = (SoapPrimitive) servicio.getProperty("nombreUsuario");
                SoapPrimitive esUsuarioValido = (SoapPrimitive) servicio.getProperty("esUsuarioValido");
                try {
                    item.setCodigo(codigo.getValue() != null ? Integer.valueOf((String) codigo.getValue()) : 1);
                    System.out.println("codigo ");
                } catch (Exception e) {
                    item.setCodigo(1);
                }
                try {
                    item.setMensaje(mensaje.getValue() != null ? (String) mensaje.getValue() : " ");
                    System.out.println("mensaje ");
                } catch (Exception e) {
                    item.setMensaje(" ");
                }
                try {
                    item.setNombreEmpleado(nombreUsuario.getValue() != null ? (String) nombreUsuario.getValue() : " ");
                    System.out.println("nombreUsuario ");
                } catch (Exception e) {
                    item.setNombreEmpleado(" ");
                }
                try {
                    item.setEmpleadoValido(esUsuarioValido.getValue() != null ? (String) esUsuarioValido.getValue().toString() : " ");
                    System.out.println("esUsuarioValido : " + esUsuarioValido.getValue().toString());
                } catch (Exception e) {
                    item.setEmpleadoValido(" ");
                }

            } catch (Exception e) {
                ViewDialog alert = new ViewDialog(context);
                alert.showDialog((Activity) context, e.getMessage(), null, TiposAlert.ALERT);
            }
        }
        return item;
    }


    /////////////////////////////////////////////ValidadaPedidos//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ValidaPedidoVO parseRespuestaValidaPedido(SoapObject servicio, Context context) {
        /*for (int i= 0;i<servicio.getPropertyCount();i ++){
            if (servicio.getProperty(i) == null){
                System.out.println("Servicio.getProperty: "+servicio.getProperty(i));
                servicio.setProperty(i,true);
                System.out.println("Servicio.getProperty Cambiado: "+servicio.getProperty(i));
            }else {
                System.out.println("Servicio.getProperty: " + servicio.getProperty(i));
            }
        }*/
        final String str = servicio.toString();
        Log.d(TAG, "parseRespuestaValidaPedido: " + str);
        ValidaPedidoVO item = new ValidaPedidoVO();
        ArrayList<ZonaVerificadoVO> listaZonaVerificado;
        int count = servicio.getPropertyCount();

        if (str.contains("Error") || servicio.equals(null)) {
            SoapPrimitive coidgo = (SoapPrimitive) servicio.getProperty("errorCode");
            SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
            item.setCodigo(Integer.valueOf((String) coidgo.getValue()));
            item.setMensaje((String) mensaje.getValue());

        } else {
            if (count > 0) {
                listaZonaVerificado = new ArrayList<>();
                try {
                    for (int i = 0; i < count; i++) {
                        ZonaVerificadoVO items = new ZonaVerificadoVO();
                        try {
                            if (servicio.getProperty(i)!= null) {
                                if (!servicio.getPropertyAsString(i).contains("listaZonasVerificado")) {
                                    SoapObject pojoSoap = null;
                                    try {
                                        pojoSoap = (SoapObject) servicio.getProperty(i);

                                    } catch (Exception e) {
                                        Log.d(TAG, "parseRespuestaValidaPedido: pojo es nulo");

                                    }
                                    if (pojoSoap != null) {
                                        SoapPrimitive zonaId = (SoapPrimitive) pojoSoap.getProperty("zonaId");
                                        SoapPrimitive descripcionZona = (SoapPrimitive) pojoSoap.getProperty("descripcionZona");
                                        SoapPrimitive nombreUsuario = (SoapPrimitive) pojoSoap.getProperty("usuarioConteo");
                                        SoapPrimitive zonaValida = (SoapPrimitive) pojoSoap.getProperty("esZonaValida");
                                        SoapPrimitive estatusZona = (SoapPrimitive) pojoSoap.getProperty("estatusConteoTransferenciaId");
                                        SoapPrimitive porcentaje = (SoapPrimitive) pojoSoap.getProperty("porcentajeMinimoVerificado");
                                        SoapPrimitive nombreCorto = (SoapPrimitive) pojoSoap.getProperty("nombreCorto");

                                        items.setIdZona(Integer.valueOf((String) zonaId.getValue()));
                                        items.setDescripcionZona((String) descripcionZona.getValue());
                                        items.setZonaValida(Integer.valueOf((String) zonaValida.getValue()));
                                        items.setNombreUsuario((String) nombreUsuario.getValue());
                                        items.setEstatusZona(Integer.valueOf((String) estatusZona.getValue()));
                                        items.setPorcentaje(Integer.valueOf((String) porcentaje.getValue()));
                                        items.setNombreCorto((String) nombreCorto.getValue());
                                        listaZonaVerificado.add(items);
                                        System.out.println("///////////////////////" + listaZonaVerificado + "/////////////////////////////");
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    item.setListaZonasVerificado(Arrays.asList(listaZonaVerificado.toArray(new ZonaVerificadoVO[listaZonaVerificado.size()])));
                    SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
                    SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
                    SoapPrimitive cedis = (SoapPrimitive) servicio.getProperty("cedis");
                    SoapPrimitive cedisId = (SoapPrimitive) servicio.getProperty("cedisId");
                    SoapPrimitive llave = (SoapPrimitive) servicio.getProperty("llave");
                    SoapPrimitive pedidoValido = (SoapPrimitive) servicio.getProperty("pedidoValido");
                    SoapPrimitive requiereLlave = (SoapPrimitive) servicio.getProperty("requiereLlave");
                    SoapPrimitive tienda = (SoapPrimitive) servicio.getProperty("tienda");
                    SoapPrimitive tiendaId = (SoapPrimitive) servicio.getProperty("tiendaId");
                    //SoapPrimitive listaZonas = (SoapPrimitive) servicio.getProperty("listaZonas");
                    try {
                        item.setCodigo(codigo.getValue() != null ? Integer.valueOf((String) codigo.getValue()) : 1);
                        System.out.println("codigo ");
                    } catch (Exception e) {
                        item.setCodigo(1);
                    }
                    try {
                        item.setMensaje(mensaje.getValue() != null ? (String) mensaje.getValue() : " ");
                        System.out.println("mensaje ");
                    } catch (Exception e) {
                        item.setMensaje(" ");
                    }
                    try {
                        item.setNombreCedis(cedis.getValue() != null ? (String) cedis.getValue() : " ");
                        System.out.println("cedis ");
                    } catch (Exception e) {
                        item.setNombreCedis(" ");
                    }
                    try {
                        item.setCedisId(cedisId.getValue() != null ? Integer.valueOf((String) cedisId.getValue()) : 0);
                        System.out.println("cedisID ");
                    } catch (Exception e) {
                        item.setCedisId(0);
                    }
                    try {
                        item.setLlave(llave.getValue() != null ? (String) llave.getValue() : " ");
                        System.out.println("llave ");
                    } catch (Exception e) {
                        item.setLlave(" ");
                    }
                    try {
                        item.setPedidoValido(pedidoValido.getValue() != null ? (String) pedidoValido.getValue().toString() : " ");
                        System.out.println("pedidoValido : " + pedidoValido.getValue().toString());
                    } catch (Exception e) {
                        item.setPedidoValido(" ");
                    }
                    try {
                        item.setRequiereLlave(requiereLlave.getValue() != null ? (Boolean) requiereLlave.getValue() : false);
                        System.out.println("requiereLlave ");
                    } catch (Exception e) {
                        item.setRequiereLlave(false);
                    }
                    try {
                        item.setNombreTienda(tienda.getValue() != null ? (String) tienda.getValue() : " ");
                        System.out.println("tienda ");
                    } catch (Exception e) {
                        item.setNombreTienda(" ");
                    }
                    try {
                        item.setTiendaId(tiendaId.getValue() != null ? Integer.valueOf((String) tiendaId.getValue()) : 0);
                        System.out.println("tiendaId ");
                    } catch (Exception e) {
                        item.setTiendaId(0);
                    }

                } catch (Exception e) {
                    ViewDialog alert = new ViewDialog(context);
                    alert.showDialog((Activity) context, e.getMessage(), null, TiposAlert.ALERT);
                }
            }
        }
        return item;
    }

    /////////////////////////////////////////////GuardaDiferencias//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public RespuestaIncidenciasVO parseRespuestaGuardaDiferencias(SoapObject servicio, Context context) {
        /*for (int i= 0;i<servicio.getPropertyCount();i ++){
            if (servicio.getProperty(i) == null){
                System.out.println("Servicio.getProperty: "+servicio.getProperty(i));
                servicio.setProperty(i,true);
                System.out.println("Servicio.getProperty Cambiado: "+servicio.getProperty(i));
            }else {
                System.out.println("Servicio.getProperty: " + servicio.getProperty(i));
            }
        }*/
        final String str = servicio.toString();
        System.out.println("SERVICIO/////////////////////////////////////"+str);
        Log.d(TAG, "parseRespuestaGuardaDiferencias: " + str);
        respuestaIncidencias = new RespuestaIncidenciasVO();//nueva respuesta
        List<RespuestaIncidenciasVO.IncidenciaVO> listaTemp = new ArrayList<>(); //nueva lista temporal de tipo IncidenciaVO (subclase)
        RespuestaIncidenciasVO.IncidenciaVO incidencia = respuestaIncidencias.new IncidenciaVO(); //nuevo objeto de tipo IncidenciaVO (subclase)

        int count = servicio.getPropertyCount();

        if (str.contains("Error") || servicio.equals(null)) {
            SoapPrimitive coidgo = (SoapPrimitive) servicio.getProperty("errorCode");
            SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
            respuestaIncidencias.setCodigo(Integer.valueOf((String) coidgo.getValue()));
            respuestaIncidencias.setMensaje((String) mensaje.getValue());

        } else {
            if (count > 0) {
                try {
                    for (int i = 0; i < count; i++) {
                        try {
                            if (servicio.getProperty(i)!= null) {
                                if (!servicio.getPropertyAsString(i).contains("incidencia")) {
                                    SoapObject pojoSoap = null;
                                    try {
                                        pojoSoap = (SoapObject) servicio.getProperty(i);

                                    } catch (Exception e) {
                                        Log.d(TAG, "parseRespuestaGuardaDiferencias: pojo es nulo");
                                    }
                                    if (pojoSoap != null) {
                                        SoapPrimitive incidenciaId = (SoapPrimitive) pojoSoap.getProperty("incidenciaId");
                                        SoapPrimitive articuloId = (SoapPrimitive) pojoSoap.getProperty("articuloId");
                                        SoapPrimitive cantidadDiferencia = (SoapPrimitive) pojoSoap.getProperty("cantidadDiferencia");
                                        SoapPrimitive estatusDiferencia = (SoapPrimitive) pojoSoap.getProperty("estatusDiferencia");

                                        incidencia.setIncidenciaId(Long.valueOf((String) articuloId.getValue()));
                                        incidencia.setArticuloId(Long.valueOf((String) incidenciaId.getValue()));
                                        incidencia.setCantidadDiferencia(Integer.valueOf((String) cantidadDiferencia.getValue()));
                                        incidencia.setEstatusDiferencia(Integer.valueOf((String) estatusDiferencia.getValue()));
                                        listaTemp.add(incidencia);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    respuestaIncidencias.setListaIncidencias(Arrays.asList(listaTemp.toArray(new RespuestaIncidenciasVO.IncidenciaVO[listaTemp.size()])));
                    SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
                    SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
                    try {
                        respuestaIncidencias.setCodigo(codigo.getValue() != null ? Integer.valueOf((String) codigo.getValue()) : 1);
                    } catch (Exception e) {
                        respuestaIncidencias.setCodigo(1);
                    }
                    try {
                        respuestaIncidencias.setMensaje(mensaje.getValue() != null ? (String) mensaje.getValue() : " ");
                    } catch (Exception e) {
                        respuestaIncidencias.setMensaje(" ");
                    }
                } catch (Exception e) {
                    ViewDialog alert = new ViewDialog(context);
                    alert.showDialog((Activity) context, e.getMessage(), null, TiposAlert.ALERT);
                }

            }
        }
        return respuestaIncidencias;
    }


    /////////////////////////////////////////////GuardaDiferencias_CargaCodigosBarra//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public RespuestaIncidenciasVO parseRespuestaGuardaDiferencias_CargaCodigosBarra(SoapObject servicio, Context context) {
        /*for (int i= 0;i<servicio.getPropertyCount();i ++){
            if (servicio.getProperty(i) == null){
                System.out.println("Servicio.getProperty: "+servicio.getProperty(i));
                servicio.setProperty(i,true);
                System.out.println("Servicio.getProperty Cambiado: "+servicio.getProperty(i));
            }else {
                System.out.println("Servicio.getProperty: " + servicio.getProperty(i));
            }
        }*/
        final String str = servicio.toString();
        System.out.println("SERVICIO/////////////////////////////////////"+str);
        Log.d(TAG, "parseRespuestaGuardaDiferencias: " + str);
        CargaCodigosBarraActivity.respuestaIncidencias = new RespuestaIncidenciasVO();//nueva respuesta
        List<RespuestaIncidenciasVO.IncidenciaVO> listaTemp = new ArrayList<>(); //nueva lista temporal de tipo IncidenciaVO (subclase)
        RespuestaIncidenciasVO.IncidenciaVO incidencia = CargaCodigosBarraActivity.respuestaIncidencias.new IncidenciaVO(); //nuevo objeto de tipo IncidenciaVO (subclase)

        int count = servicio.getPropertyCount();

        if (str.contains("Error") || servicio.equals(null)) {
            SoapPrimitive coidgo = (SoapPrimitive) servicio.getProperty("errorCode");
            SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
            CargaCodigosBarraActivity.respuestaIncidencias.setCodigo(Integer.valueOf((String) coidgo.getValue()));
            CargaCodigosBarraActivity.respuestaIncidencias.setMensaje((String) mensaje.getValue());

        } else {
            if (count > 0) {
                try {
                    for (int i = 0; i < count; i++) {
                        try {
                            if (servicio.getProperty(i)!= null) {
                                if (!servicio.getPropertyAsString(i).contains("incidencia")) {
                                    SoapObject pojoSoap = null;
                                    try {
                                        pojoSoap = (SoapObject) servicio.getProperty(i);

                                    } catch (Exception e) {
                                        Log.d(TAG, "parseRespuestaGuardaDiferencias: pojo es nulo");
                                    }
                                    if (pojoSoap != null) {
                                        SoapPrimitive incidenciaId = (SoapPrimitive) pojoSoap.getProperty("incidenciaId");
                                        SoapPrimitive articuloId = (SoapPrimitive) pojoSoap.getProperty("articuloId");
                                        SoapPrimitive cantidadDiferencia = (SoapPrimitive) pojoSoap.getProperty("cantidadDiferencia");
                                        SoapPrimitive estatusDiferencia = (SoapPrimitive) pojoSoap.getProperty("estatusDiferencia");

                                        incidencia.setIncidenciaId(Long.valueOf((String) articuloId.getValue()));
                                        incidencia.setArticuloId(Long.valueOf((String) incidenciaId.getValue()));
                                        incidencia.setCantidadDiferencia(Integer.valueOf((String) cantidadDiferencia.getValue()));
                                        incidencia.setEstatusDiferencia(Integer.valueOf((String) estatusDiferencia.getValue()));
                                        listaTemp.add(incidencia);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    CargaCodigosBarraActivity.respuestaIncidencias.setListaIncidencias(Arrays.asList(listaTemp.toArray(new RespuestaIncidenciasVO.IncidenciaVO[listaTemp.size()])));
                    SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
                    SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
                    try {
                        CargaCodigosBarraActivity.respuestaIncidencias.setCodigo(codigo.getValue() != null ? Integer.valueOf((String) codigo.getValue()) : 1);
                    } catch (Exception e) {
                        CargaCodigosBarraActivity.respuestaIncidencias.setCodigo(1);
                    }
                    try {
                        CargaCodigosBarraActivity.respuestaIncidencias.setMensaje(mensaje.getValue() != null ? (String) mensaje.getValue() : " ");
                    } catch (Exception e) {
                        CargaCodigosBarraActivity.respuestaIncidencias.setMensaje(" ");
                    }
                } catch (Exception e) {
                    ViewDialog alert = new ViewDialog(context);
                    alert.showDialog((Activity) context, e.getMessage(), null, TiposAlert.ALERT);
                }

            }
        }
        return CargaCodigosBarraActivity.respuestaIncidencias;
    }




    /////////////////////////////////////////////GuardaArticulos//////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

    /////////////////////////////////////////////GeneraCatalogoV2//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ArticuloVO parseRespuestaGeneraCatalogo(SoapObject servicio, Context context) {
        CargaCodigosBarraActivity.totalCajasSurtidas = 0;
        CargaCodigosBarraActivity.totalCajasRecibidas = 0;
        /*for (int i= 0;i<servicio.getPropertyCount();i ++){
            if (servicio.getProperty(i).toString().contains("DatosArticuloBean")){
                System.out.println("Esta en Datos ArticuloBean XDxdXD/////////////////////");
                System.out.println("Servicio.getProperty: "+servicio.getProperty(i));
                String textoAyuda = servicio.getProperty(i).toString();
                if (textoAyuda.contains("null")){
                    System.out.println("tiene nullo///////////////////////////");
                    int localizadoNull = textoAyuda.indexOf("null");
                    String inicioTexto = textoAyuda.substring(0,localizadoNull);
                    String finTexto = textoAyuda.substring(localizadoNull+4);
                    textoAyuda = inicioTexto + "\"true\"" + finTexto;
                    servicio.setProperty(i,textoAyuda);
                }
                System.out.println("Servicio.getProperty Cambiado: "+servicio.getProperty(i));
            }else {
                System.out.println("Servicio.getProperty: " + servicio.getProperty(i));
            }
        }*/
        final String str = servicio.toString();
        Log.d(TAG, "parseRespuestaGeneraCatalogo: " + str);
        ArticuloVO item = new ArticuloVO();
        ArrayList<ArticuloVO> articulos;
        int count = servicio.getPropertyCount();


        if (str.contains("Error") || servicio.equals(null)) {
            SoapPrimitive coidgo = (SoapPrimitive) servicio.getProperty("errorCode");
            SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
            item.setCodigo(Integer.valueOf((String) coidgo.getValue()));
            item.setMensaje((String) mensaje.getValue());

        } else {
            if (count > 0) {
                articulos = new ArrayList<>();
                try {
                    for (int i = 0; i < count; i++) {
                        ArticuloVO items = new ArticuloVO();
                        try {
                            if (!servicio.getPropertyAsString(i).contains("articulos")) {
                                SoapObject pojoSoap = null;
                                try {
                                    pojoSoap = (SoapObject) servicio.getProperty(i);

                                } catch (Exception e) {
                                    Log.d(TAG, "parseRespuestaValidaPedido: pojo es nulo");

                                }
                                if (pojoSoap!= null){
                                    if (servicio.getPropertyAsString(i).contains("tipoPermiso")) {
                                        SoapPrimitive tipoPermiso = (SoapPrimitive) pojoSoap.getProperty("tipoPermiso");
                                        CargaCodigosBarraActivity.tipoPermiso= (Integer.valueOf((String) tipoPermiso.getValue()));
                                        System.out.println("ValortipoPermiso/////////////////////////////"+tipoPermiso);
                                        if (CargaCodigosBarraActivity.tipoPermiso == 1) {
                                            CargaCodigosBarraActivity.banderaIncidencia = 1;
                                        }
                                    }
                                    SoapPrimitive articuloId = (SoapPrimitive) pojoSoap.getProperty("articuloId");
                                    SoapPrimitive cantidadAsignada = (SoapPrimitive) pojoSoap.getProperty("cantidadAsignada");
                                    SoapPrimitive cantidadVerificada = (SoapPrimitive) pojoSoap.getProperty("cantidadVerificada");
                                    //SoapPrimitive codigosBarraArr = (SoapPrimitive) pojoSoap.getProperty("codigosBarraArr");
                                    SoapPrimitive nombre = (SoapPrimitive) pojoSoap.getProperty("nombre");
                                    SoapPrimitive normaEmpaque = (SoapPrimitive) pojoSoap.getProperty("normaEmpaque");
                                    SoapPrimitive unidadMedida = (SoapPrimitive) pojoSoap.getProperty("unidadMedida");
                                    SoapPrimitive unidadMedidaId = (SoapPrimitive) pojoSoap.getProperty("unidadMedidaId");
                                    SoapPrimitive normaPallet = (SoapPrimitive) pojoSoap.getProperty("normaPallet");
                                    for (int j = 0;j<pojoSoap.getPropertyCount();j++){
                                        //System.out.println("/////////pojoSoap.getProperty()"+pojoSoap.getProperty(j));
                                        //System.out.println("/////////pojoSoap.getPropertyInfo()"+pojoSoap.getPropertyInfo(j));
                                        String Property = String.valueOf(pojoSoap.getPropertyInfo(j));
                                        if (Property.contains("codigosBarraArr")){
                                            SoapPrimitive codigosBarraArr = (SoapPrimitive) pojoSoap.getProperty(j);
                                            items.getCodigos().add((String) codigosBarraArr.getValue());
                                        }
                                        //SoapPrimitive codigosBarraArr = (SoapPrimitive) pojoSoap.getProperty("codigosBarraArr");
                                        //items.getCodigos().add((String) codigosBarraArr.getValue());
                                        //System.out.println("/////////////.codigos"+items.getCodigos()+"////////.size"+items.getCodigos().size()+"/////");
                                    }
                                    items.setArticuloId(Long.parseLong((String)articuloId.getValue()));
                                    items.setTotalCajasAsignadas(Integer.valueOf((String) cantidadAsignada.getValue()));
                                    CargaCodigosBarraActivity.totalCajasSurtidas += items.getTotalCajasAsignadas();
                                    items.setTotalCajasVerificadas(Integer.valueOf((String) cantidadVerificada.getValue()));
                                    //items.getCodigos().add((String) codigosBarraArr.getValue());
                                    items.setNombreArticulo((String) nombre.getValue());
                                    items.setNormaEmpaque(Integer.valueOf((String)normaEmpaque.getValue()));
                                    items.setUnidadMedida((String) unidadMedida.getValue());
                                    items.setUnidadMedidaId(Integer.valueOf((String) unidadMedidaId.getValue()));
                                    items.setNormaPallet(Integer.valueOf((String) normaPallet.getValue()));
                                    articulos.add(items);
                                    //System.out.println("////////////////////////articulos"+articulos+"/////////");
                                    System.out.println("/////////////.codigosFINAL"+items.getCodigos()+"////////.size"+items.getCodigos().size()+"/////");

                                    System.out.println("///////////////ARTICULOid" + items.getArticuloId() + "//////////////////////////////");

                                    System.out.println("///////////////" + CargaCodigosBarraActivity.mapaCatalogo + "//////////////////////////////");
                                    CargaCodigosBarraActivity.mapaCatalogo.put(items.getArticuloId(), items);
                                    System.out.println("///////////////" + items.getArticuloId() + "//////////////////////////////");



                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    item.setListaArticulo(Arrays.asList(articulos.toArray(new ArticuloVO[articulos.size()])));
                    SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
                    SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");

                    try {
                        item.setCodigo(codigo.getValue() != null ? Integer.valueOf((String) codigo.getValue()) : 1);
                        System.out.println("codigo ");
                    } catch (Exception e) {
                        item.setCodigo(1);
                    }
                    try {
                        item.setMensaje(mensaje.getValue() != null ? (String) mensaje.getValue() : " ");
                        System.out.println("mensaje ");
                    } catch (Exception e) {
                        item.setMensaje(" ");
                    }
                    CargaCodigosBarraActivity.existeCodigo = false;
                    long articuloID = 0;


                } catch (Exception e) {
                    ViewDialog alert = new ViewDialog(context);
                    alert.showDialog((Activity) context, e.getMessage(), null, TiposAlert.ALERT);
                }
            }
        }
        return item;
    }
}
