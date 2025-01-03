package com.example.acessibilit_report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.acessibilit_report.model.Denuncia;
import com.example.acessibilit_report.model.Historico;
import com.example.acessibilit_report.model.Local;
import com.example.acessibilit_report.model.Pessoa;
import com.example.acessibilit_report.model.Status;
import com.example.acessibilit_report.model.Usuario;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroDenunciaFragment extends Fragment {

    private EditText txtNomeLocal;
    private EditText txtEndereco;
    private EditText txtProblema;
    private EditText txtSugestao;
    private String imagem;
    private int usuarioId;
    private Button btnLocalizacao;
    private Button btnCadastrar;
    private Button btnImagem;
    private static final int PEGA_FOTO = 1;
    private static final int CODIGO_CAMERA = 2;
    private ImageView imgDenuncia;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_cadastro_denuncia, container, false);

        context=getContext();

        txtNomeLocal = (EditText) view.findViewById(R.id.editTextNomeLocal);
        txtEndereco = (EditText) view.findViewById(R.id.editTextEndereco);
        txtProblema = (EditText) view.findViewById(R.id.editTextProblema);
        txtSugestao = (EditText) view.findViewById(R.id.editTextSugestão);
        btnCadastrar = (Button) view.findViewById(R.id.bt_cadastrar);
        btnImagem = (Button) view.findViewById(R.id.bt_imagem);
        imgDenuncia = (ImageView) view.findViewById(R.id.imagemDenuncia);



        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtNomeLocal.getText().toString().isEmpty() &&
                        txtProblema.getText().toString().isEmpty() &&
                        txtEndereco.getText().toString().isEmpty() &&
                        txtSugestao.getText().toString().isEmpty()) {

                    Toast.makeText(view.getContext(), "Por favor, informe todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Primeiro devo selecionar o local, endereco e opcionalmente a localização
               Local local = new Local();

                //Depois pegar o usuario da denuncia
               Usuario usuario = new Usuario();


                postDenuncia(local, usuario, txtProblema.getText().toString(),
                             txtSugestao.getText().toString(), imagem);
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

        return view;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PEGA_FOTO) {
                Uri imagemSelecionada = data.getData();

                imgDenuncia.setImageURI(imagemSelecionada);

                imagem = imagemSelecionada.toString();

            }
        }
    }
    private void postDenuncia(Local local, Usuario usuario, String problema, String sugestao, String imagem) {

        Status statusAtual = new Status("Criada");

        Denuncia denuncia = new Denuncia(local, problema, sugestao, usuario, imagem, statusAtual);

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

}