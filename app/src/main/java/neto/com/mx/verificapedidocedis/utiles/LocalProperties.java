package neto.com.mx.verificapedidocedis.utiles;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import neto.com.mx.verificapedidocedis.AppController;

public class LocalProperties {

    private static final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppController.getInstance());
    private static final SharedPreferences.Editor e = prefs.edit();

    private static final String BLOQUEA_USUARIO = "bloquear";

    public static boolean isUsuarioBloqueado() {
        return prefs.getBoolean(BLOQUEA_USUARIO, true);
    }

    public static void setUsuarioBloqueado(boolean mode){
        e.putBoolean(BLOQUEA_USUARIO,mode);
        e.apply();
    }

}
