package com.example.mobiledevicemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import de.hdodenhof.circleimageview.CircleImageView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mobiledevicemanager.models.Alerts;
import com.example.mobiledevicemanager.models.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_DEVICE_ADMIN = 1;
    private static final int REQUEST_PERMISSION_LOCATION = 1;

    private ImageView activateAdminButton, logout;

    private LinearLayout linearLayout;
    private ImageView btnLocale;

    private ImageView imageViewAvatar;
    private static final int REQUEST_IMAGE_PICK = 1;
    private CircleImageView circleImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().hide();

        TextView textViewDeviceInfo = findViewById(R.id.textViewDeviceInfo);
        TextView textViewUsername = findViewById(R.id.username);

        circleImageView = findViewById(R.id.avatar);

        LinearLayout alertsLayout = findViewById(R.id.alerts);
        alertsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the alerts layout or perform any desired action
                Intent intent = new Intent(MainActivity.this, AlertsActivity.class);
                startActivity(intent);
            }
        });


        LinearLayout updatesLayout = findViewById(R.id.updates);

        updatesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateDialog();
            }
        });

        LinearLayout subscribeLayout = findViewById(R.id.subscribe);

        subscribeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPremiumMembershipDialog();
            }
        });


        btnLocale = findViewById(R.id.btnLocale);
        btnLocale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableLocation();
            }
        });

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);


        linearLayout = findViewById(R.id.remote);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the RemoteAccessActivity
                Intent intent = new Intent(MainActivity.this, RemoteAccess.class);
                startActivity(intent);
            }
        });

        imageViewAvatar = findViewById(R.id.avatar);

        registerForContextMenu(imageViewAvatar);

        imageViewAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openContextMenu(v);
                return true;
            }
        });

        imageViewAvatar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openContextMenu(v);
                return true;
            }
        });

        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to select an image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_PICK);
            }
        });


        activateAdminButton = findViewById(R.id.admin);
        activateAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDeviceAdminActivation();
            }
        });

        // Inside your activity's onCreate() method or wherever you initialize the logout button

        logout  = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear user session data
                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Redirect to login activity
                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        retrieveDeviceInfo();
    }

    private void showPremiumMembershipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Premium Membership");
        builder.setMessage("This service is only available for premium users. If you want to upgrade your membership, please contact support.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Updates Available");
        builder.setMessage("We will alert you once a new update is available. You will be notified once it is downloaded and ready to install.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            // Save the image URI as the avatar in your database
            saveAvatarToDatabase(imageUri);
            // Set the selected image as the ImageView's source
            imageViewAvatar.setImageURI(imageUri);
        } else if (requestCode == REQUEST_DEVICE_ADMIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Device administration activated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Device administration activation failed or was canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveAvatarToDatabase(Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the Firebase Storage bucket
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

            // Create a reference to the avatar image file in Firebase Storage
            StorageReference avatarRef = storageRef.child("avatars").child(userId + ".jpg");

            // Use Glide to resize, crop, and apply the circular shape with a border
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.profil)
                    .error(R.drawable.error_image)
                    .transform(new CircleCrop());

            Glide.with(this)
                    .asBitmap()
                    .load(imageUri)
                    .apply(requestOptions)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Convert the Bitmap image to a byte array or save it to Firebase Storage directly
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageData = baos.toByteArray();

                            // Upload the image file to Firebase Storage
                            UploadTask uploadTask = avatarRef.putBytes(imageData);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image upload successful
                                    // Get the download URL of the uploaded image
                                    avatarRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadUri) {
                                            // Save the download URL as the avatar reference in your database
                                            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                                            databaseRef.child("users").child(userId).child("avatarUrl").setValue(downloadUri.toString());

                                            // Show a success message or perform any other actions
                                            Toast.makeText(MainActivity.this, "Avatar saved successfully", Toast.LENGTH_SHORT).show();

                                            // Load and display the updated avatar image using Glide
                                            Glide.with(MainActivity.this)
                                                    .load(downloadUri)
                                                    .apply(requestOptions)
                                                    .into(circleImageView);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle any errors that occur while getting the download URL
                                            Toast.makeText(MainActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors that occur while uploading the image
                                    Toast.makeText(MainActivity.this, "Failed to upload avatar", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Do nothing
                        }
                    });
        } else {
            // User is not authenticated
            Toast.makeText(MainActivity.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveDeviceInfo() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Users user = dataSnapshot.getValue(Users.class);
                        if (user != null) {
                            String deviceModel = user.getDeviceModel();
                            String deviceManufacturer = user.getDeviceManufacturer();
                            String username = user.getUsername();
                            String phone = user.getPhoneNumber();

                            String capitalizedUsername = capitalizeFirstLetter(username);


                            String displayName = capitalizedUsername;

                            // Display the device model and manufacturer in a TextView or any other UI component
                            TextView textViewUsername = findViewById(R.id.username);
                            textViewUsername.setText(displayName);

                            TextView textViewDeviceInfo = findViewById(R.id.textViewDeviceInfo);
                            textViewDeviceInfo.setText("Account's Device: "  +  "\nOwner: "  + username +  "\nPhone: " + phone + "\nModel: " + deviceModel + "\nManufacturer: " + deviceManufacturer);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle any error that occurs during the database operation
                    Toast.makeText(MainActivity.this, "Failed to retrieve device information", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User is not authenticated
            Toast.makeText(MainActivity.this, "User is not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        char[] charArray = str.toCharArray();
        charArray[0] = Character.toUpperCase(charArray[0]);

        return new String(charArray);
    }

    private void enableLocation() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Location permission is already granted
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Location is enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Location permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted by the user
                Toast.makeText(MainActivity.this, "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Location permission denied by the user
                Toast.makeText(MainActivity.this, "Location permission denied", Toast.LENGTH_SHORT).show();
                openAppSettings();
            }
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    private void requestDeviceAdminActivation() {
        ComponentName adminComponent = new ComponentName(this, MyDeviceAdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device administration for Blue Security app");
        startActivityForResult(intent, REQUEST_DEVICE_ADMIN);
    }

}