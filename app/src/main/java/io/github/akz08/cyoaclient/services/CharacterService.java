package io.github.akz08.cyoaclient.services;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

public interface CharacterService {
    @GET("/characters")
    void getCharacters(@QueryMap Map<String, String> params, Callback<List<io.github.akz08.cyoaclient.models.Character>> cb);
}