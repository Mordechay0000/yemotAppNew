package com.mordechay.yemotapp.interfaces;

public interface OnRespondsMyServListener {
    void onSuccess(String result, int type);
    void onFailure(String url, int responseCode, String responseMessage);
}