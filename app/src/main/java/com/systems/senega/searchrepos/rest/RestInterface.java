package com.systems.senega.searchrepos.rest;

import com.systems.senega.searchrepos.model.GitHubRepo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestInterface {
    @GET("users/{user}/starred")
    Observable<List<GitHubRepo>> getStarredRepos(@Path("user") String userName);
}
