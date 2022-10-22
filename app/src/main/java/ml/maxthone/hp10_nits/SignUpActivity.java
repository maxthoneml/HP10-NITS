package ml.maxthone.hp10_nits;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private TextView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        backBtn = findViewById(R.id.signInLink);

        backBtn.setOnClickListener(v->{
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        });

    }
}