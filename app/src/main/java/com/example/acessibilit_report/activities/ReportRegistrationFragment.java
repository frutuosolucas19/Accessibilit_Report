package com.example.acessibilit_report.activities;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportRegistrationFragment extends Fragment {

    private static final String TAG = "ReportRegFrag";
    private static final int MAX_IMAGENS = 4;

    private LinearLayout containerPreviews;
    private final ArrayList<Uri> imagensSelecionadas = new ArrayList<>();

    private EditText txtTitulo;
    private EditText txtLogradouro;
    private EditText txtNumero;
    private EditText txtComplemento;
    private EditText txtCidade;
    private EditText txtUF;
    private EditText txtBairro;
    private EditText txtCEP;
    private EditText txtDescricao;
    private EditText txtSugestao;
    private Spinner spinnerTipo;

    private Button btnImagem;
    private Button btnCadastrar;
    private Button btnLocalizacao;

    private final ActivityResultLauncher<Intent> pickImagesLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (!isAdded()) return;
                if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                Intent data = result.getData();
                Uri single = data.getData();
                ClipData clip = data.getClipData();

                int espacoRestante = MAX_IMAGENS - imagensSelecionadas.size();
                if (espacoRestante <= 0) {
                    Toast.makeText(requireContext(), getString(R.string.imagens_maximo, MAX_IMAGENS), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(requireContext(), getString(R.string.imagens_nenhuma_adicionada), Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cadastro_denuncia, container, false);

        txtTitulo      = view.findViewById(R.id.editTextTitulo);
        txtLogradouro  = view.findViewById(R.id.editTextLogradouro);
        txtNumero      = view.findViewById(R.id.editTextNumero);
        txtComplemento = view.findViewById(R.id.editTextComplemento);
        txtCidade      = view.findViewById(R.id.editTextCidade);
        txtUF          = view.findViewById(R.id.editTextUF);
        txtBairro      = view.findViewById(R.id.editTextBairro);
        txtCEP         = view.findViewById(R.id.editTextCEP);
        txtDescricao   = view.findViewById(R.id.editTextDescricao);
        txtSugestao    = view.findViewById(R.id.editTextSugestao);
        spinnerTipo    = view.findViewById(R.id.spinnerTipo);

        ArrayAdapter<CharSequence> tipoAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.tipos_denuncia, android.R.layout.simple_spinner_item);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);

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
        String titulo    = s(txtTitulo);
        String descricao = s(txtDescricao);
        String cidade    = s(txtCidade);
        String uf        = s(txtUF);

        if (titulo.isEmpty() || descricao.isEmpty() || cidade.isEmpty() || uf.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.denuncia_campos_obrigatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        ReportRequest req = new ReportRequest();
        req.titulo    = titulo;
        req.descricao = descricao;
        req.sugestao  = s(txtSugestao);
        req.tipo      = spinnerTipo.getSelectedItem() != null
                ? spinnerTipo.getSelectedItem().toString() : "";

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
                Toast.makeText(requireContext(), getString(R.string.numero_invalido), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        req.endereco = er;

        btnCadastrar.setEnabled(false);

        ReportService api = RetrofitInitializer.getDenunciaService(requireContext());
        api.criarDenuncia(req).enqueue(new Callback<ReportResponse>() {
            @Override
            public void onResponse(Call<ReportResponse> call, Response<ReportResponse> resp) {
                if (!isAdded()) return;
                btnCadastrar.setEnabled(true);
                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(), getString(R.string.denuncia_enviada), Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                } else if (resp.code() == 401) {
                    Toast.makeText(requireContext(), getString(R.string.sessao_expirada), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                } else {
                    Toast.makeText(requireContext(), parseApiError(resp.errorBody(), resp.code()), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReportResponse> call, Throwable t) {
                if (!isAdded()) return;
                btnCadastrar.setEnabled(true);
                Toast.makeText(requireContext(), getString(R.string.erro_de_rede, t.getMessage()), Toast.LENGTH_LONG).show();
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
        pickImagesLauncher.launch(Intent.createChooser(pick, getString(R.string.imagens_selecionar, MAX_IMAGENS)));
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
        int sizePx    = (int) (88 * density);
        int marginPx  = (int) (6 * density);
        int btnSizePx = (int) (28 * density);

        for (int i = 0; i < imagensSelecionadas.size(); i++) {
            final Uri uri = imagensSelecionadas.get(i);

            android.widget.FrameLayout frame = new android.widget.FrameLayout(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sizePx, sizePx);
            lp.setMargins(marginPx, marginPx, marginPx, marginPx);
            frame.setLayoutParams(lp);

            ImageView thumb = new ImageView(requireContext());
            thumb.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumb.setImageURI(uri);
            thumb.setContentDescription(getString(R.string.imagens_contagem, i + 1, imagensSelecionadas.size()));
            frame.addView(thumb);

            ImageButton btnX = new ImageButton(requireContext());
            android.widget.FrameLayout.LayoutParams blp =
                    new android.widget.FrameLayout.LayoutParams(btnSizePx, btnSizePx);
            blp.gravity = android.view.Gravity.END | android.view.Gravity.TOP;
            btnX.setLayoutParams(blp);
            btnX.setBackgroundResource(android.R.color.transparent);
            btnX.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            btnX.setContentDescription(getString(R.string.imagens_contagem, i + 1, imagensSelecionadas.size()));
            btnX.setOnClickListener(v -> {
                imagensSelecionadas.remove(uri);
                renderizarMiniaturas();
            });
            frame.addView(btnX);

            containerPreviews.addView(frame);
        }

        Toast.makeText(requireContext(),
                getString(R.string.imagens_contagem, imagensSelecionadas.size(), MAX_IMAGENS),
                Toast.LENGTH_SHORT).show();
    }

    private String s(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void abrirMapaComEndereco() {
        StringBuilder query = new StringBuilder();
        appendPart(query, s(txtLogradouro));
        appendPart(query, s(txtNumero));
        appendPart(query, s(txtBairro));
        appendPart(query, s(txtCidade));
        appendPart(query, s(txtUF));

        if (query.length() == 0) {
            Toast.makeText(requireContext(), getString(R.string.mapa_endereco_obrigatorio), Toast.LENGTH_SHORT).show();
            return;
        }

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query.toString()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireContext(), getString(R.string.mapa_app_nao_encontrado), Toast.LENGTH_SHORT).show();
        }
    }

    private String parseApiError(ResponseBody errorBody, int code) {
        if (errorBody != null) {
            try {
                String raw = errorBody.string();
                if (!raw.isEmpty()) {
                    JSONObject json = new JSONObject(raw);
                    if (json.has("message")) return json.getString("message");
                    if (json.has("error"))   return json.getString("error");
                    return raw;
                }
            } catch (Exception e) {
                Log.w(TAG, "parseApiError: could not parse error body", e);
            }
        }
        return getString(R.string.erro_falha_codigo, code);
    }

    private void appendPart(StringBuilder sb, String value) {
        if (TextUtils.isEmpty(value)) return;
        if (sb.length() > 0) sb.append(", ");
        sb.append(value);
    }
}
