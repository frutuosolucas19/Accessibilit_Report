package com.example.acessibilit_report.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.adapter.QuestionAdapter;
import com.example.acessibilit_report.auth.TokenStore;
import com.example.acessibilit_report.model.Forum;
import com.example.acessibilit_report.model.Question;
import com.example.acessibilit_report.model.User;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.ProtectedApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessibilityForumFragment extends Fragment {

    private EditText edtPergunta;
    private Button btnEnviarPergunta;
    private RecyclerView recyclerPerguntas;
    private ProgressBar progressBar;
    private QuestionAdapter adapter;
    private final List<Question> listaPerguntas = new ArrayList<>();

    private Call<List<Question>> pendingLoad;
    private Call<Question> pendingSend;

    private String nomeUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_acessibilidade, container, false);

        nomeUsuario = new TokenStore(requireContext()).getNome();
        if (nomeUsuario.isEmpty()) nomeUsuario = getString(R.string.usuario_padrao);

        edtPergunta       = view.findViewById(R.id.edtPergunta);
        btnEnviarPergunta = view.findViewById(R.id.btnEnviarPergunta);
        recyclerPerguntas = view.findViewById(R.id.recyclerPerguntas);
        progressBar       = view.findViewById(R.id.pb_forum);

        adapter = new QuestionAdapter(listaPerguntas);
        recyclerPerguntas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPerguntas.setAdapter(adapter);

        btnEnviarPergunta.setOnClickListener(v -> enviarPergunta());

        carregarPerguntas();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pendingLoad != null) { pendingLoad.cancel(); pendingLoad = null; }
        if (pendingSend != null) { pendingSend.cancel(); pendingSend = null; }
    }

    private void enviarPergunta() {
        String texto = edtPergunta.getText().toString().trim();
        if (texto.isEmpty()) return;

        Question nova = new Question();
        nova.setPergunta(texto);

        User user = new User();
        user.setNome(nomeUsuario);
        Forum forum = new Forum();
        forum.setUsuario(user);
        nova.setForum(forum);

        btnEnviarPergunta.setEnabled(false);
        ProtectedApiService api = RetrofitInitializer.getProtectedApiService(requireContext());
        pendingSend = api.criarPergunta(nova);
        pendingSend.enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                if (!isAdded()) return;
                btnEnviarPergunta.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    listaPerguntas.add(0, response.body());
                    adapter.notifyItemInserted(0);
                    edtPergunta.setText("");
                    recyclerPerguntas.scrollToPosition(0);
                } else {
                    // Insere localmente se a API ainda nao tiver este endpoint
                    listaPerguntas.add(0, nova);
                    adapter.notifyItemInserted(0);
                    edtPergunta.setText("");
                    recyclerPerguntas.scrollToPosition(0);
                }
            }

            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                if (!isAdded()) return;
                btnEnviarPergunta.setEnabled(true);
                Toast.makeText(requireContext(), "Erro ao enviar pergunta: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarPerguntas() {
        if (pendingLoad != null) pendingLoad.cancel();
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        ProtectedApiService api = RetrofitInitializer.getProtectedApiService(requireContext());
        pendingLoad = api.perguntas();
        pendingLoad.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(),
                            "Não foi possível carregar as perguntas (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                int antigos = listaPerguntas.size();
                listaPerguntas.clear();
                if (antigos > 0) adapter.notifyItemRangeRemoved(0, antigos);

                listaPerguntas.addAll(response.body());
                adapter.notifyItemRangeInserted(0, listaPerguntas.size());
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                if (!isAdded()) return;
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
