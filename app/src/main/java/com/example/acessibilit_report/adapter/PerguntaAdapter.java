package com.example.acessibilit_report.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.acessibilit_report.R;
import com.example.acessibilit_report.model.Pergunta;
import com.example.acessibilit_report.model.Resposta;

import java.util.List;

public class PerguntaAdapter extends RecyclerView.Adapter<PerguntaAdapter.ViewHolder> {

    private List<Pergunta> lista;

    public PerguntaAdapter(List<Pergunta> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNomeUsuario;
        ImageView imgFotoUsuario;
        TextView textPergunta;
        LinearLayout layoutRespostas;
        EditText editResposta;
        Button btnResponder;

        public ViewHolder(View itemView) {
            super(itemView);
            textNomeUsuario = itemView.findViewById(R.id.textNomeUsuario);
            imgFotoUsuario = itemView.findViewById(R.id.imgFotoUsuario);
            textPergunta = itemView.findViewById(R.id.textPergunta);
            layoutRespostas = itemView.findViewById(R.id.layoutRespostas);
            editResposta = itemView.findViewById(R.id.editResposta);
            btnResponder = itemView.findViewById(R.id.btnResponder);
        }
    }

    @Override
    public PerguntaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pergunta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PerguntaAdapter.ViewHolder holder, int position) {
        Pergunta pergunta = lista.get(position);
        holder.textPergunta.setText(pergunta.getPergunta());

        // Nome e imagem do usuário
        if (pergunta.getForum() != null && pergunta.getForum().getUsuario() != null &&
                pergunta.getForum().getUsuario().getPessoa() != null) {

            String nome = pergunta.getForum().getUsuario().getPessoa().getNome();
            String fotoUrl = pergunta.getForum().getUsuario().getPessoa().getImagem();

            holder.textNomeUsuario.setText(nome != null ? nome : "Usuário");

            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(fotoUrl)
                        .placeholder(R.drawable.baseline_person_24)
                        .into(holder.imgFotoUsuario);
            } else {
                holder.imgFotoUsuario.setImageResource(R.drawable.baseline_person_24);
            }
        }

        // Respostas
        holder.layoutRespostas.removeAllViews();
        for (Resposta resposta : pergunta.getRespostas()) {
            TextView respostaTxt = new TextView(holder.itemView.getContext());
            respostaTxt.setText("• " + resposta.getResposta());
            respostaTxt.setTextSize(14f);
            respostaTxt.setPadding(8, 4, 8, 4);
            holder.layoutRespostas.addView(respostaTxt);
        }

        // Adicionar nova resposta
        holder.btnResponder.setOnClickListener(v -> {
            String texto = holder.editResposta.getText().toString().trim();
            if (!texto.isEmpty()) {
                Resposta novaResposta = new Resposta();
                novaResposta.setResposta(texto);
                pergunta.adicionarResposta(novaResposta);

                notifyItemChanged(position);
                holder.editResposta.setText("");
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
