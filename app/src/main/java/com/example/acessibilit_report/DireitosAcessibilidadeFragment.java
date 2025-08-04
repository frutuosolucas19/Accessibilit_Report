package com.example.acessibilit_report;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acessibilit_report.adapter.DireitoAdapter;
import com.example.acessibilit_report.model.DireitoAcessibilidade;

import java.util.ArrayList;
import java.util.List;

public class DireitosAcessibilidadeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DireitoAdapter adapter;
    private List<DireitoAcessibilidade> lista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direitos_acessibilidade, container, false);

        recyclerView = view.findViewById(R.id.recyclerDireitos);
        lista = new ArrayList<>();

        // Exemplo de dados
        lista.add(new DireitoAcessibilidade("1. Acessibilidade física", "Garantia de rampas, elevadores, pisos táteis, banheiros adaptados em locais públicos e privados."));
        lista.add(new DireitoAcessibilidade("2. Transporte adaptado", "Direito ao uso de veículos adaptados, assentos reservados e gratuidade em transportes públicos."));
        lista.add(new DireitoAcessibilidade("3. Atendimento prioritário", "Atendimento preferencial em hospitais, bancos, repartições públicas e estabelecimentos comerciais."));
        lista.add(new DireitoAcessibilidade("4. Inclusão no mercado de trabalho", "Cotas obrigatórias para pessoas com deficiência em empresas e concursos públicos, com condições de trabalho adaptadas."));
        lista.add(new DireitoAcessibilidade("5. Educação inclusiva", "Acesso a escolas com estrutura adaptada e recursos pedagógicos inclusivos."));
        lista.add(new DireitoAcessibilidade("6. Isenção de impostos", "Isenção de IPI, IOF, ICMS e IPVA na compra de veículos adaptados, conforme legislação vigente."));
        lista.add(new DireitoAcessibilidade("7. Acesso à tecnologia assistiva", "Direito a cadeiras de rodas, próteses, softwares e outros equipamentos por meio do SUS ou programas governamentais."));
        lista.add(new DireitoAcessibilidade("8. Lei de proteção legal", "Amparo da Lei Brasileira de Inclusão da Pessoa com Deficiência (Lei nº 13.146/2015), que garante e fiscaliza os direitos das pessoas com deficiência."));

        adapter = new DireitoAdapter(lista);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
}
