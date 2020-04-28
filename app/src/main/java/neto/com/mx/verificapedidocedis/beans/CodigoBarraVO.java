package neto.com.mx.verificapedidocedis.beans;

import java.io.Serializable;

/**
 * Created by yruizm on 20/10/16.
 */

public class CodigoBarraVO implements Serializable {

    private long articuloId;
    private String codigoBarras;
    private String nombreArticulo;
    private int cajasAsignadas;
    private int cajasVerificadas;

    public long getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(long articuloId) {
        this.articuloId = articuloId;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public int getCajasAsignadas() {
        return cajasAsignadas;
    }

    public void setCajasAsignadas(int cajasAsignadas) {
        this.cajasAsignadas = cajasAsignadas;
    }

    public int getCajasVerificadas() {
        return cajasVerificadas;
    }

    public void setCajasVerificadas(int cajasVerificadas) {
        this.cajasVerificadas = cajasVerificadas;
    }

    @Override
    public String toString() {
        return "CodigoBarraVO{" +
                "articuloId=" + articuloId +
                ", codigoBarras='" + codigoBarras + '\'' +
                ", nombreArticulo='" + nombreArticulo + '\'' +
                ", cajasPedido=" + cajasAsignadas +
                ", cajasCapturadas=" + cajasVerificadas +
                '}';
    }
}
