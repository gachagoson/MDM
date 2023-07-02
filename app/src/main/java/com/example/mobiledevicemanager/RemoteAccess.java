package com.example.mobiledevicemanager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mobiledevicemanager.models.LocationInfo;
import com.example.mobiledevicemanager.models.Users;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class RemoteAccess extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Button btnDeviceLock;
    private Button btnDeviceWipe;
    private Button btnInstallApplication;
    private Button btnUninstallApplication;
    private Button btnResetPasscode;
    private Button btnBackupData;
    private Button btnTrackLocation;
    private Button btnConfigureNetworkSettings;
    private Button btnConfigureAppSettings;
    private Button btnRebootDevice;
    private Button btnGetDeviceInfo;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int WIPE_REQUEST_CODE = 456; // Define the request code for device admin permission

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final int LOCK_REQUEST_CODE = 123;
    private static final int REBOOT_PERMISSION_REQUEST_CODE = 2;

    private static final int CONTACTS_PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remote_access);

        // Initialize buttons
        btnDeviceLock = findViewById(R.id.btnDeviceLock);
        btnDeviceWipe = findViewById(R.id.btnDeviceWipe);
        btnInstallApplication = findViewById(R.id.btnInstallApplication);
        btnUninstallApplication = findViewById(R.id.btnUninstallApplication);
        btnResetPasscode = findViewById(R.id.reset);
        btnBackupData = findViewById(R.id.backup);
        btnTrackLocation = findViewById(R.id.btnTrackLocation);
        btnConfigureNetworkSettings = findViewById(R.id.btnConfigureNetworkSettings);
        btnConfigureAppSettings = findViewById(R.id.btnConfigureAppSettings);
        btnRebootDevice = findViewById(R.id.btnRebootDevice);
        btnGetDeviceInfo = findViewById(R.id.btnGetDeviceInfo);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the back arrow click
                // Add your implementation logic here
                // This code will be executed when the back arrow is clicked

                // Navigate back to the previous page, e.g., by finishing the current activity
                finish();
            }
        });



        // Set click listeners
        btnDeviceLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockDevice();
            }
        });

        btnDeviceWipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wipeDevice();
            }
        });

        btnInstallApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installApplication();
            }
        });

        btnUninstallApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uninstallApplication();
            }
        });

        btnResetPasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPasscode();
            }
        });

        btnBackupData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backupData();
            }
        });

        btnTrackLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackLocation();
            }
        });

        btnTrackLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                updateLocation();
                return true; // Return true to consume the long click event
            }
        });

        btnConfigureNetworkSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureNetworkSettings();
            }
        });

        btnConfigureAppSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureAppSettings();
            }
        });

        btnRebootDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rebootDevice();
            }
        });

        btnGetDeviceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceInfo();
            }
        });
    }

    private void updateLocation() {
        // Check location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Create location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000); // Update interval in milliseconds
        locationRequest.setFastestInterval(500); // Fastest update interval in milliseconds

        // Create location callback
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Store the updated location in the database
                    saveLocationInDatabase(latitude, longitude);

                    // Display a toast message to indicate the location update
                    Toast.makeText(RemoteAccess.this, "Location updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Request location updates
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }



    public void onBackPressed() {
        // Add your custom logic here
        // This code will be executed when the back button or back arrow is pressed

        // Navigate back to the previous page, e.g., by finishing the current activity
        finish();
    }

    private void lockDevice() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponentName = new ComponentName(this, MyDeviceAdminReceiver.class);

        if (devicePolicyManager != null) {
            if (devicePolicyManager.isAdminActive(adminComponentName)) {
                devicePolicyManager.lockNow();
            } else {
                // Launch the activity to enable device administrator
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device administrator");
                startActivityForResult(intent, LOCK_REQUEST_CODE);
            }
        }
    }


    private boolean isAdminPermissionGranted() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponentName = new ComponentName(this, DeviceAdminReceiver.class);
        return devicePolicyManager != null && devicePolicyManager.isAdminActive(adminComponentName);
    }


    private void wipeDevice() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponentName = new ComponentName(this, DeviceAdminReceiver.class);

        if (devicePolicyManager != null) {
            if (devicePolicyManager.isAdminActive(adminComponentName)) {
                devicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE | DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);
            } else {
                // Launch the activity to enable device administrator
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponentName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device administrator");
                startActivityForResult(intent, WIPE_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WIPE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                wipeDevice();
            } else {
                Toast.makeText(this, "Device administrator permission is required to wipe data.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void installApplication() {
        Toast.makeText(this, "Install Application", Toast.LENGTH_SHORT).show();
        // Add your implementation logic here
    }

    private void uninstallApplication() {
        Toast.makeText(this, "Uninstall Application", Toast.LENGTH_SHORT).show();
        // Add your implementation logic here
    }

    private void resetPasscode() {
        Toast.makeText(this, "Reset Passcode", Toast.LENGTH_SHORT).show();
        // Add your implementation logic here
    }

    private void backupData() {
        showBackupOptionsDialog();
    }

    private void showBackupOptionsDialog() {

        // Inflate the layout with checkboxes
        View layout = getLayoutInflater().inflate(R.layout.backup_options, null);
        CheckBox checkBoxContacts = layout.findViewById(R.id.checkBoxContacts);
        CheckBox checkBoxImages = layout.findViewById(R.id.checkBoxImages);
        CheckBox checkBoxAudioVideo = layout.findViewById(R.id.checkBoxAudioVideo);
        CheckBox checkBoxDocuments = layout.findViewById(R.id.checkBoxDocuments);

        // Create and show an AlertDialog with the layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setTitle("Select Data Types to Backup");
        builder.setPositiveButton("Backup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the current user ID or any other identifier
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Build the Firebase Realtime Database reference
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference backupRef = database.getReference("backups").child(userId);

                    // Prepare the backup data based on the selected checkboxes
                    StringBuilder backupData = new StringBuilder();
                    if (checkBoxContacts.isChecked()) {
                        // Backup contacts logic
                        StringBuilder contactsData = new StringBuilder();

                        // Retrieve contacts from the device using the ContactsContract API
                        ContentResolver contentResolver = getContentResolver();
                        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
                        String[] projection = {ContactsContract.Contacts.DISPLAY_NAME};
                        String selection = null;
                        String[] selectionArgs = null;
                        String sortOrder = null;

                        Cursor cursor = contentResolver.query(contactsUri, projection, selection, selectionArgs, sortOrder);

                        if (cursor != null && cursor.moveToFirst()) {
                            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                            do {
                                String contactName = cursor.getString(nameColumnIndex);
                                contactsData.append(contactName).append("\n");
                            } while (cursor.moveToNext());
                            cursor.close();
                        }

                        // Append contactsData to backupData
                        backupData.append("Contacts:\n").append(contactsData).append("\n");
                    }

                    if (checkBoxImages.isChecked()) {
                        // Backup images logic
                        StringBuilder imagesData = new StringBuilder();

                        // Retrieve images from the device
                        // Add your code here to retrieve and backup images

                        // Example: Retrieve images from the MediaStore
                        String[] projectionImages = {MediaStore.Images.Media.DATA};
                        Cursor cursorImages = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                projectionImages, null, null, null);

                        if (cursorImages != null && cursorImages.moveToFirst()) {
                            int columnIndex = cursorImages.getColumnIndex(MediaStore.Images.Media.DATA);
                            do {
                                String imagePath = cursorImages.getString(columnIndex);
                                imagesData.append(imagePath).append("\n");
                            } while (cursorImages.moveToNext());
                            cursorImages.close();
                        }

                        // Append imagesData to backupData
                        backupData.append("Images:\n").append(imagesData).append("\n");
                    }

                    if (checkBoxAudioVideo.isChecked()) {
                        // Backup audio/video logic
                        StringBuilder audioVideoData = new StringBuilder();

                        // Retrieve audio/video files from the device
                        // Add your code here to retrieve and backup audio/video files

                        // Example: Retrieve audio files from the MediaStore
                        String[] projectionAudio = {MediaStore.Audio.Media.DATA};
                        Cursor cursorAudio = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                projectionAudio, null, null, null);

                        if (cursorAudio != null && cursorAudio.moveToFirst()) {
                            int columnIndex = cursorAudio.getColumnIndex(MediaStore.Audio.Media.DATA);
                            do {
                                String audioPath = cursorAudio.getString(columnIndex);
                                audioVideoData.append(audioPath).append("\n");
                            } while (cursorAudio.moveToNext());
                            cursorAudio.close();
                        }

                        // Append audioVideoData to backupData
                        backupData.append("Audio/Video:\n").append(audioVideoData).append("\n");
                    }

                    if (checkBoxDocuments.isChecked()) {
                        // Backup documents logic
                        StringBuilder documentsData = new StringBuilder();

                        // Retrieve documents from the device
                        // Add your code here to retrieve and backup documents

                        // Example: Retrieve documents from the Downloads directory
                        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File[] files = downloadsDirectory.listFiles();

                        if (files != null) {
                            for (File file : files) {
                                if (file.isFile()) {
                                    String documentPath = file.getAbsolutePath();
                                    documentsData.append(documentPath).append("\n");
                                }
                            }
                        }

                        // Append documentsData to backupData
                        backupData.append("Documents:\n").append(documentsData).append("\n");
                    }

                    // Save the data to the database
                    backupRef.setValue(backupData.toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Data backup successful
                                    Toast.makeText(RemoteAccess.this, "Data backup successful", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Data backup failed
                                    Toast.makeText(RemoteAccess.this, "Data backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // User is not logged in or authenticated
                    Toast.makeText(RemoteAccess.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Get the current user ID or any other identifier
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();

                    // Build the Firebase Realtime Database reference
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference backupRef = database.getReference("backups").child(userId);

                    // Check if the READ_CONTACTS permission is granted
                    if (ContextCompat.checkSelfPermission(RemoteAccess.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RemoteAccess.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
                    } else {
                        // Permission granted, perform the contacts query
                        performContactsQuery(RemoteAccess.this, backupRef);
                    }

                    // Check if the READ_EXTERNAL_STORAGE permission is granted for images
                    if (ContextCompat.checkSelfPermission(RemoteAccess.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RemoteAccess.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    } else {
                        // Permission granted, perform image backup
                        performImageBackup(backupRef);
                    }

                    // Check if the READ_EXTERNAL_STORAGE permission is granted for audio/video
                    if (ContextCompat.checkSelfPermission(RemoteAccess.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RemoteAccess.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    } else {
                        // Permission granted, perform audio/video backup
                        performAudioVideoBackup(backupRef);
                    }

                    // Check if the READ_EXTERNAL_STORAGE permission is granted for documents
                    if (ContextCompat.checkSelfPermission(RemoteAccess.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(RemoteAccess.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    } else {
                        // Permission granted, perform document backup
                        performDocumentBackup(backupRef);
                    }
                    if (requestCode == REBOOT_PERMISSION_REQUEST_CODE) {
                        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            // Permission granted, reboot the device
                            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                            if (powerManager != null) {
                                powerManager.reboot(null);
                            }
                        }
                    }
                } else {
                    // User is not logged in or authenticated
                    Toast.makeText(RemoteAccess.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Permission denied, handle it accordingly
                Toast.makeText(this, "Permission denied. Cannot perform data backup.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void performContactsQuery(RemoteAccess context, DatabaseReference backupRef) {
        // Query contacts and save the data to the database
        StringBuilder contactsData = new StringBuilder();

        // Retrieve contacts from the device using the ContactsContract API
        ContentResolver contentResolver = context.getContentResolver();
        Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = {ContactsContract.Contacts.DISPLAY_NAME};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = contentResolver.query(contactsUri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null && cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            do {
                String contactName = cursor.getString(nameColumnIndex);
                contactsData.append(contactName).append("\n");
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Save the contacts data to the database
        backupRef.child("Contacts").setValue(contactsData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Contacts backup successful
                        Toast.makeText(context, "Contacts backup successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Contacts backup failed
                        Toast.makeText(context, "Contacts backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performImageBackup(DatabaseReference backupRef) {
        // Backup images logic
        StringBuilder imagesData = new StringBuilder();

        // Retrieve images from the device
        String[] projectionImages = {MediaStore.Images.Media.DATA};
        Cursor cursorImages = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projectionImages, null, null, null);

        if (cursorImages != null && cursorImages.moveToFirst()) {
            int columnIndex = cursorImages.getColumnIndex(MediaStore.Images.Media.DATA);
            do {
                String imagePath = cursorImages.getString(columnIndex);
                imagesData.append(imagePath).append("\n");
            } while (cursorImages.moveToNext());
            cursorImages.close();
        }

        // Save the images data to the database
        backupRef.setValue(imagesData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Images backup successful
                        Toast.makeText(RemoteAccess.this, "Images backup successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Images backup failed
                        Toast.makeText(RemoteAccess.this, "Images backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performAudioVideoBackup(DatabaseReference backupRef) {
        // Backup audio/video logic
        StringBuilder audioVideoData = new StringBuilder();

        // Retrieve audio/video files from the device
        String[] projectionAudio = {MediaStore.Audio.Media.DATA};
        Cursor cursorAudio = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projectionAudio, null, null, null);

        if (cursorAudio != null && cursorAudio.moveToFirst()) {
            int columnIndex = cursorAudio.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String audioPath = cursorAudio.getString(columnIndex);
                audioVideoData.append(audioPath).append("\n");
            } while (cursorAudio.moveToNext());
            cursorAudio.close();
        }

        // Save the audio/video data to the database
        backupRef.setValue(audioVideoData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Audio/Video backup successful
                        Toast.makeText(RemoteAccess.this, "Audio/Video backup successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Audio/Video backup failed
                        Toast.makeText(RemoteAccess.this, "Audio/Video backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performDocumentBackup(DatabaseReference backupRef) {
        // Backup documents logic
        StringBuilder documentsData = new StringBuilder();

        // Retrieve documents from the device
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] files = downloadsDirectory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String documentPath = file.getAbsolutePath();
                    documentsData.append(documentPath).append("\n");
                }
            }
        }

        // Save the documents data to the database
        backupRef.setValue(documentsData.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Documents backup successful
                        Toast.makeText(RemoteAccess.this, "Documents backup successful", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Documents backup failed
                        Toast.makeText(RemoteAccess.this, "Documents backup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void trackLocation() {
        // Check location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Retrieve device location
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Store the location in the database as the latest known location
                            saveLocationInDatabase(latitude, longitude);

                            // Launch LocationActivity and pass the location data
                            Intent intent = new Intent(RemoteAccess.this, LocationActivity.class);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RemoteAccess.this, "Failed to retrieve location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveLocationInDatabase(double latitude, double longitude) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            LocationInfo locationInfo = new LocationInfo(latitude, longitude);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users").child(userId).child("latestLocation").setValue(locationInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // LocationActivity saved successfully
                            Toast.makeText(RemoteAccess.this, "Your Location has been saved", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to save location
                            Toast.makeText(RemoteAccess.this, "Failed to save location", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(RemoteAccess.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }
    }




    private void configureNetworkSettings() {
        Toast.makeText(this, "Configure Network Settings", Toast.LENGTH_SHORT).show();
        // Add your implementation logic here
    }

    private void configureAppSettings() {
        Toast.makeText(this, "Configure App Settings", Toast.LENGTH_SHORT).show();
        // Add your implementation logic here
    }

    private void rebootDevice() {
        // Check if the app has the necessary permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.REBOOT) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REBOOT}, REBOOT_PERMISSION_REQUEST_CODE);
            return;
        }

        // Add your reboot implementation logic here
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            powerManager.reboot(null);
        }
    }
    private void getDeviceInfo() {
        Toast.makeText(this, "Getting Device Info", Toast.LENGTH_SHORT).show();

        // Get the current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Build the Firebase Realtime Database reference
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference("users");

            // Retrieve the device details from the database
            usersRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Check if the user exists in the database
                    if (dataSnapshot.exists()) {
                        // Retrieve the user object
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            Users user = userSnapshot.getValue(Users.class);

                            // Extract the device details
                            String deviceManufacturer = user.getDeviceManufacturer();
                            String deviceModel = user.getDeviceModel();
                            String deviceSerialNumber = user.getDeviceSerialNumber();
                            String deviceIMEI = user.getDeviceIMEI();

                            // Inflate the layout and find the views
                            View layout = getLayoutInflater().inflate(R.layout.device_details, null);
                            TextView textViewManufacturer = layout.findViewById(R.id.textViewManufacturer);
                            TextView textViewModel = layout.findViewById(R.id.textViewModel);
                            TextView textViewSerialNumber = layout.findViewById(R.id.textViewSerialNumber);
                            TextView textViewIMEI = layout.findViewById(R.id.textViewIMEI);

                            // Set the device information in the views
                            textViewManufacturer.setText("Manufacturer: " + deviceManufacturer);
                            textViewModel.setText("Model: " + deviceModel);
                            textViewSerialNumber.setText("Serial Number: " + deviceSerialNumber);
                            textViewIMEI.setText("IMEI: " + deviceIMEI);

                            // Create and show an AlertDialog with the layout
                            AlertDialog.Builder builder = new AlertDialog.Builder(RemoteAccess.this);
                            builder.setView(layout);
                            builder.setTitle("Device Information");
                            builder.setPositiveButton("OK", null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    } else {
                        // The user does not exist in the database
                        Toast.makeText(RemoteAccess.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any errors that occur during the database operation
                    Toast.makeText(RemoteAccess.this, "Failed to get device info: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
