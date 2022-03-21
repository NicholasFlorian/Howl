package com.teamhowl.howl.activities;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.teamhowl.howl.R;
import com.teamhowl.howl.controllers.UserAdapter;
import com.teamhowl.howl.databinding.ActivityHomeBinding;
import com.teamhowl.howl.models.User;
import com.teamhowl.howl.repositories.BlockRoomDatabase;
import com.teamhowl.howl.utilities.BluetoothService;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    /** Our Activity */
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_notifications)
            .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Start the BluetoothService
        startService(new Intent(this, BluetoothService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Service stops when the activity is closed
        stopService(new Intent(this, BluetoothService.class));
    }
}