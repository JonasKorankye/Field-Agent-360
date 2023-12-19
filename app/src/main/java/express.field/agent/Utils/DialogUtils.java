package express.field.agent.Utils;

import android.app.Activity;
import android.content.Context;

import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import express.field.agent.R;

public class DialogUtils {

    public void basicDialog(Activity activity) {


    MaterialDialog mDialog = new MaterialDialog.Builder(activity)
            .setTitle("Delete?")
            .setMessage("Are you sure want to delete this file?")
            .setAnimation(R.raw.delete_bin)
            .setCancelable(false)
            .setPositiveButton("Yes", R.drawable.ic_baseline_check_24, new MaterialDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    // Delete Operation
                }
            })
            .setNegativeButton("Cancel", R.drawable.ic_dialog_close_dark, new MaterialDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    dialogInterface.dismiss();
                }
            })
            .build();

    // Show Dialog
        mDialog.show();

}
}
