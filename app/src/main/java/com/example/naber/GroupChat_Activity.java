package com.example.naber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChat_Activity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ImageButton SendButton;
    private EditText message;

    private final List<GroupMessages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private GroupMessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference,Grup_Message_referance,Grup_Message_keyref,Rootref;

    private String Grup_Adi, Kullanici_ID, Kullanici_Adi,Kullanıcı_tarih,Kullanıcı_zaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_);

        Grup_Adi = getIntent().getExtras().get("Grup_Name").toString();

        auth = FirebaseAuth.getInstance();
        Kullanici_ID = auth.getCurrentUser().getUid();
        Rootref = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        Grup_Message_referance = FirebaseDatabase.getInstance().getReference().child("Gruplar").child(Grup_Adi);
        init();
        Get_Users_info();

        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mesajları_VTKaydet();
                message.setText("");
            }
        });

        Rootref.child("Gruplar").child(Grup_Adi).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                GroupMessages messages = dataSnapshot.getValue(GroupMessages.class);

                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();

                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void Mesajları_VTKaydet()
    {
        String mesaj = message.getText().toString();
        String Message_key = Grup_Message_referance.push().getKey();

        if(mesaj.isEmpty()){

            return;
        }
        else{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            Kullanıcı_tarih = simpleDateFormat.format(calendar.getTime());

            Calendar calendar_zaman = Calendar.getInstance();
            SimpleDateFormat simpletimeFormat = new SimpleDateFormat("hh:mm a");
            Kullanıcı_zaman = simpletimeFormat.format(calendar_zaman.getTime());

            HashMap<String,Object> Grup_Mesaj_Anahtarı = new HashMap<>();

            Grup_Message_referance.updateChildren(Grup_Mesaj_Anahtarı);
            Grup_Message_keyref = Grup_Message_referance.child(Message_key);

            HashMap<String,Object> Message_info = new HashMap<>();

                Message_info.put("userID",Kullanici_ID);
                Message_info.put("name",Kullanici_Adi);
                Message_info.put("message",mesaj);
                Message_info.put("date",Kullanıcı_tarih);
                Message_info.put("time",Kullanıcı_zaman);
            Grup_Message_keyref.updateChildren(Message_info);
           }
    }

    private void Get_Users_info()
    {
        databaseReference.child(Kullanici_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Kullanici_Adi = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init()
    {


        mtoolbar =findViewById(R.id.group_chat_actinbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle(Grup_Adi);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        SendButton = findViewById(R.id.group_chat_ımage);
        message = findViewById(R.id.Group_chat_EditText);

        messageAdapter = new GroupMessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.group_message_list_bar);
        linearLayoutManager = new LinearLayoutManager(this);

        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

    }
}
