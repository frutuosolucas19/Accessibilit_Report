package com.example.acessibilit_report.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.R;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        Button btnGerenciarDenuncias = findViewById(R.id.buttonGerenciarDenuncias);
        Button btnGerenciarUsuarios = findViewById(R.id.buttonGerenciarUsuarios);
        Button btnGerenciarForum = findViewById(R.id.buttonGerenciarForum);

        btnGerenciarDenuncias.setOnClickListener(v ->
                abrirOuAvisar(
                        AdminManageReportsActivity.class,
                        getString(R.string.admin_msg_gerenciar_denuncias)
                )
        );

        btnGerenciarUsuarios.setOnClickListener(v ->
                abrirOuAvisar(
                        AdminManageUsersActivity.class,
                        getString(R.string.admin_msg_gerenciar_usuarios)
                )
        );

        btnGerenciarForum.setOnClickListener(v ->
                abrirOuAvisar(
                        QuestionRegistrationActivity.class,
                        getString(R.string.admin_msg_gerenciar_forum)
                )
        );
    }

    private void abrirOuAvisar(Class<?> activity, String fallbackMsg) {
        try {
            startActivity(new Intent(this, activity));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, fallbackMsg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.admin_msg_erro_abrir_tela), Toast.LENGTH_LONG).show();
        }
    }
}

