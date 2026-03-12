package com.example.acessibilit_report.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.adapter.QuestionAdapter;
import com.example.acessibilit_report.model.Answer;
import com.example.acessibilit_report.model.Forum;
import com.example.acessibilit_report.model.Person;
import com.example.acessibilit_report.model.Question;
import com.example.acessibilit_report.model.User;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityForumFragment extends Fragment {

    private EditText edtPergunta;
    private Button btnEnviarPergunta;
    private RecyclerView recyclerPerguntas;
    private QuestionAdapter adapter;
    private final List<Question> listaPerguntas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_acessibilidade, container, false);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);
        String nome = prefs.getString(LoginActivity.KEY_NOME, getString(R.string.usuario_padrao));

        edtPergunta = view.findViewById(R.id.edtPergunta);
        btnEnviarPergunta = view.findViewById(R.id.btnEnviarPergunta);
        recyclerPerguntas = view.findViewById(R.id.recyclerPerguntas);

        adapter = new QuestionAdapter(listaPerguntas);
        recyclerPerguntas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPerguntas.setAdapter(adapter);

        btnEnviarPergunta.setOnClickListener(v -> {
            String perguntaText = edtPergunta.getText().toString().trim();
            if (!perguntaText.isEmpty()) {
                Question nova = new Question();
                nova.setPergunta(perguntaText);

                Person person = new Person();
                person.setNome(nome);
                User user = new User();
                user.setPessoa(person);
                Forum forum = new Forum();
                forum.setUsuario(user);
                nova.setForum(forum);

                listaPerguntas.add(0, nova);
                adapter.notifyItemInserted(0);
                edtPergunta.setText("");
                recyclerPerguntas.scrollToPosition(0);
            }
        });

        carregarPerguntas();
        return view;
    }

    private void carregarPerguntas() {
        Person pessoa1 = new Person("Lucas de Liz", "https://example.com/foto1.jpg");
        User usuario1 = new User();
        usuario1.setPessoa(pessoa1);
        Forum forum1 = new Forum();
        forum1.setUsuario(usuario1);

        Question pergunta1 = new Question();
        pergunta1.setPergunta("Há rampas de acesso na praça principal?");
        pergunta1.setForum(forum1);
        pergunta1.adicionarResposta(new Answer("Sim, mas são muito íngremes."));
        pergunta1.adicionarResposta(new Answer("Falta corrimão nas rampas."));

        Person pessoa2 = new Person("Ana Costa", "https://example.com/foto2.jpg");
        User usuario2 = new User();
        usuario2.setPessoa(pessoa2);
        Forum forum2 = new Forum();
        forum2.setUsuario(usuario2);

        Question pergunta2 = new Question();
        pergunta2.setPergunta("Os banheiros do shopping são acessíveis?");
        pergunta2.setForum(forum2);
        pergunta2.adicionarResposta(new Answer("Alguns têm barras de apoio."));
        pergunta2.adicionarResposta(new Answer("Sim, mas o espaço interno é apertado."));

        int antigos = listaPerguntas.size();
        listaPerguntas.clear();
        if (antigos > 0) {
            adapter.notifyItemRangeRemoved(0, antigos);
        }

        listaPerguntas.add(pergunta1);
        listaPerguntas.add(pergunta2);
        adapter.notifyItemRangeInserted(0, listaPerguntas.size());
    }
}
