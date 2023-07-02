package com.example.mobiledevicemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobiledevicemanager.models.LockScreenInfo;
import com.example.mobiledevicemanager.models.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateLock extends AppCompatActivity {

    private EditText editTextPasscode;
    private EditText editTextConfirmPasscode;
    private EditText editTextQuestion1;
    private EditText editTextAnswer1;
    private EditText editTextQuestion2;
    private EditText editTextAnswer2;
    private EditText editTextQuestion3;
    private EditText editTextAnswer3;
    private Button btnSave;

    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lock);

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Find views
        editTextPasscode = findViewById(R.id.editTextPasscode);
        editTextConfirmPasscode = findViewById(R.id.editTextConfirmPasscode);
        editTextQuestion1 = findViewById(R.id.editTextQuestion1);
        editTextAnswer1 = findViewById(R.id.editTextAnswer1);
        editTextQuestion2 = findViewById(R.id.editTextQuestion2);
        editTextAnswer2 = findViewById(R.id.editTextAnswer2);
        editTextQuestion3 = findViewById(R.id.editTextQuestion3);
        editTextAnswer3 = findViewById(R.id.editTextAnswer3);
        btnSave = findViewById(R.id.btnSave);

        // Retrieve the userId from the intent
        userId = getIntent().getStringExtra("userId");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = getIntent().getStringExtra("userId");
                String passcode = editTextPasscode.getText().toString();
                String confirmPasscode = editTextConfirmPasscode.getText().toString();
                String question1 = editTextQuestion1.getText().toString();
                String answer1 = editTextAnswer1.getText().toString();
                String question2 = editTextQuestion2.getText().toString();
                String answer2 = editTextAnswer2.getText().toString();
                String question3 = editTextQuestion3.getText().toString();
                String answer3 = editTextAnswer3.getText().toString();

                if (passcode.equals(confirmPasscode)) {
                    saveLockScreenInfo(userId, passcode, question1, answer1, question2, answer2, question3, answer3);
                } else {
                    Toast.makeText(CreateLock.this, "Passcode and Confirm Passcode do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveLockScreenInfo(String userId, String passcode, String question1, String answer1, String question2, String answer2, String question3, String answer3) {
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(CreateLock.this, "User ID is null or empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null) {
                        LockScreenInfo lockScreenInfo = new LockScreenInfo(passcode, question1, answer1, question2, answer2, question3, answer3);
                        user.setLockScreenInfo(lockScreenInfo);

                        // Save the updated user object to Firebase Realtime Database
                        userRef.setValue(user);

                        // Display success message
                        Toast.makeText(CreateLock.this, "Credentials have been created securely", Toast.LENGTH_SHORT).show();

                        // Navigate back to the main activity
                        Intent intent = new Intent(CreateLock.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
