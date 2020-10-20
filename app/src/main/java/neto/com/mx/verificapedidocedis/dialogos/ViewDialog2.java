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

public class ViewDialog2 extends Dialog {
    private Context context;

    public ViewDialog2(Context context) {
        super(context);
        this.context = context;
    }

    public void showDialog(Activity activity, String msg, Intent intent, TiposAlert tiposAlert){
        showDialogDismiss(activity, msg, intent, tiposAlert, null);
    }

    public void showDialog(Activity activity, String msg, Intent intent, TiposAlert tiposAlert, long cerrarEnMilisegundos){
        showDialogDismiss(activity, msg, intent, tiposAlert, null, cerrarEnMilisegundos);
    }

    public void showDialogDismiss(Activity activity, String msg, final Intent intent, TiposAlert tiposAlert,
                                  final OnCancelListener dismissLintener){
        showDialogDismiss(activity, msg, intent, tiposAlert, null, 0);
    }


    public void showDialogDismiss(Activity activity, String msg, final Intent intent, TiposAlert tiposAlert,
                                  final OnCancelListener dismissLintener, long cerrarEnMilisegundos)
    {

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        //dialog.setCancelable(false);
        dialog.setContentView( R.layout.activity_dialogo_generico);
        dialog.setOnCancelListener(dismissLintener);

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
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }
            }
        };
        timer.start();

        ImageView imagenFondo = (ImageView) dialog.findViewById( R.id.aBack);
        ImageView imagenTipoDialogo = (ImageView)dialog.findViewById(R.id.a);

        if(tiposAlert == TiposAlert.ALERT) {
            imagenTipoDialogo.setImageDrawable(context.getResources().getDrawable(R.drawable.attention));
            imagenFondo.setBackgroundColor(context.getResources().getColor(R.color.colorAmarillo));
        } else if(tiposAlert == TiposAlert.ERROR) {
            imagenTipoDialogo.setImageDrawable(context.getResources().getDrawable(R.drawable.cross));
            imagenFondo.setBackgroundColor(context.getResources().getColor(R.color.colorVino));
        } else if(tiposAlert == TiposAlert.CORRECTO) {
            imagenTipoDialogo.setImageDrawable(context.getResources().getDrawable(R.drawable.check));
            imagenFondo.setBackgroundColor(context.getResources().getColor(R.color.colorOliva));
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent != null) {
                    getContext().startActivity(intent);
                } else {
                    dialog.cancel();
                }
            }
        });

        //Log.i(GlobalShare.logAplicaion, getClass().getName()+" : showDialogDismiss : futuro: "+cerrarEnMilisegundos);
        if( cerrarEnMilisegundos > 0) {
            //Log.i(GlobalShare.logAplicaion, getClass().getName()+" : showDialogDismiss : Entrando a countdown...");
            CountDownTimer countDownCerrar = new CountDownTimer(cerrarEnMilisegundos+0, 1000) {
                public void onTick(long restante){}
                public void onFinish(){
                    //Log.i(GlobalShare.logAplicaion, getClass().getName()+" : onFinish : Cerrando el dialogo...");
                    dialog.cancel();
                }
            };
            countDownCerrar.start();
        }

        dialog.show();
    }
}
