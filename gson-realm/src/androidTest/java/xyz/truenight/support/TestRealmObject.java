package xyz.truenight.support;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TestRealmObject extends RealmObject {
    @PrimaryKey
    private int id;

    private String field2;

    private boolean field3;
    private boolean field4;

    public TestRealmObject() {
    }

    public TestRealmObject(int id, String field2) {
        this.id = id;
        this.field2 = field2;
    }

    public int getId() {
        return id;
    }

    public String getField2() {
        return field2;
    }

    public TestRealmObject read() {
        field3 = true;
        return this;
    }

    public boolean isRead() {
        return field3;
    }

    public TestRealmObject write() {
        field4 = true;
        return this;
    }

    public boolean isWrite() {
        return field4;
    }
}