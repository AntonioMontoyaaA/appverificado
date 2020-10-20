package neto.com.mx.verificapedidocedis;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import neto.com.mx.verificapedidocedis.decarga_version.DescargaUltimaVersionDialog_https;

public class SplashScreenActivity extends AppCompatActivity {


    private static final int UPDATEINSTALL_CODE = 0;
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        TypedValue tv = new TypedValue();
        this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        //Versión
        try {
            TextView versionText = (TextView) findViewById(R.id.versionAppText);
            versionText.setText("© Todos los derechos reservados      v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch(PackageManager.NameNotFoundException ne) {
            Log.e("CARGA_FOLIO_TAG", "Error al obtener la versión: " + ne.getMessage());
        }



        //Verificacion de version
        Intent intentVersion = new Intent(this,
                DescargaUltimaVersionDialog_https.class);
        startActivityForResult(intentVersion, UPDATEINSTALL_CODE);


    }

    public void iniciaApp(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        // Do something in response to button
        Intent mainIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        SplashScreenActivity.this.startActivity(mainIntent);
        SplashScreenActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
    }
}
