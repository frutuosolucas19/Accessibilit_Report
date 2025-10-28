package com.example.acessibilit_report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Evita NPE quando o tema não tiver ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        View btnMenu = findViewById(R.id.imageMenu);
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        NavigationView navigationView = findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
        }

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostFragment);

        if (navHostFragment == null) {
            Toast.makeText(this, "NavHostFragment não encontrado (verifique activity_menu.xml)", Toast.LENGTH_LONG).show();
            return;
        }

        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);
    }
}
