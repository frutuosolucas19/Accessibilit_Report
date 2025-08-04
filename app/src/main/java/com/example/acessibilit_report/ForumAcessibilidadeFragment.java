package com.example.acessibilit_report;


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
import com.example.acessibilit_report.adapter.PerguntaAdapter;
import com.example.acessibilit_report.model.Forum;
import com.example.acessibilit_report.model.Pergunta;
import com.example.acessibilit_report.model.Pessoa;
import com.example.acessibilit_report.model.Resposta;
import com.example.acessibilit_report.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class ForumAcessibilidadeFragment extends Fragment {

    private EditText edtPergunta;
    private Button btnEnviarPergunta;
    private RecyclerView recyclerPerguntas;
    private PerguntaAdapter adapter;
    private List<Pergunta> listaPerguntas = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_acessibilidade, container, false);

        edtPergunta = view.findViewById(R.id.edtPergunta);
        btnEnviarPergunta = view.findViewById(R.id.btnEnviarPergunta);
        recyclerPerguntas = view.findViewById(R.id.recyclerPerguntas);

        adapter = new PerguntaAdapter(listaPerguntas);
        recyclerPerguntas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPerguntas.setAdapter(adapter);

        btnEnviarPergunta.setOnClickListener(v -> {
            String perguntaText = edtPergunta.getText().toString().trim();
            if (!perguntaText.isEmpty()) {
                Pergunta nova = new Pergunta();
                nova.setPergunta(perguntaText);
                listaPerguntas.add(0, nova); // adicionar no topo
                adapter.notifyItemInserted(0);
                edtPergunta.setText("");
                recyclerPerguntas.scrollToPosition(0);
            }
        });

        carregarPerguntas();

        return view;
    }

    private void carregarPerguntas() {
        // Pergunta 1
        Pessoa pessoa1 = new Pessoa("Lucas de Liz", "https://example.com/foto1.jpg");
        Usuario usuario1 = new Usuario();
        usuario1.setPessoa(pessoa1);
        Forum forum1 = new Forum();
        forum1.setUsuario(usuario1);

        Pergunta pergunta1 = new Pergunta();
        pergunta1.setPergunta("Há rampas de acesso na praça principal?");
        pergunta1.setForum(forum1);

        pergunta1.adicionarResposta(new Resposta("Sim, mas são muito íngremes."));
        pergunta1.adicionarResposta(new Resposta("Falta corrimão nas rampas."));

        // Pergunta 2
        Pessoa pessoa2 = new Pessoa("Ana Costa", "https://example.com/foto2.jpg");
        Usuario usuario2 = new Usuario();
        usuario2.setPessoa(pessoa2);
        Forum forum2 = new Forum();
        forum2.setUsuario(usuario2);

        Pergunta pergunta2 = new Pergunta();
        pergunta2.setPergunta("Os banheiros do shopping são acessíveis?");
        pergunta2.setForum(forum2);

        pergunta2.adicionarResposta(new Resposta("Alguns têm barras de apoio."));
        pergunta2.adicionarResposta(new Resposta("Sim, mas o espaço interno é apertado."));

        // Adiciona à lista
        listaPerguntas.clear();
        listaPerguntas.add(pergunta1);
        listaPerguntas.add(pergunta2);

        adapter.notifyDataSetChanged();
    }

}
