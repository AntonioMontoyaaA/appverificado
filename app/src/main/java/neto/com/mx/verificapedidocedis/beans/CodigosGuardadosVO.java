package neto.com.mx.verificapedidocedis.beans;

import java.io.Serializable;

/**
 * Created by yruizm on 23/10/16.
 */

public class CodigosGuardadosVO implements Serializable {

    private int totalCajasAsignadas;
    private int totalCajasVerificadas;
    private int totalArticulosVerificados;
    private int totalArticulosAsignados;
    private CodigoBarraVO[] articulosDiferencias;
    private int codigo;
    private String mensaje;
    private String estatusPedido;

    public int getTotalCajasAsignadas() {
        return totalCajasAsignadas;
    }

    public void setTotalCajasAsignadas(int totalCajasAsignadas) {
        this.totalCajasAsignadas = totalCajasAsignadas;
    }

    public int getTotalCajasVerificadas() {
        return totalCajasVerificadas;
    }

    public void setTotalCajasVerificadas(int totalCajasVerificadas) {
        this.totalCajasVerificadas = totalCajasVerificadas;
    }

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

    public CodigoBarraVO[] getArticulosDiferencias() {
        return articulosDiferencias;
    }

    public void setArticulosDiferencias(CodigoBarraVO[] articulosDiferencias) {
        this.articulosDiferencias = articulosDiferencias;
    }

    public int getTotalArticulosVerificados() {
        return totalArticulosVerificados;
    }

    public void setTotalArticulosVerificados(int totalArticulosVerificados) {
        this.totalArticulosVerificados = totalArticulosVerificados;
    }

    public int getTotalArticulosAsignados() {
        return totalArticulosAsignados;
    }

    public void setTotalArticulosAsignados(int totalArticulosAsignados) {
        this.totalArticulosAsignados = totalArticulosAsignados;
    }

    public String getEstatusPedido() {
        return estatusPedido;
    }

    public void setEstatusPedido(String estatusPedido) {
        this.estatusPedido = estatusPedido;
    }
}
