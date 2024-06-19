package com.example.appchat;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}
