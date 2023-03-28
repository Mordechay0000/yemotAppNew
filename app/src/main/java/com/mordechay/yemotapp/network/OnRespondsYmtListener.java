package com.mordechay.yemotapp.network;

public interface OnRespondsYmtListener {
    void onSuccess(String result, String type);
    void onFailure(String url, int responseCode, String responseMessage);
}