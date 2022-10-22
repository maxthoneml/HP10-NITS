package ml.maxthone.hp10_nits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    BottomNavigationView bnav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type = getIntent().getStringExtra("role");


        setContentView(R.layout.activity_dashboard);
        bnav = findViewById(R.id.bottom_navigation);

        if(type.equals("regular")){
            bnav.inflateMenu(R.menu.bottom_navigation_menu_normal);
        }
        if(type.equals("admin")){
            bnav.inflateMenu(R.menu.bottom_navigation_menu_admin);
        }
        if(type.equals("collector")){
            bnav.inflateMenu(R.menu.bottom_navigation_menu_collector);
        }
    }
}