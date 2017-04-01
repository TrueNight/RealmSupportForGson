package xyz.truenight.support.realm;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by true
 * date: 01/04/2017
 * time: 00:04
 * <p>
 * Copyright © Mikhail Frolov
 */

public class DelegateAdapter {

    private Gson gson;
    private TypeAdapterFactory skip;

    public DelegateAdapter(Gson gson, TypeAdapterFactory factory) {
        this.gson = gson;
        this.skip = factory;
    }

    @SuppressWarnings("unchecked")
    public void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws IOException {
        TypeAdapter<?> adapter = gson.getDelegateAdapter(skip, TypeToken.get(typeOfSrc));
        ((TypeAdapter<Object>) adapter).write(writer, src);
    }


    @SuppressWarnings("unchecked")
    public <T> T fromJson(JsonReader reader, Type typeOfT) throws IOException {
        TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(typeOfT);
        TypeAdapter<T> typeAdapter = gson.getDelegateAdapter(skip, typeToken);
        return typeAdapter.read(reader);
    }
}
