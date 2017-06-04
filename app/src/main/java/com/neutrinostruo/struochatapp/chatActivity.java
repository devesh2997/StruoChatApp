package com.neutrinostruo.struochatapp;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;
import android.net.Uri;


import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
/*import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;*/
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import android.support.v7.widget.LinearLayoutManager;

public class chatActivity extends AppCompatActivity {

    //Firebase authentication, database and storage instances set up.
    private FirebaseAuth auth ;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
   /* private FirebaseStorage storage;
    private StorageReference storageReference;*/
    private FirebaseUser user;
    private Context context;
    FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseRecyclerAdapter<chatPacket, ChatHolder> mRecyclerViewAdapter;

    //Creating UI elements objects
    private Toolbar chatToolbar ;
    private RecyclerView chatRecyclerView ;
    private LinearLayout messageInputContainer;
    private TextView chatMessageInput ;
    private ImageButton sendMessageButton ;
    private FloatingActionButton uploadFileFAB ;
    private LinearLayoutManager mManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar chatToolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(chatToolbar);
        context = getApplicationContext();
        mManager =new LinearLayoutManager(chatActivity.this);
        /*authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
                user = auth.getCurrentUser();
            }
        };*/

        //test code.
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword("ananddevesh22@gmail.com","devdas23").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                user = auth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName("Jane Q. User")
                        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                  Toast.makeText(chatActivity.this,"updated",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        /*storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();*/


        //Get references to UI elements.
        chatToolbar = (Toolbar) findViewById(R.id.chatToolbar);
        chatRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        messageInputContainer = (LinearLayout) findViewById(R.id.messageInputContainer);
        chatMessageInput = (TextView) findViewById(R.id.chatMessageInput);
        sendMessageButton = (ImageButton) findViewById(R.id.sendMessageButton);
        uploadFileFAB = (FloatingActionButton) findViewById(R.id.uploadFileFAB);

        //Listener for send button.
        sendMessageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!chatMessageInput.getText().equals(null)){
                    chatPacket newChat = collectChatData();
                    pushToFirebase(newChat);
                    chatMessageInput.setText(null);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
       /* auth.addAuthStateListener(authStateListener);
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(chatActivity.this, DAloginActivity.class);
            startActivity(intent);
        } else {*/
            chatRecyclerView.setLayoutManager(mManager);
            attachRecyclerViewAdapter();
        /*}*/
    }

    public chatPacket collectChatData(){
        chatPacket newChat ;
        String messageReceiverID = null;
        String messageSenderID = user.getDisplayName();
        String message = chatMessageInput.getText().toString().trim();
        /*String chatID = user.getUid().concat()*/
        Calendar calendar = Calendar.getInstance();
        String chatDate = new SimpleDateFormat("HH:mm").format(calendar.getTime());

        newChat = new chatPacket(messageReceiverID, messageSenderID, message, chatDate);
        return newChat;
    }

    public void pushToFirebase(chatPacket newChat){
        databaseReference.child("Chats").push().setValue(newChat).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(chatActivity.this,"message sent",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        View view;
        private final TextView messageSenderName;
        private final TextView messageView;
        private final LinearLayout chatContainer;
        private final TextView messageTimeView;

        public ChatHolder(View itemView) {
            super(itemView);
            view = itemView;

            messageSenderName = (TextView) itemView.findViewById(R.id.messageSenderName);
            messageView = (TextView) itemView.findViewById(R.id.messageView);
            chatContainer = (LinearLayout) itemView.findViewById(R.id.chatContainer);
            messageTimeView = (TextView) itemView.findViewById(R.id.messageTimeView) ;
        }

        public void setisSender(boolean isSender) {

            if (isSender) {
                chatContainer.setBackgroundResource(R.drawable.chat_view_background_resource_sender);
                chatContainer.setGravity(Gravity.END);

                messageSenderName.setEnabled(false);
                messageTimeView.setGravity(Gravity.END);
            } else {
                chatContainer.setBackgroundResource(R.drawable.chat_view_background_resource_receiver);
                chatContainer.setGravity(Gravity.START);
                messageSenderName.setGravity(Gravity.START);
                messageTimeView.setGravity(Gravity.END);
            }
        }
    }

    public void attachRecyclerViewAdapter() {

        Query lastFifty = databaseReference.child("chats").limitToLast(50);
        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<chatPacket, ChatHolder>(chatPacket.class, R.layout.chat_message_holder_1user,ChatHolder.class,lastFifty) {
            @Override
            protected void populateViewHolder(ChatHolder viewHolder, chatPacket newChat, int position) {
                viewHolder.messageSenderName.setText(newChat.getMessageSenderID());
                viewHolder.messageView.setText(newChat.getMessage());
                viewHolder.messageTimeView.setText(newChat.getChatDate());
                FirebaseUser currentUser = auth.getCurrentUser();
                if(currentUser!=null && newChat.getMessageSenderID().equals(currentUser.getUid())){
                    viewHolder.setisSender(true);
                }else {
                    viewHolder.setisSender(false);
                }

            }
        };


        mRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(chatRecyclerView, null, mRecyclerViewAdapter.getItemCount());
            }
        })       ;

        chatRecyclerView.setAdapter(mRecyclerViewAdapter);
    }

}
