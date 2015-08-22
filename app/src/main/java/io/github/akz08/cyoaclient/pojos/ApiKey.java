package io.github.akz08.cyoaclient.pojos;

import com.google.gson.annotations.SerializedName;

public class ApiKey {
    @SerializedName("api_key")
    private String apiKey;

    public String getApiKey() { return apiKey; }
    public void setApiKey(final String apiKey) { this.apiKey = apiKey; }
}