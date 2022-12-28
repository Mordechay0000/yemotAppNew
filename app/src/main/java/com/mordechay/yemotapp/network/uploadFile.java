package com.mordechay.yemotapp.network;
/*
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;



public class uploadFile {
    public static void uploadFile(String token, String path, File file) throws IOException {
            String yemotUrl;

        yemotUrl = "https://www.call2all.co.il/ym/api/";
        URL url = new URL(yemotUrl + "UploadFile");

         HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url.toString());

        FileBody fileBody = new FileBody(file);
        HttpEntity entity = MultipartEntityBuilder.create()
                .addPart("upload", fileBody)
                .addTextBody("token", token)
                .addTextBody("path", path + "/" + fileBody.getFilename())
                .build();
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);

        int status = response.getStatusLine().getStatusCode();

    }
}

 */