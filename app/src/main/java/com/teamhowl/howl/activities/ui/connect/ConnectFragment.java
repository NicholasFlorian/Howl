package com.teamhowl.howl.activities.ui.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.teamhowl.howl.controllers.UserAdapter;
import com.teamhowl.howl.databinding.FragmentConnectBinding;
import com.teamhowl.howl.models.User;
import com.teamhowl.howl.utilities.BluetoothService;
import com.teamhowl.howl.utilities.Crypto;

import java.util.ArrayList;

public class ConnectFragment extends Fragment {

    /** Messenger for communicating with the service. */
    Messenger messenger = null;
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
            messenger = new Messenger(service);
            isMessengerBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            messenger = null;
            isMessengerBound = false;
        }
    };

    /** Our fragment */
    private FragmentConnectBinding binding;
    private UserAdapter userAdapter;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        // Create ViewModel for MVVM
        ConnectViewModel connectViewModel =
                new ViewModelProvider(this).get(ConnectViewModel.class);

        // Create the elements of our fragment
        binding = FragmentConnectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView userListView = binding.connectListView;
        Button sendMessageButton = binding.sendMessageButton;

        // Create list view functionality
        userAdapter = new UserAdapter(getContext());
        userListView.setAdapter(userAdapter);

        // Create observation for view model
        connectViewModel.getLocalUsersObservable().observe(
            getViewLifecycleOwner(),
            new Observer<ArrayList<User>>() {

            @Override
            public void onChanged(ArrayList<User> users) {

                if(users != null) {

                    userAdapter.setUsers(users);
                }
            }
        });

        // Add action when selecting a Device
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                createChatRoom(userAdapter.getItem(position).getDevice());
            }
        });

        // Add action when the button is pressed
        sendMessageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                sendMessage();
            }
        });

        // Request the device discoverable when they open this page.
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        // Start device discovering
        //startDiscovery();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().bindService(
            new Intent(getContext(), BluetoothService.class),
            bluetoothServiceConnection,
            Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();

        startDiscovery();
    }

    public void sendMessage() {

        if(!isMessengerBound)
            return;

        Message message = Message.obtain(
            null,
            BluetoothService.MSG_SAY_HELLO,
            0,
            0);

        try {
            messenger.send(message);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startDiscovery() {

        if(!isMessengerBound)
            return;

        // Show available devices to pair with
        Message message = Message.obtain(
                null,
                BluetoothService.MSG_START_DISCOVERY,
                0,
                0);

        try {
            messenger.send(message);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

        // Connect to Bluetooth
        try {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    // Finding devices
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        userAdapter.add(new User(device));
                    }
                }
            };

            // Register our receiver to begin listening for new devices
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getContext().registerReceiver(receiver, filter);

        }
        catch (SecurityException e) {

            Toast.makeText(
                getContext(),
                "SCAN FAILED \n" + e.toString(),
                Toast.LENGTH_LONG).show();
        }
    }

    public void createChatRoom(BluetoothDevice device){

        if(!isMessengerBound)
            return;

        Message message = Message.obtain(
            null,
            BluetoothService.MSG_CREATE_CHAT_ROOM,
            0,
            0);

        Bundle bundle = new Bundle();
        bundle.putParcelable(BluetoothService.KEY_SERVICE_DEVICE, device);

        message.setData(bundle);

        try {
            messenger.send(message);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userAdapter.clear();
        binding = null;
        if (isMessengerBound) {
            getActivity().unbindService(bluetoothServiceConnection);
            isMessengerBound = false;
        }
    }

}