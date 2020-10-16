package com.avadna.luneblaze.activities;

import android.content.IntentFilter;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.MySMSBroadcastReceiver;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.rest.SMSApiClient;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmsTestActivity extends AppBaseActivity {
    MySMSBroadcastReceiver mySMSBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_test);
        mySMSBroadcastReceiver=new MySMSBroadcastReceiver();


        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

// Starts SmsRetriever, which waits for ONE matching SMS message until timeout
// (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
// action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

// Listen for success/failure of the start Task. If in a background thread, this
// can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                // ...
              //  hitSendInvitationApi("8802807763");

                mySMSBroadcastReceiver.bindListener(new MySMSBroadcastReceiver.SmsListener() {
                    @Override
                    public void messageReceived(String messageText) {
                        Log.d("SMS",messageText);

                    }
                });
                registerReceiver(mySMSBroadcastReceiver,new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));


            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
            }
        });
    }


    private void hitSendInvitationApi(final String phone) {
       String SMS="<#> Your OTP code is 123ABC78 j2SUEiWVbqN";
        ApiInterface SMSapiService=SMSApiClient.getClient().create(ApiInterface.class);

        Call<String> call = SMSapiService.sendSMS(AES.decrypt(AppKeys.SMS_UNAME, AppKeys.enKey),
                AES.decrypt(AppKeys.SMS_PASS, AppKeys.enKey), "1",
                AppKeys.SMS_COMPANY_NAME, phone, SMS);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                int a=5;
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
            }
        });
    }
}
