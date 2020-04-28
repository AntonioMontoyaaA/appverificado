package neto.com.mx.verificapedidocedis.beans;

import java.io.Serializable;

/**
 * Created by yruizm on 02/10/17.
 */

public class ZonaPickeoVO implements Serializable {

    private int zonaId;
    private String zona;
    private int usuarioId;
    private String usuario;
    private String estatus;

    public int getZonaId() {
        return zonaId;
    }

    public void setZonaId(int zonaId) {
        this.zonaId = zonaId;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}
