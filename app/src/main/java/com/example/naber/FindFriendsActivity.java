package com.example.naber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar Find_Friend_Toolbar;
    private RecyclerView recyclerView;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        reference = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        Find_Friend_Toolbar = findViewById(R.id.Find_friends_toolbar);
        setSupportActionBar(Find_Friend_Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Arkadaşlarını Bul");


        recyclerView = findViewById(R.id.Find_Friens_recycler_lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {

        super.onStart();

        FirebaseRecyclerOptions<Contats> options = new FirebaseRecyclerOptions.Builder<Contats>().setQuery(reference, Contats.class).build();



        FirebaseRecyclerAdapter<Contats,FindFriendsViewHolder> adapter= new FirebaseRecyclerAdapter<Contats, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull Contats model) {

                holder.Kullanici_Adi.setText(model.getName());
                holder.Durum.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.Profil_resmi);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();

                        Intent intent = new Intent(FindFriendsActivity.this,ProfileActivity.class);
                        intent.putExtra("Visit_user_id",visit_user_id);
                        startActivity(intent);
                    }
                });
            }


            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_displaylayouth,parent, false);

                FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                return viewHolder;

            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class FindFriendsViewHolder extends RecyclerView.ViewHolder{

        TextView Kullanici_Adi, Durum;
        CircleImageView Profil_resmi;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            Kullanici_Adi = itemView.findViewById(R.id.User_Display_Username);
            Durum = itemView.findViewById(R.id.User_Display_status);
            Profil_resmi = itemView.findViewById(R.id.UserDisplay_Profile_image);

        }
    }
}
