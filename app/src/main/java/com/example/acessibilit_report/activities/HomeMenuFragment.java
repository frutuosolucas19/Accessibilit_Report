package com.example.acessibilit_report.activities;

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

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.auth.TokenStore;

public class HomeMenuFragment extends Fragment {

    public HomeMenuFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_inicial, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvNome = view.findViewById(R.id.tvNomeUsuario);

        String nome = new TokenStore(requireContext()).getNome();

        if (tvNome != null) {
            tvNome.setText(nome.isEmpty() ? "Usuário" : nome);
        }

        // Navegação dos botões
        Button btnCriarDenuncia = view.findViewById(R.id.buttonCriarDenunciaMenu);
        Button btnMinhasDenuncias = view.findViewById(R.id.buttonMinhasDenunciasMenu);
        Button btnVerLocais = view.findViewById(R.id.buttonVerLocaisMenu);

        NavController nav = Navigation.findNavController(requireActivity(), R.id.navHostFragment);

        if (btnCriarDenuncia != null)
            btnCriarDenuncia.setOnClickListener(v -> nav.navigate(R.id.action_menu_to_criarDenuncia));

        if (btnMinhasDenuncias != null)
            btnMinhasDenuncias.setOnClickListener(v -> nav.navigate(R.id.action_menu_to_minhasDenuncias));

        if (btnVerLocais != null)
            btnVerLocais.setOnClickListener(v -> nav.navigate(R.id.action_menu_to_locaisAdaptados));
    }
}

