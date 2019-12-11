package com.example.naber;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class RequestFragment extends Fragment {

    private View requestFragmentView;
    private RecyclerView myRequestList;

    private String currentUserId;
    private DatabaseReference myRequestsReferance, usersRef, contactsRef;
    private FirebaseAuth auth;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView =  inflater.inflate(R.layout.fragment_request, container, false);
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        myRequestsReferance = FirebaseDatabase.getInstance().getReference().child("Mesaj istekleri");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Konuşmalar");

        myRequestList = requestFragmentView.findViewById(R.id.Chat_requests_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));


        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contats> options = new FirebaseRecyclerOptions.Builder<Contats>()
                .setQuery(myRequestsReferance.child(currentUserId), Contats.class).build();

        FirebaseRecyclerAdapter<Contats, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Contats, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contats model) {

                holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);

                final String listUsers = getRef(position).getKey();

                DatabaseReference getTypeRef = getRef(position).child("istek_tipi").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            String type = dataSnapshot.getValue().toString();
                            if(type.equals("alıcı")){

                                usersRef.child(listUsers).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild("image")){

                                            final String requestUserimage = dataSnapshot.child("image").getValue().toString();

                                            Picasso.get().load(requestUserimage).placeholder(R.drawable.profile_image).into(holder.Profil_resmi);
                                        }

                                        final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                        final String requestUserstatus = dataSnapshot.child("status").getValue().toString();

                                        holder.Kullanici_Adi.setText(requestUserName);
                                        holder.Durum.setText("Senden Cevap Bekliyor");


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]{
                                                        "Kabul et",
                                                        "Reddet"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(requestUserName + "Mesaj isteği");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(which == 0)
                                                        {

                                                            contactsRef.child(currentUserId).child(listUsers).child("Konuşmalar").setValue("başladı")
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                contactsRef.child(listUsers).child(currentUserId).child("Konuşmalar").setValue("başladı")
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if(task.isSuccessful()){

                                                                                                    myRequestsReferance.child(currentUserId).child(listUsers)
                                                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful())
                                                                                                            {
                                                                                                                myRequestsReferance.child(listUsers).child(currentUserId)
                                                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if(task.isSuccessful())
                                                                                                                        {
                                                                                                                            Toast.makeText(getContext(), "Yeni Konuşma Oluşturuldu...", Toast.LENGTH_SHORT).show();

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
                                                                        }
                                                                    });
                                                        }
                                                        if(which == 1)
                                                        {

                                                            myRequestsReferance.child(currentUserId).child(listUsers)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        myRequestsReferance.child(listUsers).child(currentUserId)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    Toast.makeText(getContext(), "Yeni Konuşma Oluşturuldu...", Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                });

                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            else if(type.equals("gonderici")){
                                Button requestSentButton = holder.itemView.findViewById(R.id.request_accept_button);
                                requestSentButton.setText("İstek Gönderildi");

                                holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.INVISIBLE);


                                usersRef.child(listUsers).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild("image")){

                                            final String requestUserimage = dataSnapshot.child("image").getValue().toString();

                                            Picasso.get().load(requestUserimage).placeholder(R.drawable.profile_image).into(holder.Profil_resmi);
                                        }

                                        final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                        final String requestUserstatus = dataSnapshot.child("status").getValue().toString();

                                        holder.Kullanici_Adi.setText(requestUserName);
                                        holder.Durum.setText(requestUserName + " istek Gönderdin");


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]{
                                                        "Geri Çek"
                                                };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Zaten İstek Gönderildi");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(which == 0)
                                                        {

                                                            myRequestsReferance.child(currentUserId).child(listUsers)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        myRequestsReferance.child(listUsers).child(currentUserId)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    Toast.makeText(getContext(), "İstek İptal Edildi...", Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                });

                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
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
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_displaylayouth, parent, false);
                RequestViewHolder holder = new RequestViewHolder(view);
                return holder;
            }
        };

        myRequestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView Kullanici_Adi, Durum;
        CircleImageView Profil_resmi;
        Button requests_accept_button,requests_cancel_button;


        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            Kullanici_Adi = itemView.findViewById(R.id.User_Display_Username);
            Durum = itemView.findViewById(R.id.User_Display_status);
            Profil_resmi = itemView.findViewById(R.id.UserDisplay_Profile_image);
            requests_accept_button = itemView.findViewById(R.id.request_accept_button);
            requests_cancel_button = itemView.findViewById(R.id.request_cancel_button);
        }
    }
}
