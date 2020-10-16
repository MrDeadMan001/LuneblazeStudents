package com.avadna.luneblaze.update.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.update.activities.quiz.CreateQuizActivity;
import com.avadna.luneblaze.update.activities.registration.RegistrationActivity1New;

public class LoginActivityNew extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        TextView tv_sign_up=findViewById(R.id.tv_sign_up);
        tv_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(LoginActivityNew.this, RegistrationActivity1New.class);
                startActivity(registerIntent);
            }
        });

        Button bt_login=findViewById(R.id.bt_login);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(LoginActivityNew.this, CreateQuizActivity.class);
                startActivity(registerIntent);
            }
        });
    }
}