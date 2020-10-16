package com.avadna.luneblaze.update.activities.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.update.activities.LoginActivityNew;

public class RegistrationActivity1New extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_activity1_new);
        Button bt_next=findViewById(R.id.bt_next);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(RegistrationActivity1New.this, RegistrationActivity2New.class);
                startActivity(registerIntent);
            }
        });
    }
}