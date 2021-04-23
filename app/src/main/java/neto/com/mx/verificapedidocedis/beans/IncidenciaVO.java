package neto.com.mx.verificapedidocedis.beans;



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
