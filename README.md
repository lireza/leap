## leap
A try to provide Go's concurrency mechanics in Java.

### About
As you may know Go supports CSP style concurrency mechanisms built in to the language. If you are familiar
with CSP you may be agree with its simplicity and productivity. This project is a try to implement
Go mechanics in Java. However with some limitations!

In java we don't have any native Coroutine (Light-weight thread), even Kotlin provides coroutines using
asynchronous callbacks with use of compiler tricks or some Scala libraries provide coroutines using
library features.

The main limitation is calling blocking code in a coroutine (I/O, Locking). Java supports managed blocking
mechanism, maybe will use this mechanism to improve leap to somehow overcome this limitation!

### Examples
On the example bellow a simple coroutine generates an UUID and another coroutine retrieves generated one:
```java
public class Main {

    public static void main(String[] args) throws Exception {
        var channel = make(String.class);

        go(ch -> {
            ch.send(UUID.randomUUID().toString());
        }, channel);

        go(ch -> {
            var uuid = ch.retrieve();
            System.out.println(uuid);
        }, channel);

        Thread.sleep(1000);
    }
}
```

Or here we are generating 1 million UUIDs in a single coroutine, but retrieving from two different coroutines:
```java
public class Main {

    public static void main(String[] args) throws Exception {
        var channel = make(String.class, 10000);

        go(ch -> {
            for (int i = 1; i <= 1_000_000; i++) {
                ch.send(UUID.randomUUID().toString());
            }
        }, channel);

        var latch = new CountDownLatch(1_000_000);

        // 2 coroutines each trying to retrieve 500_000 UUIDs.
        go(outer -> {
            for (int i = 1; i <= 2; i++) {
                go(inner -> {
                    for (int j = 1; j <= 500_000; j++) {
                        var uuid = inner.retrieve();
                        latch.countDown();
                    }
                }, outer);
            }
        }, channel);

        latch.await(2, TimeUnit.SECONDS);
    }
}
```

Another example shows the creation of 100000 coroutines each generate UUID and then countdown the latch:
```java
public class Main {

    public static void main(String[] args) throws Exception {
        var latch = new CountDownLatch(100_000);

        for (int i = 1; i <= 100_000; i++) {
            go(l -> {
                UUID.randomUUID();
                l.countDown();
            }, latch);
        }

        latch.await(2, TimeUnit.SECONDS);
    }
}
```

### Prerequisites to develop and test
- JDK 11 (JavaSE 11)
- Maven 3.6+
