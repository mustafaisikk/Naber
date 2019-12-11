package com.example.naber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button Button_login, Button_register;

    public void init(){
        Button_login = findViewById(R.id.i_have_account_button);
        Button_register = findViewById(R.id.create_new_account_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Login_intent = new Intent(MainActivity.this,Welcome_Activity.class);
                startActivity(Login_intent);
            }
        });

        Button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Register_intent = new Intent(MainActivity.this, Register_Activity.class);
                startActivity(Register_intent);

                /*
                Intent Register_intent = new Intent(MainActivity.this, PhoneLogin.class);
                startActivity(Register_intent);
                */
            }

        });
    }
}
