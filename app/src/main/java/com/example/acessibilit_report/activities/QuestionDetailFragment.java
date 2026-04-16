package com.example.acessibilit_report.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.adapter.RespostaAdapter;
import com.example.acessibilit_report.dto.PerguntaDetailResponse;
import com.example.acessibilit_report.dto.RespostaRequest;
import com.example.acessibilit_report.dto.RespostaResponse;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.ForumApiService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionDetailFragment extends Fragment {

    private long forumId;
    private long perguntaId;

    private TextView tvTitulo, tvConteudo, tvEmptyRespostas;
    private ProgressBar progress;
    private RecyclerView rvRespostas;
    private EditText edtResposta;
    private Button btnEnviar, btnExcluirPergunta;
    private RespostaAdapter adapter;

    private Call<PerguntaDetailResponse> pendingLoad;
    private Call<RespostaResponse> pendingCreateResposta;
    private Call<Void> pendingDeleteResposta;
    private Call<Void> pendingDeletePergunta;

    private PerguntaDetailResponse perguntaAtual;

    public QuestionDetailFragment() {}

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pergunta_detalhe, container, false);

        if (getArguments() != null) {
            forumId     = getArguments().getLong("forumId");
            perguntaId  = getArguments().getLong("perguntaId");
        }

        tvTitulo         = v.findViewById(R.id.tv_detalhe_titulo);
        tvConteudo       = v.findViewById(R.id.tv_detalhe_conteudo);
        tvEmptyRespostas = v.findViewById(R.id.tv_empty_respostas);
        progress         = v.findViewById(R.id.pb_detalhe);
        rvRespostas      = v.findViewById(R.id.rv_respostas);
        edtResposta      = v.findViewById(R.id.edt_resposta);
        btnEnviar        = v.findViewById(R.id.btn_enviar_resposta);
        btnExcluirPergunta = v.findViewById(R.id.btn_excluir_pergunta);

        adapter = new RespostaAdapter(new ArrayList<>());
        adapter.setOnDeleteListener(this::confirmarExcluirResposta);

        rvRespostas.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRespostas.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        rvRespostas.setAdapter(adapter);

        btnEnviar.setOnClickListener(view -> enviarResposta());
        btnExcluirPergunta.setOnClickListener(view -> confirmarExcluirPergunta());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pendingLoad != null)             { pendingLoad.cancel();             pendingLoad = null; }
        if (pendingCreateResposta != null)   { pendingCreateResposta.cancel();   pendingCreateResposta = null; }
        if (pendingDeleteResposta != null)   { pendingDeleteResposta.cancel();   pendingDeleteResposta = null; }
        if (pendingDeletePergunta != null)   { pendingDeletePergunta.cancel();   pendingDeletePergunta = null; }
    }

    private void loadData() {
        if (pendingLoad != null) pendingLoad.cancel();
        progress.setVisibility(View.VISIBLE);
        ForumApiService api = RetrofitInitializer.getForumApiService(requireContext());
        pendingLoad = api.obterPergunta(forumId, perguntaId);
        pendingLoad.enqueue(new Callback<PerguntaDetailResponse>() {
            @Override
            public void onResponse(Call<PerguntaDetailResponse> call, Response<PerguntaDetailResponse> resp) {
                if (!isAdded()) return;
                progress.setVisibility(View.GONE);
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(requireContext(),
                            "Erro ao carregar pergunta (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
                perguntaAtual = resp.body();
                tvTitulo.setText(perguntaAtual.titulo);
                tvConteudo.setText(perguntaAtual.conteudo);

                if (perguntaAtual.respostas != null && !perguntaAtual.respostas.isEmpty()) {
                    adapter.submit(perguntaAtual.respostas);
                    tvEmptyRespostas.setVisibility(View.GONE);
                    rvRespostas.setVisibility(View.VISIBLE);
                } else {
                    adapter.submit(new ArrayList<>());
                    tvEmptyRespostas.setVisibility(View.VISIBLE);
                    rvRespostas.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PerguntaDetailResponse> call, Throwable t) {
                if (!isAdded()) return;
                progress.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarResposta() {
        String conteudo = edtResposta.getText().toString().trim();
        if (conteudo.isEmpty()) {
            Toast.makeText(requireContext(), "Escreva uma resposta antes de enviar.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pendingCreateResposta != null) pendingCreateResposta.cancel();
        btnEnviar.setEnabled(false);
        ForumApiService api = RetrofitInitializer.getForumApiService(requireContext());
        pendingCreateResposta = api.criarResposta(forumId, perguntaId, new RespostaRequest(conteudo));
        pendingCreateResposta.enqueue(new Callback<RespostaResponse>() {
            @Override
            public void onResponse(Call<RespostaResponse> call, Response<RespostaResponse> resp) {
                if (!isAdded()) return;
                btnEnviar.setEnabled(true);
                if (resp.isSuccessful()) {
                    edtResposta.setText("");
                    loadData();
                } else {
                    Toast.makeText(requireContext(),
                            "Erro ao enviar resposta (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RespostaResponse> call, Throwable t) {
                if (!isAdded()) return;
                btnEnviar.setEnabled(true);
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarExcluirResposta(RespostaResponse r) {
        if (!isAdded()) return;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialogo_excluir_titulo)
                .setMessage(R.string.dialogo_excluir_resposta_mensagem)
                .setPositiveButton(R.string.btn_excluir, (d, w) -> deletarResposta(r))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void deletarResposta(RespostaResponse r) {
        if (pendingDeleteResposta != null) pendingDeleteResposta.cancel();
        ForumApiService api = RetrofitInitializer.getForumApiService(requireContext());
        pendingDeleteResposta = api.deletarResposta(forumId, perguntaId, r.id);
        pendingDeleteResposta.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(), "Resposta excluída.", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    String msg = resp.code() == 403
                            ? "Você não tem permissão para excluir esta resposta."
                            : "Erro ao excluir (" + resp.code() + ")";
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarExcluirPergunta() {
        if (!isAdded() || perguntaAtual == null) return;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialogo_excluir_titulo)
                .setMessage(getString(R.string.dialogo_excluir_pergunta_mensagem, perguntaAtual.titulo))
                .setPositiveButton(R.string.btn_excluir, (d, w) -> deletarPergunta())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void deletarPergunta() {
        if (pendingDeletePergunta != null) pendingDeletePergunta.cancel();
        ForumApiService api = RetrofitInitializer.getForumApiService(requireContext());
        pendingDeletePergunta = api.deletarPergunta(forumId, perguntaId);
        pendingDeletePergunta.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(), "Pergunta excluída.", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    String msg = resp.code() == 403
                            ? "Você não tem permissão para excluir esta pergunta."
                            : "Erro ao excluir (" + resp.code() + ")";
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
