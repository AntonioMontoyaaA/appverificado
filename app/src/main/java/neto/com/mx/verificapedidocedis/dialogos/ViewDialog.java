package neto.com.mx.verificapedidocedis.dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import neto.com.mx.verificapedidocedis.R;
import neto.com.mx.verificapedidocedis.utiles.TiposAlert;

/**
 * Created by yruizm on 31/10/16.
 */

public class ViewDialog extends Dialog {
    private Context context;
    public ViewDialog(Context context) {
        super(context);
        this.context = context;
    }

    public void showDialog(Activity activity, String msg, final Intent intent, TiposAlert tiposAlert){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_dialogo_generico);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        final MediaPlayer mp;

        if(tiposAlert == TiposAlert.ERROR) {
            mp = MediaPlayer.create(context, R.raw.escaneo_error_2);
        } else {
            mp = MediaPlayer.create(context, R.raw.mensaje_ok);
        }
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

        ImageView imagenFondo = (ImageView) dialog.findViewById(R.id.aBack);
        if(tiposAlert == TiposAlert.ALERT) {
            imagenFondo.setImageDrawable(context.getResources().getDrawable(R.drawable.img_alerta));
            imagenFondo.setBackgroundColor(context.getResources().getColor(R.color.colorAmarilloAlert));
        } else if(tiposAlert == TiposAlert.ERROR) {
            imagenFondo.setImageDrawable(context.getResources().getDrawable(R.drawable.img_invalido));
            imagenFondo.setBackgroundColor(context.getResources().getColor(R.color.colorRojoAlert));
        } else if(tiposAlert == TiposAlert.CORRECTO) {
            imagenFondo.setImageDrawable(context.getResources().getDrawable(R.drawable.img_valido));
            imagenFondo.setBackgroundColor(context.getResources().getColor(R.color.colorVerdeAlert));
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent != null) {
                    getContext().startActivity(intent);
                } else {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }
}
