package com.example.naber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLogin extends AppCompatActivity {

    private Button Send_code,Verify_button;
    private EditText Phone_number,Verify_number;
    private Toolbar actionbar_login;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbaks;

    private ProgressDialog progressDialog;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        auth = FirebaseAuth.getInstance();
        init();
    }

    private void init() {
        Send_code = findViewById(R.id.Phone_login_code_send);
        Verify_button = findViewById(R.id.Phone_login_Verify_button);
        Phone_number = findViewById(R.id.Phone_Login_phoneNumber);
        Verify_number = findViewById(R.id.Phone_Login_Code);
        progressDialog =new ProgressDialog(this);

        actionbar_login=(Toolbar) findViewById(R.id.actionbar_phonelogin);
        setSupportActionBar(actionbar_login);
        getSupportActionBar().setTitle("Telefon Numarası Doğrulama");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String Phone_num = Phone_number.getText().toString();

                if(TextUtils.isEmpty(Phone_num))
                {
                    Toast.makeText(PhoneLogin.this, "Lütfen Numaranızı Giriniz", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    progressDialog.setTitle("Telefon Doğrulama");
                    progressDialog.setMessage("Lütfen Bekleyiniz, Telefonunuz bağlanmaya çalışıyor...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            Phone_num,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLogin.this,               // Activity (for callback binding)
                            callbaks);        // OnVerificationStateChangedCallbacks
                }
            }
        });

        Verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send_code.setVisibility(View.INVISIBLE);
                Phone_number.setVisibility(View.INVISIBLE);

                String Verification_code = Verify_number.getText().toString();

                if(TextUtils.isEmpty(Verification_code))
                    Toast.makeText(PhoneLogin.this, "Lütfen Doğrulama Kodunu Giriniz", Toast.LENGTH_SHORT).show();

                else
                {
                    progressDialog.setTitle("Doğrulama kodu");
                    progressDialog.setMessage("Lütfen Bekleyiniz...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, Verification_code);

                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbaks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e)
            {
                progressDialog.dismiss();

                Send_code.setVisibility(View.VISIBLE);
                Phone_number.setVisibility(View.VISIBLE);

                Verify_button.setVisibility(View.INVISIBLE);
                Verify_number.setVisibility(View.INVISIBLE);

            }

            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                mVerificationId = verificationId;
                mResendToken = token;

                progressDialog.dismiss();

                Send_code.setVisibility(View.INVISIBLE);
                Phone_number.setVisibility(View.INVISIBLE);

                Verify_button.setVisibility(View.VISIBLE);
                Verify_number.setVisibility(View.VISIBLE);
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(PhoneLogin.this, "Tebrikler Doğrulama başarılı oldu", Toast.LENGTH_SHORT).show();
                            SendtoMainActivity();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(PhoneLogin.this, "Hata : " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendtoMainActivity() {
        Intent intent = new Intent(PhoneLogin.this,AnaActivity.class);
        startActivity(intent);
        finish();
    }
}
