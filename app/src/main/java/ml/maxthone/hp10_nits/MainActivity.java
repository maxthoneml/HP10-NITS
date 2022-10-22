package ml.maxthone.hp10_nits;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import ml.maxthone.hp10_nits.Admin.AdminDashActivity;
import ml.maxthone.hp10_nits.Models.ModelUser;
import ml.maxthone.hp10_nits.User.UserDashActivity;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private TextView loginBtn, signUpBtn, googleBtn;
    private ProgressDialog pd;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailInput = findViewById(R.id.login_email);
        passwordInput = findViewById(R.id.login_password);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);
        pd.setTitle("Logging in");
        pd.setCancelable(false);
        //check for login state
        if (auth.getCurrentUser() != null) {
            //user is logged in
            //nav to another page as per role
            pd.setMessage("Processing your request...");
            pd.show();
            validateUserType();
            return;
        }


        loginBtn = findViewById(R.id.login_btn);
        googleBtn = findViewById(R.id.google_sign_in_btn);
        signUpBtn = findViewById(R.id.sign_up_nav);


        loginBtn.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (!validateData(email, password)) {
            return;
        }
        pd.setMessage("Processing your request...");
        pd.show();
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(this, "User Logged in successfully", Toast.LENGTH_SHORT).show();
            validateUserType();
        }).addOnFailureListener(e -> {
            Log.e("LOGIN ERROR", e.getMessage());
            pd.dismiss();
        });
    }

    private void validateUserType() {
        pd.setMessage("Validating your data");
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                ModelUser user = documentSnapshot.toObject(ModelUser.class);
                if (user == null) {
                    Toast.makeText(this, "Unable to fetch user data", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                    pd.dismiss();
                    return;
                }
                Toast.makeText(this, "User name :- " + user.getName(), Toast.LENGTH_SHORT).show();
                String role = user.getRole();
                pd.dismiss();
                if (role.equalsIgnoreCase("admin")) {
                    startActivity(new Intent(this, AdminDashActivity.class));
//                }else if(role.equalsIgnoreCase("collector")){
//                    if(user.isActive()) {
//                        startActivity(new Intent(this, CollectorDashActivity.class));
//                    }else{
//                        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//                        alert.setTitle("Login Error")
//                                .setMessage("Your account has not been accepted by admin. Please contact admin to activate now.")
//                                .setCancelable(false)
//                                .setPositiveButton("OK", (dialog, which) -> {
//                                    auth.signOut();
//                                    pd.dismiss();
//                                    dialog.dismiss();
//                                }).show();
//                    }
                } else {
                    startActivity(new Intent(this, UserDashActivity.class));
                }
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Error to get user data.", Toast.LENGTH_SHORT).show();
                //sign out user
                auth.signOut();
                pd.dismiss();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Error to get user data.", Toast.LENGTH_SHORT).show();
            //sign out user
            Log.e("AUTH_ERROR", e.toString());
            auth.signOut();
            pd.dismiss();
        });
    }

    private boolean validateData(String email, String password) {
        if (email.equalsIgnoreCase("")) {
            emailInput.setError("Email required");
            return false;
        }
        if (password.equalsIgnoreCase("")) {
            passwordInput.setError("Password required");
            return false;
        }
        return true;
    }
}