package com.example.acessibilit_report;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.acessibilit_report.dto.DenunciaRequest;
import com.example.acessibilit_report.dto.DenunciaResponse;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.DenunciaService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroDenunciaFragment extends Fragment {

    private static final int REQ_PEGA_FOTO = 1;
    private static final int MAX_IMAGENS = 4;

    // Previews (miniaturas)
    private LinearLayout containerPreviews;
    private final ArrayList<Uri> imagensSelecionadas = new ArrayList<>();

    // Campos
    private EditText txtNomeLocal;
    private EditText txtLogradouro;
    private EditText txtNumero;
    private EditText txtComplemento;
    private EditText txtCidade;
    private EditText txtUF;          // atenção ao ID no XML
    private EditText txtBairro;
    private EditText txtCEP;
    private EditText txtProblema;
    private EditText txtSugestao;    // atenção ao ID no XML

    // Botões
    private Button btnImagem;
    private Button btnCadastrar;
    private Button btnLocalizacao;

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cadastro_denuncia, container, false);

        // Bind
        txtNomeLocal   = view.findViewById(R.id.editTextNomeLocal);
        txtLogradouro  = view.findViewById(R.id.editTextLogradouro);
        txtNumero      = view.findViewById(R.id.editTextNumero);
        txtComplemento = view.findViewById(R.id.editTextComplemento);
        txtCidade      = view.findViewById(R.id.editTextCidade);
        txtUF          = view.findViewById(R.id.editTextUF);        // se seu ID for outro, ajuste aqui
        txtBairro      = view.findViewById(R.id.editTextBairro);
        txtCEP         = view.findViewById(R.id.editTextCEP);
        txtProblema    = view.findViewById(R.id.editTextProblema);
        txtSugestao    = view.findViewById(R.id.editTextSugestão);  // se for sem acento, ajuste aqui

        containerPreviews = view.findViewById(R.id.containerPreviews);

        btnImagem      = view.findViewById(R.id.btnAdicionarImagens);
        btnCadastrar   = view.findViewById(R.id.btnCriarDenuncia);
        btnLocalizacao = view.findViewById(R.id.btnMarcarLocal);

        btnImagem.setOnClickListener(v -> selecionarImagens());
        btnCadastrar.setOnClickListener(v -> enviarDenuncia());
        btnLocalizacao.setOnClickListener(v ->
                Toast.makeText(context, "Abrir mapa para marcar localização…", Toast.LENGTH_SHORT).show()
        );

        return view;
    }

    // ================== Envio ==================

    private void enviarDenuncia() {
        String nomeLocal = s(txtNomeLocal);
        String problema  = s(txtProblema);
        String cidade    = s(txtCidade);
        String uf        = s(txtUF);

        if (nomeLocal.isEmpty() || problema.isEmpty() || cidade.isEmpty() || uf.isEmpty()) {
            Toast.makeText(context, "Informe nome do local, problema, cidade e UF.", Toast.LENGTH_SHORT).show();
            return;
        }

        DenunciaRequest req = new DenunciaRequest();
        req.nomeLocal = nomeLocal;
        req.problema  = problema;
        req.sugestao  = s(txtSugestao);

        // Endereço
        DenunciaRequest.EnderecoRequest er = new DenunciaRequest.EnderecoRequest();
        er.logradouro  = s(txtLogradouro);
        er.bairro      = s(txtBairro);
        er.cidade      = cidade;
        er.uf          = uf;
        er.cep         = s(txtCEP);
        er.complemento = s(txtComplemento);
        // número (Integer)
        String numeroStr = s(txtNumero);
        if (!numeroStr.isEmpty()) {
            try { er.numero = Integer.valueOf(numeroStr); }
            catch (NumberFormatException e) {
                Toast.makeText(context, "Número inválido.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        req.endereco = er;

        // Imagens -> Base64
        if (!imagensSelecionadas.isEmpty()) {
            req.imagens = new ArrayList<>();
            int ordem = 1;
            for (Uri uri : imagensSelecionadas) {
                String base64 = uriToBase64Jpeg(uri, 80);
                if (base64 == null) {
                    Toast.makeText(context, "Falha ao processar imagem.", Toast.LENGTH_SHORT).show();
                    return;
                }
                DenunciaRequest.ImagemRequest im = new DenunciaRequest.ImagemRequest();
                im.base64 = base64;
                im.contentType = "image/jpeg";
                im.filename = getFilename(uri);
                im.ordem = ordem++;
                req.imagens.add(im);
            }
        }

        btnCadastrar.setEnabled(false);

        DenunciaService api = RetrofitInitializer.getDenunciaService(context);
        // Se seu método no service tiver outro nome (ex.: criar), ajuste aqui:
        Call<DenunciaResponse> call = api.criarDenuncia(req);

        call.enqueue(new Callback<DenunciaResponse>() {
            @Override public void onResponse(Call<DenunciaResponse> call, Response<DenunciaResponse> resp) {
                btnCadastrar.setEnabled(true);
                if (resp.isSuccessful()) {
                    Toast.makeText(context, "Denúncia enviada!", Toast.LENGTH_LONG).show();
                    if (getActivity() != null) getActivity().onBackPressed();
                } else if (resp.code() == 401) {
                    Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(context, LoginActivity.class));
                } else {
                    Toast.makeText(context, "Falha (" + resp.code() + ")", Toast.LENGTH_LONG).show();
                }
            }
            @Override public void onFailure(Call<DenunciaResponse> call, Throwable t) {
                btnCadastrar.setEnabled(true);
                Toast.makeText(context, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ================== Seleção de imagens ==================

    private void selecionarImagens() {
        Intent pick = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pick.setType("image/*");
        pick.addCategory(Intent.CATEGORY_OPENABLE);
        pick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pick.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pick.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(pick, "Selecione até " + MAX_IMAGENS + " imagens"), REQ_PEGA_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQ_PEGA_FOTO) {
            Uri single = data.getData();
            ClipData clip = data.getClipData();

            int espacoRestante = MAX_IMAGENS - imagensSelecionadas.size();
            if (espacoRestante <= 0) {
                Toast.makeText(context, "Máximo de " + MAX_IMAGENS + " imagens.", Toast.LENGTH_SHORT).show();
                return;
            }

            int adicionadas = 0;

            if (clip != null) {
                int count = Math.min(clip.getItemCount(), espacoRestante);
                for (int i = 0; i < count; i++) {
                    Uri u = clip.getItemAt(i).getUri();
                    if (u != null && addImagem(u)) adicionadas++;
                }
            } else if (single != null) {
                if (addImagem(single)) adicionadas++;
            }

            if (adicionadas > 0) {
                renderizarMiniaturas();
            } else {
                Toast.makeText(context, "Nenhuma imagem adicionada (limite atingido).", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean addImagem(Uri uri) {
        if (imagensSelecionadas.size() >= MAX_IMAGENS) return false;
        if (imagensSelecionadas.contains(uri)) return false;

        try {
            requireContext().getContentResolver()
                    .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {}

        imagensSelecionadas.add(uri);
        return true;
    }

    private void renderizarMiniaturas() {
        if (containerPreviews == null) return;

        containerPreviews.removeAllViews();

        int sizeDp = 88;
        int marginDp = 6;
        float density = getResources().getDisplayMetrics().density;
        int sizePx = (int) (sizeDp * density);
        int marginPx = (int) (marginDp * density);
        int btnSizePx = (int) (28 * density);

        for (int i = 0; i < imagensSelecionadas.size(); i++) {
            final Uri uri = imagensSelecionadas.get(i);

            android.widget.FrameLayout frame = new android.widget.FrameLayout(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sizePx, sizePx);
            lp.setMargins(marginPx, marginPx, marginPx, marginPx);
            frame.setLayoutParams(lp);

            ImageView thumb = new ImageView(context);
            thumb.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumb.setImageURI(uri);
            thumb.setContentDescription("Imagem " + (i + 1) + " de " + imagensSelecionadas.size());
            frame.addView(thumb);

            ImageButton btnX = new ImageButton(context);
            android.widget.FrameLayout.LayoutParams blp =
                    new android.widget.FrameLayout.LayoutParams(btnSizePx, btnSizePx);
            blp.gravity = android.view.Gravity.END | android.view.Gravity.TOP;
            btnX.setLayoutParams(blp);
            btnX.setBackgroundResource(android.R.color.transparent);
            btnX.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            btnX.setContentDescription("Remover imagem " + (i + 1));
            btnX.setOnClickListener(v -> {
                imagensSelecionadas.remove(uri);
                renderizarMiniaturas();
            });
            frame.addView(btnX);

            containerPreviews.addView(frame);
        }

        Toast.makeText(context,
                imagensSelecionadas.size() + " / " + MAX_IMAGENS + " imagem(ns)",
                Toast.LENGTH_SHORT).show();
    }

    // ================== Utils ==================

    private String s(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    /** Converte uma Uri de imagem em JPEG Base64 (quality 0..100). */
    private String uriToBase64Jpeg(Uri uri, int quality) {
        try (InputStream in = requireContext().getContentResolver().openInputStream(uri)) {
            Bitmap bmp = android.graphics.BitmapFactory.decodeStream(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] bytes = baos.toByteArray();
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    /** Tenta obter nome de arquivo para preencher filename. */
    private String getFilename(Uri uri) {
        String result = "foto.jpg";
        try (Cursor c = requireContext().getContentResolver()
                .query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) result = c.getString(idx);
            }
        } catch (Exception ignored) {}
        return result;
    }
}
