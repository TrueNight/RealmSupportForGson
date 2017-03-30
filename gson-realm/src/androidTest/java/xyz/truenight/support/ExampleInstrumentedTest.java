package xyz.truenight.support;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.realm.Realm;
import xyz.truenight.support.realm.RealmSupportGsonFactory;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
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

        TestRealmObject objectProxy = Realm.getDefaultInstance().where(TestRealmObject.class).equalTo("id", id.intValue()).findFirst();

        Gson gson = RealmSupportGsonFactory.create();
        String json = gson.toJson(objectProxy);

        gson.toJson(gson.fromJson(json, TestRealmObject.class));
    }


}
