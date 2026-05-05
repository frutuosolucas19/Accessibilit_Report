package com.example.acessibilit_report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.RespostaResponse;

import java.util.List;

public class RespostaAdapter extends RecyclerView.Adapter<RespostaAdapter.VH> {

    public interface OnDeleteListener {
        void onDelete(RespostaResponse r);
    }

    private final List<RespostaResponse> lista;
    private OnDeleteListener listener;

    public RespostaAdapter(List<RespostaResponse> lista) { this.lista = lista; }

    public void setOnDeleteListener(OnDeleteListener l) { this.listener = l; }

    public void submit(List<RespostaResponse> novos) {
        lista.clear();
        lista.addAll(novos);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvConteudo;
        Button btnExcluir;

        VH(View v) {
            super(v);
            tvConteudo = v.findViewById(R.id.tv_resposta_conteudo);
            btnExcluir = v.findViewById(R.id.btn_excluir_resposta);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resposta, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        RespostaResponse r = lista.get(position);
        holder.tvConteudo.setText(r.conteudo);
        holder.btnExcluir.setOnClickListener(v -> { if (listener != null) listener.onDelete(r); });
    }

    @Override
    public int getItemCount() { return lista.size(); }
}
