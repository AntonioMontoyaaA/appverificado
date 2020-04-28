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

public class ViewDialogoErrorActivity extends Dialog {

    private Context contexto;

    public interface ViewDialogoErrorActivityListener {
        void onFinaliza();
        void onConteoDiferencias();
    }

    private ViewDialogoErrorActivity.ViewDialogoErrorActivityListener listener;

    public ViewDialogoErrorActivity(Context context) {
        super(context);
        this.contexto = context;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setViewDialogoErrorActivityListener(ViewDialogoErrorActivity.ViewDialogoErrorActivityListener listener) {
        this.listener = listener;
    }

    public void showDialog(Activity activity, String mensaje, boolean muestraBotonDiferencias){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_view_dialogo_error);

        System.out.println("*** mensaje " + mensaje);

        TextView texto = (TextView) dialog.findViewById(R.id.text_dialog);
        texto.setText("Te faltan " + mensaje + " cajas por contar");

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

        Button dialogButton = (Button) dialog.findViewById(R.id.btnFinalizar);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFinaliza();
                dialog.dismiss();
            }
        });

        Button dialogButtonNo = (Button) dialog.findViewById(R.id.btnDiferencias);
        dialogButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConteoDiferencias();
                dialog.dismiss();
            }
        });

        if(!muestraBotonDiferencias) {
            dialogButtonNo.setVisibility(View.GONE);
        }

        dialog.show();

    }
}
