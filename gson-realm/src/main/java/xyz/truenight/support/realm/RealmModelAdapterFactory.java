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
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.internal.RealmObjectProxy;

class RealmModelAdapterFactory implements TypeAdapterFactory {

    private final RealmHook hook;

    public RealmModelAdapterFactory(RealmHook hook) {
        this.hook = hook;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        return new Adapter<>(new DelegateAdapter(gson, this), this.hook, type);
    }

    public static class Adapter<T> extends TypeAdapter<T> {

        private final DelegateAdapter gson;
        private final RealmHook hook;
        private final TypeToken<T> type;

        public Adapter(DelegateAdapter gson, RealmHook hook, TypeToken<T> type) {
            this.gson = gson;
            this.hook = hook;
            this.type = type;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            value = clone(value);
            gson.toJson(value, convert(type), out);
        }

        @Override
        public T read(JsonReader in) throws IOException {
            return gson.fromJson(in, convert(type));
        }

        private Type convert(TypeToken<T> type) {
            if (RealmObjectProxy.class.isAssignableFrom(type.getRawType())) {
                return type.getRawType().getSuperclass();
            } else {
                return type.getType();
            }
        }

        @SuppressWarnings("unchecked")
        private <E> E clone(E object) {
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
