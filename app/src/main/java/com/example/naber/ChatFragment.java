package com.example.naber;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View privateChatView;
    private RecyclerView chatList;

    private String currentUID;



    private DatabaseReference chatRef,usersREF;
    private FirebaseAuth auth;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        privateChatView= inflater.inflate(R.layout.fragment_chat, container, false);

        auth = FirebaseAuth.getInstance();
        currentUID = auth.getCurrentUser().getUid();

        chatRef = FirebaseDatabase.getInstance().getReference().child("Konuşmalar").child(currentUID);
        usersREF = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        chatList = privateChatView.findViewById(R.id.Chat_List);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return privateChatView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contats> options = new FirebaseRecyclerOptions.Builder<Contats>()
                .setQuery(chatRef, Contats.class).build();

        FirebaseRecyclerAdapter<Contats, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contats, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contats model) {
                final String userIDS = getRef(position).getKey();
                final String[] retImage = {"Default_image"};

                usersREF.child(userIDS).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            if(dataSnapshot.hasChild("image")){
                                retImage[0] = dataSnapshot.child("image").getValue().toString();

                                Picasso.get().load(retImage[0]).into(holder.Profil_resmi);
                            }

                            final String retName = dataSnapshot.child("name").getValue().toString();
                            final String retStatus = dataSnapshot.child("status").getValue().toString();

                            holder.Kullanici_Adi.setText(retName);

                            if(dataSnapshot.child("kullaniciDurumu").hasChild("state")){

                                String state = dataSnapshot.child("kullaniciDurumu").child("state").getValue().toString();
                                String date = dataSnapshot.child("kullaniciDurumu").child("date").getValue().toString();
                                String time = dataSnapshot.child("kullaniciDurumu").child("time").getValue().toString();

                                if(state.equals("çevrimiçi"))
                                {
                                    holder.Durum.setText("çevrimiçi");
                                }
                                if(state.equals("çevrimdışı"))
                                {
                                    holder.Durum.setText("Son Görülme: " +date +" "+ time);
                                }
                            }

                            else{
                                holder.Durum.setText("çevrimdışı");
                            }

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent ıntent = new Intent(getContext(), ChatActivity.class);
                                    ıntent.putExtra("visitUserID", userIDS);
                                    ıntent.putExtra("visitUserName", retName);
                                    ıntent.putExtra("visitUserImage", retImage[0]);
                                    startActivity(ıntent);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_displaylayouth , parent, false);

                return new ChatsViewHolder(view);
            }
        };

        chatList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {

        TextView Kullanici_Adi, Durum;
        CircleImageView Profil_resmi;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            Kullanici_Adi = itemView.findViewById(R.id.User_Display_Username);
            Durum = itemView.findViewById(R.id.User_Display_status);
            Profil_resmi = itemView.findViewById(R.id.UserDisplay_Profile_image);
        }
    }
}
