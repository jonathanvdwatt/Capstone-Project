package local.watt.mzansitravel.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by f4720431 on 2016/09/13.
 */
public class AlertDialogManager {
    private static final String TAG = AlertDialogManager.class.getSimpleName();

    private AlertDialog mAlertDialog;

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        mAlertDialog = new AlertDialog.Builder(context).create();
        mAlertDialog.setTitle(title);
        mAlertDialog.setMessage(message);
        mAlertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mAlertDialog.show();
    }
}
