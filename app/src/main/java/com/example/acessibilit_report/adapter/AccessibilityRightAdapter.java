package com.example.acessibilit_report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.acessibilit_report.R;
import com.example.acessibilit_report.model.AccessibilityRight;
import java.util.List;

public class AccessibilityRightAdapter extends RecyclerView.Adapter<AccessibilityRightAdapter.ViewHolder> {

    private List<AccessibilityRight> lista;

    public AccessibilityRightAdapter(List<AccessibilityRight> lista) {
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
    public AccessibilityRightAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_direito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccessibilityRightAdapter.ViewHolder holder, int position) {
        AccessibilityRight direito = lista.get(position);
        holder.textTitulo.setText(direito.getTitulo());
        holder.textDescricao.setText(direito.getDescricao());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}

