package com.example.acessibilit_report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.acessibilit_report.R;
import com.example.acessibilit_report.model.DireitoAcessibilidade;
import java.util.List;

public class DireitoAdapter extends RecyclerView.Adapter<DireitoAdapter.ViewHolder> {

    private List<DireitoAcessibilidade> lista;

    public DireitoAdapter(List<DireitoAcessibilidade> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textDescricao;

        public ViewHolder(View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTitulo);
            textDescricao = itemView.findViewById(R.id.textDescricao);
        }
    }

    @NonNull
    @Override
    public DireitoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_direito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DireitoAdapter.ViewHolder holder, int position) {
        DireitoAcessibilidade direito = lista.get(position);
        holder.textTitulo.setText(direito.getTitulo());
        holder.textDescricao.setText(direito.getDescricao());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
