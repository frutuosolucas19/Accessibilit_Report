package com.example.acessibilit_report;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin); // use o nome do seu XML aqui
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // Botões da sua tela
        Button btnGerenciarDenuncias = findViewById(R.id.buttonGerenciarDenuncias);
        Button btnGerenciarUsuarios  = findViewById(R.id.buttonGerenciarUsuarios);
        Button btnGerenciarForum     = findViewById(R.id.buttonGerenciarForum);

        // Abrir Gerenciar Denúncias
        btnGerenciarDenuncias.setOnClickListener(v ->
                abrirOuAvisar(AdminGerenciarDenunciasActivity.class,
                        "Tela de Gerenciar Denúncias ainda não implementada.")
        );

        // Abrir Gerenciar Usuários
        btnGerenciarUsuarios.setOnClickListener(v ->
                abrirOuAvisar(AdminGerenciarUsuariosActivity.class,
                        "Tela de Gerenciar Usuários ainda não implementada.")
        );

    }

    private void abrirOuAvisar(Class<?> activity, String fallbackMsg) {
        try {
            startActivity(new Intent(this, activity));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, fallbackMsg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir a tela.", Toast.LENGTH_LONG).show();
        }
    }
}
