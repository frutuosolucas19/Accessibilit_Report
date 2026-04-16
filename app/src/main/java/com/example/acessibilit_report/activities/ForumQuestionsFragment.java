package com.example.acessibilit_report.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.acessibilit_report.adapter.PerguntaResumidaAdapter;
import com.example.acessibilit_report.dto.PerguntaRequest;
import com.example.acessibilit_report.dto.PerguntaResponse;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.ForumApiService;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumQuestionsFragment extends Fragment {

    private long forumId;
    private String forumTitulo;

    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView tvEmpty;
    private PerguntaResumidaAdapter adapter;
    private Call<List<PerguntaResponse>> pendingLoad;
    private Call<PerguntaResponse> pendingCreate;
    private Call<Void> pendingDelete;

    public ForumQuestionsFragment() {}

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forum_perguntas, container, false);

        if (getArguments() != null) {
            forumId     = getArguments().getLong("forumId");
            forumTitulo = getArguments().getString("forumTitulo", "Fórum");
        }

        TextView tvTitulo = v.findViewById(R.id.tv_forum_perguntas_titulo);
        if (tvTitulo != null) tvTitulo.setText(forumTitulo);

        recycler = v.findViewById(R.id.rv_perguntas);
        progress = v.findViewById(R.id.pb_perguntas);
        tvEmpty  = v.findViewById(R.id.tv_empty_perguntas);

        adapter = new PerguntaResumidaAdapter(new ArrayList<>());
        adapter.setListener(new PerguntaResumidaAdapter.OnPerguntaActionsListener() {
            @Override
            public void onClick(PerguntaResponse p) {
                if (!isAdded()) return;
                Bundle args = new Bundle();
                args.putLong("forumId", forumId);
                args.putLong("perguntaId", p.id);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_forumPerguntas_to_perguntaDetalhe, args);
            }

            @Override
            public void onDelete(PerguntaResponse p) {
                if (!isAdded()) return;
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.dialogo_excluir_titulo)
                        .setMessage(getString(R.string.dialogo_excluir_pergunta_mensagem, p.titulo))
                        .setPositiveButton(R.string.btn_excluir, (d, w) -> deletarPergunta(p))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fab_nova_pergunta);
        if (fab != null) fab.setOnClickListener(view -> mostrarDialogNovaPergunta());

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
        if (pendingLoad != null)   { pendingLoad.cancel();   pendingLoad = null; }
        if (pendingCreate != null) { pendingCreate.cancel(); pendingCreate = null; }
        if (pendingDelete != null) { pendingDelete.cancel(); pendingDelete = null; }
    }

    private void loadData() {
        if (pendingLoad != null) pendingLoad.cancel();
        showLoading(true);
        ForumApiService api = RetrofitInitializer.getForumApiService(requireContext());
        pendingLoad = api.listarPerguntas(forumId);
        pendingLoad.enqueue(new Callback<List<PerguntaResponse>>() {
            @Override
            public void onResponse(Call<List<PerguntaResponse>> call, Response<List<PerguntaResponse>> resp) {
                if (!isAdded()) return;
                showLoading(false);
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(requireContext(),
                            "Erro ao carregar perguntas (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                    showEmpty(true);
                    return;
                }
                List<PerguntaResponse> lista = resp.body();
                adapter.submit(lista);
                showEmpty(lista.isEmpty());
            }

            @Override
            public void onFailure(Call<List<PerguntaResponse>> call, Throwable t) {
                if (!isAdded()) return;
                showLoading(false);
                showEmpty(true);
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogNovaPergunta() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nova_pergunta, null);
        TextInputEditText edtTitulo   = dialogView.findViewById(R.id.edt_pergunta_titulo);
        TextInputEditText edtConteudo = dialogView.findViewById(R.id.edt_pergunta_conteudo);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.forum_nova_pergunta)
                .setView(dialogView)
                .setPositiveButton(R.string.forum_enviar, (d, w) -> {
                    String titulo = edtTitulo.getText() != null
                            ? edtTitulo.getText().toString().trim() : "";
                    String conteudo = edtConteudo.getText() != null
                            ? edtConteudo.getText().toString().trim() : "";
                    if (titulo.isEmpty() || titulo.length() > 300) {
                        Toast.makeText(requireContext(),
                                "Título obrigatório (máx. 300 caracteres)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (conteudo.isEmpty()) {
                        Toast.makeText(requireContext(), "Conteúdo obrigatório", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    criarPergunta(titulo, conteudo);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void criarPergunta(String titulo, String conteudo) {
        if (pendingCreate != null) pendingCreate.cancel();
        ForumApiService api = RetrofitInitializer.getForumApiService(requireContext());
        pendingCreate = api.criarPergunta(forumId, new PerguntaRequest(titulo, conteudo));
        pendingCreate.enqueue(new Callback<PerguntaResponse>() {
            @Override
            public void onResponse(Call<PerguntaResponse> call, Response<PerguntaResponse> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(), "Pergunta criada!", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(requireContext(),
                            "Erro ao criar pergunta (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PerguntaResponse> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletarPergunta(PerguntaResponse p) {
        if (pendingDelete != null) pendingDelete.cancel();
        showLoading(true);
        ForumApiService api = RetrofitInitializer.getForumApiService(requireContext());
        pendingDelete = api.deletarPergunta(forumId, p.id);
        pendingDelete.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(), "Pergunta excluída.", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    showLoading(false);
                    String msg = resp.code() == 403
                            ? "Você não tem permissão para excluir esta pergunta."
                            : "Erro ao excluir (" + resp.code() + ")";
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                showLoading(false);
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        recycler.setVisibility(show ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void showEmpty(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        recycler.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
