package com.example.acessibilit_report;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ForumAcessibilidadeFragment extends Fragment {

    private TextView txvCadastroPergunta;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_forum_acessibilidade, container, false);

        txvCadastroPergunta = (TextView) view.findViewById(R.id.textViewCadastroPergunta);

        txvCadastroPergunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }
}