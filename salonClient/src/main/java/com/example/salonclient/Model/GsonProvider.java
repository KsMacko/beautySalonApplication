package com.example.salonclient.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

public class GsonProvider {
    public static Gson createGson() {
        return new GsonBuilder()
                .serializeNulls() //включать поля со значением null при сериализации объекта
                .setPrettyPrinting()
                .disableHtmlEscaping() //Отключает экранирование символов HTML (<, >, &) в строках
                .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create(); } }