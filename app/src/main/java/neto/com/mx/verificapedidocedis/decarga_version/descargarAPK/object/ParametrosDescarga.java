package neto.com.mx.verificapedidocedis.decarga_version.descargarAPK.object;

import java.io.InputStream;


public class ParametrosDescarga {
    String urlHttps;
    String directorioDestino;
    String nombreApk;

    public ParametrosDescarga(
            String urlHttps,
            String directorioDestino,
            String nombreApk) {
        this.urlHttps = urlHttps;
        this.directorioDestino = directorioDestino;
        this.nombreApk = nombreApk;
    }

    public String getUrlHttps() {
        return urlHttps;
    }

    public void setUrlHttps(String urlHttps) {
        this.urlHttps = urlHttps;
    }

    public String getDirectorioDestino() {
        return directorioDestino;
    }

    public void setDirectorioDestino(String directorioDestino) {
        this.directorioDestino = directorioDestino;
    }

    public String getNombreApk() {
        return nombreApk;
    }

    public void setNombreApk(String nombreApk) {
        this.nombreApk = nombreApk;
    }
}
