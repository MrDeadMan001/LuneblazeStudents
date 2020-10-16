package com.avadna.luneblaze.update.activities.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.update.activities.settings.SettingsActivityNew;

public class RegistrationActivity2New extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_activity2_new);

        Button bt_next=findViewById(R.id.bt_next);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(RegistrationActivity2New.this, SettingsActivityNew.class);
                startActivity(registerIntent);
            }
        });
    }
}