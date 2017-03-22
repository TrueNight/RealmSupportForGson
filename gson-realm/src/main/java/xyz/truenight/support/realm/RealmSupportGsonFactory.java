package xyz.truenight.support.realm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.Realm;

/**
 * Copyright (C) 2017 Mikhail Frolov
 */

public class RealmSupportGsonFactory {
    private RealmSupportGsonFactory() {
    }

    public static Gson create() {
        return create(new GsonBuilder());
    }

    public static Gson create(GsonBuilder builder) {
        return create(builder, new RealmHook() {
            @Override
            public Realm instance() {
                return Realm.getDefaultInstance();
            }
        });
    }

    public static Gson create(RealmHook hook) {
        return create(new GsonBuilder(), hook);
    }

    public static Gson create(GsonBuilder builder, RealmHook hook) {
        return new GsonBuilder()
                .registerTypeAdapterFactory(
                        new RealmModelAdapterFactory(builder.create(), hook)
                )
                .create();
    }
}
