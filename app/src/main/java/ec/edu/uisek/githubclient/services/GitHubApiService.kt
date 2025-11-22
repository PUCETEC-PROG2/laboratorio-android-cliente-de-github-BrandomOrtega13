package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GitHubApiService {
    //Aqui vamos a hacer las llamadas a la API
    //estamos usando retrofit2
    @GET("/user/repos")
    fun getRepos(
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc",
    ): Call<List<Repo>> //usamos promise que se llama Call

    @POST("/user/repos")
    fun postFormRepo(
        @Body repoRequest: RepoRequest
    ): Call<Repo>
}