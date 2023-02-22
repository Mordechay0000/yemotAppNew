package com.mordechay.yemotapp.network;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

public interface VolleyCallback {
    void onSuccess(NetworkResponse response);
    void onError(VolleyError error);
}