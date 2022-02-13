How to run:

* Using Java 18,
* Clone https://github.com/AugustNagro/vert.x/tree/await-bug
* On branch await-bug, do `mvn clean install -DskipTests`
* In this project run `mvn compile exec:java`
* This will run class com.augustnagro.bodyhandlerbug.Main which demos the issue

The request context body is always null, despite the handler execution order being correct.
