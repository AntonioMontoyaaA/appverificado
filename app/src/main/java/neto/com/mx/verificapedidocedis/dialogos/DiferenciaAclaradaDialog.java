package neto.com.mx.verificapedidocedis.dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import neto.com.mx.verificapedidocedis.R;

/**
 * Created by yruizm on 24/07/17.
 */

public class DiferenciaAclaradaDialog extends Dialog {

    private Context contexto;

    public DiferenciaAclaradaDialog(Context context) {
        super(context);
        this.contexto = context;
    }

    public void showDialog(Activity activity, String nombreArticulo, int cajasEscaneadas, int cajasEmbarcadas){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_diferencia_aclarada);

        final MediaPlayer mp = MediaPlayer.create(contexto, R.raw.escaneo_error_2);
        mp.start();

        CountDownTimer timer = new CountDownTimer(1800, 1800) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Nothing to do
            }

            @Override
            public void onFinish() {
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }
            }
        };
        timer.start();

        TextView textView = (TextView) dialog.findViewById(R.id.text_dialog_nombreArticulo);
        textView.setText(nombreArticulo);

        TextView textView2 = (TextView) dialog.findViewById(R.id.text_total);
        textView2.setText(cajasEscaneadas+"");


        Button dialogButton = (Button) dialog.findViewById(R.id.btnAceptar);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
