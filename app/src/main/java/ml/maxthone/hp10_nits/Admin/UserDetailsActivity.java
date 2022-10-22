package ml.maxthone.hp10_nits.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.maxthone.hp10_nits.MainActivity;
import ml.maxthone.hp10_nits.Models.ModelUser;
import ml.maxthone.hp10_nits.R;

public class UserDetailsActivity extends AppCompatActivity {

    private ProgressDialog pd;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText name, phone;
    private TextView email;
    private CircleImageView profileImage;

    private AppCompatButton updateBtn, disableUserBtn, enableUserBtn;

    private RadioButton adminBtn, userBtn;

    private StorageReference mStorageRef;
    private Uri imageUri;

    String role = "User";
    String uid = "";

    public static final int PICK_IMAGE = 90101;
    ModelUser user = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("User Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception ignored) {
        }

        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        if(auth.getCurrentUser() == null){
            //user is not logged in
            //sign out user
            auth.signOut();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return;
        }
        uid = getIntent().getStringExtra("id");
        if(uid == null){
            uid = auth.getCurrentUser().getUid();
        }
        boolean admin = getIntent().getBooleanExtra("admin", false);

        name = findViewById(R.id.user_name_inp);
        email = findViewById(R.id.user_email);
        phone = findViewById(R.id.user_phone_inp);
        profileImage = findViewById(R.id.user_icon);

        adminBtn = findViewById(R.id.admin);
        userBtn = findViewById(R.id.user);

        adminBtn.setOnClickListener(v -> role = "Admin");
        userBtn.setOnClickListener(v -> role = "User");

        updateBtn = findViewById(R.id.update_btn);
        disableUserBtn = findViewById(R.id.disable_user_btn);
        enableUserBtn = findViewById(R.id.enable_user);

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setMessage("Fetching data from cloud.");
        pd.setCancelable(false);

        pd.show();

        if(!admin){
            disableUserBtn.setVisibility(View.GONE);
            adminBtn.setEnabled(false);
            userBtn.setEnabled(false);
        }

        db.collection("users").document(uid).addSnapshotListener((value, error) -> {

            if (value != null) {
                user = value.toObject(ModelUser.class);
            }
            assert user != null;
            pd.dismiss();
            name.setText(user.getName());
            email.setText(user.getEmail());
            phone.setText(user.getPhone());
            if(user.getRole().equalsIgnoreCase("admin")){
                adminBtn.setChecked(true);
            }else{
                userBtn.setChecked(true);
            }
            if(user.isActive()){
                enableUserBtn.setVisibility(View.GONE);
                disableUserBtn.setVisibility(View.VISIBLE);
            }else{
                enableUserBtn.setVisibility(View.VISIBLE);
                disableUserBtn.setVisibility(View.GONE);
            }
            if(!user.getProfileImage().equalsIgnoreCase("default")){
                //set image
                Glide.with(getApplicationContext()).load(user.getProfileImage()).into(profileImage);
            }
            if(user.getUid().equalsIgnoreCase(auth.getCurrentUser().getUid())){
                disableUserBtn.setVisibility(View.GONE);
                adminBtn.setEnabled(false);
                userBtn.setEnabled(false);
            }
        });

        profileImage.setOnClickListener(v->{
            //pick image
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

        });

        disableUserBtn.setOnClickListener(v -> {
            user.setActive(false);
            pd.setMessage("Disabling user");
            pd.show();
            updateData(pd);
        });
        enableUserBtn.setOnClickListener(v -> {
            user.setActive(true);
            pd.setMessage("Enabling user");
            pd.show();
            updateData(pd);
        });

        updateBtn.setOnClickListener(v->{
            //update btn
            user.setName(name.getText().toString());
            user.setPhone(phone.getText().toString());
            user.setRole(role);
            if(imageUri!= null){
                uploadImage();
            }else{
                pd.setMessage("Updating data");
                pd.show();
                updateData(pd);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(getApplicationContext()).load(imageUri).into(profileImage);
        } else {
            Toast.makeText(this, "Operation Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Data");
        progressDialog.setMessage("Uploading Image");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StorageReference uploadImage = mStorageRef.child("images/users/" + uid);
        uploadImage.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                    Task<Uri> task = Objects.requireNonNull(Objects.requireNonNull(taskSnapshot.getMetadata()).getReference()).getDownloadUrl();
                    task.addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        user.setProfileImage(imageUrl);
                        Glide.with(getApplicationContext()).load(imageUrl).into(profileImage);
                        progressDialog.setMessage("Updating data");
                        updateData(progressDialog);
                    });
                })
                .addOnFailureListener(exception -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed to upload" + exception, Toast.LENGTH_SHORT).show();
                }).addOnProgressListener(snapshot -> {
                    progressDialog.setMessage("Uploading Image. Please Wait.");
                });
    }

    private void updateData(ProgressDialog pd1) {
        db.collection("users").document(user.getUid()).update(user.toMap())
                .addOnSuccessListener(unused -> {
                    pd1.dismiss();
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    pd1.dismiss();
                    Toast.makeText(this, "Unable to update image.", Toast.LENGTH_SHORT).show();
                });
    }
}