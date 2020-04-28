package neto.com.mx.verificapedidocedis.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yruizm on 24/10/16.
 */

public class ValidaPedidoVO implements Serializable {

    private int tiendaId;
    private String nombreTienda;
    private int cedisId;
    private String nombreCedis;
    private String llave;
    private boolean pedidoValido;
    private boolean requiereLlave;
    private int codigo;
    private String mensaje;
    private ZonaPickeoVO[] listaZonas;
    private List<ZonaVerificadoVO> listaZonasVerificado;

    public int getTiendaId() {
        return tiendaId;
    }

    public void setTiendaId(int tiendaId) {
        this.tiendaId = tiendaId;
    }

    public String getNombreTienda() {
        return nombreTienda;
    }

    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda = nombreTienda;
    }

    public int getCedisId() {
        return cedisId;
    }

    public void setCedisId(int cedisId) {
        this.cedisId = cedisId;
    }

    public String getNombreCedis() {
        return nombreCedis;
    }

    public void setNombreCedis(String nombreCedis) {
        this.nombreCedis = nombreCedis;
    }

    public String getLlave() {
        return llave;
    }

    public void setLlave(String llave) {
        this.llave = llave;
    }

    public boolean isPedidoValido() {
        return pedidoValido;
    }

    public void setPedidoValido(boolean pedidoValido) {
        this.pedidoValido = pedidoValido;
    }

    public boolean isRequiereLlave() {
        return requiereLlave;
    }

    public void setRequiereLlave(boolean requiereLlave) {
        this.requiereLlave = requiereLlave;
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

    public ZonaPickeoVO[] getListaZonas() {
        return listaZonas;
    }

    public void setListaZonas(ZonaPickeoVO[] listaZonas) {
        this.listaZonas = listaZonas;
    }

    public List<ZonaVerificadoVO> getListaZonasVerificado() {
        return listaZonasVerificado;
    }

    public void setListaZonasVerificado(List<ZonaVerificadoVO> listaZonasVerificado) {
        this.listaZonasVerificado = listaZonasVerificado;
    }

    @Override
    public String toString() {
        return "ValidaPedidoVO{" +
                "tiendaId=" + tiendaId +
                ", nombreTienda='" + nombreTienda + '\'' +
                ", cedisId=" + cedisId +
                ", nombreCedis='" + nombreCedis + '\'' +
                ", llave='" + llave + '\'' +
                ", pedidoValido=" + pedidoValido +
                ", requiereLlave=" + requiereLlave +
                ", listaZonas=" + listaZonas.length +
                ", listaZonasVerificado=" + listaZonasVerificado.size() +
                ", codigo=" + codigo +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}
