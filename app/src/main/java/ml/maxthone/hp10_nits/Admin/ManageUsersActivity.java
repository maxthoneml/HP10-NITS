package ml.maxthone.hp10_nits.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ml.maxthone.hp10_nits.Adapters.AdminUsersAdapter;
import ml.maxthone.hp10_nits.MainActivity;
import ml.maxthone.hp10_nits.Models.ModelUser;
import ml.maxthone.hp10_nits.R;

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView usersRecycler;
    private List<ModelUser> users;
    private ProgressDialog pd;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);


        Toolbar toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Manage Users");
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
        }
        db = FirebaseFirestore.getInstance();

        usersRecycler = findViewById(R.id.admin_user_list_recycler_view);

        usersRecycler.setLayoutManager(new LinearLayoutManager(this));
        users = new ArrayList<>();
        usersRecycler.setAdapter(new AdminUsersAdapter(this, users));

        pd = new ProgressDialog(this);
        pd.setTitle("Loading...");
        pd.setMessage("Fetching data from cloud.");
        pd.setCancelable(false);

        pd.show();
        db.collection("users").addSnapshotListener((value, error) -> {
            try {
                if (error != null) {
                    Toast.makeText(this, "Error while fetching user data.", Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE_ERROR", error.toString());
                    return;
                }
                if (value != null) {
                    //data fetched
                    users = new ArrayList<>();
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        try {
                            users.add(snapshot.toObject(ModelUser.class));
                        }catch (Exception ignored2){}
                    }
                    usersRecycler.setAdapter(new AdminUsersAdapter(this, users));
                    pd.dismiss();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}