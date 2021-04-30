package neto.com.mx.verificapedidocedis.utiles;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

import neto.com.mx.verificapedidocedis.BuildConfig;
import neto.com.mx.verificapedidocedis.utiles.exception.LogException;

public class FileManager {


    public static boolean isLocalInstallationOlder(Context context,Uri u){
        final PackageManager pm = context.getPackageManager();
        String fullPath = u.getPath();
        PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);
        return LogException.getDeviceVersionCode(context) <= info.versionCode;
    }

    public static Intent getIntentForApk(Context context, File archivo,Uri u){
        Intent intentInstaller;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri uriArchivoEncontrado;
            if(u==null){
                uriArchivoEncontrado = Uri.fromFile(archivo);
            }else{
                uriArchivoEncontrado = u;
            }
            intentInstaller = new Intent( Intent.ACTION_VIEW);
            intentInstaller.setDataAndType(
                    uriArchivoEncontrado,
                    "application/vnd.android.package-archive");
            intentInstaller.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_GRANT_READ_URI_PERMISSION);
           return intentInstaller;
        }
        Uri uriArchivoEncontrado;
        if(u==null){
            uriArchivoEncontrado =FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".provider", archivo);
        }else{
            uriArchivoEncontrado = u;
        }
        intentInstaller = new Intent( Intent.ACTION_VIEW);
        intentInstaller.setDataAndType(uriArchivoEncontrado, "application/vnd.android.package-archive");
        intentInstaller.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return intentInstaller;
    }
}
