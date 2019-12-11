package com.example.naber;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Welcome_Activity extends AppCompatActivity {

    private Toolbar action_bar_login;
    private EditText Phone,Password;
    private Button Login_button;

    private FirebaseAuth auth;
    private FirebaseUser Current_user;

    private DatabaseReference usersRef;

    public void init(){
        action_bar_login = findViewById(R.id.actionbarlogin);
        setSupportActionBar(action_bar_login);
        getSupportActionBar().setTitle("GİRİŞ YAP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        Current_user = auth.getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");

        Phone = findViewById(R.id.register_Edittext_Ph);
        Password = findViewById(R.id.register_Edittext_Pw);;
        Login_button = findViewById(R.id.Login_Button);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();

        Login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login_user();
            }
        });
    }

    private void Login_user() {
        String Phone_Test = Phone.getText().toString();
        String Password_Text = Password.getText().toString();

        auth.signInWithEmailAndPassword(Phone_Test,Password_Text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String currentUserID = auth.getCurrentUser().getUid();
                    String devicesToken = FirebaseInstanceId.getInstance().getToken();

                    usersRef.child(currentUserID).child("device_token").setValue(devicesToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Welcome_Activity.this,"Giriş Başarılı.",Toast.LENGTH_LONG).show();
                                Intent ıntent = new Intent(Welcome_Activity.this,AnaActivity.class);
                                ıntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(ıntent);
                                finish();
                            }
                        }
                    });



                }
                else{
                    Toast.makeText(Welcome_Activity.this,"Hatalı Giriş.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
