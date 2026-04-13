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
import com.example.acessibilit_report.model.Answer;
import com.example.acessibilit_report.model.Question;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private final List<Question> lista;

    public QuestionAdapter(List<Question> lista) {
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pergunta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question pergunta = lista.get(position);
        holder.textPergunta.setText(pergunta.getPergunta());

        if (pergunta.getForum() != null && pergunta.getForum().getUsuario() != null) {

            String nome = pergunta.getForum().getUsuario().getNome();
            String fotoUrl = pergunta.getForum().getUsuario().getFotoPerfil();

            holder.textNomeUsuario.setText(nome != null ? nome : holder.itemView.getContext().getString(R.string.usuario_padrao));

            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(fotoUrl)
                        .placeholder(R.drawable.baseline_person_24)
                        .into(holder.imgFotoUsuario);
            } else {
                holder.imgFotoUsuario.setImageResource(R.drawable.baseline_person_24);
            }
        }

        holder.layoutRespostas.removeAllViews();
        for (Answer resposta : pergunta.getRespostas()) {
            TextView respostaTxt = new TextView(holder.itemView.getContext());
            respostaTxt.setText(holder.itemView.getContext().getString(R.string.resposta_bullet, resposta.getResposta()));
            respostaTxt.setTextSize(14f);
            respostaTxt.setPadding(8, 4, 8, 4);
            holder.layoutRespostas.addView(respostaTxt);
        }

        holder.btnResponder.setOnClickListener(v -> {
            String texto = holder.editResposta.getText().toString().trim();
            if (!texto.isEmpty()) {
                Answer novaResposta = new Answer();
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
