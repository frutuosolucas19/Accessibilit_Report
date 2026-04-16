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
import com.example.acessibilit_report.adapter.ForumAdapter;
import com.example.acessibilit_report.dto.ForumRequest;
import com.example.acessibilit_report.dto.ForumResponse;
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

public class AccessibilityForumFragment extends Fragment {

    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView tvEmpty;
    private ForumAdapter adapter;
    private Call<List<ForumResponse>> pendingLoad;
    private Call<ForumResponse> pendingCreate;

    public AccessibilityForumFragment() {}

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forum_acessibilidade, container, false);

        recycler = v.findViewById(R.id.rv_forums);
        progress = v.findViewById(R.id.pb_forum);
        tvEmpty  = v.findViewById(R.id.tv_empty_forum);

        adapter = new ForumAdapter(new ArrayList<>());
        adapter.setOnForumClickListener(forum -> {
            if (!isAdded()) return;
            Bundle args = new Bundle();
            args.putLong("forumId", forum.id);
            args.putString("forumTitulo", forum.titulo);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_forumAcessibilidade_to_forumPerguntas, args);
        });

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fab_novo_forum);
        if (fab != null) fab.setOnClickListener(view -> mostrarDialogNovoForum());

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
        if (pendingLoad != null) { pendingLoad.cancel(); pendingLoad = null; }
        if (pendingCreate != null) { pendingCreate.cancel(); pendingCreate = null; }
    }

    private void loadData() {
        if (pendingLoad != null) pendingLoad.cancel();
        showLoading(true);
        ForumApiService api = RetrofitInitializer.getForumApiService(requireContext());
        pendingLoad = api.listarForuns();
        pendingLoad.enqueue(new Callback<List<ForumResponse>>() {
            @Override
            public void onResponse(Call<List<ForumResponse>> call, Response<List<ForumResponse>> resp) {
                if (!isAdded()) return;
                showLoading(false);
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(requireContext(),
                            "Erro ao carregar fóruns (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                    showEmpty(true);
                    return;
                }
                List<ForumResponse> lista = resp.body();
                adapter.submit(lista);
                showEmpty(lista.isEmpty());
            }

            @Override
            public void onFailure(Call<List<ForumResponse>> call, Throwable t) {
                if (!isAdded()) return;
                showLoading(false);
                showEmpty(true);
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogNovoForum() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_novo_forum, null);
        TextInputEditText edtTitulo   = dialogView.findViewById(R.id.edt_forum_titulo);
        TextInputEditText edtDescricao = dialogView.findViewById(R.id.edt_forum_descricao);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.forum_novo_forum)
                .setView(dialogView)
                .setPositiveButton(R.string.forum_criar, (d, w) -> {
                    String titulo = edtTitulo.getText() != null
                            ? edtTitulo.getText().toString().trim() : "";
                    if (titulo.isEmpty() || titulo.length() > 200) {
                        Toast.makeText(requireContext(),
                                "Título obrigatório (máx. 200 caracteres)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String desc = edtDescricao.getText() != null
                            ? edtDescricao.getText().toString().trim() : "";
                    criarForum(titulo, desc.isEmpty() ? null : desc);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void criarForum(String titulo, String descricao) {
        if (pendingCreate != null) pendingCreate.cancel();
        ForumApiService api = RetrofitInitializer.getForumApiService(requireContext());
        pendingCreate = api.criarForum(new ForumRequest(titulo, descricao));
        pendingCreate.enqueue(new Callback<ForumResponse>() {
            @Override
            public void onResponse(Call<ForumResponse> call, Response<ForumResponse> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(), "Fórum criado!", Toast.LENGTH_SHORT).show();
                    loadData();
                } else {
                    Toast.makeText(requireContext(),
                            "Erro ao criar fórum (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForumResponse> call, Throwable t) {
                if (!isAdded()) return;
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
