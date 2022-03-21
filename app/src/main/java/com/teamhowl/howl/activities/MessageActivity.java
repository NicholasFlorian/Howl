package com.teamhowl.howl.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.teamhowl.howl.R;
import com.teamhowl.howl.controllers.MessageAdapter;
import com.teamhowl.howl.controllers.PendingBlockDao;
import com.teamhowl.howl.controllers.UserDao;
import com.teamhowl.howl.models.BlockChain;
import com.teamhowl.howl.models.BlockChainStub;
import com.teamhowl.howl.models.Message;
import com.teamhowl.howl.models.PendingBlock;
import com.teamhowl.howl.models.User;
import com.teamhowl.howl.repositories.BlockRoomDatabase;
import com.teamhowl.howl.repositories.UserRoomDatabase;

import java.text.BreakIterator;

public class MessageActivity extends AppCompatActivity {

    /** Bundle Keys */
    public static final String KEY_CHAT_ID = "KEY_CHAT_ID";

    private String chatId;
    private BlockChain blockChain;
    private MessageAdapter messageAdapter;
    private TextView messageTextView;
    private BlockRoomDatabase blockRoomDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        blockRoomDatabase = BlockRoomDatabase.getDatabase(getApplicationContext());

        chatId = getIntent().getExtras().getString(KEY_CHAT_ID);
        messageAdapter = new MessageAdapter(this);
        blockChain = new BlockChain(this, chatId);

        ListView messageListView = findViewById(R.id.listViewMessage);
        messageListView.setAdapter(messageAdapter);

        messageTextView = findViewById(R.id.editTextMessage);

        Button sendButton = findViewById(R.id.buttonSend);

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                sendMessage();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        blockChain.rerefresh();
        blockChain.buildAllMessages();

        messageAdapter.clear();
        messageAdapter.updateMessages(blockChain.getMessages());
    }

    public void sendMessage(){

        PendingBlockDao pendingBlockDao = blockRoomDatabase.pendingBlockDao();

        PendingBlock block = blockChain.buildSentMessage(messageTextView.getText().toString());
        pendingBlockDao.insert(block);

        blockChain.rerefresh();
        blockChain.buildAllMessages();

        messageAdapter.clear();
        messageAdapter.updateMessages(blockChain.getMessages());

        messageTextView.setText("");
    }

}