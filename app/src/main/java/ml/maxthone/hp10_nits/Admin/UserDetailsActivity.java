package ml.maxthone.hp10_nits.Admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

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

    private AppCompatButton updateBtn, deleteBtn;

    private RadioButton adminBtn, userBtn;

    String role = "User";

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
        if(auth.getCurrentUser() == null){
            //user is not logged in
            //sign out user
            auth.signOut();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return;
        }
        String uid = getIntent().getStringExtra("id");
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
        deleteBtn = findViewById(R.id.delete_btn);

        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setMessage("Fetching data from cloud.");
        pd.setCancelable(false);

        pd.show();

        if(!admin){
            deleteBtn.setVisibility(View.GONE);
            adminBtn.setEnabled(false);
            userBtn.setEnabled(false);
        }

        db.collection("users").document(uid).addSnapshotListener((value, error) -> {
            ModelUser user = null;
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
            if(!user.getProfileImage().equalsIgnoreCase("default")){
                //set image
                Glide.with(getApplicationContext()).load(user.getProfileImage()).into(profileImage);
            }
            if(user.getUid().equalsIgnoreCase(auth.getCurrentUser().getUid())){
                deleteBtn.setVisibility(View.GONE);
                adminBtn.setEnabled(false);
                userBtn.setEnabled(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}