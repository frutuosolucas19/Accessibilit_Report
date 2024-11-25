package com.example.acessibilit_report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.model.Pessoa;
import com.example.acessibilit_report.model.Usuario;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CadastroUsuarioActivity extends AppCompatActivity {

    private ImageView imgPerfil;
    private EditText txtNome;
    private EditText txtUsuario;
    private EditText txtEmail;
    private EditText txtSenha;
    private EditText txtConfirmaSenha;
    private Button btnCadastro;
    private Button btnImagem;
    private TextView txvEsqueceuSenha;
    private TextView txvCadastrar;
    private static final int PEGA_FOTO = 1;
    private static final int CODIGO_CAMERA = 2;
    private Context context = this;
    private String imagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().hide();

        imgPerfil = (ImageView) findViewById(R.id.imagemPerfil);
        txtNome = (EditText) findViewById(R.id.editTextNome);
        txtUsuario = (EditText) findViewById(R.id.editTextUsuario);
        txtEmail = (EditText) findViewById(R.id.editTextEmail);
        txtSenha = (EditText) findViewById(R.id.editTextSenha);
        txtConfirmaSenha = (EditText) findViewById(R.id.editTextConfirmaSenha);
        btnCadastro = (Button) findViewById(R.id.buttonCadastro);
        btnImagem = (Button) findViewById(R.id.buttonSelecionImagem);
        txvCadastrar = (TextView) findViewById(R.id.textViewTelaCadastro);



        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtNome.getText().toString().isEmpty() &&
                        txtUsuario.getText().toString().isEmpty() &&
                        txtEmail.getText().toString().isEmpty() &&
                        txtSenha.getText().toString().isEmpty() &&
                        txtConfirmaSenha.getText().toString().isEmpty()) {

                    Toast.makeText(context, "Por favor, informe todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (txtSenha.getText().equals(txtConfirmaSenha.getText())) {

                    Toast.makeText(context, "As senhas informadas não são iguais", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    postUsuario(txtNome.getText().toString(),
                            txtUsuario.getText().toString(),
                            txtEmail.getText().toString(),
                            imagem,
                            txtSenha.getText().toString());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        btnImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPegaFoto = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intentPegaFoto, "Selecione uma imagem"), PEGA_FOTO);
                intentPegaFoto.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intentPegaFoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PEGA_FOTO) {
                Uri imagemSelecionada = data.getData();

                imgPerfil.setImageURI(imagemSelecionada);

                imagem = imagemSelecionada.toString();

            }
        }
    }

    private void postUsuario(String nome, String nomeUsuario, String email, String imagem, String senha) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        Pessoa pessoa = new Pessoa(nome, nomeUsuario, email, imagem);

        Usuario usuario = new Usuario(pessoa, "normal", email, senha);

        Call<Usuario> call = new RetrofitInitializer().getUsuario().createPost(usuario);
        
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Toast.makeText(context, "Data add na API", Toast.LENGTH_SHORT).show();

        }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(context, "Falha na requisição", Toast.LENGTH_LONG).show();
            }
        });

    }

    private String criptografarSenha(String senha) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
        byte messageDigest[] = algorithm.digest(senha.getBytes("UTF-8"));

        return messageDigest.toString();
    }
}


