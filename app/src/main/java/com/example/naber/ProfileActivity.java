package com.example.naber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiver_User_Id,current_State,sender_User_ID;

    private CircleImageView visited_profile_image;
    private TextView user_profil_name,user_profil_status;
    private Button send_message,decline_message;

    private DatabaseReference reference,chat_request_referance,Contactsreferance, notificationRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        reference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        chat_request_referance = FirebaseDatabase.getInstance().getReference().child("Mesaj istekleri");
        Contactsreferance = FirebaseDatabase.getInstance().getReference().child("Konuşmalar");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Bildirimler");
        receiver_User_Id = getIntent().getExtras().get("Visit_user_id").toString();
        auth = FirebaseAuth.getInstance();
        sender_User_ID = auth.getCurrentUser().getUid();

        init();

        kullaniciBilgileri();

    }

    private void kullaniciBilgileri() {
        reference.child(receiver_User_Id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && dataSnapshot.hasChild("image")){

                    String user_Name = dataSnapshot.child("name").getValue().toString();
                    String user_Image = dataSnapshot.child("image").getValue().toString();
                    String user_Status = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(user_Image).placeholder(R.drawable.profile_image).into(visited_profile_image);

                    user_profil_name.setText(user_Name);
                    user_profil_status.setText(user_Status);

                    chat_Request();
                }
                else{
                    String user_Name = dataSnapshot.child("name").getValue().toString();
                    String user_Status = dataSnapshot.child("status").getValue().toString();

                    user_profil_name.setText(user_Name);
                    user_profil_status.setText(user_Status);
                    chat_Request();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void chat_Request() {

        chat_request_referance.child(sender_User_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(receiver_User_Id)){
                    String istek_tipi = dataSnapshot.child(receiver_User_Id).child("istek_tipi").getValue().toString();

                    if(istek_tipi.equals("gonderici")){
                        current_State = "request_send";
                        send_message.setText("İsteği geri çek");
                    }
                    else if(istek_tipi.equals("alıcı")){
                        current_State = "request_received";
                        send_message.setText("Mesaj İsteğini Kabul Et");
                        decline_message.setVisibility(View.VISIBLE);
                        decline_message.setEnabled(true);
                        decline_message.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancel_chat_request();
                            }
                        });
                    }
                }
                else{
                    Contactsreferance.child(sender_User_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(receiver_User_Id))
                            {
                                current_State = "friends";
                                send_message.setText("Konuşmayı Bitir");

                                decline_message.setVisibility(View.INVISIBLE);
                                decline_message.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!sender_User_ID.equals(receiver_User_Id)){
            send_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send_message.setEnabled(false);

                    if(current_State.equals("new")){
                        Send_Chat_Request();
                    }
                    if(current_State.equals("request_send")){
                        cancel_chat_request();
                    }
                    if(current_State.equals("request_received")){
                        Accept_Chat_Request();
                    }
                    if(current_State.equals("friends")){
                        RemoveContats();
                    }
                }
            });
        }
        else{
            send_message.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveContats() {

        Contactsreferance.child(sender_User_ID).child(receiver_User_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Contactsreferance.child(receiver_User_Id).child(sender_User_ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                send_message.setEnabled(true);
                                current_State = "new";
                                send_message.setText("Mesaj Gönder");

                                decline_message.setVisibility(View.INVISIBLE);
                                decline_message.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }

    private void Accept_Chat_Request() {

        Contactsreferance.child(sender_User_ID).child(receiver_User_Id).child("Konuşma").setValue("başladı").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Contactsreferance.child(receiver_User_Id).child(sender_User_ID).child("Konuşma").setValue("başladı").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                chat_request_referance.child(sender_User_ID).child(receiver_User_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            chat_request_referance.child(receiver_User_Id).child(sender_User_ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    send_message.setEnabled(true);
                                                    current_State = "friends";
                                                    send_message.setText("Konuşmayı Bitir");

                                                    decline_message.setVisibility(View.INVISIBLE);
                                                    decline_message.setEnabled(false);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    private void cancel_chat_request() {
        chat_request_referance.child(sender_User_ID).child(receiver_User_Id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chat_request_referance.child(receiver_User_Id).child(sender_User_ID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                send_message.setEnabled(true);
                                current_State = "new";
                                send_message.setText("Mesaj Gönder");

                                decline_message.setVisibility(View.INVISIBLE);
                                decline_message.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void Send_Chat_Request() {
        chat_request_referance.child(sender_User_ID).child(receiver_User_Id).child("istek_tipi")
                .setValue("gonderici").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    chat_request_referance.child(receiver_User_Id).child(sender_User_ID)
                            .child("istek_tipi").setValue("alıcı").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                HashMap<String,String> chatNotificationMap = new HashMap<>();
                                chatNotificationMap.put("from",sender_User_ID);
                                chatNotificationMap.put("type","alıcı");

                                notificationRef.child(receiver_User_Id).push().setValue(chatNotificationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            send_message.setEnabled(true);
                                            current_State = "request_send";
                                            send_message.setText("İsteği geri çek");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });

    }

    private void init() {
        visited_profile_image = findViewById(R.id.Visited_profile_image);
        user_profil_name = findViewById(R.id.Visit_profile_Uname);
        user_profil_status = findViewById(R.id.Visit_profile_Status);
        send_message = findViewById(R.id.Visit_Profile_request_button);
        decline_message = findViewById(R.id.Visit_Profile_declare_request_button);

        current_State = "new";
    }
}
