package com.example.naber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID, getMessageReceiverName, getMessageReceiverImage;
    private String messageSenderID, saveCurringTime, saveCurringDate, checker = "", myUrl="";

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private Toolbar chatToolBar;

    private ImageButton sendmessagebutton, sendFilesButton;
    private EditText sendMessageText;

    private FirebaseAuth auth;
    private DatabaseReference RootRef;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;


    private ProgressDialog loadingbar;
    private StorageTask uploadTask;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        messageSenderID = auth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        messageReceiverID = getIntent().getExtras().get("visitUserID").toString();
        getMessageReceiverName = getIntent().getExtras().get("visitUserName").toString();
        getMessageReceiverImage = getIntent().getExtras().get("visitUserImage").toString();

        init();


        userName.setText(getMessageReceiverName);
        Picasso.get().load(getMessageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);

        sendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        DisplayLastSeen();


        sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "Resim",
                        "PDF Dosyası",
                        "Ms Word Dosyası"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);

                builder.setTitle("Dosya Tipi");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            checker = "image";
                            Intent ıntent = new Intent();
                            ıntent.setType("image/*");
                            ıntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(ıntent, 438);
                        }
                        if(which == 1){
                            checker = "pdf";

                            Intent ıntent = new Intent();
                            ıntent.setType("application/pdf");
                            ıntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(ıntent.createChooser(ıntent,"Pdf Dosyasını Seçiniz"), 438);
                        }
                        if(which == 2){
                            checker = "docx";

                            Intent ıntent = new Intent();
                            ıntent.setType("application/msword");
                            ıntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(ıntent.createChooser(ıntent,"Ms Word Dosyasını Seçiniz"), 438);
                        }
                    }
                });

                builder.show();
            }
        });

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);

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

    private void SendMessage()
    {
        String Message = sendMessageText.getText().toString();

        if(TextUtils.isEmpty(Message))
        {
            Toast.makeText(this, "Lütfen Mesajınızı Giriniz...", Toast.LENGTH_SHORT).show();
        }
        else{
            String messageSenderRef = "Messages/" + messageSenderID + "/"+messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/"+messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(messageSenderID).child(messageReceiverID).push();

            String MessagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",Message);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);
            messageTextBody.put("to",messageReceiverID);
            messageTextBody.put("messageID",MessagePushID);
            messageTextBody.put("time",saveCurringTime);
            messageTextBody.put("date",saveCurringDate);

            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(messageSenderRef + "/" + MessagePushID ,messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + MessagePushID ,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                        Toast.makeText(ChatActivity.this, "Mesaj Gönderildi...", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ChatActivity.this, "HATA...", Toast.LENGTH_SHORT).show();

                    sendMessageText.setText("");
                }
            });
        }
    }

    private void init() {

        chatToolBar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);


        userImage = findViewById(R.id.custom_profile_image);
        userName = findViewById(R.id.custom_profile_name);
        userLastSeen =  findViewById(R.id.custom_LastSeen);

        sendmessagebutton = findViewById(R.id.send_message_button);
        sendFilesButton = findViewById(R.id.send_file_button);
        sendMessageText = findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = findViewById(R.id.private_message_list_bar);
        linearLayoutManager = new LinearLayoutManager(this);

        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        loadingbar = new ProgressDialog(this);

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurringDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurringTime = currentTime.format(calendar.getTime());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 438 && resultCode == RESULT_OK && data!=null && data.getData()!=null){

            loadingbar.setTitle("Belge Gönderiliyor");
            loadingbar.setMessage("Lütfen belge gönderilirken bekleyiniz...");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            fileUri = data.getData();


            if(!checker.equals("image")){

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                final String messageSenderRef = "Messages/" + messageSenderID + "/"+messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/"+messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(messageSenderID).child(messageReceiverID).push();

                final String MessagePushID = userMessageKeyRef.getKey();


                final StorageReference filePath = storageReference.child(MessagePushID+"."+checker);


                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message",task.getResult().getMetadata().getReference().getDownloadUrl().toString());
                            messageTextBody.put("name",fileUri.getLastPathSegment());
                            messageTextBody.put("type",checker);
                            messageTextBody.put("from",messageSenderID);
                            messageTextBody.put("to",messageReceiverID);
                            messageTextBody.put("messageID",MessagePushID);
                            messageTextBody.put("time",saveCurringTime);
                            messageTextBody.put("date",saveCurringDate);

                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(messageSenderRef + "/" + MessagePushID ,messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + MessagePushID ,messageTextBody);

                            RootRef.updateChildren(messageBodyDetails);
                            loadingbar.dismiss();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingbar.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double p = (100.0*taskSnapshot.getBytesTransferred()) / taskSnapshot.getBytesTransferred();
                        loadingbar.setMessage((int) p + " % Yüklendi...");
                    }
                });
            }
            else if(checker.equals("image")){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                final String messageSenderRef = "Messages/" + messageSenderID + "/"+messageReceiverID;
                final String messageReceiverRef = "Messages/" + messageReceiverID + "/"+messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(messageSenderID).child(messageReceiverID).push();

                final String MessagePushID = userMessageKeyRef.getKey();


                final StorageReference filePath = storageReference.child(MessagePushID+".jpg");

                uploadTask = filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful())
                        {

                            Uri downloadUrl = task.getResult();

                            myUrl = downloadUrl.toString();

                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message",myUrl);
                            messageTextBody.put("name",fileUri.getLastPathSegment());
                            messageTextBody.put("type",checker);
                            messageTextBody.put("from",messageSenderID);
                            messageTextBody.put("to",messageReceiverID);
                            messageTextBody.put("messageID",MessagePushID);
                            messageTextBody.put("time",saveCurringTime);
                            messageTextBody.put("date",saveCurringDate);

                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(messageSenderRef + "/" + MessagePushID ,messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + MessagePushID ,messageTextBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful())
                                    {
                                        loadingbar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Mesaj Gönderildi...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        loadingbar.dismiss();
                                        Toast.makeText(ChatActivity.this, "HATA...", Toast.LENGTH_SHORT).show();
                                    }

                                    sendMessageText.setText("");
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        System.out.println(exception);
                    }
                });
            }
            else{
                loadingbar.dismiss();
                Toast.makeText(this, "Dosya Seçilmedi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void DisplayLastSeen(){
        RootRef.child("Kullanicilar").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("kullaniciDurumu").hasChild("state")){

                    String state = dataSnapshot.child("kullaniciDurumu").child("state").getValue().toString();
                    String date = dataSnapshot.child("kullaniciDurumu").child("date").getValue().toString();
                    String time = dataSnapshot.child("kullaniciDurumu").child("time").getValue().toString();

                    if(state.equals("çevrimiçi"))
                    {
                        userLastSeen.setText("çevrimiçi");
                    }
                    if(state.equals("çevrimdışı"))
                    {
                        userLastSeen.setText("Son Görülme: " +date +" "+ time);
                    }
                }
                else{
                    userLastSeen.setText("çevrimdışı");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
