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


    public class IncidenciaVO{

        private long incidenciaId;
        private long articuloId;
        private int cantidadDiferencia;
        private int estatusDiferencia;

        public long getIncidenciaId() {
            return incidenciaId;
        }

        public void setIncidenciaId(long incidenciaId) {
            this.incidenciaId = incidenciaId;
        }

        public long getArticuloId() {
            return articuloId;
        }

        public void setArticuloId(long articuloId) {
            this.articuloId = articuloId;
        }

        public int getCantidadDiferencia() {
            return cantidadDiferencia;
        }

        public void setCantidadDiferencia(int cantidadDiferencia) {
            this.cantidadDiferencia = cantidadDiferencia;
        }

        public int getEstatusDiferencia() {
            return estatusDiferencia;
        }

        public void setEstatusDiferencia(int estatusDiferencia) {
            this.estatusDiferencia = estatusDiferencia;
        }
    }

}
