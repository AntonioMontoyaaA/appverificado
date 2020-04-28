package neto.com.mx.verificapedidocedis.dialogos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import neto.com.mx.verificapedidocedis.R;

/**
 * Created by yruizm on 24/07/17.
 */

public class PantallaInicioDialogo extends Dialog {

    public PantallaInicioDialogo(Context context) {
        super(context);
    }

    public void showDialog(Activity activity, String nombreTienda, String folio, int cajasPorContar){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_pantalla_inicio_dialogo);

        TextView nombreTiendaText = (TextView) dialog.findViewById(R.id.text_dialog_nombreTienda);
        nombreTiendaText.setText(nombreTienda);

        TextView folioText = (TextView) dialog.findViewById(R.id.text_dialog_folio);
        folioText.setText(folio);

        TextView cajasText = (TextView) dialog.findViewById(R.id.text_dialog_cajasPorRecibir);
        cajasText.setText(String.valueOf(cajasPorContar));


        Button dialogButton = (Button) dialog.findViewById(R.id.iniciaBoton);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
