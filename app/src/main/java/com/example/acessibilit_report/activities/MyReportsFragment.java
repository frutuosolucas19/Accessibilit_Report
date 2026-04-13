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
import com.example.acessibilit_report.services.ReportService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReportsFragment extends Fragment {

    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView empty;
    private ReportAdapter adapter;
    private Call<List<ReportResponse>> pendingCall;

    public MyReportsFragment() { }

    public static MyReportsFragment newInstance() {
        return new MyReportsFragment();
    }

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_minhas_denuncias, container, false);

        recycler = v.findViewById(R.id.rv_denuncias);
        progress = v.findViewById(R.id.pb_loading);
        empty    = v.findViewById(R.id.tv_empty);

        adapter = new ReportAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fab_refresh);
        if (fab != null) fab.setOnClickListener(view -> loadData());

        loadData();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pendingCall != null) {
            pendingCall.cancel();
            pendingCall = null;
        }
    }

    private void loadData() {
        if (pendingCall != null) pendingCall.cancel();
        showLoading(true);
        ReportService api = RetrofitInitializer.getInstance(requireContext()).create(ReportService.class);
        pendingCall = api.minhas();
        pendingCall.enqueue(new Callback<List<ReportResponse>>() {
            @Override public void onResponse(Call<List<ReportResponse>> call, Response<List<ReportResponse>> resp) {
                if (!isAdded()) return;
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
                List<ReportResponse> lista = resp.body();
                adapter.submit(lista);
                showEmpty(lista.isEmpty());
            }
            @Override public void onFailure(Call<List<ReportResponse>> call, Throwable t) {
                if (!isAdded()) return;
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
