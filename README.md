Prerequisites: Java, Elasticsearch on [http://localhost:9200](http://localhost:9200)

Set up project
--------------

`git clone https://gitlab.com/oersi/oersi-etl.git`

`cd oersi-etl`

`sh install_snapshots.sh`

User documentation
==================

Create data
-----------

Setup in `ETL.main`: run all `*.flux` workflows in `data/`, write Elasticsearch bulk file:

`./gradlew run`

Index data
----------

Index the Elasticsearch bulk file:

`curl -s -H "Content-Type: application/x-ndjson" -X POST localhost:9200/_bulk --data-binary "@data/oersi.ndjson"; echo`

Query data
----------

Query the index:

[http://localhost:9200/oerindex/_search](http://localhost:9200/oerindex/_search)

`curl http://localhost:9200/oersi/_search | jq`

Delete index
------------

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