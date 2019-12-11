package com.example.naber;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessageLists;
    private FirebaseAuth auth;
    private DatabaseReference UsersRef;
    private StorageReference storageReference;



    public MessageAdapter(List<Messages> userMessageLists){
        this.userMessageLists = userMessageLists;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMEssageText,receiverMessageText;
        public CircleImageView receiverProfileImage;
        private ImageView messageSenderPicture,messageReceiverPicture;



        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMEssageText = itemView.findViewById(R.id.sender_message_test);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_test);
            receiverProfileImage = itemView.findViewById(R.id.message_profil_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");


        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layouth,parent,false);

        auth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
        String messageSenderID = auth.getCurrentUser().getUid();
        Messages messages = userMessageLists.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

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

        if(fromMessageType.equals("text"))
        {
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
                holder.receiverMessageText.setText(messages.getMessage() + "\n \n"+ messages.getTime() + " - " + messages.getDate());

            }
        }
        else if(fromMessageType.equals("image")){

            if(fromUserID.equals(messageSenderID)){
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPicture);
            }
            else {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPicture);
            }

        }
        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx")){

            if(fromUserID.equals(messageSenderID)){

                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/naber-cfc48.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=2d283347-5b97-45e4-ac48-0125368c2995")
                        .into(holder.messageSenderPicture);

            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/naber-cfc48.appspot.com/o/Image%20Files%2Ffile.png?alt=media&token=2d283347-5b97-45e4-ac48-0125368c2995")
                        .into(holder.messageReceiverPicture);
            }
        }

        if (fromUserID.equals(messageSenderID)){


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userMessageLists.get(position).getType().equals("pdf") || userMessageLists.get(position).getType().equals("docx") ){
                        CharSequence options[] = new CharSequence[]{

                                "Mesajı benden sil",
                                "Dosyayı indir ve aç",
                                "İptal",
                                "Herkesten sil"
                        };
                        AlertDialog.Builder builder =  new AlertDialog.Builder(holder.itemView.getContext());

                        builder.setTitle("Mesajı silinsin mi?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    DeleteSendMessage(position,holder);

                                    Intent intent = new Intent(holder.itemView.getContext(),AnaActivity.class);
                                    holder.itemView.getContext().startActivity(intent);

                                }
                                else if(which == 1){

                                    String pdf_name = userMessageLists.get(position).getMessageID();
                                    storageReference.child(userMessageLists.get(position).getMessageID() + "."+userMessageLists.get(position).getType()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Intent ıntent = new Intent(Intent.ACTION_VIEW, uri);
                                            holder.itemView.getContext().startActivity(ıntent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {

                                            Toast.makeText(holder.itemView.getContext(), "Birşeyler Ters Gitti", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                                else if(which == 3){
                                    DeleteMessageForEveryone(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),AnaActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }

                        });
                        builder.show();
                    }

                    else if(userMessageLists.get(position).getType().equals("text") ){
                        CharSequence options[] = new CharSequence[]{

                                "Mesajı benden sil",
                                "İptal",
                                "Herkesten sil"
                        };
                        AlertDialog.Builder builder =  new AlertDialog.Builder(holder.itemView.getContext());

                        builder.setTitle("Mesajı silinsin mi?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    DeleteSendMessage(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),AnaActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                else if(which == 2){
                                    DeleteMessageForEveryone(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),AnaActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }

                        });
                        builder.show();
                    }

                    else if(userMessageLists.get(position).getType().equals("image") ){
                        CharSequence options[] = new CharSequence[]{

                                "Mesajı benden sil",
                                "Resmi Görüntüle",
                                "İptal",
                                "Herkesten sil"
                        };
                        AlertDialog.Builder builder =  new AlertDialog.Builder(holder.itemView.getContext());

                        builder.setTitle("Mesajı silinsin mi?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    DeleteSendMessage(position,holder);

                                    Intent intent = new Intent(holder.itemView.getContext(),AnaActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                else if(which == 1){
                                    Intent intent = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                                    intent.putExtra("url",userMessageLists.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                else if(which == 3){
                                    DeleteMessageForEveryone(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),AnaActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }

                        });
                        builder.show();
                    }

                }
            });

        }
        else{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userMessageLists.get(position).getType().equals("pdf") || userMessageLists.get(position).getType().equals("docx") ){
                        CharSequence options[] = new CharSequence[]{

                                "Mesajı benden sil",
                                "Dosyayı indir ve aç",
                                "İptal"
                        };
                        AlertDialog.Builder builder =  new AlertDialog.Builder(holder.itemView.getContext());

                        builder.setTitle("Mesajı silinsin mi?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    DeleteReceivedMessage(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),AnaActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                else if(which == 1){

                                    String pdf_name = userMessageLists.get(position).getMessageID();
                                    storageReference.child(userMessageLists.get(position).getMessageID() + "."+userMessageLists.get(position).getType()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Intent ıntent = new Intent(Intent.ACTION_VIEW, uri);
                                            holder.itemView.getContext().startActivity(ıntent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {

                                            Toast.makeText(holder.itemView.getContext(), "Birşeyler Ters Gitti", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }

                        });
                        builder.show();
                    }

                    else if(userMessageLists.get(position).getType().equals("text") ){
                        CharSequence options[] = new CharSequence[]{

                                "Mesajı benden sil",
                                "İptal"
                        };
                        AlertDialog.Builder builder =  new AlertDialog.Builder(holder.itemView.getContext());

                        builder.setTitle("Mesajı silinsin mi?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    DeleteReceivedMessage(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),AnaActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }

                        });
                        builder.show();
                    }

                    else if(userMessageLists.get(position).getType().equals("image") ){
                        CharSequence options[] = new CharSequence[]{

                                "Mesajı sil",
                                "Resmi Görüntüle",
                                "İptal"
                        };
                        AlertDialog.Builder builder =  new AlertDialog.Builder(holder.itemView.getContext());

                        builder.setTitle("Mesajı silinsin mi?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    DeleteReceivedMessage(position,holder);
                                    Intent intent = new Intent(holder.itemView.getContext(),AnaActivity.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                                else if(which == 1){

                                    Intent intent = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                                    intent.putExtra("url",userMessageLists.get(position).getMessage());
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }

                        });
                        builder.show();
                    }

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return userMessageLists.size();
    }

    private void DeleteSendMessage(final int position, final MessageViewHolder holder){
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages").child(userMessageLists.get(position).getFrom())
                .child(userMessageLists.get(position).getTo())
                .child(userMessageLists.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Silme İşlemi Gerçekleşti", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(holder.itemView.getContext(), "Hata.", Toast.LENGTH_SHORT).show();
                }

            }
        }) ;
    }

    private void DeleteReceivedMessage(final int position, final MessageViewHolder holder){
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")
                .child(userMessageLists.get(position).getTo())
                .child(userMessageLists.get(position).getFrom())
                .child(userMessageLists.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Silme İşlemi Gerçekleşti", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(holder.itemView.getContext(), "Hata.", Toast.LENGTH_SHORT).show();
                }

            }
        }) ;
    }

    private void DeleteMessageForEveryone(final int position, final MessageViewHolder holder){
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")
                .child(userMessageLists.get(position).getTo())
                .child(userMessageLists.get(position).getFrom())
                .child(userMessageLists.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    RootRef.child("Messages")
                            .child(userMessageLists.get(position).getFrom())
                            .child(userMessageLists.get(position).getTo())
                            .child(userMessageLists.get(position).getMessageID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(holder.itemView.getContext(), "Silme İşlemi Gerçekleşti", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(holder.itemView.getContext(), "Hata.", Toast.LENGTH_SHORT).show();
                }

            }
        }) ;
    }
}
