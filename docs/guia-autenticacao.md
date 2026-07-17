# Guia de Autenticação — AccessibiliT Report

> Use este documento como referência para implementar ou conectar o sistema de login, recuperação de senha e redefinição de senha com a API backend.

---

## Visão Geral do Fluxo

```
[Login] ──────────────────────────────► MenuActivity (CLIENTE)
                                      ► AdminActivity (MEDIADOR)

[Esqueceu Senha] ──► POST auth/esqueci-senha ──► [Redefinir Senha]

[Redefinir Senha] ──► POST auth/nova-senha ──► [Login]
```

---

## 1. Configuração da URL Base (`BuildConfig.BASE_URL`)

**`app/build.gradle`**
```groovy
buildTypes {
    debug {
        buildConfigField "String", "BASE_URL", "\"http://10.0.2.2:8080/\""
    }
    release {
        buildConfigField "String", "BASE_URL", "\"${releaseApiBaseUrl}\""
    }
}
```

- **Debug**: `http://10.0.2.2:8080/` — endereço padrão do emulador Android para `localhost` da máquina host.
- **Release**: lido de `gradle.properties` via `API_BASE_URL_RELEASE=https://sua-api.com/`. Obrigatório começar com `https://` — o `RetrofitInitializer` lança exceção se não for HTTPS em release.

**`gradle.properties`** (não commitar a URL real se for sensível):
```properties
API_BASE_URL_RELEASE=https://sua-api-de-producao.com/
```

---

## 2. Cliente HTTP — `RetrofitInitializer`

**`retrofit/RetrofitInitializer.java`** — Singleton que cria e compartilha as instâncias de `Retrofit` e `OkHttpClient`.

```java
public class RetrofitInitializer {
    private static Retrofit retrofit;
    private static OkHttpClient httpClient;

    public static synchronized Retrofit getInstance(Context ctx) {
        if (retrofit == null) {
            String baseUrl = BuildConfig.BASE_URL;
            if (!BuildConfig.DEBUG && !baseUrl.startsWith("https://")) {
                throw new IllegalStateException("Release build requires HTTPS base URL.");
            }

            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(BuildConfig.DEBUG
                    ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);

            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(ctx.getApplicationContext()))
                    .addNetworkInterceptor(log)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(httpClient)
                    .build();
        }
        return retrofit;
    }

    public static UserService getUsuarioService(Context ctx) {
        return getInstance(ctx).create(UserService.class);
    }

    // Outros serviços: getDenunciaService, getForumApiService, etc.
}
```

**Dependências (`app/build.gradle`):**
```groovy
implementation 'com.squareup.retrofit2:retrofit:2.11.0'
implementation 'com.squareup.retrofit2:converter-jackson:2.11.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
implementation 'androidx.security:security-crypto:1.0.0'
```

---

## 3. Interceptor de Autenticação — `AuthInterceptor`

**`network/AuthInterceptor.java`** — Adiciona automaticamente `Authorization: Bearer <token>` em todas as requisições protegidas.

```java
public class AuthInterceptor implements Interceptor {

    private final TokenStore tokenStore;

    public AuthInterceptor(Context ctx) {
        this.tokenStore = new TokenStore(ctx);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String path = req.url().encodedPath();

        // Rotas públicas — NÃO recebem o header Authorization
        boolean isPublic =
                (path.equals("/auth/cadastro") && req.method().equals("POST"))
                        || path.equals("/auth/login")
                        || path.equals("/auth/esqueci-senha")
                        || path.equals("/auth/nova-senha");

        String token = tokenStore.get();
        if (!isPublic && token != null && !token.isEmpty()) {
            req = req.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
        }
        return chain.proceed(req);
    }
}
```

> **Regra**: `auth/login`, `auth/cadastro`, `auth/esqueci-senha` e `auth/nova-senha` são rotas públicas — não enviam o token. Todas as demais rotas (`/denuncias`, `/usuario`, etc.) são protegidas e recebem o Bearer token automaticamente.

---

## 4. Armazenamento Seguro do Token — `TokenStore`

**`auth/TokenStore.java`** — Wrapper sobre `EncryptedSharedPreferences` (AES256-GCM). Faz fallback para `SharedPreferences` padrão em dispositivos antigos.

```java
public class TokenStore {

    public static final String PREFS_NAME = "user_secure_prefs";
    public static final String KEY_TOKEN  = "access_token";
    public static final String KEY_NOME   = "nome";
    public static final String KEY_EMAIL  = "email";
    public static final String KEY_TIPO   = "tipoUsuario";

    private final SharedPreferences prefs;

    public TokenStore(Context ctx) {
        SharedPreferences p;
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            p = EncryptedSharedPreferences.create(
                    PREFS_NAME, masterKeyAlias,
                    ctx.getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Fallback para SharedPreferences sem criptografia
            p = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        this.prefs = p;
    }

    /** Salva todos os dados da sessão após login bem-sucedido */
    public void saveSession(String token, String nome, String email, String tipoUsuario) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_NOME, nome)
                .putString(KEY_EMAIL, email)
                .putString(KEY_TIPO, tipoUsuario)
                .apply();
    }

    public String get()            { return prefs.getString(KEY_TOKEN, null); }
    public String getNome()        { return prefs.getString(KEY_NOME, ""); }
    public String getEmail()       { return prefs.getString(KEY_EMAIL, ""); }
    public String getTipoUsuario() { return prefs.getString(KEY_TIPO, ""); }

    /** Limpa a sessão no logout */
    public void clear() { prefs.edit().clear().apply(); }
}
```

---

## 5. Interface Retrofit — `UserService`

**`services/UserService.java`** — Declara todos os endpoints de usuário e autenticação.

```java
public interface UserService {

    // ── Autenticação ──────────────────────────────────────────────────
    @POST("auth/cadastro")
    Call<User> create(@Body RegisterRequest request);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/esqueci-senha")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/nova-senha")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    // ── Usuário ───────────────────────────────────────────────────────
    @GET("usuario")
    Call<User> me();

    @GET("usuario/usuarios")
    Call<List<User>> list();

    @GET("usuario/{id}")
    Call<User> getById(@Path("id") Long id);

    @PUT("usuario/{id}")
    Call<User> update(@Path("id") Long id, @Body User usuario);

    @DELETE("usuario/{id}")
    Call<Void> delete(@Path("id") Long id);
}
```

---

## 6. DTOs de Autenticação

### 6.1 Login

**Request** — `dto/LoginRequest.java`:
```java
public class LoginRequest {
    public String email;
    public String senha;

    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
```

JSON enviado:
```json
{ "email": "usuario@exemplo.com", "senha": "minha_senha" }
```

**Response** — `dto/LoginResponse.java`:
```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {
    public String accessToken;
    public String refreshToken;
    public long expiresIn;
    public User usuario;          // contém nome, email, tipo

    public String getAccessToken()  { return accessToken; }
    public String getNome()         { return usuario != null ? usuario.getNome() : null; }
    public String getEmail()        { return usuario != null ? usuario.getEmail() : null; }
    public String getTipoUsuario()  { return usuario != null ? usuario.getTipo() : null; }
}
```

JSON recebido (exemplo):
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "expiresIn": 3600,
  "usuario": {
    "id": 1,
    "nome": "João Silva",
    "email": "usuario@exemplo.com",
    "tipo": "CLIENTE"
  }
}
```

**Tipos de usuário** (`usuario.tipo`):
| Valor      | Destino após login |
|------------|-------------------|
| `CLIENTE`  | `MenuActivity`    |
| `MEDIADOR` | `AdminActivity`   |

---

### 6.2 Esqueceu a Senha

**Request** — `dto/ForgotPasswordRequest.java`:
```java
public class ForgotPasswordRequest {
    public String email;

    public ForgotPasswordRequest(String email) { this.email = email; }
}
```

JSON enviado:
```json
{ "email": "usuario@exemplo.com" }
```

**Response** — `dto/ForgotPasswordResponse.java`:
```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForgotPasswordResponse {
    public String mensagem;
}
```

> O backend (AWS Cognito) envia um código de verificação para o e-mail do usuário. A API retorna 200 com uma mensagem de confirmação.

---

### 6.3 Redefinir Senha (nova senha)

**Request** — `dto/ResetPasswordRequest.java`:
```java
public class ResetPasswordRequest {
    public String email;
    public String codigo;               // código recebido por e-mail (Cognito)
    public String novaSenha;
    public String confirmacaoNovaSenha;

    public ResetPasswordRequest(String email, String codigo,
                                String novaSenha, String confirmacaoNovaSenha) {
        this.email = email;
        this.codigo = codigo;
        this.novaSenha = novaSenha;
        this.confirmacaoNovaSenha = confirmacaoNovaSenha;
    }
}
```

JSON enviado:
```json
{
  "email": "usuario@exemplo.com",
  "codigo": "123456",
  "novaSenha": "NovaSenha@123",
  "confirmacaoNovaSenha": "NovaSenha@123"
}
```

**Response**: `Call<Void>` — HTTP 200 sem corpo indica sucesso.

---

## 7. Activities de Autenticação

### 7.1 `LoginActivity`

**Layout**: `R.layout.activity_login`

**Views**:
- `R.id.editTextEmail` → campo de e-mail
- `R.id.editTextSenha` → campo de senha (com toggle de visibilidade via `PasswordToggle.setup()`)
- `R.id.buttonLogin` → botão "Entrar"
- `R.id.textViewTelaCadastro` → link para cadastro
- `R.id.textViewEsqueceuSenha` → link para recuperação de senha

**Lógica principal:**

```java
private void doLogin() {
    String email = txtLogin.getText().toString().trim().toLowerCase(Locale.ROOT);
    String senha = txtSenha.getText().toString();

    if (email.isEmpty() || senha.isEmpty()) {
        Toast.makeText(this, getString(R.string.login_campos_obrigatorios), Toast.LENGTH_SHORT).show();
        return;
    }

    btnEntrar.setEnabled(false);  // evita duplo clique

    UserService service = RetrofitInitializer.getUsuarioService(this);
    service.login(new LoginRequest(email, senha)).enqueue(new Callback<LoginResponse>() {

        @Override
        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> resp) {
            btnEntrar.setEnabled(true);

            if (!resp.isSuccessful() || resp.body() == null) {
                Toast.makeText(LoginActivity.this,
                        getString(R.string.login_credenciais_invalidas), Toast.LENGTH_SHORT).show();
                return;
            }

            LoginResponse lr = resp.body();

            // 1. Salvar sessão criptografada
            new TokenStore(LoginActivity.this).saveSession(
                    safe(lr.getAccessToken()),
                    safe(lr.getNome()),
                    safe(lr.getEmail()),
                    safe(lr.getTipoUsuario())
            );

            // 2. Rotear por tipo de usuário
            String tipo = lr.getTipoUsuario();
            if ("CLIENTE".equalsIgnoreCase(tipo)) {
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();
            } else if ("MEDIADOR".equalsIgnoreCase(tipo)) {
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this,
                        getString(R.string.login_tipo_desconhecido), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Call<LoginResponse> call, Throwable t) {
            btnEntrar.setEnabled(true);
            Toast.makeText(LoginActivity.this,
                    getString(R.string.erro_de_rede, t.getMessage()), Toast.LENGTH_SHORT).show();
        }
    });
}

private String safe(String s) { return s == null ? "" : s; }
```

---

### 7.2 `ForgotPasswordActivity`

**Layout**: `R.layout.activity_forgot_password`

**Views**:
- `R.id.editTextEmailRecuperacao` → campo de e-mail
- `R.id.buttonEnviarTrocaSenha` → botão "Enviar"

**Lógica principal:**

```java
private void enviarSolicitacao() {
    String email = txtEmail.getText().toString().trim().toLowerCase(Locale.ROOT);
    if (email.isEmpty()) {
        Toast.makeText(this, getString(R.string.forgot_email_obrigatorio), Toast.LENGTH_SHORT).show();
        return;
    }

    btnEnviar.setEnabled(false);
    UserService service = RetrofitInitializer.getUsuarioService(this);

    service.forgotPassword(new ForgotPasswordRequest(email))
            .enqueue(new Callback<ForgotPasswordResponse>() {

        @Override
        public void onResponse(Call<ForgotPasswordResponse> call,
                               Response<ForgotPasswordResponse> response) {
            btnEnviar.setEnabled(true);
            if (!response.isSuccessful()) {
                Toast.makeText(ForgotPasswordActivity.this,
                        getString(R.string.forgot_erro_enviar), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(ForgotPasswordActivity.this,
                    getString(R.string.forgot_codigo_enviado), Toast.LENGTH_SHORT).show();

            // Passa o e-mail para a próxima tela (necessário para POST auth/nova-senha)
            Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        }

        @Override
        public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
            btnEnviar.setEnabled(true);
            Toast.makeText(ForgotPasswordActivity.this,
                    getString(R.string.erro_de_rede, t.getMessage()), Toast.LENGTH_SHORT).show();
        }
    });
}
```

---

### 7.3 `ResetPasswordActivity`

**Layout**: `R.layout.activity_reset_password`

**Como chega o e-mail**: via `getIntent().getStringExtra("email")` — passado pela `ForgotPasswordActivity`.

**Views**:
- `R.id.editTextTokenAws` → código recebido por e-mail (Cognito)
- `R.id.editTextNovaSenha` → nova senha
- `R.id.editTextRepetirSenha` → confirmação da senha
- `R.id.buttonSalvarNovaSenha` → botão "Salvar"

**Lógica principal:**

```java
private void redefinirSenha() {
    String codigo       = txtTokenAws.getText().toString().trim();
    String novaSenha    = txtNovaSenha.getText().toString();
    String repetirSenha = txtRepetirSenha.getText().toString();

    if (codigo.isEmpty() || novaSenha.isEmpty() || repetirSenha.isEmpty()) {
        Toast.makeText(this, getString(R.string.reset_campos_obrigatorios), Toast.LENGTH_SHORT).show();
        return;
    }
    if (!novaSenha.equals(repetirSenha)) {
        Toast.makeText(this, getString(R.string.registro_senhas_diferentes), Toast.LENGTH_SHORT).show();
        return;
    }

    btnSalvar.setEnabled(false);
    UserService service = RetrofitInitializer.getUsuarioService(this);

    service.resetPassword(new ResetPasswordRequest(email, codigo, novaSenha, repetirSenha))
            .enqueue(new Callback<Void>() {

        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            btnSalvar.setEnabled(true);
            if (!response.isSuccessful()) {
                Toast.makeText(ResetPasswordActivity.this,
                        getString(R.string.reset_erro_redefinir), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(ResetPasswordActivity.this,
                    getString(R.string.reset_senha_alterada), Toast.LENGTH_SHORT).show();

            // Volta para Login limpando a back stack
            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            btnSalvar.setEnabled(true);
            Toast.makeText(ResetPasswordActivity.this,
                    getString(R.string.erro_de_rede, t.getMessage()), Toast.LENGTH_SHORT).show();
        }
    });
}
```

---

## 8. Resumo dos Endpoints

| Endpoint                 | Método | Auth? | Body                                                | Resposta              |
|--------------------------|--------|-------|-----------------------------------------------------|-----------------------|
| `auth/login`             | POST   | Não   | `{ email, senha }`                                  | `LoginResponse` (JWT) |
| `auth/cadastro`          | POST   | Não   | `RegisterRequest`                                   | `User`                |
| `auth/esqueci-senha`     | POST   | Não   | `{ email }`                                         | `{ mensagem }`        |
| `auth/nova-senha`        | POST   | Não   | `{ email, codigo, novaSenha, confirmacaoNovaSenha}` | `204 No Content`      |
| `usuario` (GET)          | GET    | Sim   | —                                                   | `User` (dados próprios)|
| `denuncias` (e demais)   | *      | Sim   | —                                                   | varia                 |

---

## 9. Fluxo Completo de Autenticação

```
Usuário digita e-mail + senha
        │
        ▼
POST auth/login
        │
   ┌────┴────┐
   │ Sucesso │  ──► TokenStore.saveSession(token, nome, email, tipo)
   └────┬────┘          │
        │          tipo == "CLIENTE" ──► MenuActivity
        │          tipo == "MEDIADOR" ──► AdminActivity
        │
   ┌────┴────┐
   │  Erro   │  ──► Toast "Credenciais inválidas"
   └─────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Usuário clica "Esqueceu a senha"
        │
        ▼
Digita e-mail → POST auth/esqueci-senha
        │
   ┌────┴────┐
   │ Sucesso │  ──► Navega para ResetPasswordActivity (passa email via Intent)
   └─────────┘       AWS Cognito envia código por e-mail

        │
Usuário recebe código no e-mail
        │
        ▼
Digita código + nova senha + confirmação → POST auth/nova-senha
        │
   ┌────┴────┐
   │ Sucesso │  ──► Navega para LoginActivity (FLAG_ACTIVITY_CLEAR_TOP)
   └─────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Qualquer requisição protegida (automático via AuthInterceptor):
        │
        ▼
OkHttp adiciona: Authorization: Bearer <accessToken>
```

---

## 10. Logout

Para deslogar o usuário, limpe o `TokenStore` e redirecione para `LoginActivity`:

```java
new TokenStore(context).clear();
Intent intent = new Intent(context, LoginActivity.class);
intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
context.startActivity(intent);
```

---

## 11. Strings de Feedback ao Usuário (`strings.xml`)

| Chave                           | Uso                                              |
|---------------------------------|--------------------------------------------------|
| `login_campos_obrigatorios`     | E-mail ou senha vazios                           |
| `login_credenciais_invalidas`   | HTTP não 2xx no login                            |
| `login_bem_vindo`               | Saudação com nome (formato: `"Bem-vindo, %1$s"`) |
| `login_tipo_desconhecido`       | `tipo` diferente de CLIENTE/MEDIADOR             |
| `forgot_email_obrigatorio`      | E-mail vazio em ForgotPassword                   |
| `forgot_erro_enviar`            | HTTP não 2xx em esqueci-senha                    |
| `forgot_codigo_enviado`         | Sucesso — código enviado por e-mail              |
| `reset_campos_obrigatorios`     | Algum campo vazio em ResetPassword               |
| `registro_senhas_diferentes`    | Nova senha ≠ confirmação                         |
| `reset_erro_redefinir`          | HTTP não 2xx em nova-senha                       |
| `reset_senha_alterada`          | Senha alterada com sucesso                       |
| `erro_de_rede`                  | Falha de conexão (formato: `"Erro de rede: %1$s"`) |
