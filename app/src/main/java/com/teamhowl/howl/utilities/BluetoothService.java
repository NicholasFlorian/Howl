package com.teamhowl.howl.utilities;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.teamhowl.howl.models.User;

import java.util.ArrayList;
import java.util.Date;

public class BluetoothService extends Service {
    private static final String TAG = "HOWL :: BluetoothService";

    /** Service commands */
    public static final int MSG_SAY_HELLO = -1;
    public static final int MSG_START_DISCOVERY = 1;
    public static final int MSG_CREATE_CHAT_ROOM = 2;
    public static final int MSG_EXCHANGE_BLOCKS = 3;

    /** Bundle Keys */
    public static final String KEY_DEVICE = "BLUETOOTH_SERVICE_DEVICE";

    /** Target we publish for clients to send messages to IncomingHandler */
    Messenger messenger;

    /** Threaded Bluetooth Operator */
    BluetoothOperator operator;

    /** Local user management */
    private ArrayList<User> localUsers;

    /** Lifecycle of our Operator */
    @Override
    public void onCreate() {

        Log.d(TAG, "Starting new BluetoothService");
        operator = new BluetoothOperator();
        operator.start();

        localUsers = new ArrayList<>();
    }

    @Override
    public void onDestroy(){

        Log.d(TAG, "Ending BluetoothService");
        operator.stop();
    }

    /** Services */
    public void startDiscovery() {
        Log.d(TAG, "Running : Start Discovery");

        if(operator.isDiscovering()) {

            Log.d(TAG, "Operator is already discovering.");
            operator.startDiscovery();
        }
        else {

            operator.startDiscovery();
        }
    }

    public void createChatRoom(Bundle bundle){
        Log.d(TAG, "Running : Create ChatRoom");

        BluetoothDevice device = bundle.getParcelable(KEY_DEVICE);
        operator.createChatRoom(device);
    }

    public void exchangeBlocks(Bundle bundle){
        Log.d(TAG, "Running : Exchange Blocks");

    }

    /** Handler of incoming messages from Fragments or Activities */
    private final class IncomingHandler extends Handler {

        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message message) {

            switch (message.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(applicationContext, "hello!", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_START_DISCOVERY:
                    startDiscovery();
                    break;
                case MSG_CREATE_CHAT_ROOM:
                    createChatRoom(message.getData());
                    break;
                case MSG_EXCHANGE_BLOCKS:
                    exchangeBlocks(message.getData());
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        messenger = new Messenger(new IncomingHandler(this));
        
        return messenger.getBinder();
    }

}
