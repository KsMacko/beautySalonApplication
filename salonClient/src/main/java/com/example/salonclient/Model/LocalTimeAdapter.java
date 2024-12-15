package com.example.salonclient.Model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeAdapter extends TypeAdapter<LocalTime> {
    @Override public void write(JsonWriter jsonWriter, LocalTime localTime)
            throws IOException {
        jsonWriter.value(localTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
    }
    @Override public LocalTime read(JsonReader jsonReader) throws IOException {
        return LocalTime.parse(jsonReader.nextString(), DateTimeFormatter.ISO_LOCAL_TIME);
    }
}
