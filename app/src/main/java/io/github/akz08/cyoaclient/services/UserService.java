package io.github.akz08.cyoaclient.services;

import java.util.Map;

import io.github.akz08.cyoaclient.pojos.ApiKey;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface UserService {
    @POST("/users/{fb_user_id}")
    void authenticate(@Path("fb_user_id") long fbUserId, @Body Map<String, String> params,
                      Callback<ApiKey> cb);

    @PUT("/users/{fb_user_id}/reset")
    void reset(@Path("fb_user_id") long fbUserId, @Body Map<String, String> params, Callback<Void> cb);
}