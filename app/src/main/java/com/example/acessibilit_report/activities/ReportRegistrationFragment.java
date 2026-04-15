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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
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
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportRegistrationFragment extends Fragment {

    private static final int MAX_IMAGENS = 5;
    private static final long MAX_IMAGE_BYTES = 10L * 1024 * 1024; // 10 MB

    private LinearLayout containerPreviews;
    private final ArrayList<Uri> imagensSelecionadas = new ArrayList<>();

    private EditText editTextTitulo;
    private Spinner spinnerTipo;
    private EditText editTextLogradouro;
    private EditText editTextNumero;
    private EditText editTextComplemento;
    private EditText editTextCidade;
    private EditText editTextUF;
    private EditText editTextBairro;
    private EditText editTextCEP;
    private EditText editTextDescricao;
    private EditText editTextSugestao;
    private TextView tvCoordenadas;
    private MaterialButton btnCriarDenuncia;

    private Double latitude;
    private Double longitude;
    private Uri photoUri;
    private Call pendingCall;

    // ── Camera ──────────────────────────────────────────────────────────────
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoUri != null && isAdded()) {
                    if (addImagem(photoUri)) renderizarMiniaturas();
                }
            });

    // ── Camera permission ───────────────────────────────────────────────────
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    launchCamera();
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Permissão de câmera negada.", Toast.LENGTH_SHORT).show();
                }
            });

    // ── Gallery ─────────────────────────────────────────────────────────────
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null || !isAdded()) return;
                Intent data = result.getData();
                Uri single = data.getData();
                ClipData clip = data.getClipData();

                int espacoRestante = MAX_IMAGENS - imagensSelecionadas.size();
                if (espacoRestante <= 0) {
                    Toast.makeText(requireContext(), "Limite de " + MAX_IMAGENS + " imagens atingido.", Toast.LENGTH_SHORT).show();
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
            });

    // ── Map picker ──────────────────────────────────────────────────────────
    private final ActivityResultLauncher<Intent> mapLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null || !isAdded()) return;
                latitude  = result.getData().getDoubleExtra("latitude", 0);
                longitude = result.getData().getDoubleExtra("longitude", 0);
                tvCoordenadas.setVisibility(View.VISIBLE);
                tvCoordenadas.setText(getString(R.string.local_marcado,
                        String.format("%.5f", latitude),
                        String.format("%.5f", longitude)));
            });

    // ── Lifecycle ───────────────────────────────────────────────────────────
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cadastro_denuncia, container, false);

        editTextTitulo      = view.findViewById(R.id.editTextTitulo);
        spinnerTipo         = view.findViewById(R.id.spinnerTipo);
        editTextLogradouro  = view.findViewById(R.id.editTextLogradouro);
        editTextNumero      = view.findViewById(R.id.editTextNumero);
        editTextComplemento = view.findViewById(R.id.editTextComplemento);
        editTextCidade      = view.findViewById(R.id.editTextCidade);
        editTextUF          = view.findViewById(R.id.editTextUF);
        editTextBairro      = view.findViewById(R.id.editTextBairro);
        editTextCEP         = view.findViewById(R.id.editTextCEP);
        editTextDescricao   = view.findViewById(R.id.editTextDescricao);
        editTextSugestao    = view.findViewById(R.id.editTextSugestao);
        tvCoordenadas       = view.findViewById(R.id.tvCoordenadas);
        containerPreviews   = view.findViewById(R.id.containerPreviews);
        btnCriarDenuncia    = view.findViewById(R.id.btnCriarDenuncia);

        String[] tipos = getResources().getStringArray(R.array.tipos_denuncia);
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);

        view.findViewById(R.id.btnAdicionarImagens).setOnClickListener(v -> mostrarDialogImagem());
        view.findViewById(R.id.btnMarcarLocal).setOnClickListener(v ->
                mapLauncher.launch(new Intent(requireContext(), MapPickerActivity.class)));
        btnCriarDenuncia.setOnClickListener(v -> enviarDenuncia());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pendingCall != null) {
            pendingCall.cancel();
            pendingCall = null;
        }
    }

    // ── Image selection ─────────────────────────────────────────────────────
    private void mostrarDialogImagem() {
        if (imagensSelecionadas.size() >= MAX_IMAGENS) {
            Toast.makeText(requireContext(),
                    "Limite de " + MAX_IMAGENS + " imagens atingido.", Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Adicionar imagem")
                .setItems(new String[]{"Tirar foto", "Escolher da galeria"}, (dialog, which) -> {
                    if (which == 0) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                    } else {
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
            Toast.makeText(requireContext(), "Falha ao criar arquivo de foto.", Toast.LENGTH_SHORT).show();
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
            thumb.setContentDescription("Imagem " + (i + 1));
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
    }

    // ── Submit ──────────────────────────────────────────────────────────────
    private void enviarDenuncia() {
        String titulo    = t(editTextTitulo);
        String descricao = t(editTextDescricao);
        String cidade    = t(editTextCidade);
        String uf        = t(editTextUF);
        String tipo      = spinnerTipo.getSelectedItem() != null
                ? spinnerTipo.getSelectedItem().toString() : "";

        if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(descricao)
                || TextUtils.isEmpty(cidade) || TextUtils.isEmpty(uf)
                || TextUtils.isEmpty(tipo)) {
            Toast.makeText(requireContext(),
                    "Informe título, tipo, descrição, cidade e UF.", Toast.LENGTH_SHORT).show();
            return;
        }

        MultipartBody.Builder body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("titulo",    titulo)
                .addFormDataPart("descricao", descricao)
                .addFormDataPart("tipo",      tipo)
                .addFormDataPart("cidade",    cidade)
                .addFormDataPart("uf",        uf);

        String sugestao    = t(editTextSugestao);
        String logradouro  = t(editTextLogradouro);
        String bairro      = t(editTextBairro);
        String cep         = t(editTextCEP);
        String complemento = t(editTextComplemento);
        String numeroStr   = t(editTextNumero);

        if (!sugestao.isEmpty())    body.addFormDataPart("sugestao",    sugestao);
        if (!logradouro.isEmpty())  body.addFormDataPart("logradouro",  logradouro);
        if (!bairro.isEmpty())      body.addFormDataPart("bairro",      bairro);
        if (!cep.isEmpty())         body.addFormDataPart("cep",         cep);
        if (!complemento.isEmpty()) body.addFormDataPart("complemento", complemento);
        if (!numeroStr.isEmpty()) {
            try {
                Integer.parseInt(numeroStr);
                body.addFormDataPart("numero", numeroStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Número de endereço inválido.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (latitude  != null) body.addFormDataPart("latitude",  String.valueOf(latitude));
        if (longitude != null) body.addFormDataPart("longitude", String.valueOf(longitude));

        for (Uri uri : new ArrayList<>(imagensSelecionadas)) {
            byte[] bytes = uriToBytes(uri);
            if (bytes == null) return; // toast already shown inside uriToBytes
            body.addFormDataPart("imagens", getFilename(uri),
                    RequestBody.create(bytes, MediaType.parse("image/jpeg")));
        }

        btnCriarDenuncia.setEnabled(false);
        if (pendingCall != null) pendingCall.cancel();

        Request request = new Request.Builder()
                .url(BuildConfig.BASE_URL + "denuncias")
                .post(body.build())
                .build();

        OkHttpClient client = RetrofitInitializer.getOkHttpClient(requireContext());
        pendingCall = client.newCall(request);
        pendingCall.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    btnCriarDenuncia.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Denúncia enviada com sucesso!", Toast.LENGTH_LONG).show();
                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    } else if (response.code() == 401) {
                        Toast.makeText(requireContext(),
                                "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                    } else {
                        Toast.makeText(requireContext(),
                                "Erro ao enviar (" + response.code() + ")", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    btnCriarDenuncia.setEnabled(true);
                    Toast.makeText(requireContext(),
                            "Erro de rede: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // ── Helpers ─────────────────────────────────────────────────────────────
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
                                "Imagem muito grande (máx 10 MB).", Toast.LENGTH_SHORT).show();
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
        } catch (Exception ignored) {}
        return result;
    }

    private String t(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
