package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.ReportRequest;
import com.example.acessibilit_report.dto.ReportResponse;
import com.example.acessibilit_report.model.Address;
import com.example.acessibilit_report.model.Answer;
import com.example.acessibilit_report.model.Forum;
import com.example.acessibilit_report.model.GeoLocation;
import com.example.acessibilit_report.model.Person;
import com.example.acessibilit_report.model.Place;
import com.example.acessibilit_report.model.Question;
import com.example.acessibilit_report.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProtectedApiService {

    @GET("usuario")
    Call<User> usuario();

    @GET("usuario/usuarios")
    Call<List<User>> usuarios();

    @GET("usuario/{id}")
    Call<User> usuarioPorId(@Path("id") Long id);

    @PUT("usuario/{id}")
    Call<User> atualizarUsuario(@Path("id") Long id, @Body User usuario);

    @DELETE("usuario/{id}")
    Call<Void> deletarUsuario(@Path("id") Long id);

    @GET("denuncia")
    Call<List<ReportResponse>> denuncia();

    @GET("denuncia/denuncias")
    Call<List<ReportResponse>> denuncias();

    @GET("denuncia/minhas")
    Call<List<ReportResponse>> minhasDenuncias();

    @GET("denuncia/{id}")
    Call<ReportResponse> denunciaPorId(@Path("id") Long id);

    @POST("denuncia")
    Call<ReportResponse> criarDenuncia(@Body ReportRequest body);

    @PUT("denuncia/{id}")
    Call<ReportResponse> atualizarDenuncia(@Path("id") Long id, @Body ReportRequest body);

    @DELETE("denuncia/{id}")
    Call<Void> deletarDenuncia(@Path("id") Long id);

    @GET("denuncia/{id}/imagens/{imgId}")
    Call<ResponseBody> imagemDenuncia(@Path("id") Long id, @Path("imgId") Long imgId);

    @GET("pessoa")
    Call<Person> pessoa();

    @GET("pessoa/pessoas")
    Call<List<Person>> pessoas();

    @GET("pessoa/{id}")
    Call<Person> pessoaPorId(@Path("id") Long id);

    @POST("pessoa")
    Call<Person> criarPessoa(@Body Person pessoa);

    @PUT("pessoa/{id}")
    Call<Person> atualizarPessoa(@Path("id") Long id, @Body Person pessoa);

    @DELETE("pessoa/{id}")
    Call<Void> deletarPessoa(@Path("id") Long id);

    @GET("endereco")
    Call<Address> endereco();

    @GET("endereco/enderecos")
    Call<List<Address>> enderecos();

    @GET("endereco/{id}")
    Call<Address> enderecoPorId(@Path("id") Long id);

    @POST("endereco")
    Call<Address> criarEndereco(@Body Address endereco);

    @PUT("endereco/{id}")
    Call<Address> atualizarEndereco(@Path("id") Long id, @Body Address endereco);

    @DELETE("endereco/{id}")
    Call<Void> deletarEndereco(@Path("id") Long id);

    @GET("localizacao")
    Call<GeoLocation> localizacao();

    @GET("localizacao/localizacoes")
    Call<List<GeoLocation>> localizacoes();

    @GET("localizacao/{id}")
    Call<GeoLocation> localizacaoPorId(@Path("id") Long id);

    @POST("localizacao")
    Call<GeoLocation> criarLocalizacao(@Body GeoLocation localizacao);

    @PUT("localizacao/{id}")
    Call<GeoLocation> atualizarLocalizacao(@Path("id") Long id, @Body GeoLocation localizacao);

    @DELETE("localizacao/{id}")
    Call<Void> deletarLocalizacao(@Path("id") Long id);

    @GET("local")
    Call<Place> local();

    @GET("local/pessoas")
    Call<List<Person>> localPessoas();

    @GET("local/locais")
    Call<List<Place>> locais();

    @GET("local/{id}")
    Call<Place> localPorId(@Path("id") Long id);

    @POST("local")
    Call<Place> criarLocal(@Body Place local);

    @PUT("local/{id}")
    Call<Place> atualizarLocal(@Path("id") Long id, @Body Place local);

    @DELETE("local/{id}")
    Call<Void> deletarLocal(@Path("id") Long id);

    @GET("forum")
    Call<Forum> forum();

    @GET("forum/foruns")
    Call<List<Forum>> foruns();

    @GET("forum/{id}")
    Call<Forum> forumPorId(@Path("id") Long id);

    @POST("forum")
    Call<Forum> criarForum(@Body Forum forum);

    @PUT("forum/{id}")
    Call<Forum> atualizarForum(@Path("id") Long id, @Body Forum forum);

    @DELETE("forum/{id}")
    Call<Void> deletarForum(@Path("id") Long id);

    @GET("pergunta")
    Call<Question> pergunta();

    @GET("pergunta/perguntas")
    Call<List<Question>> perguntas();

    @GET("pergunta/{id}")
    Call<Question> perguntaPorId(@Path("id") Long id);

    @POST("pergunta")
    Call<Question> criarPergunta(@Body Question pergunta);

    @PUT("pergunta/{id}")
    Call<Question> atualizarPergunta(@Path("id") Long id, @Body Question pergunta);

    @DELETE("pergunta/{id}")
    Call<Void> deletarPergunta(@Path("id") Long id);

    @GET("resposta")
    Call<Answer> resposta();

    @GET("resposta/respostas")
    Call<List<Answer>> respostas();

    @GET("resposta/{id}")
    Call<Answer> respostaPorId(@Path("id") Long id);

    @POST("resposta")
    Call<Answer> criarResposta(@Body Answer resposta);

    @PUT("resposta/{id}")
    Call<Answer> atualizarResposta(@Path("id") Long id, @Body Answer resposta);

    @DELETE("resposta/{id}")
    Call<Void> deletarResposta(@Path("id") Long id);
}
