package ml.maxthone.hp10_nits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import ml.maxthone.hp10_nits.Models.ModelUser;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private TextView loginBtn, signUpBtn, googleBtn;
    private ProgressDialog pd;
    private TextView forgotpass;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailInput = findViewById(R.id.login_email);
        passwordInput = findViewById(R.id.login_password);
        signUpBtn = findViewById(R.id.sign_up_nav);
        forgotpass = findViewById(R.id.forgotPassword);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        pd = new ProgressDialog(this);
        pd.setTitle("Logging in");
        pd.setCancelable(false);
        //check for login state
        if(auth.getCurrentUser()!= null){
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
        signUpBtn.setOnClickListener(v-> {
            Intent intent = new Intent(this,SignUpActivity.class);
            startActivity(intent);
        });

        forgotpass.setOnClickListener(v->{
            Intent intent = new Intent(this,ForgotPassActivity.class);
            startActivity(intent);
        });


    }

    private void loginUser(){
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if(!validateData(email, password)){
            return;
        }
        pd.setMessage("Processing your request...");
        pd.show();
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(this, "User Logged in successfully", Toast.LENGTH_SHORT).show();
            validateUserType();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Unable to log in user.", Toast.LENGTH_SHORT).show();
            Log.e("LOGIN ERROR",e.getMessage());
            pd.dismiss();
        });
    }

    private void validateUserType() {
        pd.setMessage("Validating your data");
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                ModelUser user = documentSnapshot.toObject(ModelUser.class);
                Toast.makeText(this, "User name :- "+user.getName(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "Error to get user data.", Toast.LENGTH_SHORT).show();
                //sign out user
                auth.signOut();
                pd.dismiss();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Error to get user data.", Toast.LENGTH_SHORT).show();
            //sign out user
            auth.signOut();
            pd.dismiss();
        });
    }

    private boolean validateData(String email, String password) {
        if(email.equalsIgnoreCase("")){
            emailInput.setError("Email required");
            return false;
        }
        if(password.equalsIgnoreCase("")){
            passwordInput.setError("Password required");
            return false;
        }
        return true;
    }
}