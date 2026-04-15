package com.example.acessibilit_report.activities;

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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.acessibilit_report.BuildConfig;
import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.ReportResponse;
import com.example.acessibilit_report.model.Address;
import com.example.acessibilit_report.model.Image;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.ReportService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class ReportEditFragment extends Fragment {

    private static final int MAX_IMAGENS = 5;
    private static final long MAX_IMAGE_BYTES = 10L * 1024 * 1024;

    // ── State ────────────────────────────────────────────────────────────────
    private ReportResponse report;
    private List<Image> imagensExistentes = new ArrayList<>(); // imagens já no servidor
    private List<Long> imagensParaRemover = new ArrayList<>();  // IDs a deletar antes do PUT
    private List<Uri> imagensNovas = new ArrayList<>();          // URIs locais a enviar no PUT

    // ── Views ────────────────────────────────────────────────────────────────
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
    private LinearLayout containerPreviews;
    private MaterialButton btnSalvar;

    private Uri photoUri;
    private okhttp3.Call pendingPutCall;

    // ── Camera ───────────────────────────────────────────────────────────────
    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && photoUri != null && isAdded()) {
                    if (addImagemNova(photoUri)) renderizarMiniaturas();
                }
            });

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) launchCamera();
                else if (isAdded())
                    Toast.makeText(requireContext(), "Permissão de câmera negada.", Toast.LENGTH_SHORT).show();
            });

    // ── Gallery ──────────────────────────────────────────────────────────────
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null || !isAdded()) return;
                Intent data = result.getData();
                Uri single = data.getData();
                ClipData clip = data.getClipData();

                int espacoRestante = MAX_IMAGENS - totalImagens();
                if (espacoRestante <= 0) {
                    Toast.makeText(requireContext(), "Limite de " + MAX_IMAGENS + " imagens atingido.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int adicionadas = 0;
                if (clip != null) {
                    int count = Math.min(clip.getItemCount(), espacoRestante);
                    for (int i = 0; i < count; i++) {
                        Uri u = clip.getItemAt(i).getUri();
                        if (u != null && addImagemNova(u)) adicionadas++;
                    }
                } else if (single != null) {
                    if (addImagemNova(single)) adicionadas++;
                }

                if (adicionadas > 0) renderizarMiniaturas();
            });

    // ── Lifecycle ────────────────────────────────────────────────────────────
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            report = (ReportResponse) getArguments().getSerializable("report");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_denuncia, container, false);

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
        containerPreviews   = view.findViewById(R.id.containerPreviews);
        btnSalvar           = view.findViewById(R.id.btnSalvarAlteracoes);

        String[] tipos = getResources().getStringArray(R.array.tipos_denuncia);
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, tipos);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(tipoAdapter);

        preencherCampos(tipos);

        view.findViewById(R.id.btnAdicionarImagens).setOnClickListener(v -> mostrarDialogImagem());
        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
        view.findViewById(R.id.btnExcluirDenuncia).setOnClickListener(v -> confirmarExclusao());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pendingPutCall != null) {
            pendingPutCall.cancel();
            pendingPutCall = null;
        }
    }

    // ── Pre-fill ─────────────────────────────────────────────────────────────
    private void preencherCampos(String[] tipos) {
        if (report == null) return;

        if (report.titulo != null)    editTextTitulo.setText(report.titulo);
        if (report.descricao != null) editTextDescricao.setText(report.descricao);
        if (report.sugestao != null)  editTextSugestao.setText(report.sugestao);

        if (report.tipo != null) {
            for (int i = 0; i < tipos.length; i++) {
                if (tipos[i].equals(report.tipo)) {
                    spinnerTipo.setSelection(i);
                    break;
                }
            }
        }

        Address e = report.endereco;
        if (e != null) {
            if (e.getLogradouro() != null)  editTextLogradouro.setText(e.getLogradouro());
            if (e.getNumero() != null)       editTextNumero.setText(String.valueOf(e.getNumero()));
            if (e.getComplemento() != null)  editTextComplemento.setText(e.getComplemento());
            if (e.getCidade() != null)       editTextCidade.setText(e.getCidade());
            if (e.getUf() != null)           editTextUF.setText(e.getUf());
            if (e.getBairro() != null)       editTextBairro.setText(e.getBairro());
            if (e.getCep() != null)          editTextCEP.setText(e.getCep());
        }

        // Copia lista de imagens existentes e renderiza
        if (report.imagens != null) {
            imagensExistentes = new ArrayList<>(report.imagens);
        }
        renderizarMiniaturas();
    }

    // ── Image management ─────────────────────────────────────────────────────
    private int totalImagens() {
        return imagensExistentes.size() + imagensNovas.size();
    }

    private void mostrarDialogImagem() {
        if (totalImagens() >= MAX_IMAGENS) {
            Toast.makeText(requireContext(), "Limite de " + MAX_IMAGENS + " imagens atingido.", Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Adicionar imagem")
                .setItems(new String[]{"Tirar foto", "Escolher da galeria"}, (dialog, which) -> {
                    if (which == 0) cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
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
            Toast.makeText(requireContext(), "Falha ao criar arquivo de foto.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean addImagemNova(Uri uri) {
        if (totalImagens() >= MAX_IMAGENS) return false;
        if (imagensNovas.contains(uri)) return false;
        try {
            requireContext().getContentResolver()
                    .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) {}
        imagensNovas.add(uri);
        return true;
    }

    private void renderizarMiniaturas() {
        if (containerPreviews == null || !isAdded()) return;
        containerPreviews.removeAllViews();

        float density  = getResources().getDisplayMetrics().density;
        int sizePx     = (int) (88 * density);
        int marginPx   = (int) (6 * density);
        int btnSizePx  = (int) (28 * density);

        // ── Imagens existentes (servidor) ────────────────────────────────────
        for (Image img : new ArrayList<>(imagensExistentes)) {
            ImageView thumb = makeThumbView(sizePx);
            Glide.with(thumb.getContext())
                    .load(img.getUrl())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(thumb);
            addThumbToContainer(thumb, sizePx, marginPx, btnSizePx, () -> {
                imagensExistentes.remove(img);
                imagensParaRemover.add(img.getId());
                renderizarMiniaturas();
            });
        }

        // ── Imagens novas (local) ────────────────────────────────────────────
        for (Uri uri : new ArrayList<>(imagensNovas)) {
            ImageView thumb = makeThumbView(sizePx);
            thumb.setImageURI(uri);
            addThumbToContainer(thumb, sizePx, marginPx, btnSizePx, () -> {
                imagensNovas.remove(uri);
                renderizarMiniaturas();
            });
        }
    }

    private ImageView makeThumbView(int sizePx) {
        ImageView thumb = new ImageView(requireContext());
        thumb.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return thumb;
    }

    private void addThumbToContainer(ImageView thumb, int sizePx, int marginPx, int btnSizePx, Runnable onRemove) {
        FrameLayout frame = new FrameLayout(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sizePx, sizePx);
        lp.setMargins(marginPx, marginPx, marginPx, marginPx);
        frame.setLayoutParams(lp);
        frame.addView(thumb);

        ImageButton btnX = new ImageButton(requireContext());
        FrameLayout.LayoutParams blp = new FrameLayout.LayoutParams(btnSizePx, btnSizePx);
        blp.gravity = android.view.Gravity.END | android.view.Gravity.TOP;
        btnX.setLayoutParams(blp);
        btnX.setBackgroundResource(android.R.color.transparent);
        btnX.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        btnX.setContentDescription("Remover imagem");
        btnX.setOnClickListener(v -> onRemove.run());
        frame.addView(btnX);

        containerPreviews.addView(frame);
    }

    // ── Save ─────────────────────────────────────────────────────────────────
    private void salvarAlteracoes() {
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

        btnSalvar.setEnabled(false);

        if (!imagensParaRemover.isEmpty()) {
            deletarImagensSequencialmente(new ArrayList<>(imagensParaRemover), 0,
                    () -> executarPut(titulo, descricao, tipo, cidade, uf));
        } else {
            executarPut(titulo, descricao, tipo, cidade, uf);
        }
    }

    /**
     * Deleta imagens removidas uma a uma, de forma recursiva-assíncrona.
     * Só chama onComplete após todas as deleções bem-sucedidas.
     */
    private void deletarImagensSequencialmente(List<Long> ids, int index, Runnable onComplete) {
        if (index >= ids.size()) {
            if (isAdded()) requireActivity().runOnUiThread(onComplete);
            return;
        }
        ReportService api = RetrofitInitializer.getDenunciaService(requireContext());
        api.deletarImagem(report.id, ids.get(index)).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful() || resp.code() == 404) {
                    // 404 = já foi deletada, continua mesmo assim
                    deletarImagensSequencialmente(ids, index + 1, onComplete);
                } else {
                    requireActivity().runOnUiThread(() -> {
                        if (!isAdded()) return;
                        btnSalvar.setEnabled(true);
                        Toast.makeText(requireContext(),
                                "Erro ao remover imagem (" + resp.code() + ")", Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    btnSalvar.setEnabled(true);
                    Toast.makeText(requireContext(),
                            "Erro de rede ao remover imagem: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void executarPut(String titulo, String descricao, String tipo,
                             String cidade, String uf) {
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
                if (isAdded()) {
                    btnSalvar.setEnabled(true);
                    Toast.makeText(requireContext(), "Número inválido.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        // Novas imagens como partes multipart
        for (Uri uri : new ArrayList<>(imagensNovas)) {
            byte[] bytes = uriToBytes(uri);
            if (bytes == null) {
                if (isAdded()) btnSalvar.setEnabled(true);
                return;
            }
            body.addFormDataPart("imagens", getFilename(uri),
                    RequestBody.create(bytes, MediaType.parse("image/jpeg")));
        }

        if (pendingPutCall != null) pendingPutCall.cancel();

        String url = BuildConfig.BASE_URL + "denuncia/" + report.id;
        Request request = new Request.Builder().url(url).put(body.build()).build();

        OkHttpClient client = RetrofitInitializer.getOkHttpClient(requireContext());
        pendingPutCall = client.newCall(request);
        pendingPutCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    btnSalvar.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                R.string.msg_denuncia_atualizada, Toast.LENGTH_LONG).show();
                        requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    } else if (response.code() == 401) {
                        Toast.makeText(requireContext(),
                                "Sessão expirada. Faça login novamente.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                    } else {
                        Toast.makeText(requireContext(),
                                "Erro ao salvar (" + response.code() + ")", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    btnSalvar.setEnabled(true);
                    Toast.makeText(requireContext(),
                            "Erro de rede: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // ── Delete report ─────────────────────────────────────────────────────────
    private void confirmarExclusao() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialogo_excluir_titulo)
                .setMessage(getString(R.string.dialogo_excluir_mensagem,
                        report != null ? report.titulo : ""))
                .setPositiveButton(R.string.btn_excluir, (d, w) -> executarDeleteReport())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void executarDeleteReport() {
        if (report == null) return;
        ReportService api = RetrofitInitializer.getDenunciaService(requireContext());
        api.deletar(report.id).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> resp) {
                if (!isAdded()) return;
                if (resp.isSuccessful()) {
                    Toast.makeText(requireContext(), R.string.msg_denuncia_excluida, Toast.LENGTH_SHORT).show();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                } else {
                    Toast.makeText(requireContext(),
                            "Erro ao excluir (" + resp.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
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
