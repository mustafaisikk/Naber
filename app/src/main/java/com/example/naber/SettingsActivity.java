package com.example.naber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button Update_button;
    private EditText Username, Status;
    private CircleImageView User_Pimage;
    private String Kullanici_id;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private ProgressDialog loadingbar;

    private static final int Galerypick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        auth = FirebaseAuth.getInstance();
        Kullanici_id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        init();

        User_Pimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Galeryintent = new Intent();
                Galeryintent.setAction(Intent.ACTION_GET_CONTENT);
                Galeryintent.setType("image/*");
                startActivityForResult(Galeryintent,Galerypick);
            }
        });




        Update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update_Account();
            }
        });

        reference.child("Kullanicilar").child(Kullanici_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image")))
                {
                    String Temp_Uname = dataSnapshot.child("name").getValue().toString();
                    String Temp_status = dataSnapshot.child("status").getValue().toString();
                    String Temp_İmage = dataSnapshot.child("image").getValue().toString();

                    Username.setText(Temp_Uname);
                    Status.setText(Temp_status);
                    Picasso.get().load(Temp_İmage).into(User_Pimage);

                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){

                    String Temp_Uname = dataSnapshot.child("name").getValue().toString();
                    String Temp_status = dataSnapshot.child("status").getValue().toString();

                    Username.setText(Temp_Uname);
                    Status.setText(Temp_status);

                }
                else{
                    Toast.makeText(SettingsActivity.this, "Lütfen Bilgilerinizi Doldurunuz", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void Update_Account() {
        String Kullanici_adi = Username.getText().toString();
        String Durum = Status.getText().toString();

        if(TextUtils.isEmpty(Kullanici_adi) || TextUtils.isEmpty(Durum))
        {
            Toast.makeText(SettingsActivity.this, "Lütfen Boş Alan Bırakmayınız", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> profile_map = new HashMap<>();
            profile_map.put("uid",Kullanici_id);
            profile_map.put("name",Kullanici_adi);
            profile_map.put("status",Durum);

            reference.child("Kullanicilar").child(Kullanici_id).updateChildren(profile_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        GotoAnaActivity();
                        Toast.makeText(SettingsActivity.this, "Profiliniz Başarı ile Güncellendi", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Hata : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Galerypick && resultCode == RESULT_OK && data !=null)
        {
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                loadingbar.setTitle("Profil Resmi Güncelle");
                loadingbar.setMessage("Lütfen Resim Güncellenirken bekleyiniz...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                final Uri resuUri = result.getUri();

                final StorageReference filepath = storageReference.child(Kullanici_id + ".jpg");

                filepath.putFile(resuUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String Download_url = uri.toString();

                                reference.child("Kullanicilar").child(Kullanici_id).child("image").setValue(Download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(SettingsActivity.this, "Kayıdınız Oluşturuldu", Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                        }
                                        else{
                                            String message = task.getException().toString();
                                            Toast.makeText(SettingsActivity.this, "Hata : " + message, Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });

            }
        }
    }

    private void GotoAnaActivity(){
        Intent ıntent = new Intent(SettingsActivity.this,AnaActivity.class);
        ıntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(ıntent);
        finish();
    }

    private void init() {
        toolbar = findViewById(R.id.Settings_chat_actinbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ayarlar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Update_button = findViewById(R.id.Settings_update_button);
        Username = findViewById(R.id.Settings_User_Name);
        Status = findViewById(R.id.Settings_profile_status);
        User_Pimage = findViewById(R.id.set_profile_image);
        loadingbar = new ProgressDialog(this);
    }
}
