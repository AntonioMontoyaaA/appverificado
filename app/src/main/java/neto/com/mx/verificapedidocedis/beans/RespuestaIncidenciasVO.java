package neto.com.mx.verificapedidocedis.beans;

import java.util.List;

/**
 * Created by yruizm on 30/10/16.
 */

public class RespuestaIncidenciasVO {
    private int codigo;
    private String mensaje;
    private List<IncidenciaVO> listaIncidencias;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<IncidenciaVO> getListaIncidencias() {
        return listaIncidencias;
    }

    public void setListaIncidencias(List<IncidenciaVO> listaIncidencias) {
        this.listaIncidencias = listaIncidencias;
    }


}
