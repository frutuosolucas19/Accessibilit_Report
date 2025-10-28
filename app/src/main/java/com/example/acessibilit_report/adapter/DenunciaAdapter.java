package com.example.acessibilit_report.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.DenunciaResponse;
import com.example.acessibilit_report.model.Endereco;
import com.example.acessibilit_report.model.Imagem;

import java.util.ArrayList;
import java.util.List;

public class DenunciaAdapter extends RecyclerView.Adapter<DenunciaAdapter.VH> {

    private List<DenunciaResponse> data;

    public DenunciaAdapter(List<DenunciaResponse> data) {
        this.data = data != null ? data : new ArrayList<>();
    }

    public void submit(List<DenunciaResponse> nova) {
        this.data = nova != null ? nova : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_denuncia, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        DenunciaResponse d = data.get(pos);

        h.titulo.setText(safe(d.nomeLocal));
        h.problema.setText(safe(d.problema));
        h.data.setText(safe(d.criadoEm));

        // endereço formatado
        h.endereco.setText(formatEndereco(d.endereco));

        // imagem (pega a primeira da lista)
        String thumbUrl = firstImageUrl(d.imagens);
        if (!TextUtils.isEmpty(thumbUrl)) {
            h.img.setVisibility(View.VISIBLE);
            Glide.with(h.img.getContext())
                    .load(thumbUrl)
                    .centerCrop()
                    .into(h.img);
        } else {
            h.img.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return data != null ? data.size() : 0; }

    // ===== Helpers =====

    private static String safe(String s) { return s == null ? "" : s; }

    private static String formatEndereco(Endereco e) {
        if (e == null) return "";
        StringBuilder sb = new StringBuilder();
        if (!isBlank(e.getLogradouro())) sb.append(e.getLogradouro());
        if (e.getNumero() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.getNumero());
        }
        if (!isBlank(e.getBairro())) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(e.getBairro());
        }
        if (!isBlank(e.getCidade())) {
            if (sb.length() > 0) sb.append(" • ");
            sb.append(e.getCidade());
        }
        if (!isBlank(e.getUf())) {
            if (!isBlank(e.getCidade())) sb.append("/");
            sb.append(e.getUf());
        }
        return sb.toString();
    }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static String firstImageUrl(List<Imagem> imagens) {
        if (imagens == null || imagens.isEmpty()) return null;
        // já vem ordenado por backend, mas garantimos pegar a primeira com url
        for (Imagem m : imagens) {
            if (m != null && !TextUtils.isEmpty(m.getUrl())) return m.getUrl();
        }
        return null;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView titulo, endereco, problema, data;
        ImageView img;
        VH(@NonNull View v) {
            super(v);
            titulo   = v.findViewById(R.id.tv_titulo);
            endereco = v.findViewById(R.id.tv_endereco);
            problema = v.findViewById(R.id.tv_problema);
            data     = v.findViewById(R.id.tv_data);
            img      = v.findViewById(R.id.iv_imagem);
        }
    }
}
