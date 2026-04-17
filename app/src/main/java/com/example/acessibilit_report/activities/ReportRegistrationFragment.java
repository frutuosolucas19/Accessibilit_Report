package com.example.acessibilit_report.activities;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.ReportRequest;
import com.example.acessibilit_report.dto.ReportResponse;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportRegistrationFragment extends Fragment {

    private static final int MAX_IMAGENS = 4;

    private LinearLayout containerPreviews;
    private final ArrayList<Uri> imagensSelecionadas = new ArrayList<>();

    private EditText txtNomeLocal;
    private EditText txtLogradouro;
    private EditText txtNumero;
    private EditText txtComplemento;
    private EditText txtCidade;
    private EditText txtUF;
    private EditText txtBairro;
    private EditText txtCEP;
    private EditText txtProblema;
    private EditText txtSugestao;

    private Button btnImagem;
    private Button btnCadastrar;
    private Button btnLocalizacao;

    private Context context;

    private final ActivityResultLauncher<Intent> pickImagesLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                Intent data = result.getData();
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
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cadastro_denuncia, container, false);

        txtNomeLocal   = view.findViewById(R.id.editTextNomeLocal);
        txtLogradouro  = view.findViewById(R.id.editTextLogradouro);
        txtNumero      = view.findViewById(R.id.editTextNumero);
        txtComplemento = view.findViewById(R.id.editTextComplemento);
        txtCidade      = view.findViewById(R.id.editTextCidade);
        txtUF          = view.findViewById(R.id.editTextUF);
        txtBairro      = view.findViewById(R.id.editTextBairro);
        txtCEP         = view.findViewById(R.id.editTextCEP);
        txtProblema    = view.findViewById(R.id.editTextProblema);
        txtSugestao    = view.findViewById(R.id.editTextSugestao);

        containerPreviews = view.findViewById(R.id.containerPreviews);

        btnImagem      = view.findViewById(R.id.btnAdicionarImagens);
        btnCadastrar   = view.findViewById(R.id.btnCriarDenuncia);
        btnLocalizacao = view.findViewById(R.id.btnMarcarLocal);

        btnImagem.setOnClickListener(v -> selecionarImagens());
        btnCadastrar.setOnClickListener(v -> enviarDenuncia());
        btnLocalizacao.setOnClickListener(v -> abrirMapaComEndereco());

        return view;
    }

    private void enviarDenuncia() {
        String nomeLocal = s(txtNomeLocal);
        String problema  = s(txtProblema);
        String cidade    = s(txtCidade);
        String uf        = s(txtUF);

        if (nomeLocal.isEmpty() || problema.isEmpty() || cidade.isEmpty() || uf.isEmpty()) {
            Toast.makeText(context, "Informe nome do local, problema, cidade e UF.", Toast.LENGTH_SHORT).show();
            return;
        }

        ReportRequest req = new ReportRequest();
        req.nomeLocal = nomeLocal;
        req.problema  = problema;
        req.sugestao  = s(txtSugestao);

        ReportRequest.AddressRequest er = new ReportRequest.AddressRequest();
        er.logradouro  = s(txtLogradouro);
        er.bairro      = s(txtBairro);
        er.cidade      = cidade;
        er.uf          = uf;
        er.cep         = s(txtCEP);
        er.complemento = s(txtComplemento);

        String numeroStr = s(txtNumero);
        if (!numeroStr.isEmpty()) {
            try {
                er.numero = Integer.valueOf(numeroStr);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Número inválido.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        req.endereco = er;

        if (!imagensSelecionadas.isEmpty()) {
            req.imagens = new ArrayList<>();
            int ordem = 1;
            for (Uri uri : imagensSelecionadas) {
                String base64 = uriToBase64Jpeg(uri, 80);
                if (base64 == null) {
                    Toast.makeText(context, "Falha ao processar imagem.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ReportRequest.ImageRequest im = new ReportRequest.ImageRequest();
                im.base64 = base64;
                im.contentType = "image/jpeg";
                im.filename = getFilename(uri);
                im.ordem = ordem++;
                req.imagens.add(im);
            }
        }

        btnCadastrar.setEnabled(false);

        ReportService api = RetrofitInitializer.getDenunciaService(context);
        api.criarDenuncia(req).enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> resp) {
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

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                btnCadastrar.setEnabled(true);
                Toast.makeText(context, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void selecionarImagens() {
        Intent pick = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pick.setType("image/*");
        pick.addCategory(Intent.CATEGORY_OPENABLE);
        pick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pick.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pick.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        pickImagesLauncher.launch(Intent.createChooser(pick, "Selecione até " + MAX_IMAGENS + " imagens"));
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

        float density = getResources().getDisplayMetrics().density;
        int sizePx   = (int) (88 * density);
        int marginPx = (int) (6 * density);
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

    private String s(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private String uriToBase64Jpeg(Uri uri, int quality) {
        try (InputStream in = requireContext().getContentResolver().openInputStream(uri)) {
            Bitmap bmp = android.graphics.BitmapFactory.decodeStream(in);
            if (bmp == null) return null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

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

    private void abrirMapaComEndereco() {
        StringBuilder query = new StringBuilder();
        appendPart(query, s(txtLogradouro));
        appendPart(query, s(txtNumero));
        appendPart(query, s(txtBairro));
        appendPart(query, s(txtCidade));
        appendPart(query, s(txtUF));

        if (query.length() == 0) {
            Toast.makeText(context, "Preencha endereço/cidade para abrir no mapa.", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query.toString()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(context, "Nenhum app de mapa encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void appendPart(StringBuilder sb, String value) {
        if (TextUtils.isEmpty(value)) return;
        if (sb.length() > 0) sb.append(", ");
        sb.append(value);
    }
}
