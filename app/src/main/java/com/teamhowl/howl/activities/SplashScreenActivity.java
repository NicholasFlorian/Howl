package com.teamhowl.howl.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.teamhowl.howl.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int  ACCESS_LOCATION    = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);

        // Check location permission is already granted
        boolean hasPermissionLocation = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED);

        // Get location permission as per new Android Bluetooth standards
        if (!hasPermissionLocation) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_LOCATION);
        }
        else{

            // If granted, load the home activity
            Intent myIntent = new Intent(this, HomeActivity.class);
            this.startActivity(myIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case ACCESS_LOCATION : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();

                    // If granted, load the home activity
                    Intent myIntent = new Intent(this, HomeActivity.class);
                    this.startActivity(myIntent);
                }
                else {

                    Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}