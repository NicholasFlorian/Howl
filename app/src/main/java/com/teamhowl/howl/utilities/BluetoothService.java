package com.teamhowl.howl.utilities;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import com.teamhowl.howl.controllers.PendingBlockDao;
import com.teamhowl.howl.controllers.StashedBlockDao;
import com.teamhowl.howl.controllers.UserDao;
import com.teamhowl.howl.models.BlockChain;
import com.teamhowl.howl.models.PendingBlock;
import com.teamhowl.howl.models.StashedBlock;
import com.teamhowl.howl.models.User;
import com.teamhowl.howl.repositories.BlockRoomDatabase;
import com.teamhowl.howl.repositories.UserRoomDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothService extends Service {
    private static final String TAG = "HOWL :: BluetoothService:";

    /** Bluetooth service information */
    private static final String SERVICE_SECURE = "SERVICE_SECURE";
    private static final String SERVICE_INSECURE = "SERVICE_INSECURE";
    private static final UUID UUID_SECURE = ParcelUuid.fromString("00001101-0000-1000-8000-00805F9B34FB").getUuid();
    private static final UUID UUID_INSECURE = ParcelUuid.fromString("00001101-0000-1000-8000-00805F9B34FB").getUuid();
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    /** Thread states */
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /** Internal Ui Commands */
    public static final int UI_TOAST = 1;

    /** Internal Bundle Keys */
    public static final String UI_KEY_TEXT = "KEY_UI_TEXT";

    /** Incoming Handler Commands */
    public static final int MSG_START_DISCOVERY = 1;
    public static final int MSG_CREATE_CHAT_ROOM = 2;
    public static final int MSG_ATTEMPT_AUTO_CONNECT = 3;

    /** Bundle Keys */
    public static final String KEY_SERVICE_DEVICE = "KEY_SERVICE_DEVICE";

    /** Target we publish for clients to send messages to IncomingHandler */
    Messenger messenger;

    /** Bluetooth adapter */
    private BluetoothAdapter adapter;

    /** Bluetooth operation threads and their state */
    private AcceptThread acceptThread;
    private ConnectThread connectThread;
    private CommunicateThread communicateThread;
    private int currentState;

    /** Data Management */
    private BlockRoomDatabase blockRoomDatabase;
    private UserRoomDatabase userRoomDatabase;
    private ArrayList<BluetoothDevice> previouslyBondedDevices;
    private ArrayList<User> foreignUsers;

    /** Lifecycle of our service */
    @Override
    public void onCreate() {
        Log.d(TAG, "Starting Service:");

        this.adapter = BluetoothAdapter.getDefaultAdapter();

        this.acceptThread = null;
        this.connectThread = null;
        this.communicateThread = null;
        start();

        blockRoomDatabase = BlockRoomDatabase.getDatabase(getApplicationContext());
        userRoomDatabase = UserRoomDatabase.getDatabase(getApplicationContext());
        previouslyBondedDevices = new ArrayList<>();
        foreignUsers = new ArrayList<>();
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "Ending Service:");

        stop();
    }

    /** Utility functions*/
    private String getStateString(int state){

        switch(state){

            case STATE_NONE:
                return "None";
            case STATE_LISTEN:
                return "Listen";
            case STATE_CONNECTING:
                return "Connecting";
            case STATE_CONNECTED:
                return "Connected";
            default:
                return "Unknown";
        }
    }

    private String getSecureString(boolean secure){
        return secure ? "Secure" : "Insecure";
    }

    /** Status Operations
     *  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */

    private synchronized void updateState(int newState){

        String oldStateString = getStateString(currentState);
        String newStateString = getStateString(newState);

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Log.d(TAG, "Updating state of operator.");
        Log.d(TAG, "OldState : " + oldStateString);
        Log.d(TAG, "NewState : " + newStateString);

        if(acceptThread != null)
            Log.d(TAG, "Accept Thread : " + acceptThread.getState());
        else
            Log.d(TAG, "Accept Thread : null");

        if(connectThread != null)
            Log.d(TAG, "Connect Thread : " + connectThread.getState());
        else
            Log.d(TAG, "Connect Thread : null");

        if(communicateThread != null)
            Log.d(TAG, "Connected Thread : " + communicateThread.getState());
        else
            Log.d(TAG, "Connected Thread : null");

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        currentState = newState;
    }

    private synchronized void start() {
        Log.d(TAG, "start():");

        updateState(STATE_NONE);

        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (communicateThread != null) {
            communicateThread.cancel();
            communicateThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (acceptThread == null) {
            acceptThread = new AcceptThread(false);
            acceptThread.start();
        }
    }

    private synchronized void stop() {
        Log.d(TAG, "stop():");

        updateState(STATE_NONE);

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (communicateThread != null) {
            communicateThread.cancel();
            communicateThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
    }

    private synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect(): " + device);

        // Cancel any thread attempting to make a connection
        if (currentState == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

//        if (currentState == STATE_CONNECTED) {
//            if (connectThread != null) {
//                connectThread.cancel();
//                connectThread = null;
//            }
//        }

        // Cancel any thread currently running a connection
        if (communicateThread != null) {
            communicateThread.cancel();
            communicateThread = null;
        }

        // Start the thread to connect with the given device
        connectThread = new ConnectThread(device, secure);
        connectThread.start();
    }

    private synchronized void communicate(
            BluetoothSocket socket,
            BluetoothDevice device,
            final String socketType) {

        Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (communicateThread != null) {
            communicateThread.cancel();
            communicateThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        communicateThread = new CommunicateThread(socket, socketType);
        communicateThread.start();

        try{
            UserDao userDao = userRoomDatabase.userDao();

            /** Exchange User Ids and Generate ChatId **/
            Log.d(TAG, "Exchanging User Ids:");

            String localUserId = Crypto.generateUserId(adapter.getName());
            communicateThread.write(localUserId);
            String foreignUserId = communicateThread.readSafe(126);

            String chatId = Crypto.generateSortChatId(localUserId, foreignUserId);
            User user = userDao.findUserByChatId(chatId);

            // if the user does not exist create a chatroom, otherwise exchange blocks
            if(user == null)
                createChatRoom();
            else
                exchangeMessages();

            //disconnect(); //
        }
        catch(SecurityException e){
            Log.e(TAG, e.getMessage());
        }
    }

    private synchronized void createChatRoom() {

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Log.d(TAG, "createChatRoom:");

        sendThreadSafeToast("Creating Chatroom.");

        try {

            PendingBlockDao pendingBlockDao = blockRoomDatabase.pendingBlockDao();
            StashedBlockDao stashedBlockDao = blockRoomDatabase.stashedBlockDao();
            UserDao userDao = userRoomDatabase.userDao();

            /** Exchange User Ids and Generate ChatId **/
            Log.d(TAG, "Exchanging User Ids:");

            String localUserId = Crypto.generateUserId(adapter.getName());
            communicateThread.write(localUserId);
            String foreignUserId = communicateThread.readSafe(126);

            String chatId = Crypto.generateSortChatId(localUserId, foreignUserId);

            /** Exchange User Names **/
            Log.d(TAG, "Exchanging User Names:");

            String localUserName = adapter.getName();
            communicateThread.write(localUserName);
            String foreignUserName = communicateThread.read();

            User newUser = new User(chatId, foreignUserName);
            userDao.insert(newUser);

            /** Exchange Keys **/
            Log.d(TAG, "Exchanging Keys:");

            String keys[] = Crypto.generateKeyPair();
            String localPublicKey = keys[0];
            String localPrivateKey = keys[1];

            communicateThread.write(localPublicKey);
            String foreignPublicKey = communicateThread.readSafe(774);

            Key.store(
                getApplicationContext(),
                chatId,
                Key.PUBLIC_KEY,
                Key.LOCAL_KEY,
                localPublicKey);

            Key.store(
                getApplicationContext(),
                chatId,
                Key.PRIVATE_KEY,
                Key.LOCAL_KEY,
                localPrivateKey);

            Key.store(
                getApplicationContext(),
                chatId,
                Key.PUBLIC_KEY,
                Key.FOREIGN_KEY,
                foreignPublicKey);

            /** Exchange GENISIS BLOCKS **/
            Log.d(TAG, "Exchanging Blocks:");

            BlockChain blockChain = new BlockChain(getApplicationContext(), chatId);
            PendingBlock pendingBlock = blockChain.buildGenesisBlock();

            communicateThread.write(pendingBlock.getEncryptedBlock());
            String encryptedBlock = communicateThread.readSafe(1024);
            StashedBlock stashedBlock = new StashedBlock(chatId, encryptedBlock);

            stashedBlockDao.insert(stashedBlock);
            pendingBlockDao.insert(pendingBlock);

            Log.d(TAG, "Create Chat Room Complete:");

            sendThreadSafeToast("Created Chatroom.");
        }
        catch(SecurityException e){
            Log.e(TAG, e.getMessage());
        }

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
    }

    private synchronized void exchangeMessages() {

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Log.d(TAG, "exchangeMessages:");

        //sendThreadSafeToast("Exchanging Messages.");

        try{
            PendingBlockDao pendingBlockDao = blockRoomDatabase.pendingBlockDao();
            StashedBlockDao stashedBlockDao = blockRoomDatabase.stashedBlockDao();

            /** Exchange User Ids and Generate ChatId **/
            Log.d(TAG, "Exchanging User Ids:");

            String localUserId = Crypto.generateUserId(adapter.getName());
            communicateThread.write(localUserId);
            String foreignUserId = communicateThread.readSafe(126);

            String chatId = Crypto.generateSortChatId(localUserId, foreignUserId);

            /** Exchange Blocks **/
            List<PendingBlock> pendingBlocks = pendingBlockDao.findBlocksByChatId(chatId);
            int i = 0;
            int pendingBlockLength = pendingBlocks.size();


            boolean isReceiving = true;
            while (isReceiving) {

                String response;

                // If we are out of blocks send a fake one
                if(i >= pendingBlockLength){

                    Log.d(TAG, "SENDING EMPTY BLOCK: ");
                    communicateThread.write("EMPTY");
                }
                else{

                    Log.d(TAG, "SENDING BLOCK: ");
                    communicateThread.write(pendingBlocks.get(i).getEncryptedBlock());
                }

                response = communicateThread.readSafe(1024);
                if(response.equals("EMPTY")){

                    if(i >= pendingBlockLength - 1)
                        isReceiving = false;
                }
                else{

                    StashedBlock stashedBlock = new StashedBlock(chatId, response);
                    stashedBlockDao.insert(stashedBlock);
                }

                i++;
            }

            Log.d(TAG, "Exchange Complete:");
            sendThreadSafeToast("Exchanged Messages.");
        }
        catch(SecurityException e){
            Log.e(TAG, e.getMessage());
        }

        sendThreadSafeToast("Exchange complete");
        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
    }

    private void connectionFailed() {

        Log.e(TAG, "connectionFailed()");
        sendThreadSafeToast("Connection Failed.");

        // Start the service over to restart listening mode
        this.start();
    }

    private void connectionLost() {

        Log.e(TAG, "connectionLost()");
        sendThreadSafeToast("Connection Lost.");

        // Start the service over to restart listening mode
        this.start();
    }

    private void disconnect(){

        Log.e(TAG, "disconnect()");

        // Start the service over to restart listening mode
        this.start();
    }

    public void startDiscovery() {
        Log.d(TAG, "Running: startDiscovery()");

        try {
            if (adapter.isDiscovering()) {

                Log.d(TAG, "Operator is already discovering.");
            } else {

                adapter.startDiscovery();

                // Connect to Bluetooth
                try {
                    BroadcastReceiver receiver = new BroadcastReceiver() {
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();

                            // Finding devices
                            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                foreignUsers.add(new User(device));
                            }
                        }
                    };

                    // Register our receiver to begin listening for new devices
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    getApplicationContext().registerReceiver(receiver, filter);

                }
                catch (SecurityException e) {

                    Toast.makeText(
                            getApplicationContext(),
                            "SCAN FAILED \n" + e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
        catch(SecurityException e){

            Log.e(TAG, "Error: Unable to start discovery");
        }
    }

    public void attemptAutoConnect(){
        Log.d(TAG, "Running: attemptAutoConnect()");

        try{

            UserDao userDao = userRoomDatabase.userDao();
            String localUserId = Crypto.generateUserId(adapter.getName());

            previouslyBondedDevices.clear();
            previouslyBondedDevices.addAll(adapter.getBondedDevices());

            for(BluetoothDevice device: previouslyBondedDevices){

                String foreignUserId = Crypto.generateUserId(device.getName());
                String tempChatId = Crypto.generateSortChatId(localUserId, foreignUserId);
                User user = userDao.findUserByChatId(tempChatId);

                if(user != null) {

                    String correctAddress = device.getAddress();

                    Log.d(TAG, "Bonded Device Found" + correctAddress);
                    for(User confirmUser : foreignUsers){

                        BluetoothDevice confirmDevice = confirmUser.getDevice();
                        if(confirmDevice.getAddress().equals(correctAddress)){

                            Log.d(TAG, "DEVICE FOUND FOUND FOUND");
                            connect(confirmDevice,true);
                        }
                    }
                }
            }
        }
        catch(SecurityException e){
            Log.e(TAG, e.getMessage());
        }
    }

    /** Internal UI Calls
     *  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */
    public void sendThreadSafeToast(String text) {

        Message message = internalHandler.obtainMessage(UI_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(UI_KEY_TEXT, text);
        message.setData(bundle);
        message.sendToTarget();
    }

    /** Handler of internal thread safe UI requests to the Fragments or Activities */
    Handler internalHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message message){

            switch (message.what) {

                case UI_TOAST:
                default:

                    Bundle bundle = message.getData();
                    String text = bundle.getString(UI_KEY_TEXT);
                    Toast.makeText(
                        getApplicationContext(),
                        text,
                        Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    /** Handler of incoming messages from Fragments or Activities
     *  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */
    private final class IncomingHandler extends Handler {

        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message message) {

            switch (message.what) {

                case MSG_START_DISCOVERY:

                    startDiscovery();
                    break;

                case MSG_CREATE_CHAT_ROOM:

                    Bundle createChatBundle = message.getData();
                    BluetoothDevice device = createChatBundle.getParcelable(KEY_SERVICE_DEVICE);
                    connect(device, true);
                    break;

                case MSG_ATTEMPT_AUTO_CONNECT:

                    attemptAutoConnect();

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

        messenger = new Messenger(new IncomingHandler(this));
        return messenger.getBinder();
    }

    /** Local Threads for Bluetooth operation
     *  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */
    private class AcceptThread extends Thread {

        private final BluetoothServerSocket serverSocket;
        private String socketType;

        public AcceptThread(boolean secure) {
            socketType = getSecureString(secure);
            Log.d(TAG, "CREATE AcceptThread: " + socketType);

            BluetoothServerSocket temp = null;

            // Create a new listening server socket
            try {
                if (secure) {
                    temp = adapter.listenUsingRfcommWithServiceRecord(
                            SERVICE_SECURE,
                            UUID_SECURE);
                }
                else {
                    temp = adapter.listenUsingInsecureRfcommWithServiceRecord(
                            SERVICE_INSECURE,
                            UUID_INSECURE);
                }
            }
            catch (IOException e) {
                Log.e(TAG, "Socket Type: " + socketType + "listen() failed", e);
            }
            catch (SecurityException e) {
                Log.e(TAG, "Socket Type: " + socketType + "listen() failed", e);
            }

            serverSocket = temp;
            updateState(STATE_LISTEN);
        }

        public void run() {
            Log.i(TAG, "BEGIN acceptThread SocketType: " + socketType);
            setName("AcceptThread" + socketType);

            BluetoothSocket socket;

            // Listen to the server socket if we're not connected
            while (currentState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connecting on or an exception
                    socket = serverSocket.accept();
                }
                catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + socketType + " accept() failed", e);
                    break;
                }

                // If a connection was accepted handle the requests
                if (socket != null) {
                    synchronized (this) {
                        switch (currentState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:

                                // TODO Check if the user wants to agree to create a chatroom

                                // Situation normal. Start the connected thread.
                                communicate(
                                        socket,
                                        socket.getRemoteDevice(),
                                        socketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:

                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                }
                                catch (IOException e) {

                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }

            Log.i(TAG, "END OF RUN: acceptThread, SocketType: " + socketType);
        }

        public void cancel() {
            Log.i(TAG, "ENDING acceptThread SocketType: " + socketType);

            try {
                serverSocket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "Socket Type" + socketType + "close() of server failed", e);
            }
        }
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket socket;
        private final BluetoothDevice mmDevice;
        private String socketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            socketType = getSecureString(secure);
            Log.d(TAG, "CREATE ConnectThread: " + socketType);

            BluetoothSocket temp = null;
            mmDevice = device;

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                if (secure) {

                    temp = device.createRfcommSocketToServiceRecord(UUID_SECURE);
                    //temp = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                    //temp = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                }
                else {

                    temp = device.createInsecureRfcommSocketToServiceRecord(UUID_INSECURE);
                }
            }
            catch (IOException e) {

                Log.e(TAG, "Socket Type #####: " + socketType + "create() failed", e);
            }
            catch (SecurityException e) {

                Log.e(TAG, "Socket Type #####: " + socketType + "listen() failed", e);

            }
            catch (Exception e){

                Log.e(TAG, "Socket Type #####: " + socketType + "method() failed", e);
            }

            socket = temp;
            updateState(STATE_CONNECTING);
        }

        public void run() {
            Log.i(TAG, "BEGIN connectThread SocketType: " + socketType);
            setName("ConnectThread" + socketType);

            // Make a connection to the BluetoothSocket
            try {

                // Always cancel discovery because it will slow down a connection
                adapter.cancelDiscovery();

                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket.connect();
            }
            catch (IOException e) {

                // Close the socket
                try {
                    Log.e(TAG, "attempting socket close", e);
                    socket.close();
                }
                catch (IOException e2) {

                    Log.e(TAG, "unable to close() " + socketType +
                            " socket during connection failure", e2);
                }

                Log.e(TAG, "unable to connect", e);
                connectionFailed();
                return;
            }
            catch (SecurityException e) {

                Log.e(TAG, "Socket Type: " + socketType + "listen() failed", e);
            }

            // Reset the ConnectThread because we're done
            synchronized (this) {

                connectThread = null;
            }

            // Start the connected thread
            communicate(socket, mmDevice, socketType);
        }

        public void cancel() {
            Log.i(TAG, "ENDING connectThread SocketType: " + socketType);

            try {

                socket.close();
            }
            catch (IOException e) {

                Log.e(TAG, "close() of connect " + socketType + " socket failed", e);
            }
        }
    }

    private class CommunicateThread extends Thread {

        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public CommunicateThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "CREATE CommunicateThread: " + socketType);

            InputStream tempIn = null;
            OutputStream tempOut = null;
            this.socket = socket;

            // Get the BluetoothSocket input and output streams
            try {

                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            }
            catch (IOException e) {

                Log.e(TAG, "temp sockets not created", e);
            }

            inputStream = tempIn;
            outputStream = tempOut;

            updateState(STATE_CONNECTED);
        }

        public void run() {
            Log.i(TAG, "BEGIN: communicateThread - " + currentState);

        }

        public synchronized void write(String text) {

            Log.d(TAG, "SENDING: \n" + text);

            byte[] buffer = text.getBytes(CHARSET);

            try {

                outputStream.write(buffer);
                outputStream.flush();
            }
            catch (IOException e) {

                Log.e(TAG, "Exception during write", e);
            }
        }

        // This is a blocking thread
        public synchronized String readSafe(int size) {

            byte[] buffer = new byte[1024];

            // Keep listening to the InputStream while connected
            if (currentState == STATE_CONNECTED) {

                Log.d(TAG, "AWAITING MESSAGE SIZE: " + size);

                try {

                    String text = "";
                    int bytes = 0;
                    while(bytes < size){

                        // Read from the InputStream
                        int receivedBytes = inputStream.read(buffer);
                        String receivedText = new String(buffer, 0, receivedBytes, CHARSET);

                        bytes = bytes + receivedBytes;
                        text = text + receivedText;

                        if(receivedText.equals("EMPTY"))
                            break;
                    }
                    Log.d(TAG, "RECEIVED MESSAGE: \n" + text);

                    return text;
                }
                catch (IOException e) {

                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                }
            }

            return "";
        }

        // This is a blocking thread
        public synchronized String read() {

            byte[] buffer = new byte[1024];

            // Keep listening to the InputStream while connected
            if (currentState == STATE_CONNECTED) {

                try {

                    // Read from the InputStream
                    int bytes = inputStream.read(buffer);
                    String text = new String(buffer, 0, bytes, CHARSET);

                    Log.d(TAG, "RECEIVED MESSAGE: \n" + text);

                    return text;
                }
                catch (IOException e) {

                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                }
            }

            return "";
        }

        public void cancel() {
            Log.i(TAG, "ENDING: communicateThread - " + currentState);

            try {

                socket.close();
            }
            catch (IOException e) {

                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
