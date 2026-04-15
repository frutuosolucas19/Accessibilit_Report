package com.example.acessibilit_report.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.acessibilit_report.BuildConfig;
import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.ReportResponse;
import com.example.acessibilit_report.model.Address;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.ReportService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportEditFragment extends Fragment {

    private ReportResponse report;

    private EditText editTextTitulo;
    private Spinner spinnerTipo;
    private EditText editTextLogradouro;
    private EditText editTextNumero;
    private EditText editTextComplemento;
    private EditText editTextCidade;
    private EditText editTextUF;
    private EditText editTextBairro;
    private EditText editTextCEP;
    private EditText editTextDescricao;
    private EditText editTextSugestao;
    private MaterialButton btnSalvar;

    // okhttp3.Call to cancel on destroy
    private okhttp3.Call pendingCall;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            report = (ReportResponse) getArguments().getSerializable("report");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_denuncia, container, false);

        editTextTitulo      = view.findViewById(R.id.editTextTitulo);
        spinnerTipo         = view.findViewById(R.id.spinnerTipo);
        editTextLogradouro  = view.findViewById(R.id.editTextLogradouro);
        editTextNumero      = view.findViewById(R.id.editTextNumero);
        editTextComplemento = view.findViewById(R.id.editTextComplemento);
        editTextCidade      = view.findViewById(R.id.editTextCidade);
        editTextUF          = view.findViewById(R.id.editTextUF);
        editTextBairro      = view.findViewById(R.id.editTextBairro);
        editTextCEP         = view.findViewById(R.id.editTextCEP);
        editTextDescricao   = view.findViewById(R.id.editTextDescricao);
        editTextSugestao    = view.findViewById(R.id.editTextSugestao);
        btnSalvar           = view.findViewById(R.id.btnSalvarAlteracoes);

        String[] tipos = getResources().getStringArray(R.array.tipos_denuncia);
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);

        preencherCampos(tipos);

        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
        view.findViewById(R.id.btnExcluirDenuncia).setOnClickListener(v -> confirmarExclusao());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pendingCall != null) {
            pendingCall.cancel();
            pendingCall = null;
        }
    }

    // ── Pre-fill ─────────────────────────────────────────────────────────────
    private void preencherCampos(String[] tipos) {
        if (report == null) return;

        if (report.titulo != null)   editTextTitulo.setText(report.titulo);
        if (report.descricao != null) editTextDescricao.setText(report.descricao);
        if (report.sugestao != null)  editTextSugestao.setText(report.sugestao);

        if (report.tipo != null) {
            for (int i = 0; i < tipos.length; i++) {
                if (tipos[i].equals(report.tipo)) {
                    spinnerTipo.setSelection(i);
                    break;
                }
            }
        }

        Address e = report.endereco;
        if (e != null) {
            if (e.getLogradouro() != null) editTextLogradouro.setText(e.getLogradouro());
            if (e.getNumero() != null)     editTextNumero.setText(String.valueOf(e.getNumero()));
            if (e.getComplemento() != null) editTextComplemento.setText(e.getComplemento());
            if (e.getCidade() != null)     editTextCidade.setText(e.getCidade());
            if (e.getUf() != null)         editTextUF.setText(e.getUf());
            if (e.getBairro() != null)     editTextBairro.setText(e.getBairro());
            if (e.getCep() != null)        editTextCEP.setText(e.getCep());
        }
    }

    // ── Save (PUT multipart) ─────────────────────────────────────────────────
    private void salvarAlteracoes() {
        String titulo    = t(editTextTitulo);
        String descricao = t(editTextDescricao);
        String cidade    = t(editTextCidade);
        String uf        = t(editTextUF);
        String tipo      = spinnerTipo.getSelectedItem() != null
                ? spinnerTipo.getSelectedItem().toString() : "";

        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(descricao)
                || TextUtils.isEmpty(cidade) || TextUtils.isEmpty(uf)
                || TextUtils.isEmpty(tipo)) {
            Toast.makeText(requireContext(),
                    "Informe título, tipo, descrição, cidade e UF.", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Builder body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("titulo",    titulo)
                .addFormDataPart("descricao", descricao)
                .addFormDataPart("tipo",      tipo)
                .addFormDataPart("cidade",    cidade)
                .addFormDataPart("uf",        uf);

        String sugestao    = t(editTextSugestao);
        String logradouro  = t(editTextLogradouro);
        String bairro      = t(editTextBairro);
        String cep         = t(editTextCEP);
        String complemento = t(editTextComplemento);
        String numeroStr   = t(editTextNumero);

        if (!sugestao.isEmpty())    body.addFormDataPart("sugestao",    sugestao);
        if (!logradouro.isEmpty())  body.addFormDataPart("logradouro",  logradouro);
        if (!bairro.isEmpty())      body.addFormDataPart("bairro",      bairro);
        if (!cep.isEmpty())         body.addFormDataPart("cep",         cep);
        if (!complemento.isEmpty()) body.addFormDataPart("complemento", complemento);
        if (!numeroStr.isEmpty()) {
            try {
                Integer.parseInt(numeroStr);
                body.addFormDataPart("numero", numeroStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Número de endereço inválido.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        btnSalvar.setEnabled(false);
        if (pendingCall != null) pendingCall.cancel();

        String url = BuildConfig.BASE_URL + "denuncia/" + report.id;
        Request request = new Request.Builder()
                .url(url)
                .put(body.build())
                .build();

        OkHttpClient client = RetrofitInitializer.getOkHttpClient(requireContext());
        pendingCall = client.newCall(request);
        pendingCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    btnSalvar.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                R.string.msg_denuncia_atualizada, Toast.LENGTH_LONG).show();
                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    } else if (response.code() == 401) {
                        Toast.makeText(requireContext(),
                                "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                    } else {
                        Toast.makeText(requireContext(),
                                "Erro ao salvar (" + response.code() + ")", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    btnSalvar.setEnabled(true);
                    Toast.makeText(requireContext(),
                            "Erro de rede: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // ── Delete ───────────────────────────────────────────────────────────────
    private void confirmarExclusao() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialogo_excluir_titulo)
                .setMessage(getString(R.string.dialogo_excluir_mensagem,
                        report != null ? report.titulo : ""))
                .setPositiveButton(R.string.btn_excluir, (d, w) -> executarDelete())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void executarDelete() {
        if (report == null) return;
        ReportService api = RetrofitInitializer.getDenunciaService(requireContext());
        retrofit2.Call<Void> call = api.deletar(report.id);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(),
                            R.string.msg_denuncia_excluida, Toast.LENGTH_SHORT).show();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                } else {
                    Toast.makeText(requireContext(),
                            "Erro ao excluir (" + resp.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(),
                        "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String t(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
