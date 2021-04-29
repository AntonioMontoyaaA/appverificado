package neto.com.mx.verificapedidocedis.utiles.exception;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

import neto.com.mx.verificapedidocedis.AppController;

public class LogException {
    private static final int TOAST = 0, FILE = 1, WS = 2;

    private static final int LOG_TO = TOAST;
    private final Context context;


    public LogException(Context context, Exception e) {
        this.context = context;
    }

    public void logTo(Exception e, String msg) {
        switch (LOG_TO) {
            case TOAST:
                logToToast(e, msg);
                break;
            case FILE:
                logToFile(e);
                break;
            case WS:
                logToWs(e);
                break;
        }
    }

    private void logToWs(Exception e) {
        //Write WS method
    }

    private void logToFile(Exception e) {

    }

    private void logToToast(Exception e, String msg) {
        Toast.makeText(context,getBasicDeviceInfo(e,msg),Toast.LENGTH_LONG).show();
    }

    private String getBasicDeviceInfo(Exception e, String msg){
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Informacion del Dispositivo");
        stringBuilder.append("\n");
        stringBuilder.append("API-Level: ").append(getAPIlevel());
        stringBuilder.append("\n");
        stringBuilder.append("Device Name: ").append(getDeviceName());
        stringBuilder.append("\n");
        stringBuilder.append("Device Version: ").append(getDeviceVersion(context));
        stringBuilder.append("\n");
        stringBuilder.append("Device UUID: ").append(getUUID(context));
        stringBuilder.append("\n");
        if (e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            stringBuilder.append("Stack Trace: ").append(sw.toString());
            stringBuilder.append("\n");
        }
        if (!msg.equals("") && msg.length() > 0) {
            stringBuilder.append("Mensaje: ").append(msg);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }


    public static String getAPIlevel() {
        return "Android API :" + Build.VERSION.SDK_INT;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        return manufacturer + " " + model;
    }

    public static String getDeviceVersion(Context activity) {
        String v;
        try {
            v = AppController.getInstance().getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
            return v.replace("-staging", "");
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @SuppressLint("HardwareIds")
    public static String getUUID(Context activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            String tmDevice = "" + tm.getDeviceId();
            String tmSerial = "" + tm.getSimSerialNumber();
            String androidId = "" + Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());

            return deviceUuid.toString();
        } else {
            return Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }


}
