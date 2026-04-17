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
import com.example.acessibilit_report.adapter.PlaceAdapter;
import com.example.acessibilit_report.model.Place;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.ProtectedApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessiblePlacesFragment extends Fragment {

    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView empty;
    private PlaceAdapter adapter;
    private Call<List<Place>> pendingLoad;

    public AccessiblePlacesFragment() {}

    @Override
    public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_locais_adaptados, container, false);

        recycler = v.findViewById(R.id.rv_locais);
        progress = v.findViewById(R.id.pb_loading_locais);
        empty = v.findViewById(R.id.tv_empty_locais);

        adapter = new PlaceAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fab_refresh_locais);
        if (fab != null) fab.setOnClickListener(view -> loadData());

        loadData();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pendingLoad != null) { pendingLoad.cancel(); pendingLoad = null; }
    }

    private void loadData() {
        if (pendingLoad != null) pendingLoad.cancel();
        showLoading(true);
        ProtectedApiService api = RetrofitInitializer.getProtectedApiService(requireContext());
        pendingLoad = api.locais();
        pendingLoad.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>> call, Response<List<Place>> response) {
                if (!isAdded()) return;
                showLoading(false);
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(),
                            response.code() == 401
                                    ? getString(R.string.sessao_expirada)
                                    : getString(R.string.erro_falha_codigo, response.code()),
                            Toast.LENGTH_LONG).show();
                    showEmpty(true);
                    return;
                }
                List<Place> lista = response.body();
                adapter.submit(lista);
                showEmpty(lista.isEmpty());
            }

            @Override
            public void onFailure(Call<List<Place>> call, Throwable t) {
                if (!isAdded()) return;
                showLoading(false);
                showEmpty(true);
                Toast.makeText(requireContext(), getString(R.string.erro_de_rede, t.getMessage()), Toast.LENGTH_LONG).show();
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
