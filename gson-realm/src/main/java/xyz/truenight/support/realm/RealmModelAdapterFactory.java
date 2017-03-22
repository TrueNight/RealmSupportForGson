package xyz.truenight.support.realm;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

class RealmModelAdapterFactory implements TypeAdapterFactory {

    private final Gson gson;
    private final RealmHook hook;

    public RealmModelAdapterFactory(Gson gson, RealmHook hook) {
        this.gson = gson;
        this.hook = hook;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        return new Adapter<>(this.gson, this.hook, type);
    }

    public static class Adapter<T> extends TypeAdapter<T> {

        private final Gson gson;
        private final RealmHook hook;
        private final TypeToken<T> type;

        public Adapter(Gson gson, RealmHook hook, TypeToken<T> type) {
            this.gson = gson;
            this.hook = hook;
            this.type = type;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            value = clone(value);
            gson.toJson(value, type.getType(), out);
        }

        @Override
        public T read(JsonReader in) throws IOException {
            return gson.fromJson(in, type.getType());
        }

        @SuppressWarnings("unchecked")
        public final <E> E clone(E object) {
            if (object == null) {
                return null;
            }
            if (object instanceof RealmModel) {
                if (!(RealmObject.isManaged((RealmModel) object) && RealmObject.isValid((RealmModel) object))) {
                    return object;
                }
                return (E) hook.instance().copyFromRealm((RealmModel) object);
            } else {
                return object;
            }
        }
    }
}
