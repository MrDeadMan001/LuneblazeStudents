package com.avadna.luneblaze.helperClasses;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;
import android.text.Html;
import android.view.View;

public class CheckConnectivity extends BroadcastReceiver {
    Snackbar snackbar;
    CommonFunctions commonFunctions;

    @Override
    public void onReceive(Context context, Intent arg1) {

        commonFunctions=new CommonFunctions(context);
        try{
            if (snackbar == null) {
                snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                        Html.fromHtml("<font color=\"#ffffff\">No internet connection</font>"), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
            }

           // boolean isConnected = arg1.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            boolean isConnected = commonFunctions.is_internet_connected();

            if (!isConnected) {
                snackbar.show();
                //  Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            } else {
              //  Toast.makeText(context, context.getString(R.string.internet_connected), Toast.LENGTH_LONG).show();
                snackbar.dismiss();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
