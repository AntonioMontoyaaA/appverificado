package neto.com.mx.verificapedidocedis;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import neto.com.mx.verificapedidocedis.beans.CodigoBarraVO;
import neto.com.mx.verificapedidocedis.beans.CodigosGuardadosVO;

public class DiferenciasRecibidasActivity extends AppCompatActivity {

    private String folio = "";
    private String nombreEmpleado = "";
    private String numeroEmpleado = "";
    private String estatusPedido = "";
    private String nombreTienda = "";
    private boolean descargaCatalogoFlag = false;
    private String nombreZona = "";
    private int idZona = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("metodo de la lista");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diferencias_recibidas);
        getSupportActionBar().hide();
        ScrollView scroll = (ScrollView) findViewById(R.id.container_scroll_view);


        descargaCatalogoFlag = this.getIntent().getBooleanExtra("descargaCatalogo",false);

        CodigosGuardadosVO codigos = (CodigosGuardadosVO) getIntent().getSerializableExtra("CodigosGuardados");
        folio = new String(getIntent().getStringExtra("folio"));
        nombreEmpleado = new String(this.getIntent().getStringExtra("nombreEmpleado").trim());
        numeroEmpleado = new String(this.getIntent().getStringExtra("numeroEmpleado").trim());
        estatusPedido = new String(this.getIntent().getStringExtra("estatusPedido").trim());
        nombreTienda = new String(this.getIntent().getStringExtra("nombreTienda").trim());
        nombreZona = new String(this.getIntent().getStringExtra("nombreZona").trim());
        idZona = this.getIntent().getIntExtra("idZona", 0);


        TextView artSurtidosText = (TextView) findViewById(R.id.artSurtidos);
        artSurtidosText.setText(String.valueOf(codigos.getTotalArticulosAsignados()));

        TextView artContadosText = (TextView) findViewById(R.id.artContados);
        artContadosText.setText(String.valueOf(codigos.getTotalArticulosVerificados()));

        TextView cajasSurtidosText = (TextView) findViewById(R.id.cajasSurtidos);
        cajasSurtidosText.setText(String.valueOf(codigos.getTotalCajasAsignadas()));

        TextView cajasContadosText = (TextView) findViewById(R.id.cajasContados);
        cajasContadosText.setText(String.valueOf(codigos.getTotalCajasVerificadas()));

        if(codigos.getArticulosDiferencias().length > 0) {
            dibujaDiferencias(codigos.getArticulosDiferencias());
        }

        TextView porcentajeArticulosText = (TextView) findViewById(R.id.porcentajeArticulos);
        if(codigos.getTotalArticulosAsignados() != 0) {
            int porcentajeArticulos= (int)((codigos.getTotalArticulosVerificados() * 100) / codigos.getTotalArticulosAsignados());
            porcentajeArticulosText.setText(String.valueOf(porcentajeArticulos + "%"));

            ProgressBar progressBarArticulos = (ProgressBar) findViewById(R.id.progressBarArticulos);
            ObjectAnimator animation = ObjectAnimator.ofInt (progressBarArticulos, "progress", 0, porcentajeArticulos); // see this max value coming back here, we animale towards that value
            animation.setDuration (2000); //in milliseconds
            animation.setInterpolator (new DecelerateInterpolator());
            animation.start ();
        } else {
            porcentajeArticulosText.setText("0%");
        }

        TextView porcentajeCajasText = (TextView) findViewById(R.id.porcentajeCajas);
        if(codigos.getTotalCajasAsignadas() != 0) {
            int porcentajeCajas= (int)((codigos.getTotalCajasVerificadas() * 100) / codigos.getTotalCajasAsignadas());
            porcentajeCajasText.setText(String.valueOf(porcentajeCajas + "%"));

            ProgressBar progressBarCajas = (ProgressBar) findViewById(R.id.progressBarCajas);
            ObjectAnimator animationCajas = ObjectAnimator.ofInt (progressBarCajas, "progress", 0, porcentajeCajas); // see this max value coming back here, we animale towards that value
            animationCajas.setDuration (2000); //in milliseconds
            animationCajas.setInterpolator (new DecelerateInterpolator());
            animationCajas.start ();
        } else {
            porcentajeCajasText.setText("0%");
        }
    }

    public void dibujaDiferencias(CodigoBarraVO[] codigos) {
        int width_Ancho = this.getResources().getConfiguration().screenWidthDp;
        int height_Largo = this.getResources().getConfiguration().screenHeightDp;
        TableLayout ll = (TableLayout) findViewById(R.id.tabla_diferencias_view);
        TableRow row = new TableRow(this);
        row.setGravity(Gravity.CENTER_HORIZONTAL);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        row.setLayoutParams(lp);

        TextView tArticuloHeader = new TextView(this);
        int anchoArticulo = 400;
        int anchoTextoArticulo = 130;
        if (width_Ancho > 350 && height_Largo > 740){
            anchoArticulo = 700;
            anchoTextoArticulo = 180;
        }
        tArticuloHeader.setWidth(anchoArticulo);
        tArticuloHeader.setTextSize(23);

        TextView tCantidadHeader = new TextView(this);
        tCantidadHeader.setWidth(200);
        tCantidadHeader.setTextSize(23);

        //This generates the caption row
        tArticuloHeader.setText("Artículo");
        //tArticuloHeader.setPadding(20, 3, 3, 3);
        if (width_Ancho <= 320){
            tArticuloHeader.setPadding(85, 0, 0, 0);
        }else{
            tArticuloHeader.setPadding(0, 0, 0, 0);
        }
        tArticuloHeader.setTextColor(getResources().getColor(R.color.colorFuenteActivo));

        tCantidadHeader.setText("Cant.");
        //tCantidadHeader.setPadding(5, 3, 3, 3);
        if (width_Ancho <= 320){
            tCantidadHeader.setPadding(-100, 0, 0, 0);
        }else{
            tCantidadHeader.setPadding(0, 0, 0, 0);
        }
        tCantidadHeader.setTextColor(getResources().getColor(R.color.colorFuenteActivo));
        tCantidadHeader.setGravity(Gravity.CENTER);

        row.addView(tArticuloHeader);
        row.addView(tCantidadHeader);
        ll.addView(row,0);


        for (int i = 1; i <= codigos.length; i++) {
            row = new TableRow(this);
            row.setGravity(Gravity.CENTER_HORIZONTAL);
            lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            lp.height = anchoTextoArticulo;
            row.setLayoutParams(lp);
            TextView tArticulo = new TextView(this);
            tArticulo.setWidth(400);
            tArticulo.setText("*" + codigos[i-1].getNombreArticulo());
            tArticulo.setTextColor(getResources().getColor(R.color.colorFuenteAzul));
            if (width_Ancho <= 320){
                tArticulo.setPadding(85, 20, 0, 0);
            }else{
                tArticulo.setPadding(0, 20, 0, 0);
            }
            tArticulo.setLayoutParams(lp);

            TextView tCantidad = new TextView(this);
            tCantidad.setWidth(200);
            tCantidad.setText(codigos[i-1].getCajasVerificadas()+"");
            tCantidad.setTextColor(getResources().getColor(R.color.colorFuenteAzul));
            if (width_Ancho <= 320){
                tCantidad.setPadding(-100, 3, 3, 3);
            }else{
                tCantidad.setPadding(0, 3, 3, 3);
            }
            tCantidad.setTextSize(25);
            tCantidad.setLayoutParams(lp);
            tCantidad.setGravity(Gravity.CENTER);

            row.addView(tArticulo);
            row.addView(tCantidad);
            ll.addView(row,i);
        }
    }

    public void reiniciaCargaCodigos(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, CargaCodigosBarraActivity.class);
        intent.putExtra("folio", folio);
        intent.putExtra("descargaCatalogo", true);
        intent.putExtra("nombreEmpleado", nombreEmpleado);
        intent.putExtra("numeroEmpleado", numeroEmpleado);
        intent.putExtra("nombreTienda", nombreTienda);
        intent.putExtra("nombreZona", nombreZona);
        intent.putExtra("idZona", idZona);
        startActivity(intent);
    }

    public void salirMain(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        // Do something in response to button
        Intent intent = new Intent(this, FinalizaActivity.class);
        intent.putExtra("numeroEmpleado", numeroEmpleado);
        intent.putExtra("nombreEmpleado", nombreEmpleado);
        intent.putExtra("estatusPedido", estatusPedido);
        intent.putExtra("nombreTienda", nombreTienda);
        intent.putExtra("nombreZona", nombreZona);
        startActivity(intent);
    }

    public void regresarMenuFront(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        regresaMenu();
    }

    public void salirMenuFront(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        salirMenu();
    }

    public void regresaMenu() {
        Intent intent = new Intent(getApplicationContext(), CargaFolioPedidoActivity.class);
        intent.putExtra("folio", folio);
        intent.putExtra("numeroEmpleado", numeroEmpleado);
        intent.putExtra("nombreEmpleado", nombreEmpleado);
        intent.putExtra("nombreTienda", nombreTienda);
        intent.putExtra("descargaCatalogo", true);
        intent.putExtra("nombreZona", nombreZona);
        intent.putExtra("idZona", idZona);
        startActivity(intent);
    }

    public void salirMenu() {
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("LOGOUT", true);
        startActivity(intent);
    }
}
