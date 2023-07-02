package com.example.mobiledevicemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledevicemanager.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText inputUsername, inputPhoneNumber, inputEmail, inputPassword, inputConfirmPassword;

    private Button btnRegister;

    private TextView textView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);


        inputUsername = findViewById(R.id.inputUsername);
        inputPhoneNumber = findViewById(R.id.inputPhoneNumber);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConformPassword);
        btnRegister = findViewById(R.id.btnRegister);
        textView = findViewById(R.id.alreadyHaveAccount);

        mAuth = FirebaseAuth.getInstance();


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the login activity or navigate to the login screen
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String username = inputUsername.getText().toString().trim();
        String phoneNumber = inputPhoneNumber.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // Perform validation
        if (username.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the device details
        String deviceManufacturer = DeviceDetailsUtils.getDeviceManufacturer();
        String deviceModel = DeviceDetailsUtils.getDeviceModel();
        String deviceSerialNumber = DeviceDetailsUtils.getDeviceSerialNumber(getApplicationContext());
        String deviceIMEI = DeviceDetailsUtils.getDeviceIMEI(getApplicationContext());

        // Register the user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save additional user details to the database
                            String userId = user.getUid();

                            Users newUser = new Users(userId, username, phoneNumber, email, deviceManufacturer, deviceModel, deviceSerialNumber, deviceIMEI);

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                            usersRef.child(userId).setValue(newUser)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                                            // Open the main page
                                            Intent intent = new Intent(Register.this, Terms.class);
                                            intent.putExtra("userId", userId);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // Failed to save user details to the database
                                            Toast.makeText(getApplicationContext(), "Failed to save user details to the database", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Registration failed
                        Toast.makeText(getApplicationContext(), "Registration failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
