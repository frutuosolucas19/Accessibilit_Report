package com.example.acessibilit_report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.ForumResponse;

import java.util.List;

public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.VH> {

    public interface OnForumClickListener {
        void onClick(ForumResponse forum);
    }

    private final List<ForumResponse> lista;
    private OnForumClickListener listener;

    public ForumAdapter(List<ForumResponse> lista) {
        this.lista = lista;
    }

    public void setOnForumClickListener(OnForumClickListener l) {
        this.listener = l;
    }

    public void submit(List<ForumResponse> novos) {
        lista.clear();
        lista.addAll(novos);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescricao, tvTotalPerguntas;

        VH(View v) {
            super(v);
            tvTitulo = v.findViewById(R.id.tv_forum_titulo);
            tvDescricao = v.findViewById(R.id.tv_forum_descricao);
            tvTotalPerguntas = v.findViewById(R.id.tv_forum_total_perguntas);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_forum, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ForumResponse f = lista.get(position);
        holder.tvTitulo.setText(f.titulo);
        if (f.descricao != null && !f.descricao.isEmpty()) {
            holder.tvDescricao.setVisibility(View.VISIBLE);
            holder.tvDescricao.setText(f.descricao);
        } else {
            holder.tvDescricao.setVisibility(View.GONE);
        }
        holder.tvTotalPerguntas.setText(
                holder.itemView.getContext().getString(R.string.forum_total_perguntas, f.totalPerguntas));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(f);
        });
    }

    @Override
    public int getItemCount() { return lista.size(); }
}
