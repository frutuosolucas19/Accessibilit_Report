package com.example.acessibilit_report.activities;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.acessibilit_report.BuildConfig;
import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.ReportResponse;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportRegistrationFragment extends Fragment {

    private static final String TAG = "ReportRegFrag";
    private static final int MAX_IMAGENS = 5;
    private static final long MAX_IMAGE_BYTES = 10L * 1024 * 1024;

    // ── Views ────────────────────────────────────────────────────────────────
    private EditText txtTitulo;
    private Spinner  spinnerTipo;
    private EditText txtLogradouro;
    private EditText txtNumero;
    private EditText txtComplemento;
    private EditText txtCidade;
    private EditText txtEstado;
    private EditText txtBairro;
    private EditText txtCEP;
    private EditText txtDescricao;
    private EditText txtSugestao;
    private LinearLayout containerPreviews;
    private TextView tvCoordenadas;
    private Button   btnCadastrar;

    // ── State ────────────────────────────────────────────────────────────────
    private final ArrayList<Uri> imagensSelecionadas = new ArrayList<>();
    private Double latitude  = null;
    private Double longitude = null;
    private Uri    photoUri;
    private okhttp3.Call pendingCall;
    private String[] tiposValores;

    // ── Camera ───────────────────────────────────────────────────────────────
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoUri != null && isAdded()) {
                    if (addImagem(photoUri)) renderizarMiniaturas();
                }
            });

    private final ActivityResultLauncher<String> cameraPermLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) launchCamera();
                else if (isAdded())
                    Toast.makeText(requireContext(),
                            getString(R.string.camera_permissao_negada), Toast.LENGTH_SHORT).show();
            });

    // ── Gallery ──────────────────────────────────────────────────────────────
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null || !isAdded()) return;
                Intent data = result.getData();
                Uri single = data.getData();
                ClipData clip = data.getClipData();

                int espacoRestante = MAX_IMAGENS - imagensSelecionadas.size();
                if (espacoRestante <= 0) {
                    Toast.makeText(requireContext(),
                            getString(R.string.imagens_limite_atingido, MAX_IMAGENS), Toast.LENGTH_SHORT).show();
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

                if (adicionadas > 0) renderizarMiniaturas();
                else Toast.makeText(requireContext(),
                        getString(R.string.imagens_nenhuma_adicionada), Toast.LENGTH_SHORT).show();
            });

    // ── Map picker ───────────────────────────────────────────────────────────
    private final ActivityResultLauncher<Intent> mapPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (!isAdded() || result.getResultCode() != RESULT_OK || result.getData() == null) return;
                double lat = result.getData().getDoubleExtra("latitude", Double.NaN);
                double lng = result.getData().getDoubleExtra("longitude", Double.NaN);
                if (!Double.isNaN(lat) && !Double.isNaN(lng)) {
                    latitude  = lat;
                    longitude = lng;
                    tvCoordenadas.setVisibility(View.VISIBLE);
                    tvCoordenadas.setText(getString(R.string.coordenadas_formato, lat, lng));
                }
            });

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cadastro_denuncia, container, false);

        txtTitulo      = view.findViewById(R.id.editTextTitulo);
        spinnerTipo    = view.findViewById(R.id.spinnerTipo);
        txtLogradouro  = view.findViewById(R.id.editTextLogradouro);
        txtNumero      = view.findViewById(R.id.editTextNumero);
        txtComplemento = view.findViewById(R.id.editTextComplemento);
        txtCidade      = view.findViewById(R.id.editTextCidade);
        txtEstado      = view.findViewById(R.id.editTextUF);
        txtBairro      = view.findViewById(R.id.editTextBairro);
        txtCEP         = view.findViewById(R.id.editTextCEP);
        txtDescricao   = view.findViewById(R.id.editTextDescricao);
        txtSugestao    = view.findViewById(R.id.editTextSugestao);
        containerPreviews = view.findViewById(R.id.containerPreviews);
        tvCoordenadas  = view.findViewById(R.id.tvCoordenadas);
        btnCadastrar   = view.findViewById(R.id.btnCriarDenuncia);

        tiposValores = getResources().getStringArray(R.array.tipos_denuncia_valores);
        ArrayAdapter<CharSequence> tipoAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.tipos_denuncia, android.R.layout.simple_spinner_item);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);

        view.findViewById(R.id.btnAdicionarImagens).setOnClickListener(v -> mostrarDialogImagem());
        view.findViewById(R.id.btnMarcarLocal).setOnClickListener(v ->
                mapPickerLauncher.launch(new Intent(requireContext(), MapPickerActivity.class)));
        btnCadastrar.setOnClickListener(v -> enviarDenuncia());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pendingCall != null) { pendingCall.cancel(); pendingCall = null; }
    }

    // ── Image management ─────────────────────────────────────────────────────
    private void mostrarDialogImagem() {
        if (imagensSelecionadas.size() >= MAX_IMAGENS) {
            Toast.makeText(requireContext(),
                    getString(R.string.imagens_limite_atingido, MAX_IMAGENS), Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_adicionar_imagem)
                .setItems(new String[]{
                        getString(R.string.camera_tirar_foto),
                        getString(R.string.camera_galeria)
                }, (dialog, which) -> {
                    if (which == 0) cameraPermLauncher.launch(Manifest.permission.CAMERA);
                    else {
                        Intent pick = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        pick.setType("image/*");
                        pick.addCategory(Intent.CATEGORY_OPENABLE);
                        pick.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        galleryLauncher.launch(pick);
                    }
                })
                .show();
    }

    private void launchCamera() {
        try {
            File dir = new File(requireContext().getCacheDir(), "images");
            if (!dir.exists()) //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            File photo = File.createTempFile("foto_", ".jpg", dir);
            photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photo);
            cameraLauncher.launch(photoUri);
        } catch (IOException e) {
            Toast.makeText(requireContext(),
                    getString(R.string.falha_processar_imagem), Toast.LENGTH_SHORT).show();
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
        if (containerPreviews == null || !isAdded()) return;
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
            btnX.setContentDescription("Remover imagem " + (i + 1));
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

    // ── Submit ────────────────────────────────────────────────────────────────
    private void enviarDenuncia() {
        String titulo     = s(txtTitulo);
        String descricao  = s(txtDescricao);
        String logradouro = s(txtLogradouro);
        String bairro     = s(txtBairro);
        String cidade     = s(txtCidade);
        String estado     = s(txtEstado);

        if (titulo.isEmpty() || descricao.isEmpty() || logradouro.isEmpty()
                || bairro.isEmpty() || cidade.isEmpty() || estado.isEmpty()) {
            Toast.makeText(requireContext(),
                    getString(R.string.denuncia_campos_obrigatorios), Toast.LENGTH_SHORT).show();
            return;
        }

        int pos = spinnerTipo.getSelectedItemPosition();
        String tipo = (tiposValores != null && pos >= 0 && pos < tiposValores.length)
                ? tiposValores[pos] : "OUTROS";

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("titulo",     titulo)
                .addFormDataPart("descricao",  descricao)
                .addFormDataPart("tipo",       tipo)
                .addFormDataPart("logradouro", logradouro)
                .addFormDataPart("bairro",     bairro)
                .addFormDataPart("cidade",     cidade)
                .addFormDataPart("estado",     estado);

        String numero      = s(txtNumero);
        String complemento = s(txtComplemento);
        String cep         = s(txtCEP);
        String sugestao    = s(txtSugestao);

        if (!numero.isEmpty())      builder.addFormDataPart("numero",      numero);
        if (!complemento.isEmpty()) builder.addFormDataPart("complemento", complemento);
        if (!cep.isEmpty())         builder.addFormDataPart("cep",         cep);
        if (!sugestao.isEmpty())    builder.addFormDataPart("sugestao",    sugestao);
        if (latitude  != null)      builder.addFormDataPart("latitude",    String.valueOf(latitude));
        if (longitude != null)      builder.addFormDataPart("longitude",   String.valueOf(longitude));

        for (Uri uri : new ArrayList<>(imagensSelecionadas)) {
            byte[] bytes = uriToBytes(uri);
            if (bytes == null) {
                Toast.makeText(requireContext(),
                        getString(R.string.falha_processar_imagem), Toast.LENGTH_SHORT).show();
                return;
            }
            builder.addFormDataPart("imagens", getFilename(uri),
                    RequestBody.create(bytes, MediaType.parse("image/jpeg")));
        }

        btnCadastrar.setEnabled(false);

        if (pendingCall != null) pendingCall.cancel();
        OkHttpClient client = RetrofitInitializer.getOkHttpClient(requireContext());
        Request request = new Request.Builder()
                .url(BuildConfig.BASE_URL + "denuncias")
                .post(builder.build())
                .build();

        pendingCall = client.newCall(request);
        pendingCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response resp) throws IOException {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    btnCadastrar.setEnabled(true);
                    if (resp.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                getString(R.string.denuncia_enviada), Toast.LENGTH_LONG).show();
                        requireActivity().onBackPressed();
                    } else if (resp.code() == 401) {
                        Toast.makeText(requireContext(),
                                getString(R.string.sessao_expirada), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                    } else {
                        String msg = parseApiError(resp);
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull java.io.IOException e) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    btnCadastrar.setEnabled(true);
                    Toast.makeText(requireContext(),
                            getString(R.string.erro_de_rede, e.getMessage()), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String s(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private byte[] uriToBytes(Uri uri) {
        try (InputStream in = requireContext().getContentResolver().openInputStream(uri)) {
            if (in == null) return null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            long total = 0;
            while ((n = in.read(buf)) != -1) {
                total += n;
                if (total > MAX_IMAGE_BYTES) {
                    if (isAdded())
                        Toast.makeText(requireContext(),
                                getString(R.string.imagem_muito_grande), Toast.LENGTH_SHORT).show();
                    return null;
                }
                baos.write(buf, 0, n);
            }
            return baos.toByteArray();
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
        } catch (Exception e) {
            Log.w(TAG, "getFilename error", e);
        }
        return result;
    }

    private String parseApiError(Response resp) {
        try {
            if (resp.body() != null) {
                String raw = resp.body().string();
                if (!raw.isEmpty()) {
                    JSONObject json = new JSONObject(raw);
                    if (json.has("message")) return json.getString("message");
                    if (json.has("error"))   return json.getString("error");
                    return raw;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "parseApiError error", e);
        }
        return getString(R.string.erro_falha_codigo, resp.code());
    }
}
