package com.example.salonclient.Model;

import com.google.gson.Gson;

import java.util.Map;

public class JsonConverting {
        public static String createJson(Map<String, Object> params) {
            Gson gson =  GsonProvider.createGson();
            return gson.toJson(params); }
}
