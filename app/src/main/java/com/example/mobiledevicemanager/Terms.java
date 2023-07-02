package com.example.mobiledevicemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Terms extends AppCompatActivity {

    private CheckBox checkboxAgree;
    private Button btnProceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        checkboxAgree = findViewById(R.id.checkboxAgree);
        btnProceed = findViewById(R.id.btnProceed);

        checkboxAgree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnProceed.setEnabled(isChecked);
        });

        btnProceed.setOnClickListener(view -> {
            CheckBox checkboxAgreement = findViewById(R.id.checkboxAgree);
            if (checkboxAgreement.isChecked()) {
                // Checkbox is checked, proceed to registration
                String userId = getIntent().getStringExtra("userId");
                Intent intent = new Intent(Terms.this, MainActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            } else {
                // Checkbox is not checked, show an error message or toast
                Toast.makeText(Terms.this, "Please agree to the MDM guidelines and disclaimer", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
