package com.mordechay.yemotapp.interfaces;

public interface OnRespondsMyServListener {
    void onSuccess(String result, String type);
    void onFailure(String url, int responseCode, String responseMessage);
}