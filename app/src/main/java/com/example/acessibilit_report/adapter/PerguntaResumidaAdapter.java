package com.example.acessibilit_report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.PerguntaDetailResponse;
import com.example.acessibilit_report.dto.PerguntaResponse;
import com.example.acessibilit_report.dto.RespostaRequest;
import com.example.acessibilit_report.dto.RespostaResponse;
import com.example.acessibilit_report.services.ForumApiService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerguntaResumidaAdapter extends RecyclerView.Adapter<PerguntaResumidaAdapter.VH> {

    public interface OnDeleteListener {
        void onDelete(PerguntaResponse p);
    }

    private final List<PerguntaResponse> lista;
    private final ForumApiService api;
    private final long forumId;
    private OnDeleteListener deleteListener;

    private final Set<Long> expandedIds = new HashSet<>();
    private final Map<Long, PerguntaDetailResponse> detailCache = new HashMap<>();
    private final Map<Long, Call<PerguntaDetailResponse>> pendingDetailCalls = new HashMap<>();
    private final Map<Long, Call<RespostaResponse>> pendingReplyCalls = new HashMap<>();

    public PerguntaResumidaAdapter(List<PerguntaResponse> lista, ForumApiService api, long forumId) {
        this.lista   = lista;
        this.api     = api;
        this.forumId = forumId;
    }

    public void setOnDeleteListener(OnDeleteListener l) { this.deleteListener = l; }

    public void submit(List<PerguntaResponse> novos) {
        lista.clear();
        lista.addAll(novos);
        expandedIds.clear();
        detailCache.clear();
        notifyDataSetChanged();
    }

    public void cancelAllCalls() {
        for (Call<?> c : pendingDetailCalls.values()) c.cancel();
        pendingDetailCalls.clear();
        for (Call<?> c : pendingReplyCalls.values()) c.cancel();
        pendingReplyCalls.clear();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvConteudo, tvTotalRespostas, tvExpandir, tvSemRespostas;
        Button btnExcluir, btnEnviarResposta;
        LinearLayout llRespostas, llListaRespostas;
        ProgressBar pbRespostas;
        EditText edtNovaResposta;

        VH(View v) {
            super(v);
            tvTitulo          = v.findViewById(R.id.tv_pergunta_titulo);
            tvConteudo        = v.findViewById(R.id.tv_pergunta_conteudo);
            tvTotalRespostas  = v.findViewById(R.id.tv_pergunta_total_respostas);
            tvExpandir        = v.findViewById(R.id.tv_expandir);
            btnExcluir        = v.findViewById(R.id.btn_excluir_pergunta);
            llRespostas       = v.findViewById(R.id.ll_respostas);
            pbRespostas       = v.findViewById(R.id.pb_respostas);
            tvSemRespostas    = v.findViewById(R.id.tv_sem_respostas);
            llListaRespostas  = v.findViewById(R.id.ll_lista_respostas);
            edtNovaResposta   = v.findViewById(R.id.edt_nova_resposta);
            btnEnviarResposta = v.findViewById(R.id.btn_enviar_resposta);
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

        boolean expanded = expandedIds.contains(p.id);
        holder.llRespostas.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.tvExpandir.setText(expanded
                ? holder.itemView.getContext().getString(R.string.forum_ocultar_respostas)
                : holder.itemView.getContext().getString(R.string.forum_ver_respostas));

        if (expanded) renderRespostas(holder, p.id);

        holder.edtNovaResposta.setText("");

        View.OnClickListener toggleClick = v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;
            if (expandedIds.contains(p.id)) {
                expandedIds.remove(p.id);
            } else {
                expandedIds.add(p.id);
                if (!detailCache.containsKey(p.id)) carregarDetalhe(p.id);
            }
            notifyItemChanged(pos);
        };
        holder.itemView.setOnClickListener(toggleClick);
        holder.tvExpandir.setOnClickListener(toggleClick);

        holder.btnExcluir.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(p);
        });

        holder.btnEnviarResposta.setOnClickListener(v -> {
            String text = holder.edtNovaResposta.getText().toString().trim();
            if (!text.isEmpty()) enviarResposta(p.id, text, holder);
        });
    }

    private void renderRespostas(VH holder, long perguntaId) {
        PerguntaDetailResponse detail = detailCache.get(perguntaId);
        if (detail == null) {
            holder.pbRespostas.setVisibility(View.VISIBLE);
            holder.llListaRespostas.setVisibility(View.GONE);
            holder.tvSemRespostas.setVisibility(View.GONE);
            return;
        }
        holder.pbRespostas.setVisibility(View.GONE);
        holder.llListaRespostas.removeAllViews();
        if (detail.respostas == null || detail.respostas.isEmpty()) {
            holder.tvSemRespostas.setVisibility(View.VISIBLE);
            holder.llListaRespostas.setVisibility(View.GONE);
        } else {
            holder.tvSemRespostas.setVisibility(View.GONE);
            holder.llListaRespostas.setVisibility(View.VISIBLE);
            LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
            for (RespostaResponse r : detail.respostas) {
                View row = inflater.inflate(R.layout.item_resposta_inline, holder.llListaRespostas, false);
                ((TextView) row.findViewById(R.id.tv_resposta_inline_conteudo)).setText(r.conteudo);
                holder.llListaRespostas.addView(row);
            }
        }
    }

    private void carregarDetalhe(long perguntaId) {
        if (pendingDetailCalls.containsKey(perguntaId)) return;
        Call<PerguntaDetailResponse> call = api.obterPergunta(forumId, perguntaId);
        pendingDetailCalls.put(perguntaId, call);
        call.enqueue(new Callback<PerguntaDetailResponse>() {
            @Override
            public void onResponse(Call<PerguntaDetailResponse> c, Response<PerguntaDetailResponse> resp) {
                pendingDetailCalls.remove(perguntaId);
                if (resp.isSuccessful() && resp.body() != null) {
                    detailCache.put(perguntaId, resp.body());
                    int pos = findPosition(perguntaId);
                    if (pos >= 0) notifyItemChanged(pos);
                }
            }
            @Override
            public void onFailure(Call<PerguntaDetailResponse> c, Throwable t) {
                pendingDetailCalls.remove(perguntaId);
            }
        });
    }

    private void enviarResposta(long perguntaId, String conteudo, VH holder) {
        holder.btnEnviarResposta.setEnabled(false);
        Call<RespostaResponse> existing = pendingReplyCalls.get(perguntaId);
        if (existing != null) existing.cancel();
        Call<RespostaResponse> call = api.criarResposta(forumId, perguntaId, new RespostaRequest(conteudo));
        pendingReplyCalls.put(perguntaId, call);
        call.enqueue(new Callback<RespostaResponse>() {
            @Override
            public void onResponse(Call<RespostaResponse> c, Response<RespostaResponse> resp) {
                pendingReplyCalls.remove(perguntaId);
                holder.btnEnviarResposta.setEnabled(true);
                if (resp.isSuccessful()) {
                    holder.edtNovaResposta.setText("");
                    detailCache.remove(perguntaId);
                    carregarDetalhe(perguntaId);
                } else {
                    Toast.makeText(holder.itemView.getContext(),
                            "Erro ao enviar resposta (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RespostaResponse> c, Throwable t) {
                pendingReplyCalls.remove(perguntaId);
                holder.btnEnviarResposta.setEnabled(true);
                Toast.makeText(holder.itemView.getContext(),
                        "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int findPosition(long perguntaId) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).id != null && lista.get(i).id == perguntaId) return i;
        }
        return -1;
    }

    @Override
    public int getItemCount() { return lista.size(); }
}
