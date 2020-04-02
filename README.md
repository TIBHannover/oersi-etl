Prerequisites: Java, Elasticsearch on [http://localhost:9200](http://localhost:9200)

Set up project
--------------

`git clone https://gitlab.com/oersi/oersi-etl.git`

`cd oersi-etl`

`sh install_snapshots.sh`

Run tests
---------

Tests in `src/test/java`:

`./gradlew check`

Create data
-----------

Setup in `Indexer.main`, process 2 resources, write Elasticsearch bulk file:

`./gradlew run --args='2'`

Index data
----------

Index the Elasticsearch bulk file:

`curl -s -H "Content-Type: application/x-ndjson" -X POST localhost:9200/_bulk --data-binary "@elasticsearch-bulk.ndjson"; echo`

Query data
----------

Query the index:

[http://localhost:9200/oerindex/_search](http://localhost:9200/oerindex/_search)

`curl http://localhost:9200/oerindex/_search | jq`

Delete index
------------

Delete the index:

`curl -X DELETE http://localhost:9200/oerindex; echo`