package com.example.acessibilit_report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CadastroDenunciaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CadastroDenunciaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText txtNomeLocal;
    private EditText txtEndereco;
    private EditText txtProblema;
    private EditText txtSugestao;
    private String imagem;
    private Button btnLocalizacao;
    private Button btnCadastrar;
    private Button btnImagem;
    private static final int PEGA_FOTO = 1;
    private static final int CODIGO_CAMERA = 2;
    private ImageView imgDenuncia;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_cadastro_denuncia, container, false);


        txtNomeLocal = (EditText) view.findViewById(R.id.editTextNomeLocal);
        txtProblema = (EditText) view.findViewById(R.id.editTextProblema);
        txtSugestao = (EditText) view.findViewById(R.id.editTextSugestão);
        btnCadastrar = (Button) view.findViewById(R.id.bt_cadastrar);
        btnImagem = (Button) view.findViewById(R.id.bt_imagem);
        imgDenuncia = (ImageView) view.findViewById(R.id.imagemPerfil);



        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        ActivityResultLauncher<Intent> exampleActivityResult= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent data = new Intent();
                                Uri imagemSelecionada = data.getData();
                                imgDenuncia.setImageURI(imagemSelecionada);
                                imagem = imagemSelecionada.toString();

                        }
                    }
                });
        return view;
    }

}