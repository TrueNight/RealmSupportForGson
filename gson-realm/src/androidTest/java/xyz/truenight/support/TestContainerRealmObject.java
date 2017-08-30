package xyz.truenight.support;

/**
 * Created by true
 * date: 01/04/2017
 * time: 00:19
 * <p>
 * Copyright © Mikhail Frolov
 */

public class TestContainerRealmObject {

    public TestRealmObject object;

    public TestContainerRealmObject(TestRealmObject objectProxy) {
        object = objectProxy;
    }

    public TestRealmObject getObject() {
        return object;
    }
}
