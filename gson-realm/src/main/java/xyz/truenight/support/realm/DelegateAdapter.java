package xyz.truenight.support.realm;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.EOFException;
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
    public void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
        TypeAdapter<?> adapter = gson.getDelegateAdapter(skip, TypeToken.get(typeOfSrc));
        boolean oldLenient = writer.isLenient();
        writer.setLenient(true);
        boolean oldHtmlSafe = writer.isHtmlSafe();
        writer.setHtmlSafe(gson.htmlSafe());
        boolean oldSerializeNulls = writer.getSerializeNulls();
        writer.setSerializeNulls(gson.serializeNulls());
        try {
            ((TypeAdapter<Object>) adapter).write(writer, src);
        } catch (IOException e) {
            throw new JsonIOException(e);
        } finally {
            writer.setLenient(oldLenient);
            writer.setHtmlSafe(oldHtmlSafe);
            writer.setSerializeNulls(oldSerializeNulls);
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        boolean isEmpty = true;
        boolean oldLenient = reader.isLenient();
        reader.setLenient(true);
        try {
            reader.peek();
            isEmpty = false;
            TypeToken<T> typeToken = (TypeToken<T>) TypeToken.get(typeOfT);
            TypeAdapter<T> typeAdapter = gson.getDelegateAdapter(skip, typeToken);
            T object = typeAdapter.read(reader);
            return object;
        } catch (EOFException e) {
            /*
             * For compatibility with JSON 1.5 and earlier, we return null for empty
             * documents instead of throwing.
             */
            if (isEmpty) {
                return null;
            }
            throw new JsonSyntaxException(e);
        } catch (IllegalStateException e) {
            throw new JsonSyntaxException(e);
        } catch (IOException e) {
            // TODO(inder): Figure out whether it is indeed right to rethrow this as JsonSyntaxException
            throw new JsonSyntaxException(e);
        } finally {
            reader.setLenient(oldLenient);
        }
    }
}
