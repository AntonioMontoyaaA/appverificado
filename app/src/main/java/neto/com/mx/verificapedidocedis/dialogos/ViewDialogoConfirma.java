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
 * Created by yruizm on 14/12/16.
 */

public class ViewDialogoConfirma extends Dialog {

    private Context contexto;

    public interface ViewDialogoConfirmaListener {
        void onIncrementaContador();
        void onLimpiaCampo();
    }

    private ViewDialogoConfirmaListener listener;

    public ViewDialogoConfirma(Context context) {
        super(context);
        this.contexto = context;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setViewDialogoConfirmaListener(ViewDialogoConfirmaListener listener) {
        this.listener = listener;
    }

    public void showDialog(Activity activity, int cajasContadas, int cajasEmbarcadas){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_confirma_dialogo_generico);

        TextView textView = (TextView) dialog.findViewById(R.id.text_dialog_escaneadas);
        textView.setText((cajasContadas - 1) + " de " + cajasEmbarcadas);

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


        Button dialogButton = (Button) dialog.findViewById(R.id.btnSi);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onIncrementaContador();
                dialog.dismiss();
            }
        });

        Button dialogButtonNo = (Button) dialog.findViewById(R.id.btnNo);
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLimpiaCampo();
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
