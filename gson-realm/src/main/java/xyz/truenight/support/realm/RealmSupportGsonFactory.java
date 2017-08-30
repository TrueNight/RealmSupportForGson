/**
 * Copyright (C) 2017 Mikhail Frolov
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.truenight.support.realm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.Realm;

/**
 * Factory to create {@link com.google.gson.Gson}
 * with support of {@link io.realm.internal.RealmObjectProxy} serialization
 */
public final class RealmSupportGsonFactory {
    private RealmSupportGsonFactory() {
    }

    /**
     * Returns default {@link com.google.gson.Gson} instance which supports serialization of {@link io.realm.internal.RealmObjectProxy}.
     * <p>
     * By default to unmanage {@link io.realm.RealmObject} used {@link io.realm.Realm} obtained by {@link io.realm.Realm#getDefaultInstance()}.
     * If you need specify the way to get {@link io.realm.Realm} instance use {@link #create(RealmHook)}
     */
    public static Gson create() {
        return create(new GsonBuilder());
    }

    /**
     * Returns {@link com.google.gson.Gson} instance which supports serialization of {@link io.realm.internal.RealmObjectProxy}.
     * <p>
     * By default to unmanage {@link io.realm.RealmObject} used {@link io.realm.Realm} obtained by {@link io.realm.Realm#getDefaultInstance()}.
     * If you need specify the way to get {@link io.realm.Realm} instance use {@link #create(GsonBuilder, RealmHook)}
     *
     * @param builder builder with preset of params
     */
    public static Gson create(GsonBuilder builder) {
        return create(builder, new RealmHook() {
            @Override
            public Realm instance() {
                return Realm.getDefaultInstance();
            }
        });
    }

    /**
     * Returns {@link com.google.gson.Gson} instance which supports serialization of {@link io.realm.internal.RealmObjectProxy}.
     *
     * @param hook provider of {@link io.realm.Realm} which will be used to unmanage {@link io.realm.RealmObject} before serialization.
     */
    public static Gson create(RealmHook hook) {
        return create(new GsonBuilder(), hook);
    }

    /**
     * Returns {@link com.google.gson.Gson} instance which supports serialization of {@link io.realm.internal.RealmObjectProxy}.
     *
     * @param builder builder with preset of params
     * @param hook    provider of {@link io.realm.Realm} which will be used to unmanage {@link io.realm.RealmObject} before serialization.
     */
    public static Gson create(GsonBuilder builder, RealmHook hook) {
        return builder
                .registerTypeAdapterFactory(
                        new RealmModelAdapterFactory(hook)
                )
                .create();
    }
}
