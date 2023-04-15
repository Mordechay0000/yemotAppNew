package com.mordechay.yemotapp.interfaces;

public interface OnRespondsYmtListener {
    void onSuccess(String result, String type);
    void onFailure(String url, int responseCode, String responseMessage);
}