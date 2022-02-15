package com.teamhowl.howl.activities.ui.connect;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.teamhowl.howl.R;
import com.teamhowl.howl.controllers.UserAdapter;
import com.teamhowl.howl.databinding.FragmentConnectBinding;
import com.teamhowl.howl.models.User;

import java.util.ArrayList;
import java.util.List;

public class ConnectFragment extends Fragment {
//    public class ProductListFragment extends Fragment {
//    public static final String TAG = "ProductListViewModel";
//    private ProductAdapter mProductAdapter;
//    private ListFragmentBinding mBinding;

    public static final String TAG = "ConnectUserListViewModel";
    private ArrayList<User> users;
    private UserAdapter userAdapter;
    private FragmentConnectBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //ConnectViewModel connectViewModel =
        //        new ViewModelProvider(this).get(ConnectViewModel.class);

        binding = FragmentConnectBinding.inflate(inflater, container, false);

        users = new ArrayList<>();
        users.add(new User("Nicholas", "asda"));
        userAdapter = new UserAdapter(getContext(), users);
        binding.connectListView.setAdapter(userAdapter);

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