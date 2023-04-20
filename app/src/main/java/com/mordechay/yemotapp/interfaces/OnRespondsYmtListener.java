package com.mordechay.yemotapp.interfaces;

import org.json.JSONException;

public interface OnRespondsYmtListener {
    void onSuccess(String result, String type) throws JSONException;
    void onFailure(String url, int responseCode, String responseMessage);
}