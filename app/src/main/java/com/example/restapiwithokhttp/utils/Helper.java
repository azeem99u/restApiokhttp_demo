package com.example.restapiwithokhttp.utils;

import com.example.restapiwithokhttp.model.RequestPackage;
import com.google.gson.Gson;

public class Helper {

    // Serialize
    public static String serializeToJson(RequestPackage requestPackage) {
        Gson gson = new Gson();
        return gson.toJson(requestPackage);
    }

    // Deserialize to single object.
    public static RequestPackage deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, RequestPackage.class);
    }

}
