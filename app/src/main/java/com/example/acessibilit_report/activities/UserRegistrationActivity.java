package com.example.acessibilit_report.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.model.User;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.UserService;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "UserRegistration";

    private ImageView imgPerfil;
    private EditText txtNome;
    private EditText txtEmail;
    private EditText txtSenha;
    private EditText txtConfirmaSenha;
    private Button btnCadastro;
    private Button btnImagem;
    private TextView txvJaTenhoConta;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null) return;
                Uri uri = result.getData().getData();
                if (uri == null) return;
                try {
                    getContentResolver().takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {}
                imgPerfil.setImageURI(uri);
                // URI local não é enviada ao servidor — apenas exibida localmente
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        imgPerfil        = findViewById(R.id.imagemPerfil);
        txtNome          = findViewById(R.id.editTextNome);
        txtEmail         = findViewById(R.id.editTextEmail);
        txtSenha         = findViewById(R.id.editTextSenha);
        txtConfirmaSenha = findViewById(R.id.editTextConfirmaSenha);
        btnCadastro      = findViewById(R.id.buttonCadastro);
        btnImagem        = findViewById(R.id.buttonSelecionImagem);
        txvJaTenhoConta  = findViewById(R.id.textViewTelaCadastro);

        txvJaTenhoConta.setOnClickListener(v ->
                startActivity(new Intent(UserRegistrationActivity.this, LoginActivity.class)));

        btnImagem.setOnClickListener(v -> {
            Intent intentPegaFoto = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intentPegaFoto.setType("image/*");
            intentPegaFoto.addCategory(Intent.CATEGORY_OPENABLE);
            intentPegaFoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentPegaFoto.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            pickImageLauncher.launch(Intent.createChooser(intentPegaFoto, "Selecione uma imagem"));
        });

        btnCadastro.setOnClickListener(v -> {
            String nome     = txtNome.getText().toString().trim();
            String email    = txtEmail.getText().toString().trim().toLowerCase(Locale.ROOT);
            String senha    = txtSenha.getText().toString();
            String confirma = txtConfirmaSenha.getText().toString();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirma.isEmpty()) {
                Toast.makeText(this, "Por favor, informe todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!senha.equals(confirma)) {
                Toast.makeText(this, "As senhas informadas não são iguais", Toast.LENGTH_SHORT).show();
                return;
            }

            btnCadastro.setEnabled(false);
            postUsuario(nome, email, senha);
        });
    }

    private void postUsuario(String nome, String email, String senha) {
        User usuario = new User();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);

        Log.d(TAG, "Enviando cadastro: nome=" + nome + " email=" + email);

        UserService api = RetrofitInitializer.getUsuarioService(this);

        api.create(usuario).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                btnCadastro.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UserRegistrationActivity.this,
                            "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        startActivity(new Intent(UserRegistrationActivity.this, LoginActivity.class));
                        finish();
                    }, 1200);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao ler errorBody", e);
                    }
                    Log.e(TAG, "Falha no cadastro: HTTP " + response.code() + " | " + errorBody);

                    String msg;
                    if (response.code() == 409) {
                        msg = "E-mail já cadastrado";
                    } else if (response.code() == 400) {
                        msg = errorBody.isEmpty()
                                ? "Dados inválidos (400)"
                                : "Erro: " + (errorBody.length() > 120
                                        ? errorBody.substring(0, 120) + "…"
                                        : errorBody);
                    } else {
                        msg = "Falha (" + response.code() + ")"
                                + (errorBody.isEmpty() ? "" : ": " + errorBody.substring(0, Math.min(80, errorBody.length())));
                    }
                    Toast.makeText(UserRegistrationActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                btnCadastro.setEnabled(true);
                Log.e(TAG, "Falha de rede no cadastro", t);
                Toast.makeText(UserRegistrationActivity.this,
                        "Falha na requisição: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
