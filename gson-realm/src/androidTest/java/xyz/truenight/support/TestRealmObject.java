package xyz.truenight.support;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TestRealmObject extends RealmObject {
    @PrimaryKey
    private int id;

    private String field2;

    public TestRealmObject() {
    }

    public TestRealmObject(int id, String field2) {
        this.id = id;
        this.field2 = field2;
    }
}