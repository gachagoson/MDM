package com.example.mobiledevicemanager;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LockDevice extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the userId from the intent or any other source
        userId = getIntent().getStringExtra("userId");

        // Lock the screen immediately after displaying the activity
        lockDevice();
    }

    private void lockDevice() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        if (keyguardManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keyguardManager.requestDismissKeyguard(this, null);
            } else {
                // Handle devices with lower API levels if needed
            }
        }
    }
}
