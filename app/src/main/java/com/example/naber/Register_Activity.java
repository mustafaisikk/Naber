package com.example.naber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register_Activity extends AppCompatActivity {

    private Toolbar action_bar_Register;
    private EditText Name,Phone,Password;
    private Button Create_button;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference Db_Ref = database.getReference();

    public void Create_new_user(String id,String Name,String Tel,String Pass){
        User user = new User(Name,Tel,Pass);
        Db_Ref.child("Users").child(id).setValue(user);
    }

    public void init(){
        action_bar_Register = findViewById(R.id.action_bar_register);
        setSupportActionBar(action_bar_Register);
        getSupportActionBar().setTitle("KAYIT OL");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Name = findViewById(R.id.register_Edittext_Un);
        Phone = findViewById(R.id.register_Edittext_Tp);
        Password = findViewById(R.id.register_Edittext_Pw);
        Create_button = findViewById(R.id.register_create_account);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);

        init();
        Create_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Create_New_Account();
            }
        });
    }

    private void Create_New_Account() {

        String name_new_account = Name.getText().toString();
        String phone_new_account = Phone.getText().toString();
        String password_new_account = Password.getText().toString();

        if(TextUtils.isEmpty(name_new_account) || TextUtils.isEmpty(phone_new_account) || TextUtils.isEmpty(password_new_account)){
            Toast.makeText(Register_Activity.this,"Lütfen Boş Alan Bırakmayınız ! ",Toast.LENGTH_LONG).show();
        }
        else{
            Create_new_user(phone_new_account,name_new_account,phone_new_account,password_new_account);
        }
    }
}
