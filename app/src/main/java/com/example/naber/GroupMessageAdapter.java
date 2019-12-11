package com.example.naber;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder>{

    private List<GroupMessages> groupMessagesList;
    private FirebaseAuth auth;
    private DatabaseReference UsersRef;

    public GroupMessageAdapter(List<GroupMessages> groupMessagesList) {
        this.groupMessagesList = groupMessagesList;
    }


    public class GroupMessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMEssageText,receiverMessageText;
        public CircleImageView receiverProfileImage;
        private ImageView messageSenderPicture,messageReceiverPicture;


        public GroupMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMEssageText = itemView.findViewById(R.id.sender_message_test);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_test);
            receiverProfileImage = itemView.findViewById(R.id.message_profil_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
        }
    }


    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layouth,parent,false);
        auth = FirebaseAuth.getInstance();

        return new GroupMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupMessageViewHolder holder, int position) {

        String messageSenderID = auth.getCurrentUser().getUid();
        GroupMessages messages = groupMessagesList.get(position);

        String fromUserID = messages.getUserID();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(fromUserID);
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("image"))
                {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMEssageText.setVisibility(View.GONE);
        holder.messageSenderPicture.setVisibility(View.GONE);
        holder.messageReceiverPicture.setVisibility(View.GONE);


        if(fromUserID.equals(messageSenderID))
        {
            holder.senderMEssageText.setVisibility(View.VISIBLE);
            holder.senderMEssageText.setBackgroundResource(R.drawable.sender_messages_layouth);
            holder.senderMEssageText.setTextColor(Color.BLACK);
            holder.senderMEssageText.setText(messages.getMessage() + "\n \n"+ messages.getTime() + " - " + messages.getDate());

        }
        else {

            holder.receiverMessageText.setVisibility(View.VISIBLE);
            holder.receiverProfileImage.setVisibility(View.VISIBLE);


            holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layouth);
            holder.receiverMessageText.setTextColor(Color.BLACK);
            holder.receiverMessageText.setText(messages.getName()+" : "+messages.getMessage() + "\n \n"+ messages.getTime() + " - " + messages.getDate());

        }


    }

    @Override
    public int getItemCount() {
        return groupMessagesList.size();
    }


}
