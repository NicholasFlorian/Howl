package com.teamhowl.howl.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
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
import com.teamhowl.howl.utilities.BluetoothService;

import java.text.BreakIterator;

public class MessageActivity extends AppCompatActivity {

    /** Messenger for communicating with the service. */
    Messenger bluetoothServiceMessenger = null;
    boolean isMessengerBound;

    /** Our service connection for the Bluetooth Service*/
    ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            bluetoothServiceMessenger = new Messenger(service);
            isMessengerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            bluetoothServiceMessenger = null;
            isMessengerBound = false;
        }
    };

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
    public void onStart() {
        super.onStart();

        this.bindService(
                new Intent(this, BluetoothService.class),
                bluetoothServiceConnection,
                Context.BIND_AUTO_CREATE);
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

        String text = messageTextView.getText().toString();

        if (text.equals(""))
            return;

        int length = text.length();
        String piece;

        for (int i = 0; i < length; i += 56) {
            piece = text.substring(i, Math.min(length, i + 56));

            PendingBlock block = blockChain.buildSentMessage(piece);
            pendingBlockDao.insert(block);
        }

        messageTextView.setText("");

        blockChain.rerefresh();
        blockChain.buildAllMessages();
        messageAdapter.clear();
        messageAdapter.updateMessages(blockChain.getMessages());

        exchangeMessages();

        blockChain.rerefresh();
        blockChain.buildAllMessages();
        messageAdapter.clear();
        messageAdapter.updateMessages(blockChain.getMessages());
    }

    public void exchangeMessages(){

        android.os.Message message = android.os.Message.obtain(
                null,
                BluetoothService.MSG_ATTEMPT_AUTOCONNECT,
                0,
                0);

        Bundle bundle = new Bundle();
        bundle.putString(BluetoothService.KEY_SERVICE_DEVICE, chatId);

        message.setData(bundle);

        try {
            bluetoothServiceMessenger.send(message);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}