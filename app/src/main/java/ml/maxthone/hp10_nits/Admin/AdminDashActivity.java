package ml.maxthone.hp10_nits.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ml.maxthone.hp10_nits.MainActivity;
import ml.maxthone.hp10_nits.Models.ModelUser;
import ml.maxthone.hp10_nits.R;

public class AdminDashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView name, email;
    private CircleImageView profileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash);

        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Admin Home");
        } catch (Exception ignored) {
        }

        //get user data

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            //user is not signed in
            auth.signOut();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return;
        }

        name = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        profileImage =findViewById(R.id.user_icon);

        //get the user data from db
        fetchData();
    }

    private void fetchData() {
        db.collection("users").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).addSnapshotListener((value, error) -> {
            try {
                if (error != null) {
                    Toast.makeText(this, "Error while fetching user data.", Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE_ERROR", error.toString());
                    return;
                }
                if (value != null) {
                    //data fetched
                    ModelUser user = value.toObject(ModelUser.class);
                    assert user != null;
                    name.setText(user.getName());
                    email.setText(user.getEmail());
                    if(!user.getProfileImage().equalsIgnoreCase("default")){
                        //set image
                        Glide.with(getApplicationContext()).load(user.getProfileImage()).into(profileImage);
                    }
                    return;
                }
                throw new Exception("Something went wrong.");
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e("FIRESTORE_ERROR", e.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_home_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_users_menu_item:
                //manage user navigation
                startActivity(new Intent(getApplicationContext(), ManageUsersActivity.class));
                return true;
            case R.id.profile:
                Intent i =  new Intent(getApplicationContext(), UserDetailsActivity.class);
                i.putExtra("id", Objects.requireNonNull(auth.getCurrentUser()).getUid());
                startActivity(i);
                return true;
            case R.id.admin_logout:
                auth.signOut();
                Intent i1 = new Intent(getApplicationContext(), MainActivity.class);
                i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}