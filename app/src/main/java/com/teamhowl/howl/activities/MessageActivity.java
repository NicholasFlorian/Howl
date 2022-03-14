package com.teamhowl.howl.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.teamhowl.howl.R;
import com.teamhowl.howl.controllers.MessageAdapter;
import com.teamhowl.howl.controllers.PendingBlockDao;
import com.teamhowl.howl.controllers.UserDao;
import com.teamhowl.howl.models.BlockChain;
import com.teamhowl.howl.models.Message;
import com.teamhowl.howl.models.User;
import com.teamhowl.howl.repositories.BlockRoomDatabase;
import com.teamhowl.howl.repositories.UserRoomDatabase;

public class MessageActivity extends AppCompatActivity {

    /** Bundle Keys */
    public static final String KEY_CHAT_ID = "KEY_CHAT_ID";

    private String chatId;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        chatId = getIntent().getExtras().getString(KEY_CHAT_ID);
        messageAdapter = new MessageAdapter(this);

        ListView messageListView = findViewById(R.id.listViewMessage);
        messageListView.setAdapter(messageAdapter);

        Button sendMessageButton = findViewById(R.id.sendMessageButton);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                BlockRoomDatabase.
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO remove Mock
        //BlockChain blockChain = new BlockChain(this, chatId);
        //blockChain.refresh();


        //messageAdapter.updateMessages(blockChain.getMessages());

        messageAdapter.add(new Message("Hello how are you?", 1));
        messageAdapter.add(new Message("I am well, how are you?", 2));
        messageAdapter.add(new Message("I am okay, I just got a dog.", 1));
        messageAdapter.add(new Message("That's cool, whats his name?", 2));
        messageAdapter.add(new Message("His name is Rosco.", 1));
        messageAdapter.add(new Message("That's cool!", 2));
    }

}