# RealmSupportForGson
[![Download](https://api.bintray.com/packages/truenight/maven/gson-realm/images/download.svg)](https://bintray.com/truenight/maven/gson-realm/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.support/gson-realm/badge.svg)](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.support/gson-realm)
[![Licence](https://img.shields.io/badge/Licence-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Support for serializing RealmObject with Gson

# Overview

When you try to serialize ``RealmObject`` taken from database (Proxy) you will get ``StackOverflowError`` the library resolves that problem.

# Installation

Add dependency to your `build.gradle` file:

```groovy
dependencies {
    compile 'xyz.truenight.support:gson-realm:1.2.0'
}
```

or to your `pom.xml` if you're using Maven:

```xml
<dependency>
  <groupId>xyz.truenight.support</groupId>
  <artifactId>gson-realm</artifactId>
  <version>1.2.0</version>
  <type>pom</type>
</dependency>
```
# Usage

## Creating instance

```java
  // similar to new Gson() but with RealmObject serializing support
  Gson gson = RealmSupportGsonFactory.create();
  gson.toJson(someRealmObjectProxy);
```
## Creating instance with ``GsonBuilder``

```java
  // similar to new GsonBuilder().registerTypeAdapter(SomeRealmObject.class, new SomeRealmObjectAdapter()).create()
  Gson gson = RealmSupportGsonFactory.create(new GsonBuilder()
                .registerTypeAdapter(SomeRealmObject.class, new SomeRealmObjectAdapter()));
  gson.toJson(someRealmObjectProxy);
```

## Specifying instance of ``Realm``

By default library use ``Realm`` instance obtained by ``Realm.getDefaultInstance()`` but you can specify the way to get instance:

```java
  Gson gson = RealmSupportGsonFactory.create(new RealmHook() {
      @Override
      public Realm instance() {
          return Realm.getInstance(myRealmConfiguration);
      }
  });
  gson.toJson(someRealmObjectProxy);
```

