package neto.com.mx.verificapedidocedis.beans;

import java.io.Serializable;

/**
 * Created by yruizm on 06/11/17.
 */

public class ZonaVerificadoVO implements Serializable {

    private int idZona;
    private String descripcionZona;
    private int zonaValida;
    private String nombreUsuario;
    private int estatusZona;
    private int porcentaje;
    private String nombreCorto;

    public int getIdZona() {
        return idZona;
    }

    public void setIdZona(int idZona) {
        this.idZona = idZona;
    }

    public String getDescripcionZona() {
        return descripcionZona;
    }

    public void setDescripcionZona(String descripcionZona) {
        this.descripcionZona = descripcionZona;
    }

    public int getZonaValida() {
        return zonaValida;
    }

    public void setZonaValida(int zonaValida) {
        this.zonaValida = zonaValida;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getEstatusZona() {
        return estatusZona;
    }

    public void setEstatusZona(int estatusZona) {
        this.estatusZona = estatusZona;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }
}
