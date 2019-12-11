package com.example.naber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AnaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabsAdapter tabsAdapter;

    private String currentUserID;

    private FirebaseAuth auth;
    private DatabaseReference RootRef;

    public void init(){

        toolbar = findViewById(R.id.AnaActivity_ActionBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        auth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        viewPager = findViewById(R.id.AnaActivity_ViewPager);
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);

        tabLayout = findViewById(R.id.Anaactivity_Tabbar);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana);

        init();

    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser == null){
            Intent ıntent = new Intent(AnaActivity.this,MainActivity.class);
            ıntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(ıntent);
            finish();
        }
        else
        {
            updateUserStatus("çevrimiçi");

            String Kullanıcı_ID = auth.getCurrentUser().getUid();
            RootRef.child("Kullanicilar").child(Kullanıcı_ID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!(dataSnapshot.child("name").exists())){
                        GoToSettings();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser firebaseUser = auth.getCurrentUser();


        if(firebaseUser != null)
        {
            updateUserStatus("çevrimdışı");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseUser firebaseUser = auth.getCurrentUser();


        if(firebaseUser != null)
        {
            updateUserStatus("çevrimdışı");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_items,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.Quit){

            updateUserStatus("çevrimdışı");

           Quit();
        }
        if(item.getItemId() == R.id.Create_Group_Message){

            Create_Grup_Message();
        }
        if(item.getItemId() == R.id.User_Settings){
            GoToSettings();
        }
        if(item.getItemId() == R.id.Find_Friends){
            GotoFindFriends();
        }

        return true;
    }

    private void GotoFindFriends() {
        Intent intent = new Intent(AnaActivity.this,FindFriendsActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void GoToSettings() {
        Intent intent = new Intent(AnaActivity.this,SettingsActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void Create_Grup_Message() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AnaActivity.this,R.style.AlertDialog);
        builder.setTitle("Grup Adını Giriniz: ");
        final EditText Group_Name_Etext = new EditText(AnaActivity.this);
        Group_Name_Etext.setHint("Örnek: NABER");
        builder.setView(Group_Name_Etext);
        builder.setPositiveButton("Oluştur", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Grup_name = Group_Name_Etext.getText().toString();
                if(TextUtils.isEmpty(Grup_name))
                    Toast.makeText(AnaActivity.this,"Lütfen boş nırakmayınız.",Toast.LENGTH_LONG).show();
                else{
                    Yeni_Grup_Olustur(Grup_name);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void Quit() {
        auth.signOut();
        Intent ıntent = new Intent(AnaActivity.this,MainActivity.class);
        startActivity(ıntent);
        finish();
    }

    private void Yeni_Grup_Olustur(final String grup_name)
    {
        RootRef.child("Gruplar").child(grup_name).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(AnaActivity.this, grup_name+" başarılı bir şekilde oluşturuldu...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserStatus(String state)
    {
        String saveCurringTime, saveCurringDate;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurringDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurringTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time",saveCurringTime);
        onlineStateMap.put("date",saveCurringDate);
        onlineStateMap.put("state",state);


        currentUserID = auth.getCurrentUser().getUid();
        RootRef.child("Kullanicilar").child(currentUserID).child("kullaniciDurumu")
        .updateChildren(onlineStateMap);

    }
}
