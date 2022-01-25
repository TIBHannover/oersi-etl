Prerequisites: Java, Elasticsearch on [http://localhost:9200](http://localhost:9200)

Set up project
--------------

`git clone https://gitlab.com/oersi/oersi-etl.git`

`cd oersi-etl`

User documentation
==================

The ETL workflows are based on Metafacture, see https://metafacture.org

Run workflows
-------------

Pass a directory name to run all `*.flux` workflows in that directory (or pass a single `*.flux` file), e.g.:

`./gradlew run --args 'data/production/openRub'`

This will run all `*.flux` workflows in `data/production/openRub`.

To use a `*.properties` file from another directory for workflow variables, pass its location as the second argument:

`./gradlew run --args 'data/production/openRub data/production/oersi.properties'`

A `oersi.properties` file in the same location as the `*.flux` file is picked up automatically.

Write to backend API
--------------------

By default a local `oersi-setup` with `vagrant up` is expected:

`cd ../oersi-setup ; vagrant up ; cd ../oersi-etl`

Run the workflows in `data/production/openRub`:

`./gradlew run --args 'data/production/openRub data/production/oersi.properties'`

Check the responses in `*-response.json`, search in the backend, e.g.:

[http://192.168.98.115/resources?provider=["OpenRub"]](http://192.168.98.115/resources/?provider=%5B%22OpenRub%22%5D)

Write to elasticsearch
----------------------

### Create data

Run the workflows that write an Elasticsearch bulk file:

`./gradlew run --args 'data/experimental'`

### Index data

Index the Elasticsearch bulk file:

`curl -s -H "Content-Type: application/x-ndjson" -X POST localhost:9200/_bulk --data-binary "@data/oersi.ndjson"; echo`

### Query data

Query the index:

[http://localhost:9200/oersi/_search](http://localhost:9200/oersi/_search)

`curl http://localhost:9200/oersi/_search | jq`

### Delete index

Delete the index:

`curl -X DELETE http://localhost:9200/oersi; echo`

Developer documentation
=======================

Run tests
---------

Tests in `src/test/java`:

`./gradlew check`

Coverage
--------

Generate coverage report in `build/reports/jacoco/`:

`./gradlew jacocoTestReport`

SonarQube
---------

Generate SONARCLOUD_TOKEN at [https://sonarcloud.io/account/security](https://sonarcloud.io/account/security)

Set up a `~/.gradle/gradle.properties` file:

```
systemProp.sonar.host.url=https://sonarcloud.io
systemProp.sonar.login=<SONARCLOUD_TOKEN>
```

Run SonarQube analysis on sonarcloud.io:

`./gradlew sonarqube`

See results at [https://sonarcloud.io/dashboard?id=oersi_oersi-etl](https://sonarcloud.io/dashboard?id=oersi_oersi-etl)
