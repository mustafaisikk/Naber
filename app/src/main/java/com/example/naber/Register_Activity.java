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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


public class Register_Activity extends AppCompatActivity {

    private Toolbar action_bar_Register;
    private EditText Name,Phone,Password;
    private Button Create_button;

    private FirebaseAuth auth;
    private DatabaseReference RootReferance;

    public void init(){
        action_bar_Register = findViewById(R.id.action_bar_register);
        setSupportActionBar(action_bar_Register);
        getSupportActionBar().setTitle("KAYIT OL");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance();
        RootReferance = FirebaseDatabase.getInstance().getReference();
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

        final String name_new_account = Name.getText().toString();
        final String phone_new_account = Phone.getText().toString();
        final String password_new_account = Password.getText().toString();

        if(TextUtils.isEmpty(name_new_account) || TextUtils.isEmpty(phone_new_account) || TextUtils.isEmpty(password_new_account)){
            Toast.makeText(Register_Activity.this,"Lütfen Boş Alan Bırakmayınız ! ",Toast.LENGTH_LONG).show();
        }
        else{
            auth.createUserWithEmailAndPassword(phone_new_account,password_new_account).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    String devicesToken = FirebaseInstanceId.getInstance().getToken();


                    String kullanıcı_id = auth.getCurrentUser().getUid();
                    RootReferance.child("Kullanicilar").child(kullanıcı_id).child("name").setValue(name_new_account);
                    RootReferance.child("Kullanicilar").child(kullanıcı_id).child("uid").setValue(kullanıcı_id);
                    RootReferance.child("Kullanicilar").child(kullanıcı_id).child("status").setValue("Merhaba...");
                    RootReferance.child("Kullanicilar").child(kullanıcı_id).child("device_token").setValue(devicesToken);

                    Toast.makeText(Register_Activity.this,"Kayıt Oluşturuldu",Toast.LENGTH_LONG).show();
                    Intent ıntent = new Intent(Register_Activity.this,Welcome_Activity.class);
                    ıntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(ıntent);
                    finish();
                }
            });
        }
    }
}
