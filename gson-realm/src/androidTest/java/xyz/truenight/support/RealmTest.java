package xyz.truenight.support;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import io.realm.Realm;
import xyz.truenight.support.realm.RealmSupportGsonFactory;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RealmTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Realm.init(appContext);

        Number id = Realm.getDefaultInstance().where(TestRealmObject.class).max("id");
        id = id == null ? 1 : (id.intValue() + 1);
        Realm.getDefaultInstance().beginTransaction();
        Realm.getDefaultInstance().insertOrUpdate(new TestRealmObject(id.intValue(), "wow" + id));
        Realm.getDefaultInstance().commitTransaction();

        Gson gson = RealmSupportGsonFactory.create(new GsonBuilder()
                .registerTypeAdapter(TestRealmObject.class, new TypeAdapter<TestRealmObject>() {
                    @Override
                    public void write(JsonWriter out, TestRealmObject value) throws IOException {
                        new Gson().toJson(value.write(), TestRealmObject.class, out);
                    }

                    @Override
                    public TestRealmObject read(JsonReader in) throws IOException {
                        TestRealmObject o = new Gson().fromJson(in, TestRealmObject.class);
                        return o.read();
                    }
                }));

        TestRealmObject objectProxy = Realm.getDefaultInstance().where(TestRealmObject.class).equalTo("id", id.intValue()).findFirst();

        TestRealmObject object = gson.fromJson(gson.toJson(objectProxy), TestRealmObject.class);
        assertEquals(objectProxy.getId(), object.getId());
        assertEquals(objectProxy.getField2(), object.getField2());
        assertEquals(true, object.isRead());
        assertEquals(true, object.isWrite());

        TestContainer container = new TestContainer(objectProxy);
        TestContainer deserializeContainer = gson.fromJson(gson.toJson(container), TestContainer.class);
        assertEquals(objectProxy.getId(), deserializeContainer.getObject().getId());
        assertEquals(objectProxy.getField2(), deserializeContainer.getObject().getField2());
        assertEquals(true, deserializeContainer.getObject().isRead());
        assertEquals(true, deserializeContainer.getObject().isWrite());

        TestContainerRealmObject containerRealmObject = new TestContainerRealmObject(objectProxy);
        TestContainerRealmObject deserializeContainerRealmObject = gson.fromJson(gson.toJson(containerRealmObject), TestContainerRealmObject.class);
        assertEquals(objectProxy.getId(), deserializeContainerRealmObject.getObject().getId());
        assertEquals(objectProxy.getField2(), deserializeContainerRealmObject.getObject().getField2());
        assertEquals(true, deserializeContainerRealmObject.getObject().isRead());
        assertEquals(true, deserializeContainerRealmObject.getObject().isWrite());
    }


}
