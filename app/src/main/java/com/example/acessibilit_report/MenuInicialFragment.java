package com.example.acessibilit_report;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class MenuInicialFragment extends Fragment {

    public MenuInicialFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_inicial, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onViewCreated(view, savedInstanceState);

        TextView tvNome = view.findViewById(R.id.tvNomeUsuario);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE);

        String nome = prefs.getString(LoginActivity.KEY_NOME, "Usuário");

        if (tvNome != null) {
            tvNome.setText(nome.isEmpty() ? "Usuário" : nome);
        }

        // Navegação dos botões
        Button btnCriarDenuncia = view.findViewById(R.id.buttonCriarDenunciaMenu);
        Button btnMinhasDenuncias = view.findViewById(R.id.buttonMinhasDenunciasMenu);
        Button btnVerLocais = view.findViewById(R.id.buttonVerLocaisMenu);

        if (btnCriarDenuncia != null) {
            btnCriarDenuncia.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
                navController.navigate(R.id.criarDenuncia);
            });
        }

        if (btnMinhasDenuncias != null) {
            btnMinhasDenuncias.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
                navController.navigate(R.id.minhasDenuncias);
            });
        }

        if (btnVerLocais != null) {
            btnVerLocais.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);
                navController.navigate(R.id.locaisAdaptados);
            });
        }
    }
}
