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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.adapter.ReportAdapter;
import com.example.acessibilit_report.dto.ReportResponse;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.ProtectedApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportedPlacesFragment extends Fragment {

    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView empty;
    private ReportAdapter adapter;

    public ReportedPlacesFragment() {}

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_locais_denunciados, container, false);

        recycler = v.findViewById(R.id.rv_denuncias_todas);
        progress = v.findViewById(R.id.pb_loading_denunciados);
        empty = v.findViewById(R.id.tv_empty_denunciados);

        adapter = new ReportAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fab_refresh_denunciados);
        if (fab != null) fab.setOnClickListener(view -> loadData());

        loadData();
        return v;
    }

    private void loadData() {
        showLoading(true);
        ProtectedApiService api = RetrofitInitializer.getProtectedApiService(requireContext());
        api.denuncias().enqueue(new Callback<List<ReportResponse>>() {
            @Override
            public void onResponse(Call<List<ReportResponse>> call, Response<List<ReportResponse>> response) {
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    if (response.code() == 401) {
                        Toast.makeText(requireContext(), "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), "Falha (" + response.code() + ")", Toast.LENGTH_LONG).show();
                    }
                    showEmpty(true);
                    return;
                }
                List<ReportResponse> lista = response.body();
                adapter.submit(lista);
                showEmpty(lista.isEmpty());
            }

            @Override
            public void onFailure(Call<List<ReportResponse>> call, Throwable t) {
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

