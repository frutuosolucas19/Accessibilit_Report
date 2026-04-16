package com.example.acessibilit_report.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.ResetPasswordRequest;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.UserService;
import com.example.acessibilit_report.util.PasswordToggle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText txtTokenAws;
    private EditText txtNovaSenha;
    private EditText txtRepetirSenha;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        txtTokenAws = findViewById(R.id.editTextTokenAws);
        txtNovaSenha = findViewById(R.id.editTextNovaSenha);
        txtRepetirSenha = findViewById(R.id.editTextRepetirSenha);
        btnSalvar = findViewById(R.id.buttonSalvarNovaSenha);

        PasswordToggle.setup(txtNovaSenha);
        PasswordToggle.setup(txtRepetirSenha);

        btnSalvar.setOnClickListener(v -> redefinirSenha());
    }

    private void redefinirSenha() {
        String tokenAws = txtTokenAws.getText().toString().trim();
        String novaSenha = txtNovaSenha.getText().toString();
        String repetirSenha = txtRepetirSenha.getText().toString();

        if (tokenAws.isEmpty() || novaSenha.isEmpty() || repetirSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!novaSenha.equals(repetirSenha)) {
            Toast.makeText(this, "As senhas informadas nao sao iguais", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSalvar.setEnabled(false);
        UserService service = RetrofitInitializer.getUsuarioService(this);
        service.resetPassword(new ResetPasswordRequest(tokenAws, novaSenha)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnSalvar.setEnabled(true);
                if (!response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Nao foi possivel redefinir a senha", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ResetPasswordActivity.this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSalvar.setEnabled(true);
                Toast.makeText(ResetPasswordActivity.this, "Erro ao conectar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
