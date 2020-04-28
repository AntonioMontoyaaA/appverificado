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

public class ViewDialogoGenerico extends Dialog {
    private Context contexto;

    public interface ViewDialogoGenericoListener {
        void onVerde();
        void onRojo();
        void onExtra();
    }
    private ViewDialogoGenerico.ViewDialogoGenericoListener listener;

    public ViewDialogoGenerico(Context context) {
        super(context);
        this.contexto = context;
    }
    public void setViewDialogoGenericoListener(ViewDialogoGenerico.ViewDialogoGenericoListener listener) {
        this.listener = listener;
    }

    public void showDialog(Activity activity, String mensaje, String textoBtn1, String textoBtn2, String textoBtn3, boolean muestraBotones){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_view_dialogo_generico);

        TextView texto = (TextView) dialog.findViewById(R.id.text_dialog);
        texto.setText(mensaje);

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

        Button dialogVerde = (Button) dialog.findViewById(R.id.btn1);
        Button dialogRojo = (Button) dialog.findViewById(R.id.btn2);
        Button dialogExtra = (Button) dialog.findViewById(R.id.btn3);


        dialogVerde.setText(textoBtn1);
        dialogRojo.setText(textoBtn2);
        dialogExtra.setText(textoBtn3);


        dialogVerde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onVerde();
                dialog.dismiss();
            }
        });
        dialogRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRojo();
                dialog.dismiss();
            }
        });

        dialogExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onExtra();
            }
        });

        if(textoBtn2 == ""){
            dialogRojo.setVisibility(View.GONE);
        }
        if(textoBtn3 == ""){
            dialogExtra.setVisibility(View.GONE);
        }
        if(!muestraBotones) {
            dialogRojo.setVisibility(View.GONE);
            dialogExtra.setVisibility(View.GONE);
        }
        dialog.show();

    }

}
