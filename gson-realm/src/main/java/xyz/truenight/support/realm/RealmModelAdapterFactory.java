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

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.internal.RealmObjectProxy;

class RealmModelAdapterFactory implements TypeAdapterFactory {

    private final RealmHook hook;

    public RealmModelAdapterFactory(RealmHook hook) {
        this.hook = hook;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        // in type can be real class (if it's internal field of any other class)
        // or proxy (if it's direct call)
        Class<? super T> rawType = typeToken.getRawType();
        if (RealmModel.class.isAssignableFrom(rawType)) {
            return new Adapter<>(gson, this.hook, typeToken);
        } else {
            return null;
        }
    }

    public class Adapter<T> extends TypeAdapter<T> {

        private final Gson gson;
        private final RealmHook hook;
        private final TypeToken<T> type;

        public Adapter(Gson gson, RealmHook hook, TypeToken<T> type) {
            this.gson = gson;
            this.hook = hook;
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void write(JsonWriter out, T value) throws IOException {
            value = unmanage(value);
            TypeAdapter<T> adapter = (TypeAdapter<T>) gson.getDelegateAdapter(RealmModelAdapterFactory.this, TypeToken.get(convert(type)));
            adapter.write(out, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T read(JsonReader in) throws IOException {
            TypeAdapter<T> adapter = (TypeAdapter<T>) gson.getDelegateAdapter(RealmModelAdapterFactory.this, TypeToken.get(convert(type)));
            return adapter.read(in);
        }

        // convert Proxy type to original type
        private Type convert(TypeToken<T> type) {
            if (RealmObjectProxy.class.isAssignableFrom(type.getRawType())) {
                return type.getRawType().getSuperclass();
            } else {
                return type.getType();
            }
        }

        // convert Proxy object to original object
        @SuppressWarnings("unchecked")
        private <E> E unmanage(E object) {
            if (object == null) {
                return null;
            }
            if (object instanceof RealmObjectProxy) {
                Realm instance = hook.instance();
                E e = (E) instance.copyFromRealm((RealmModel) object);
                instance.close();
                return e;
            } else {
                return object;
            }
        }
    }
}
