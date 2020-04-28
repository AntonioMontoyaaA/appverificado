package neto.com.mx.verificapedidocedis;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.List;

import neto.com.mx.verificapedidocedis.beans.ZonaVerificadoVO;
import neto.com.mx.verificapedidocedis.dialogos.ViewDialog;
import neto.com.mx.verificapedidocedis.utiles.TiposAlert;

public class CargaZonasDisponiblesActivity extends AppCompatActivity {

    private static final int ZONA_NO_PERMITIDA      =   0;
    private static final int ZONA_INICIADA          =   1;
    private static final int ZONA_FINALIZADA        =   2;


    private String[] listaZonaLetra = {"AA-AB", "AC-AD", "AE-AF", "BA-BB", "BC-BD", "CG-CH", "CJ", "DJ", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N"};

    private List<ZonaVerificadoVO> listaZonas = null;
    private String folio = null;
    private String nombreEmpleado = null;
    private String numeroEmpleado = null;
    private String nombreTienda = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_zonas_disponibles);
        getSupportActionBar().hide();

        listaZonas = (List<ZonaVerificadoVO>)this.getIntent().getSerializableExtra("listaZonasVerificado");
        nombreEmpleado = new String(this.getIntent().getStringExtra("nombreEmpleado"));
        numeroEmpleado = new String(this.getIntent().getStringExtra("numeroEmpleado"));
        nombreTienda = new String(this.getIntent().getStringExtra("nombreTienda"));
        folio = new String(this.getIntent().getStringExtra("folio"));

        dibujaZonas();
    }

    public void dibujaZonas() {
        GridLayout contenedorZonas = (GridLayout) findViewById(R.id.contenedorZonas);
        contenedorZonas.setFocusable(false);

        for(int i = 0; i < listaZonas.size(); i++) {
            switch(i) {
                case 0:
                    GridLayout contenedor1 = (GridLayout) findViewById(R.id.contenedor1);
                    contenedor1.setVisibility(View.VISIBLE);
                    Button btn = (Button) findViewById(R.id.zona1Boton);
                    btn.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn.setEnabled(true);
                        btn.setAlpha(1f);
                    }

                    TextView text = (TextView) findViewById(R.id.zona1Text);
                    text.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 1:
                    GridLayout contenedor2 = (GridLayout) findViewById(R.id.contenedor2);
                    contenedor2.setVisibility(View.VISIBLE);
                    Button btn1 = (Button) findViewById(R.id.zona2Boton);
                    btn1.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn1.setEnabled(true);
                        btn1.setAlpha(1f);
                    }

                    TextView text1 = (TextView) findViewById(R.id.zona2Text);
                    text1.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 2:
                    GridLayout contenedor3 = (GridLayout) findViewById(R.id.contenedor3);
                    contenedor3.setVisibility(View.VISIBLE);
                    Button btn2 = (Button) findViewById(R.id.zona3Boton);
                    btn2.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn2.setEnabled(true);
                        btn2.setAlpha(1f);
                    }

                    TextView text2 = (TextView) findViewById(R.id.zona3Text);
                    text2.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 3:
                    GridLayout contenedor4 = (GridLayout) findViewById(R.id.contenedor4);
                    contenedor4.setVisibility(View.VISIBLE);
                    Button btn3 = (Button) findViewById(R.id.zona4Boton);
                    btn3.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn3.setEnabled(true);
                        btn3.setAlpha(1f);
                    }

                    TextView text3 = (TextView) findViewById(R.id.zona4Text);
                    text3.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 4:
                    GridLayout contenedor5 = (GridLayout) findViewById(R.id.contenedor5);
                    contenedor5.setVisibility(View.VISIBLE);
                    Button btn4 = (Button) findViewById(R.id.zona5Boton);
                    btn4.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn4.setEnabled(true);
                        btn4.setAlpha(1f);
                    }

                    TextView text4 = (TextView) findViewById(R.id.zona5Text);
                    text4.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 5:
                    GridLayout contenedor6 = (GridLayout) findViewById(R.id.contenedor6);
                    contenedor6.setVisibility(View.VISIBLE);
                    Button btn5 = (Button) findViewById(R.id.zona6Boton);
                    btn5.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn5.setAlpha(1f);
                    }

                    TextView text5 = (TextView) findViewById(R.id.zona6Text);
                    text5.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 6:
                    GridLayout contenedor7 = (GridLayout) findViewById(R.id.contenedor7);
                    contenedor7.setVisibility(View.VISIBLE);
                    Button btn7 = (Button) findViewById(R.id.zona7Boton);
                    btn7.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn7.setAlpha(1f);
                    }

                    TextView text7 = (TextView) findViewById(R.id.zona7Text);
                    text7.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 7:
                    GridLayout contenedor8 = (GridLayout) findViewById(R.id.contenedor8);
                    contenedor8.setVisibility(View.VISIBLE);
                    Button btn8 = (Button) findViewById(R.id.zona8Boton);
                    btn8.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn8.setAlpha(1f);
                    }

                    TextView text8 = (TextView) findViewById(R.id.zona8Text);
                    text8.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 8:
                    GridLayout contenedor9 = (GridLayout) findViewById(R.id.contenedor9);
                    contenedor9.setVisibility(View.VISIBLE);
                    Button btn9 = (Button) findViewById(R.id.zona9Boton);
                    btn9.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn9.setAlpha(1f);
                    }

                    TextView text9 = (TextView) findViewById(R.id.zona9Text);
                    text9.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 9:
                    GridLayout contenedor10 = (GridLayout) findViewById(R.id.contenedor10);
                    contenedor10.setVisibility(View.VISIBLE);
                    Button btn10 = (Button) findViewById(R.id.zona10Boton);
                    btn10.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn10.setAlpha(1f);
                    }

                    TextView text10 = (TextView) findViewById(R.id.zona10Text);
                    text10.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 10:
                    GridLayout contenedor11 = (GridLayout) findViewById(R.id.contenedor11);
                    contenedor11.setVisibility(View.VISIBLE);
                    Button btn11 = (Button) findViewById(R.id.zona11Boton);
                    btn11.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn11.setAlpha(1f);
                    }

                    TextView text11 = (TextView) findViewById(R.id.zona11Text);
                    text11.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 11:
                    GridLayout contenedor12 = (GridLayout) findViewById(R.id.contenedor12);
                    contenedor12.setVisibility(View.VISIBLE);
                    Button btn12 = (Button) findViewById(R.id.zona12Boton);
                    btn12.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn12.setAlpha(1f);
                    }

                    TextView text12 = (TextView) findViewById(R.id.zona12Text);
                    text12.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 12:
                    GridLayout contenedor13 = (GridLayout) findViewById(R.id.contenedor13);
                    contenedor13.setVisibility(View.VISIBLE);
                    Button btn13 = (Button) findViewById(R.id.zona13Boton);
                    btn13.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn13.setAlpha(1f);
                    }

                    TextView text13 = (TextView) findViewById(R.id.zona13Text);
                    text13.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 13:
                    GridLayout contenedor14 = (GridLayout) findViewById(R.id.contenedor14);
                    contenedor14.setVisibility(View.VISIBLE);
                    Button btn14 = (Button) findViewById(R.id.zona14Boton);
                    btn14.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn14.setAlpha(1f);
                    }

                    TextView text14 = (TextView) findViewById(R.id.zona14Text);
                    text14.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 14:
                    GridLayout contenedor15 = (GridLayout) findViewById(R.id.contenedor15);
                    contenedor15.setVisibility(View.VISIBLE);
                    Button btn15 = (Button) findViewById(R.id.zona15Boton);
                    btn15.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn15.setAlpha(1f);
                    }

                    TextView text15 = (TextView) findViewById(R.id.zona15Text);
                    text15.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 15:
                    GridLayout contenedor16 = (GridLayout) findViewById(R.id.contenedor16);
                    contenedor16.setVisibility(View.VISIBLE);
                    Button btn16 = (Button) findViewById(R.id.zona16Boton);
                    btn16.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn16.setAlpha(1f);
                    }

                    TextView text16 = (TextView) findViewById(R.id.zona16Text);
                    text16.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 16:
                    GridLayout contenedor17 = (GridLayout) findViewById(R.id.contenedor17);
                    contenedor17.setVisibility(View.VISIBLE);
                    Button btn17 = (Button) findViewById(R.id.zona17Boton);
                    btn17.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn17.setAlpha(1f);
                    }

                    TextView text17 = (TextView) findViewById(R.id.zona17Text);
                    text17.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 17:
                    GridLayout contenedor18 = (GridLayout) findViewById(R.id.contenedor18);
                    contenedor18.setVisibility(View.VISIBLE);
                    Button btn18 = (Button) findViewById(R.id.zona18Boton);
                    btn18.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn18.setAlpha(1f);
                    }

                    TextView text18 = (TextView) findViewById(R.id.zona18Text);
                    text18.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 18:
                    GridLayout contenedor19 = (GridLayout) findViewById(R.id.contenedor19);
                    contenedor19.setVisibility(View.VISIBLE);
                    Button btn19 = (Button) findViewById(R.id.zona19Boton);
                    btn19.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn19.setAlpha(1f);
                    }

                    TextView text19 = (TextView) findViewById(R.id.zona19Text);
                    text19.setText(listaZonas.get(i).getDescripcionZona());
                    break;
                case 19:
                    GridLayout contenedor20 = (GridLayout) findViewById(R.id.contenedor20);
                    contenedor20.setVisibility(View.VISIBLE);
                    Button btn20 = (Button) findViewById(R.id.zona20Boton);
                    btn20.setText(listaZonas.get(i).getNombreCorto());

                    if(listaZonas.get(i).getZonaValida() != ZONA_NO_PERMITIDA) {
                        //btn5.setEnabled(true);
                        btn20.setAlpha(1f);
                    }

                    TextView text20 = (TextView) findViewById(R.id.zona20Text);
                    text20.setText(listaZonas.get(i).getDescripcionZona());
                    break;
            }
        }
    }

    public void iniciaConteo(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
        int tag = Integer.parseInt(view.getTag().toString());

        if(listaZonas.get(tag - 1).getZonaValida() != ZONA_NO_PERMITIDA) {
            Intent intent = new Intent(getApplicationContext(), CargaCodigosBarraActivity.class);
            intent.putExtra("folio", folio);
            intent.putExtra("nombreEmpleado", nombreEmpleado);
            intent.putExtra("numeroEmpleado", numeroEmpleado);
            intent.putExtra("nombreTienda", nombreTienda);
            intent.putExtra("nombreZona", listaZonas.get(tag - 1).getNombreCorto());
            intent.putExtra("descargaCatalogo", true);
            intent.putExtra("idZona", listaZonas.get(tag - 1).getIdZona());
            startActivity(intent);
        } else {
            String mensaje = null;

            if(listaZonas.get(tag - 1).getEstatusZona() == ZONA_INICIADA) {
                mensaje = "Esta zona se está verificando por: " + listaZonas.get(tag - 1).getNombreUsuario();
            } else if(listaZonas.get(tag - 1).getEstatusZona() == ZONA_FINALIZADA) {
                mensaje = "Esta zona fue verificada por: " + listaZonas.get(tag - 1).getNombreUsuario();
            }

            ViewDialog alert = new ViewDialog(CargaZonasDisponiblesActivity.this);
            alert.showDialog(CargaZonasDisponiblesActivity.this, mensaje, null, TiposAlert.ERROR);
        }


    }

    public void salirMenuFront(View view) {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("LOGOUT", true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }
}
