package com.example.acessibilit_report.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.ReportResponse;
import com.example.acessibilit_report.model.Image;
import com.example.acessibilit_report.util.AddressFormatter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.VH> {

    public interface OnItemActionsListener {
        void onEdit(ReportResponse report);
        void onDelete(ReportResponse report);
    }

    private List<ReportResponse> data;
    private OnItemActionsListener actionsListener;

    public ReportAdapter(List<ReportResponse> data) {
        this.data = data != null ? data : new ArrayList<>();
    }

    public void setOnItemActionsListener(OnItemActionsListener listener) {
        this.actionsListener = listener;
        notifyDataSetChanged();
    }

    public void submit(List<ReportResponse> nova) {
        int oldSize = this.data != null ? this.data.size() : 0;
        this.data = nova != null ? nova : new ArrayList<>();

        if (oldSize > 0) notifyItemRangeRemoved(0, oldSize);
        if (!this.data.isEmpty()) notifyItemRangeInserted(0, this.data.size());
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_denuncia, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ReportResponse d = data.get(pos);

        h.titulo.setText(safe(d.titulo));
        h.problema.setText(safe(d.descricao));
        h.data.setText(safe(d.criadoEm));
        h.endereco.setText(AddressFormatter.format(d.endereco));

        String thumbUrl = firstImageUrl(d.imagens);
        if (!TextUtils.isEmpty(thumbUrl)) {
            h.img.setVisibility(View.VISIBLE);
            Glide.with(h.img.getContext()).load(thumbUrl).centerCrop().into(h.img);
        } else {
            h.img.setVisibility(View.GONE);
        }

        if (actionsListener != null) {
            h.llActions.setVisibility(View.VISIBLE);
            h.btnEditar.setOnClickListener(v -> actionsListener.onEdit(d));
            h.btnDeletar.setOnClickListener(v -> actionsListener.onDelete(d));
        } else {
            h.llActions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String firstImageUrl(List<Image> imagens) {
        if (imagens == null || imagens.isEmpty()) return null;
        for (Image m : imagens) {
            if (m != null && !TextUtils.isEmpty(m.getUrl())) return m.getUrl();
        }
        return null;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView titulo, endereco, problema, data;
        ImageView img;
        LinearLayout llActions;
        MaterialButton btnEditar, btnDeletar;

        VH(@NonNull View v) {
            super(v);
            titulo     = v.findViewById(R.id.tv_titulo);
            endereco   = v.findViewById(R.id.tv_endereco);
            problema   = v.findViewById(R.id.tv_problema);
            data       = v.findViewById(R.id.tv_data);
            img        = v.findViewById(R.id.iv_imagem);
            llActions  = v.findViewById(R.id.ll_actions);
            btnEditar  = v.findViewById(R.id.btn_editar);
            btnDeletar = v.findViewById(R.id.btn_deletar);
        }
    }
}
