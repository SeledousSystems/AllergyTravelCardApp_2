package com.wright.paul.allergytravelcardapp.model;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.wright.paul.allergytravelcardapp.R;

public class DialogManager {
String TAG = "DialogManager";

    void showAlert(Context context, String title, String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(context);
        bld.setIcon(R.mipmap.ic_logo);
        bld.setTitle(title);
        bld.setMessage(message);
        bld.setNeutralButton("CLOSE", null);
        Log.d(TAG, "Showing showAlert dialog: " + message);
        bld.create();
        AlertDialog dialog = bld.show();

        //center the text in the showAlert
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }


}
