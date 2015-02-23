package com.james.testgithub.main.Another;


import java.util.List;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface GitHubInterface {

    @POST("/authorizations")
    void createToken(@Header("Authorization") String authorization, @Body TokenRequest request, Callback<AuthorizationResult> callback);

    @GET("/authorizations")
    void checkAuth(@Header("Authorization") String authorization, @Query("page") int currentPage, Callback<List<AuthorizationResult>> callback);

    @GET("/user")
    void getBasicUserInfo(Callback<User> callback);

    @GET("/user/repos")
    void getRepositoriesList(@Query("per_page") int perPage, @Query("page") int currentPage, Callback<List<Repository>> callback);

    @GET("/repos/{owner}/{repo}/commits")
    void getRepositoryCommits(@Path("owner") String owner, @Path("repo") String repo, @Query("per_page") int perPage, @Query("page") int currentPage, Callback<List<Commit>> cb);

}