package com.avadna.luneblaze.helperClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    private static MySMSBroadcastReceiver.SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        int a=5;
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    mListener.messageReceived(message);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    int b=5;
                    break;
            }
        }
    }

    public  void bindListener(MySMSBroadcastReceiver.SmsListener listener) {
        mListener = listener;
    }

    public  void unbindListener() {
        mListener = null;
    }

    public interface SmsListener {
        public void messageReceived(String messageText);
    }
}