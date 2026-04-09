package com.example.acessibilit_report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.model.Place;
import com.example.acessibilit_report.util.AddressFormatter;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.VH> {

    private List<Place> data;

    public PlaceAdapter(List<Place> data) {
        this.data = data != null ? data : new ArrayList<>();
    }

    public void submit(List<Place> nova) {
        int oldSize = this.data != null ? this.data.size() : 0;
        this.data = nova != null ? nova : new ArrayList<>();
        if (oldSize > 0) notifyItemRangeRemoved(0, oldSize);
        if (!this.data.isEmpty()) notifyItemRangeInserted(0, this.data.size());
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Place place = data.get(position);
        holder.nome.setText(safe(place.getNome()));
        holder.endereco.setText(AddressFormatter.format(place.getEndereco()));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView nome;
        TextView endereco;

        VH(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.tv_local_nome);
            endereco = itemView.findViewById(R.id.tv_local_endereco);
        }
    }
}
