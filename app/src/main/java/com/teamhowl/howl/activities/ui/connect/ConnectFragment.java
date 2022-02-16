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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.teamhowl.howl.controllers.UserAdapter;
import com.teamhowl.howl.databinding.FragmentConnectBinding;
import com.teamhowl.howl.models.User;

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

        users = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), users);
        userListView = binding.connectListView;
        userListView.setAdapter(userAdapter);

        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.startDiscovery();

            BroadcastReceiver mReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();

                    //Finding devices
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        userAdapter.add(new User(
                                device.getName(),
                                device.getAddress()));
                    }
                }
            };

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getContext().registerReceiver(mReceiver, filter);

            Toast.makeText(getContext(), "SCAN COMPLETE", Toast.LENGTH_LONG).show();
        }
        catch(SecurityException e) {
            Toast.makeText(getContext(), "SCAN FAILED \n" + e.toString(), Toast.LENGTH_LONG).show();
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        users.clear();
        userAdapter.clear();
        binding = null;
    }

}