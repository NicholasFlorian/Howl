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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.teamhowl.howl.controllers.UserAdapter;
import com.teamhowl.howl.databinding.FragmentConnectBinding;
import com.teamhowl.howl.models.User;
import com.teamhowl.howl.utilities.BluetoothOperator;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.Set;

public class ConnectFragment extends Fragment {

    // Our fragment
    private FragmentConnectBinding binding;
    private ListView userListView;
    private Button sendMessageButton;

    // Our local View
    private ArrayList<User> users;
    private UserAdapter userAdapter;
    private BluetoothOperator operator;

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
        userListView = binding.connectListView;
        sendMessageButton = binding.sendMessageButton;

        // Create list view functionality
        users = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), users);
        userListView.setAdapter(userAdapter);

        // Set up the bluetooth operator
        operator = new BluetoothOperator();
        operator.start();

        // Add action for connecting
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Request a connection when clicked
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                operator.createChatRoom(
                        userAdapter.getItem((int) id).getDevice());
            }
        });

        // Start Bluetooth features
        startDiscovery();

        return root;
    }

    public void startDiscovery() {

        // Connect to Bluetooth
        try {

            // Show available devices to pair with
            operator.startDiscovery();

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

        } catch (SecurityException e) {

            Toast.makeText(getContext(), "SCAN FAILED \n" + e.toString(), Toast.LENGTH_LONG).show();
        }

        // Make the device discoverable
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        users.clear();
        userAdapter.clear();
        operator.stop();
        binding = null;
    }

}