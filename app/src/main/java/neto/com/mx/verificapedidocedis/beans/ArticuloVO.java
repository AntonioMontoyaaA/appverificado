package neto.com.mx.verificapedidocedis.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

/**
 * Created by yruizm on 30/10/16.
 */

public class ArticuloVO implements Serializable {

    private long articuloId;
    private HashSet<String> codigos;
    private int totalCajasAsignadas;
    private int totalCajasVerificadas;
    private String nombreArticulo;
    private int normaEmpaque;
    private int normaPallet;
    private String unidadMedida;
    private int unidadMedidaId;
    private boolean esCapturado = false;
    private boolean esBuscadoDiferencias = false;

    public int getNormaPallet() {
        return normaPallet;
    }

    public void setNormaPallet(int normaPallet) {
        this.normaPallet = normaPallet;
    }

    public ArticuloVO() {
        codigos = new HashSet<String>();
    }

    public long getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(long articuloId) {
        this.articuloId = articuloId;
    }

    public HashSet<String> getCodigos() {
        return codigos;
    }

    public void setCodigos(HashSet<String> codigos) {
        this.codigos = codigos;
    }


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

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public int getNormaEmpaque() {
        return normaEmpaque;
    }

    public void setNormaEmpaque(int normaEmpaque) {
        this.normaEmpaque = normaEmpaque;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public int getUnidadMedidaId() {
        return unidadMedidaId;
    }

    public void setUnidadMedidaId(int unidadMedidaId) {
        this.unidadMedidaId = unidadMedidaId;
    }

    public boolean isEsCapturado() {
        return esCapturado;
    }

    public void setEsCapturado(boolean esCapturado) {
        this.esCapturado = esCapturado;
    }

    public boolean isEsBuscadoDiferencias() {
        return esBuscadoDiferencias;
    }

    public void setEsBuscadoDiferencias(boolean esBuscadoDiferencias) {
        this.esBuscadoDiferencias = esBuscadoDiferencias;
    }

}
