package com.teamhowl.howl.activities.ui.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.teamhowl.howl.controllers.UserAdapter;
import com.teamhowl.howl.databinding.FragmentConnectBinding;
import com.teamhowl.howl.models.User;

import java.lang.reflect.Method;
import java.util.ArrayList;

import java.util.Set;

public class ConnectFragment extends Fragment {

    private ArrayList<User> users;
    private UserAdapter userAdapter;
    private ListView userListView;
    private FragmentConnectBinding binding;
    private BluetoothAdapter bluetoothAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentConnectBinding.inflate(inflater, container, false);

        // Create list view functionality
        users = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), users);
        userListView = binding.connectListView;
        userListView.setAdapter(userAdapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // then pressed A create dialog for connecting to another device
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                requestConnection(userAdapter.getItem((int)id));
            }
        });

        // Start Bluetooth features
        startBluetoothDiscovery();

        return binding.getRoot();
    }

    public void startBluetoothDiscovery(){

        // Connect to Bluetooth
        try {

            // Show avaiable device to pair with
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.startDiscovery();

            BroadcastReceiver receiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    //Finding devices
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        userAdapter.add(new User(
                                device.getName(),
                                bluetoothAdapter.getAddress(),
                                device.getAddress()));
                    }
                }
            };

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getContext().registerReceiver(receiver, filter);
        }
        catch(SecurityException e) {

            Toast.makeText(getContext(), "SCAN FAILED \n" + e.toString(), Toast.LENGTH_LONG).show();
        }

        // Make the device discoverable
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    public void requestConnection(User user){

        try{

            Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
            Method createBondMethod = class1.getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
            return returnValue.booleanValue();

        }
        catch(SecurityException e) {

            Toast.makeText(getContext(), "SCAN FAILED \n" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void createChatRoom(User user){


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        users.clear();
        userAdapter.clear();
        binding = null;
    }

}