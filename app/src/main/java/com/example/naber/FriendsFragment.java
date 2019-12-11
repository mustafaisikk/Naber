package com.example.naber;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
public class FriendsFragment extends Fragment {

    private View contactsView;
    private RecyclerView myContactsList;

    private DatabaseReference contactRef, usersRef;
    private FirebaseAuth auth;
    private String currentUserID;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView =  inflater.inflate(R.layout.fragment_friends, container, false);
        myContactsList = contactsView.findViewById(R.id.contacts_view);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        contactRef = FirebaseDatabase.getInstance().getReference().child("Konuşmalar").child(currentUserID);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contats>().setQuery(contactRef, Contats.class).build();

        FirebaseRecyclerAdapter<Contats, ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contats, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contats model) {
                String userIds = getRef(position).getKey();

                usersRef.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){

                            if(dataSnapshot.child("kullaniciDurumu").hasChild("state")){

                                String state = dataSnapshot.child("kullaniciDurumu").child("state").getValue().toString();
                                String date = dataSnapshot.child("kullaniciDurumu").child("date").getValue().toString();
                                String time = dataSnapshot.child("kullaniciDurumu").child("time").getValue().toString();

                                if(state.equals("çevrimiçi"))
                                {
                                    holder.onlineIcon.setVisibility(View.VISIBLE);
                                }
                                if(state.equals("çevrimdışı"))
                                {
                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            }

                            else{
                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }


                            if(dataSnapshot.hasChild("image")){
                                String profileImage = dataSnapshot.child("image").getValue().toString();
                                String profilestatus= dataSnapshot.child("status").getValue().toString();
                                String profilename = dataSnapshot.child("name").getValue().toString();

                                holder.Kullanici_Adi.setText(profilename);
                                holder.Durum.setText(profilestatus);
                                Picasso.get().load(profileImage).placeholder(R.drawable.profile_image).into(holder.Profil_resmi);
                            }
                            else{
                                String profilestatus= dataSnapshot.child("status").getValue().toString();
                                String profilename = dataSnapshot.child("name").getValue().toString();

                                holder.Kullanici_Adi.setText(profilename);
                                holder.Durum.setText(profilestatus);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_displaylayouth,parent,false);

                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }

    public  static class ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView Kullanici_Adi, Durum;
        CircleImageView Profil_resmi;
        ImageView onlineIcon;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            Kullanici_Adi = itemView.findViewById(R.id.User_Display_Username);
            Durum = itemView.findViewById(R.id.User_Display_status);
            Profil_resmi = itemView.findViewById(R.id.UserDisplay_Profile_image);
            onlineIcon = itemView.findViewById(R.id.User_Display_Online_İcon);
        }
    }
}
