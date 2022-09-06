package com.example.restapiwithokhttp.utils;


import com.example.restapiwithokhttp.model.RequestPackage;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHelper {
    public static String downloadUrl(RequestPackage requestPackage) throws Exception {

        String address = requestPackage.getEndPoint();
        String encodedParams = requestPackage.getEncodedParams();
        if (requestPackage.getMethod().equals("GET")&& encodedParams.length()>0) {
            address = String.format("%s?%s",address,encodedParams);
        }

        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder()
                .url(address);

        if (requestPackage.getMethod().equals("POST")){
            MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            Map<String,String> params = requestPackage.getParams();
            for (String key:params.keySet()) {
                bodyBuilder.addFormDataPart(key,params.get(key));
                RequestBody requestBody = bodyBuilder.build();
                requestBuilder.method(requestPackage.getMethod(), requestBody);
            }
        }

        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()){
            return response.body().string();
        }else {
            throw new Exception("Error: Got response code "+response.code());
        }

    }

}
