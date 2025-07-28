package com.example.acessibilit_report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class MenuInicialFragment extends Fragment {

    public MenuInicialFragment() {
        // Construtor vazio obrigatório
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu_inicial, container, false);

        Button btnCriarDenuncia = view.findViewById(R.id.button2); // mesmo id do layout atual
        btnCriarDenuncia.setOnClickListener(v -> {
            // Navegar para o fragment CadastroDenunciaFragment
            //Navigation.findNavController(view).navigate(R.id.cadastroDenunciaFragment);
        });

        // Aqui você pode adicionar listeners pros outros botões também

        return view;
    }
}
