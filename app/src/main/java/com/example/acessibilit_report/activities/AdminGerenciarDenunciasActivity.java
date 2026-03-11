package com.example.acessibilit_report.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.R;

public class AdminGerenciarDenunciasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_gerenciar_denuncias);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
    }
}
