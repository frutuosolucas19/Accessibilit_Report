package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.ForumRequest;
import com.example.acessibilit_report.dto.ForumResponse;
import com.example.acessibilit_report.dto.PerguntaDetailResponse;
import com.example.acessibilit_report.dto.PerguntaRequest;
import com.example.acessibilit_report.dto.PerguntaResponse;
import com.example.acessibilit_report.dto.RespostaRequest;
import com.example.acessibilit_report.dto.RespostaResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ForumApiService {

    // ── Fóruns ──────────────────────────────────────────────────────────────

    @GET("forums")
    Call<List<ForumResponse>> listarForuns();

    @GET("forums/{id}")
    Call<ForumResponse> obterForum(@Path("id") Long id);

    @POST("forums")
    Call<ForumResponse> criarForum(@Body ForumRequest body);

    @PUT("forums/{id}")
    Call<ForumResponse> atualizarForum(@Path("id") Long id, @Body ForumRequest body);

    @DELETE("forums/{id}")
    Call<Void> deletarForum(@Path("id") Long id);

    // ── Perguntas ────────────────────────────────────────────────────────────

    @GET("forums/{forumId}/perguntas")
    Call<List<PerguntaResponse>> listarPerguntas(@Path("forumId") Long forumId);

    @GET("forums/{forumId}/perguntas/{id}")
    Call<PerguntaDetailResponse> obterPergunta(@Path("forumId") Long forumId, @Path("id") Long id);

    @POST("forums/{forumId}/perguntas")
    Call<PerguntaResponse> criarPergunta(@Path("forumId") Long forumId, @Body PerguntaRequest body);

    @PUT("forums/{forumId}/perguntas/{id}")
    Call<PerguntaResponse> atualizarPergunta(@Path("forumId") Long forumId, @Path("id") Long id, @Body PerguntaRequest body);

    @DELETE("forums/{forumId}/perguntas/{id}")
    Call<Void> deletarPergunta(@Path("forumId") Long forumId, @Path("id") Long id);

    // ── Respostas ────────────────────────────────────────────────────────────

    @POST("forums/{forumId}/perguntas/{perguntaId}/respostas")
    Call<RespostaResponse> criarResposta(@Path("forumId") Long forumId,
                                         @Path("perguntaId") Long perguntaId,
                                         @Body RespostaRequest body);

    @PUT("forums/{forumId}/perguntas/{perguntaId}/respostas/{id}")
    Call<RespostaResponse> atualizarResposta(@Path("forumId") Long forumId,
                                              @Path("perguntaId") Long perguntaId,
                                              @Path("id") Long id,
                                              @Body RespostaRequest body);

    @DELETE("forums/{forumId}/perguntas/{perguntaId}/respostas/{id}")
    Call<Void> deletarResposta(@Path("forumId") Long forumId,
                                @Path("perguntaId") Long perguntaId,
                                @Path("id") Long id);
}
