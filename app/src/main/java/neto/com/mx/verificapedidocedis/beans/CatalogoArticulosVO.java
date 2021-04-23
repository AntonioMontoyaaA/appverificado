package neto.com.mx.verificapedidocedis.beans;

import java.util.HashMap;
import java.util.List;

public class CatalogoArticulosVO {
    private int codigo;
    private String mensaje;
    private int tipoPermiso;
    private int banderaIncidencia;
    private int permiteConteo;

    private List<ArticuloVO> articuloVOList;

    public CatalogoArticulosVO() {

    }

    public CatalogoArticulosVO(int codigo, String mensaje, int tipoPermiso, int banderaIncidencia, int totalCajasAsignadas, int totalCajasSurtidas, List<ArticuloVO> articuloVOList) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.tipoPermiso = tipoPermiso;
        this.banderaIncidencia = banderaIncidencia;
        this.articuloVOList = articuloVOList;
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

    public int getTipoPermiso() {
        return tipoPermiso;
    }

    public void setTipoPermiso(int tipoPermiso) {
        this.tipoPermiso = tipoPermiso;
    }

    public int getBanderaIncidencia() {
        return banderaIncidencia;
    }

    public void setBanderaIncidencia(int banderaIncidencia) {
        this.banderaIncidencia = banderaIncidencia;
    }

    public List<ArticuloVO> getArticuloVOList() {
        return articuloVOList;
    }

    public void setArticuloVOList(List<ArticuloVO> articuloVOList) {
        this.articuloVOList = articuloVOList;
    }

    public int getPermiteConteo() {
        return permiteConteo;
    }

    public void setPermiteConteo(int permiteConteo) {
        this.permiteConteo = permiteConteo;
    }

    public HashMap<Long, ArticuloVO> toHashMap() {
        HashMap<Long, ArticuloVO> mapaCatalagoArticulos = new HashMap<Long, ArticuloVO>();
        for (ArticuloVO a : articuloVOList) {
            mapaCatalagoArticulos.put(a.getArticuloId(), a);
        }
        return mapaCatalagoArticulos;
    }

    public int getTotalCajasSurtidas(){
        int counter = 0;
        for (ArticuloVO a : articuloVOList) {
            counter+= a.getTotalCajasAsignadas();
        }
        return counter;
    }

    public int getTotalCajasVerificadas(){
        int counter = 0;
        for (ArticuloVO a : articuloVOList) {
            counter+= a.getTotalCajasVerificadas();
        }
        return counter;
    }

}
