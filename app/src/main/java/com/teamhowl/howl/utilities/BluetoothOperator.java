package com.teamhowl.howl.utilities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BluetoothOperator {
    private static final String TAG = "HOWL :: BluetoothOperator";

    /** Bluetooth service information */
    private static final String SERVICE_SECURE = "SERVICE_SECURE";
    private static final String SERVICE_INSECURE = "SERVICE_INSECURE";
    private static final UUID UUID_SECURE =
        ParcelUuid.fromString("00001101-0000-1000-8000-00805F9B34FB").getUuid();
    private static final UUID UUID_INSECURE =
        ParcelUuid.fromString("00001101-0000-1000-8000-00805F9B34FB").getUuid();

    /** Thread states */
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    /** Bluetooth adapter */
    private BluetoothAdapter adapter;

    /** Bluetooth operation threads and their state */
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private AcceptThread acceptThread;
    private int currentState;

    public BluetoothOperator() {

        this.adapter = BluetoothAdapter.getDefaultAdapter();

        this.acceptThread = null;
        this.connectThread = null;
        this.connectedThread = null;

        updateState(STATE_NONE);
    }

    /** Status Operations
     *  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */
    public synchronized void start() {
        Log.d(TAG, "start");

        updateState(STATE_NONE);

        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (acceptThread == null) {
            acceptThread = new AcceptThread(false);
            acceptThread.start();
        }
    }

    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        updateState(STATE_NONE);

        // Update UI title
        //updateUserInterfaceTitle();
    }

    private void updateState(int newState){

        String stateString = "";

        switch(newState){

            case STATE_NONE:
                stateString = "None";
                break;
            case STATE_LISTEN:
                stateString = "Listen";
                break;
            case STATE_CONNECTING:
                stateString = "Connecting";
                break;
            case STATE_CONNECTED:
                stateString = "Connected";
                break;
            default:
                stateString = "Unknown";
        }

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Log.d(TAG, "Updating state of operator.");
        Log.d(TAG, "State : " + stateString);

        if(acceptThread != null)
            Log.d(TAG, "Accept Thread Status : " + acceptThread.getState());
        else
            Log.d(TAG, "Accept Thread Status : null");

        if(connectThread != null)
            Log.d(TAG, "Connect Thread Status : " + connectThread.getState());
        else
            Log.d(TAG, "Connect Thread Status : null");

        if(connectedThread != null)
            Log.d(TAG, "Connected Thread Status : " + connectedThread.getState());
        else
            Log.d(TAG, "Connected Thread Status : null");

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");

        currentState = newState;
    }

    private synchronized void connect(BluetoothDevice device, boolean secure) {

        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (currentState == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Start the thread to connect with the given device
        connectThread = new ConnectThread(device, secure);
        connectThread.start();
    }

    private synchronized void connected(
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
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket, socketType);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
        //Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        //Bundle bundle = new Bundle();
        //bundle.putString(Constants.DEVICE_NAME, device.getName());
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);

    }

    private void connectionFailed() {

        Log.e(TAG, "connectionFailed");

        // Send a failure message back to the Activity
        //Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        //Bundle bundle = new Bundle();
        //bundle.putString(Constants.TOAST, "Unable to connect device");
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);

        updateState(STATE_NONE);
        // Update UI title
        //updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        this.start();
    }

    private void connectionLost() {
        // Send a failure message back to the Activity
        //Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        //Bundle bundle = new Bundle();
        //bundle.putString(Constants.TOAST, "Device connection was lost");
        //msg.setData(bundle);
        //mHandler.sendMessage(msg);

        updateState(STATE_NONE);
        // Update UI title
        //updateUserInterfaceTitle();

        // Start the service over to restart listening mode
        this.start();
    }

    private void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (currentState != STATE_CONNECTED) return;
            r = connectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /** Main Intent Operations
     *  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     */

    public void startDiscovery() throws SecurityException {

        adapter.startDiscovery();
    }

    public boolean isDiscovering() throws SecurityException {

        return adapter.isDiscovering();
    }

    public void createChatRoom(BluetoothDevice device){

        // Establish a connection first
        connect(device, false);
    }

    public void exchangeMessages(BluetoothDevice device){

        // Establish a connection first
        connect(device, false);

        // Exchange messages
    }

    /** Local Threads for Bluetooth operation
     *  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    */

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket serverSocket;
        private String socketType;

        public AcceptThread(boolean secure) {

            socketType = secure ? "Secure" : "Insecure";
            Log.d(TAG, "Create AcceptThread: " + socketType);

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

            Log.d(TAG, "Socket Type: " + socketType + "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + socketType);

            BluetoothSocket socket;

            // Listen to the server socket if we're not connected
            while (currentState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connecti on or an exception
                    socket = serverSocket.accept();
                }
                catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + socketType + " accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (this) {
                        switch (currentState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:

                                // Situation normal. Start the connected thread.
                                connected(
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

            Log.i(TAG, "END mAcceptThread, socket Type: " + socketType);
        }

        public void cancel() {

            Log.d(TAG, "Socket Type" + socketType + "cancel " + this);

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

            socketType = secure ? "Secure" : "Insecure";
            Log.d(TAG, "Create ConnectThread: " + socketType);

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

            Log.i(TAG, "BEGIN connectThread SocketType:" + socketType);
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
            synchronized (BluetoothOperator.this) {

                connectThread = null;
            }

            // Start the connected thread
            connected(socket, mmDevice, socketType);
        }

        public void cancel() {

            try {

                socket.close();
            }
            catch (IOException e) {

                Log.e(TAG, "close() of connect " + socketType + " socket failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread {

        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {

            Log.d(TAG, "Create ConnectedThread: " + socketType);

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
            Log.i(TAG, "BEGIN connectedThread - " + currentState);
            byte[] buffer = new byte[1024];
            int bytes;

            // Exchange information needed for creating a chatroom
            write("TESTING WRITING TO OTHER DEVICES ->".getBytes(StandardCharsets.UTF_8));

            // Keep listening to the InputStream while connected
            while (currentState == STATE_CONNECTED) {

                Log.i(TAG, "Looping");

                try {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);

                    // Send the obtained bytes to the UI Activity TODO update handler
                    // handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                    //        .sendToTarget();

                    Log.d(TAG, "RECEIVED: " + new String(buffer, StandardCharsets.UTF_8));
                }
                catch (IOException e) {

                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {

            try {
                outputStream.write(buffer);
                outputStream.flush();

                // Share the sent message back to the UI Activity TODO update handler
                //mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                //        .sendToTarget();
            }
            catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {

            try {
                socket.close();
            }
            catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

}