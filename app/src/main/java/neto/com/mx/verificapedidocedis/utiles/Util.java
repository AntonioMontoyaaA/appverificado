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
import neto.com.mx.verificapedidocedis.beans.CatalogoArticulosVO;
import neto.com.mx.verificapedidocedis.beans.CodigoBarraVO;
import neto.com.mx.verificapedidocedis.beans.CodigosGuardadosVO;
import neto.com.mx.verificapedidocedis.beans.IncidenciaVO;
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

    }

    public static final String HEX_DIGITS = "0123456789ABCDEF";
    public static String HexadecilaToDecimal(String hexaNumber) {
        char[] sources = hexaNumber.toCharArray();
        long dec = 0;
        for (int i = 0; i < sources.length; i++) {
            int digit = HEX_DIGITS.indexOf(Character.toUpperCase(sources[i]));
            dec += digit * Math.pow(16, (sources.length - (i + 1)));
        }
        return String.valueOf(dec);
    }

    /////////////////////////////////////////////ValidaUsuario//////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                    item.setCodigo(codigo.getValue() != null ? Integer.parseInt((String) codigo.getValue()) : 1);
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
            item.setCodigo(Integer.parseInt((String) coidgo.getValue()));
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

                                        items.setIdZona(Integer.parseInt((String) zonaId.getValue()));
                                        items.setDescripcionZona((String) descripcionZona.getValue());
                                        items.setZonaValida(Integer.parseInt((String) zonaValida.getValue()));
                                        items.setNombreUsuario((String) nombreUsuario.getValue());
                                        items.setEstatusZona(Integer.parseInt((String) estatusZona.getValue()));
                                        items.setPorcentaje(Integer.parseInt((String) porcentaje.getValue()));
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
                        item.setCodigo(codigo.getValue() != null ? Integer.parseInt((String) codigo.getValue()) : 1);
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
                        item.setCedisId(cedisId.getValue() != null ? Integer.parseInt((String) cedisId.getValue()) : 0);
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
                        item.setTiendaId(tiendaId.getValue() != null ? Integer.parseInt((String) tiendaId.getValue()) : 0);
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

        final String str = servicio.toString();
        System.out.println("SERVICIO/////////////////////////////////////"+str);
        Log.d(TAG, "parseRespuestaGuardaDiferencias: " + str);
        respuestaIncidencias = new RespuestaIncidenciasVO();//nueva respuesta
        List<IncidenciaVO> listaTemp = new ArrayList<>(); //nueva lista temporal de tipo IncidenciaVO (subclase)
        IncidenciaVO incidencia = new IncidenciaVO(); //nuevo objeto de tipo IncidenciaVO (subclase)

        int count = servicio.getPropertyCount();

        if (str.contains("Error") || servicio.equals(null)) {
            SoapPrimitive coidgo = (SoapPrimitive) servicio.getProperty("errorCode");
            SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
            respuestaIncidencias.setCodigo(Integer.parseInt((String) coidgo.getValue()));
            respuestaIncidencias.setMensaje((String) mensaje.getValue());

        } else {
            if (count > 0) {
                try {
                    for (int i = 0; i < count; i++) {
                        try {
                            if (servicio.getProperty(i)!= null) {
                                if (servicio.getPropertyInfo(i).name.equals("incidencia")) {
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

                                        incidencia.setIncidenciaId(Long.parseLong((String) incidenciaId.getValue()));
                                        incidencia.setArticuloId(Long.parseLong((String) articuloId.getValue()));
                                        incidencia.setCantidadDiferencia(Integer.parseInt((String) cantidadDiferencia.getValue()));
                                        incidencia.setEstatusDiferencia(Integer.parseInt((String) estatusDiferencia.getValue()));
                                        listaTemp.add(incidencia);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    respuestaIncidencias.setListaIncidencias(Arrays.asList(listaTemp.toArray(new IncidenciaVO[listaTemp.size()])));
                    SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
                    SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
                    try {
                        respuestaIncidencias.setCodigo(codigo.getValue() != null ? Integer.parseInt((String) codigo.getValue()) : 1);
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

        final String str = servicio.toString();
        System.out.println("SERVICIO/////////////////////////////////////"+str);
        Log.d(TAG, "parseRespuestaGuardaDiferencias: " + str);
       RespuestaIncidenciasVO respuestaIncidenciasVO = new RespuestaIncidenciasVO();//nueva respuesta
        List<IncidenciaVO> listaIncidenciasVO = new ArrayList<>(); //nueva lista temporal de tipo IncidenciaVO
        IncidenciaVO incidenciaVO; //nuevo objeto de tipo IncidenciaVO

        int count = servicio.getPropertyCount();

        if (str.contains("Error")) {
            SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
            SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
            respuestaIncidenciasVO.setCodigo(Integer.parseInt((String) codigo.getValue()));
            respuestaIncidenciasVO.setMensaje((String) mensaje.getValue());
        } else {
            if (count > 0) {
                try {
                    for (int i = 0; i < count; i++) {
                        incidenciaVO = new IncidenciaVO();
                        try {
                            if (servicio.getProperty(i)!= null) {
                                if (servicio.getPropertyInfo(i).name.equals("incidencia")) {
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

                                        incidenciaVO.setIncidenciaId(Long.parseLong((String) incidenciaId.getValue()));
                                        incidenciaVO.setArticuloId(Long.parseLong((String) articuloId.getValue()));
                                        incidenciaVO.setCantidadDiferencia(Integer.parseInt((String) cantidadDiferencia.getValue()));
                                        incidenciaVO.setEstatusDiferencia(Integer.parseInt((String) estatusDiferencia.getValue()));
                                        listaIncidenciasVO.add(incidenciaVO);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    respuestaIncidenciasVO.setListaIncidencias(listaIncidenciasVO);
                    SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
                    SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
                    try {
                        respuestaIncidenciasVO.setCodigo(codigo.getValue() != null ? Integer.parseInt((String) codigo.getValue()) : 1);
                    } catch (Exception e) {
                        respuestaIncidenciasVO.setCodigo(1);
                    }
                    try {
                        respuestaIncidenciasVO.setMensaje(mensaje.getValue() != null ? (String) mensaje.getValue() : " ");
                    } catch (Exception e) {
                        respuestaIncidenciasVO.setMensaje(" ");
                    }
                } catch (Exception e) {
                    ViewDialog alert = new ViewDialog(context);
                    alert.showDialog((Activity) context, e.getMessage(), null, TiposAlert.ALERT);
                }

            }
        }
        return respuestaIncidenciasVO;
    }




    /////////////////////////////////////////////GuardaArticulos//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public CodigosGuardadosVO parseRespuestaGuardaArticulos(SoapObject servicio, Context context) {
        final String str = servicio.toString();
        Log.d(TAG, "parseRespuestaGuardaArticulos: " + str);
        CodigosGuardadosVO item = new CodigosGuardadosVO();
        ArrayList<CodigoBarraVO> listaCodigosFaltantes;
        int count = servicio.getPropertyCount();

        if (str.contains("Error")) {
            SoapPrimitive coidgo = (SoapPrimitive) servicio.getProperty("errorCode");
            SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
            item.setCodigo(Integer.parseInt((String) coidgo.getValue()));
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

                                    items.setArticuloId(Long.parseLong((String) articuloId.getValue()));
                                    items.setCodigoBarras((String) codigobarras.getValue());
                                    items.setNombreArticulo((String) nombre.getValue());
                                    items.setCajasAsignadas(Integer.parseInt((String) cantidadAsignada.getValue()));
                                    items.setCajasVerificadas(Integer.parseInt((String) cantidadVerificada.getValue()));
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
                        item.setCodigo(codigo.getValue() != null ? Integer.parseInt((String) codigo.getValue()) : 1);
                    } catch (Exception e) {
                        item.setCodigo(1);
                    }
                    try {
                        item.setMensaje(mensaje.getValue() != null ? (String) mensaje.getValue() : " ");
                    } catch (Exception e) {
                        item.setMensaje(" ");
                    }
                    try {
                        item.setTotalArticulosAsignados(articulosAsignados.getValue() != null ? Integer.parseInt((String) articulosAsignados.getValue()) : 0);
                    } catch (Exception e) {
                        item.setTotalArticulosAsignados(0);
                    }
                    try {
                        item.setTotalArticulosVerificados(articulosVerificados.getValue() != null ? Integer.parseInt((String) articulosVerificados.getValue()) : 0);
                    } catch (Exception e) {
                        item.setTotalArticulosVerificados(0);
                    }
                    try {
                        item.setEstatusPedido(estatusPedido.getValue().equals(null) ? " " : (String) estatusPedido.getValue());
                    } catch (Exception e) {
                        item.setEstatusPedido(" ");
                    }
                    try {
                        item.setTotalCajasAsignadas(totalCajasAsignadas.getValue() != null ? Integer.parseInt((String) totalCajasAsignadas.getValue()) : 0);
                    } catch (Exception e) {
                        item.setTotalCajasAsignadas(0);
                    }
                    try {
                        item.setTotalCajasVerificadas(totalCajasVerificadas.getValue() != null ? Integer.parseInt((String) totalCajasVerificadas.getValue()) : 0);
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
    public CatalogoArticulosVO parseRespuestaGeneraCatalogo(SoapObject servicio, Context context) {
        CatalogoArticulosVO catalogoArticulosVO = new CatalogoArticulosVO();

        final String str = servicio.toString();
        Log.d(TAG, "parseRespuestaGeneraCatalogo: " + str);
        List<ArticuloVO> listaArticulos;
        int count = servicio.getPropertyCount();


        if (str.contains("Error")) {
            SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
            SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
            catalogoArticulosVO.setCodigo(Integer.parseInt((String) codigo.getValue()));
            catalogoArticulosVO.setMensaje((String) mensaje.getValue());

        } else {
            if (count > 0) {
                listaArticulos = new ArrayList<>();
                try {
                    for (int i = 0; i < count; i++) {
                        ArticuloVO articuloVO = new ArticuloVO();
                        try {
                            if (servicio.getPropertyInfo(i).name.equals("articulos")) {
                                SoapObject pojoSoap = null;
                                try {
                                    pojoSoap = (SoapObject) servicio.getProperty(i);

                                } catch (Exception e) {
                                    Log.d(TAG, "parseRespuestaGeneraCatalogo: pojo es nulo");

                                }
                                if (pojoSoap!= null){
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
                                            articuloVO.getCodigos().add((String) codigosBarraArr.getValue());
                                        }
                                        //SoapPrimitive codigosBarraArr = (SoapPrimitive) pojoSoap.getProperty("codigosBarraArr");
                                        //items.getCodigos().add((String) codigosBarraArr.getValue());
                                        //System.out.println("/////////////.codigos"+items.getCodigos()+"////////.size"+items.getCodigos().size()+"/////");
                                    }
                                    articuloVO.setArticuloId(Long.parseLong((String)articuloId.getValue()));
                                    articuloVO.setTotalCajasAsignadas(Integer.parseInt((String) cantidadAsignada.getValue()));
                                    articuloVO.setTotalCajasVerificadas(Integer.parseInt((String) cantidadVerificada.getValue()));
                                    //items.getCodigos().add((String) codigosBarraArr.getValue());
                                    articuloVO.setNombreArticulo((String) nombre.getValue());
                                    articuloVO.setNormaEmpaque(Integer.parseInt((String)normaEmpaque.getValue()));
                                    articuloVO.setUnidadMedida((String) unidadMedida.getValue());
                                    articuloVO.setUnidadMedidaId(Integer.parseInt((String) unidadMedidaId.getValue()));
                                    articuloVO.setNormaPallet(Integer.parseInt((String) normaPallet.getValue()));
                                    listaArticulos.add(articuloVO);
                                    //System.out.println("////////////////////////articulos"+articulos+"/////////");
                                    System.out.println("/////////////.codigosFINAL"+articuloVO.getCodigos()+"////////.size"+articuloVO.getCodigos().size()+"/////");

                                    System.out.println("///////////////ARTICULOid" + articuloVO.getArticuloId() + "//////////////////////////////");

                                    System.out.println("///////////////" + articuloVO.getArticuloId() + "//////////////////////////////");

                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    catalogoArticulosVO.setArticuloVOList(listaArticulos);
                    SoapPrimitive codigo = (SoapPrimitive) servicio.getProperty("errorCode");
                    SoapPrimitive mensaje = (SoapPrimitive) servicio.getProperty("errorDesc");
                    SoapPrimitive tipoPermiso = (SoapPrimitive) servicio.getProperty("tipoPermiso");
                    SoapPrimitive permiteConteo = (SoapPrimitive) servicio.getProperty("permiteConteo");

                    try {
                        catalogoArticulosVO.setTipoPermiso(tipoPermiso.getValue() != null ? Integer.parseInt((String) tipoPermiso.getValue()) : 1);
                        System.out.println("tipoPermiso "+catalogoArticulosVO.getTipoPermiso());
                        if (catalogoArticulosVO.getTipoPermiso() == 1) {
                            catalogoArticulosVO.setBanderaIncidencia(1);
                        }
                    } catch (Exception e) {
                        catalogoArticulosVO.setTipoPermiso(1);
                        catalogoArticulosVO.setBanderaIncidencia(1);
                    }

                    try {
                        catalogoArticulosVO.setPermiteConteo(permiteConteo.getValue() != null ? Integer.parseInt((String) permiteConteo.getValue()) : 1);
                        System.out.println("permiteConteo "+catalogoArticulosVO.getPermiteConteo());
                    } catch (Exception e) {
                        catalogoArticulosVO.setPermiteConteo(0);
                    }

                    try {
                        catalogoArticulosVO.setCodigo(codigo.getValue() != null ? Integer.parseInt((String) codigo.getValue()) : 1);
                        System.out.println("codigo ");
                    } catch (Exception e) {
                        catalogoArticulosVO.setCodigo(1);
                    }
                    try {
                        catalogoArticulosVO.setMensaje(mensaje.getValue() != null ? (String) mensaje.getValue() : " ");
                        System.out.println("mensaje ");
                    } catch (Exception e) {
                        catalogoArticulosVO.setMensaje(" ");
                    }
                    CargaCodigosBarraActivity.existeCodigo = false;
                    long articuloID = 0;


                } catch (Exception e) {
                    ViewDialog alert = new ViewDialog(context);
                    alert.showDialog((Activity) context, e.getMessage(), null, TiposAlert.ALERT);
                }
            }
        }
        return catalogoArticulosVO;
    }
}
