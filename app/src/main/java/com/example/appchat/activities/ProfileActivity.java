package com.example.appchat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appchat.R;
import com.example.appchat.utils.Constants;
import com.example.appchat.utils.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText etName, etGmail, etPassword;
    private Button btnSave;
    private ImageView ivAvatar;
    private FirebaseFirestore db;
    private static final String TAG = "ProfileActivity";
    private DocumentReference docRef;
    private String encodeImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferenceManager = new PreferenceManager(getApplicationContext());

        etName = findViewById(R.id.etName);
        etGmail = findViewById(R.id.etGmail);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);
        ivAvatar = findViewById(R.id.ivAvatar);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        // Get user ID from PreferenceManager
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        if (userId == null) {
            Log.e(TAG, "User ID is null. Check the preference manager initialization.");
            Toast.makeText(this, "Error: User ID is null", Toast.LENGTH_SHORT).show();
            return;
        }
        db = FirebaseFirestore.getInstance();

        docRef = db.collection("users").document(userId);

        fetchDataFromFirestore();

        btnSave.setOnClickListener(v -> saveDataToFirestore());
        ivAvatar.setOnClickListener(v -> pickImage());

    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        ivAvatar.setImageBitmap(bitmap);
                        encodeImage = encodeImage(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayInputStream);
        byte[] bytes = byteArrayInputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void fetchDataFromFirestore() {
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Map<String, Object> data = document.getData();
                    if (data != null) {
                        etName.setText((String) data.get("name"));
                        etGmail.setText((String) data.get("email"));
                        etPassword.setText((String) data.get("password")); // Note: Ensure passwords are handled securely
                        String imageBase64 = (String) data.get("image");
                        if (imageBase64 != null) {
                            byte[] bytes = Base64.decode(imageBase64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ivAvatar.setImageBitmap(bitmap);
                        }
                    }
                } else {
                    Log.d(TAG, "No such document");
                    Toast.makeText(ProfileActivity.this, "Document does not exist!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(ProfileActivity.this, "Error while loading!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDataToFirestore() {
        String name = etName.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        if (encodeImage != null) {
            user.put("image", encodeImage);
        }

        docRef.update(user).addOnSuccessListener(aVoid -> {
            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error updating document", e);
            Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
        });
    }
}
