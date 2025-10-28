package com.example.acessibilit_report;

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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.adapter.DenunciaAdapter;
import com.example.acessibilit_report.dto.DenunciaResponse;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.DenunciaService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MinhasDenunciasFragment extends Fragment {

    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView empty;
    private DenunciaAdapter adapter;

    public MinhasDenunciasFragment() { }

    public static MinhasDenunciasFragment newInstance() {
        return new MinhasDenunciasFragment();
    }

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_minhas_denuncias, container, false);

        recycler = v.findViewById(R.id.rv_denuncias);
        progress = v.findViewById(R.id.pb_loading);
        empty    = v.findViewById(R.id.tv_empty);

        adapter = new DenunciaAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        // opcional: botão para recarregar
        FloatingActionButton fab = v.findViewById(R.id.fab_refresh);
        if (fab != null) fab.setOnClickListener(view -> loadData());

        loadData();
        return v;
    }

    private void loadData() {
        showLoading(true);
        DenunciaService api = RetrofitInitializer.getInstance(requireContext()).create(DenunciaService.class);
        api.minhas().enqueue(new Callback<List<DenunciaResponse>>() {
            @Override public void onResponse(Call<List<DenunciaResponse>> call, Response<List<DenunciaResponse>> resp) {
                showLoading(false);
                if (!resp.isSuccessful() || resp.body() == null) {
                    if (resp.code() == 401) {
                        Toast.makeText(requireContext(), "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), "Falha (" + resp.code() + ")", Toast.LENGTH_LONG).show();
                    }
                    showEmpty(true);
                    return;
                }
                List<DenunciaResponse> lista = resp.body();
                adapter.submit(lista);
                showEmpty(lista.isEmpty());
            }
            @Override public void onFailure(Call<List<DenunciaResponse>> call, Throwable t) {
                showLoading(false);
                showEmpty(true);
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        recycler.setVisibility(show ? View.GONE : View.VISIBLE);
        empty.setVisibility(View.GONE);
    }

    private void showEmpty(boolean show) {
        empty.setVisibility(show ? View.VISIBLE : View.GONE);
        recycler.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
