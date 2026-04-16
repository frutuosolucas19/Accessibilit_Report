package com.example.acessibilit_report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.PerguntaResponse;

import java.util.List;

public class PerguntaResumidaAdapter extends RecyclerView.Adapter<PerguntaResumidaAdapter.VH> {

    public interface OnPerguntaActionsListener {
        void onClick(PerguntaResponse p);
        void onDelete(PerguntaResponse p);
    }

    private final List<PerguntaResponse> lista;
    private OnPerguntaActionsListener listener;

    public PerguntaResumidaAdapter(List<PerguntaResponse> lista) {
        this.lista = lista;
    }

    public void setListener(OnPerguntaActionsListener l) { this.listener = l; }

    public void submit(List<PerguntaResponse> novos) {
        lista.clear();
        lista.addAll(novos);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvConteudo, tvTotalRespostas;
        Button btnExcluir;

        VH(View v) {
            super(v);
            tvTitulo = v.findViewById(R.id.tv_pergunta_titulo);
            tvConteudo = v.findViewById(R.id.tv_pergunta_conteudo);
            tvTotalRespostas = v.findViewById(R.id.tv_pergunta_total_respostas);
            btnExcluir = v.findViewById(R.id.btn_excluir_pergunta);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pergunta_resumo, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PerguntaResponse p = lista.get(position);
        holder.tvTitulo.setText(p.titulo);
        holder.tvConteudo.setText(p.conteudo);
        holder.tvTotalRespostas.setText(
                holder.itemView.getContext().getString(R.string.forum_total_respostas, p.totalRespostas));
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(p); });
        holder.btnExcluir.setOnClickListener(v -> { if (listener != null) listener.onDelete(p); });
    }

    @Override
    public int getItemCount() { return lista.size(); }
}
