package com.example.mobiledevicemanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledevicemanager.models.Alerts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AlertsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAlerts;
    private List<Alerts> alertList;
    private AlertsAdapter alertsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        recyclerViewAlerts = findViewById(R.id.recyclerViewAlerts);
        recyclerViewAlerts.setLayoutManager(new LinearLayoutManager(this));
        alertList = new ArrayList<>();
        alertsAdapter = new AlertsAdapter(alertList);
        recyclerViewAlerts.setAdapter(alertsAdapter);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Go back to the previous activity
            }
        });

        retrieveAlertsFromFirebase();
    }

    private void retrieveAlertsFromFirebase() {
        DatabaseReference alertsRef = FirebaseDatabase.getInstance().getReference("alerts");
        alertsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alertList.clear();
                for (DataSnapshot alertSnapshot : dataSnapshot.getChildren()) {
                    Alerts alert = alertSnapshot.getValue(Alerts.class);
                    if (alert != null) {
                        alertList.add(alert);
                    }
                }
                alertsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AlertsActivity.this, "Failed to retrieve alerts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
