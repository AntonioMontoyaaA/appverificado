package neto.com.mx.verificapedidocedis.beans;

import java.io.Serializable;

/**
 * Created by yruizm on 28/09/17.
 */

public class UsuarioVO implements Serializable {

    private int numeroEmpleado;
    private String nombreEmpleado;
    private String empleadoValido;
    private int codigo;
    private String mensaje;

    public int getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(int numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String isEmpleadoValido() {
        return empleadoValido;
    }

    public void setEmpleadoValido(String empleadoValido) {
        this.empleadoValido = empleadoValido;
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
}
