package com.example.salonclient.Model;

import javafx.concurrent.Task;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.List;

public class ServerAccess {
    private static void executeRequest(String url, String method, String jsonInputString, Consumer<String> onSuccess, Consumer<Throwable> onError, boolean parseResponse) {

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                HttpURLConnection con = (HttpURLConnection) URI.create("http://localhost:8080"+url).toURL().openConnection();
                con.setRequestMethod(method);
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                if (jsonInputString != null && !jsonInputString.isEmpty()) {
                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                }

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStreamReader reader = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)) {
                        String response = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
                        System.out.println("\n\nJSON Response: " + response);
                        return response;
                    }
                } else { return handleErrorResponse(con); }
            }
            private String handleErrorResponse(HttpURLConnection con) throws Exception {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line.trim()); }
                    return response.toString(); } }
            @Override protected void succeeded() {
                if (getValue().startsWith("{\"statusCode\"")) {
                    onError.accept(new RuntimeException(getValue()));
                } else { onSuccess.accept(getValue()); }}
            @Override protected void failed() {
                onError.accept(getException());}
        };
        new Thread(task).start();
    }

    public static void sendRequest(String url, String method, String jsonInputString, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        executeRequest(url, method, jsonInputString, response ->{
            if (response.startsWith("{\"statusCode\"")) {
                Gson gson = new Gson();
                AppError error = gson.fromJson(response, AppError.class);
                onError.accept(new RuntimeException(error.getMessage()));
            } else { onSuccess.accept(response); }
        }, onError, true);

    }

    public static <T> void sendRequest(String url, String method, String jsonInputString, Class<T> responseType,
                                       Consumer<T> onSuccess, Consumer<Throwable> onError) {
        executeRequest(url, method, jsonInputString, response -> {
            Gson gson = GsonProvider.createGson();
            T result = gson.fromJson(response, responseType);
            onSuccess.accept(result);
        }, onError, false);
    }
    public static <T> void sendListRequest(String url, String method, String jsonInputString, Class<T[]> responseType,
                                           Consumer<List<T>> onSuccess, Consumer<Throwable> onError) {
        executeRequest(url, method, jsonInputString, response -> {
            Gson gson = GsonProvider.createGson();
            T[] array = gson.fromJson(response, responseType);
            List<T> result = Arrays.asList(array);
            onSuccess.accept(result);
            }, onError, false);
    }
}
