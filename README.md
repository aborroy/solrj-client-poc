# SolrJ Client sample invocations

Sample project to describe how SolrJ Client invocations should be done from `alfresco-repository` for Query and Admin operations.

## Description

This project includes following folders:

* `solrj-client` is an standalone SolrJ Client based in Spring Boot
* `docker` includes four Docker Compose templates for Alfresco Search Services http, https, sharding (http) and sharding (https)


## Using the project

In order to have a working Alfresco Repository with Alfresco Search Services running, starting **Docker** is required.

```sh
$ cd docker/http
$ docker-compose up --build
```

Once Docker is ready, SOLR will be available at:

http://localhost:8083/solr

When using https, SOLR will be available at:

https://localhost:8083/solr

When using Sharing, secondary SOLR will be available at port 8084.

After that, **Maven** project can be built.

```sh
$ cd solrj-client
$ mvn clean package
```

Running the sample project will provide an output similar to following lines.

```sh
$ java -jar target/solrj-client-0.0.1-SNAPSHOT.jar

----------QUERY RESULTS----------
responseHeader:{status=0,QTime=7}
_original_parameters_:{carrot.url=id, spellcheck.collateExtendedResults=true, df=text, spellcheck.maxCollations=3, fq=content.mimetype:'application/x-javascript', spellcheck.maxCollationTries=5, locale=en, hl.qparser=rrafts, defType=afts, spellcheck.maxResultsForSuggest=5, rqq={!rrafts}RERANK_QUERY_FROM_CONTEXT, carrot.outputSubClusters=false, wt=javabin, qt=/afts, carrot.produceSummary=true, start=0, rows=100, version=2, spellcheck.alternativeTermCount=2, spellcheck.extendedResults=false, q=cm:name:'*test*', spellcheck=false, spellcheck.count=5, carrot.title=mltext@m___t@{http://www.alfresco.org/model/content/1.0}title, carrot.snippet=content@s___t@{http://www.alfresco.org/model/content/1.0}content, spellcheck.collate=true, rq={!alfrescoReRank reRankQuery=$rqq reRankDocs=500 scale=true reRankWeight=3}}
_field_mappings_:{}
_date_mappings_:{}
_range_mappings_:{}
_pivot_mappings_:{}
_interval_mappings_:{}
_stats_field_mappings_:{}
_stats_facet_mappings_:{}
_facet_function_mappings_:{}
lastIndexedTx:21
lastIndexedTxTime:1562944460854
txRemaining:0
response:{numFound=2,start=0,docs=[SolrDocument{id=_DEFAULT_!800000000000000d!80000000000001a7, _version_=0, DBID=423}, SolrDocument{id=_DEFAULT_!800000000000000d!80000000000001ab, _version_=0, DBID=427}]}
processedDenies:false

----------ADMIN STATUS RESULTS----------
responseHeader:{status=0,QTime=0}
initFailures:{}
status:{alfresco={name=alfresco,instanceDir=/opt/alfresco-search-services/solrhome/alfresco,dataDir=/opt/alfresco-search-services/data/alfresco/,config=solrconfig.xml,schema=schema.xml,startTime=Mon Jul 15 09:42:59 CEST 2019,uptime=1662502,index={numDocs=897,maxDoc=903,deletedDocs=6,indexHeapUsageBytes=-1,version=62,segmentCount=6,current=true,hasDeletions=true,directory=org.apache.lucene.store.NRTCachingDirectory:NRTCachingDirectory(MMapDirectory@/opt/alfresco-search-services/data/alfresco/index lockFactory=org.apache.lucene.store.NativeFSLockFactory@7c0e1aca; maxCacheMB=48.0 maxMergeSizeMB=4.0),segmentsFile=segments_9,segmentsFileSizeInBytes=475,userData={commitTimeMSec=1562944490055},lastModified=Fri Jul 12 17:14:50 CEST 2019,sizeInBytes=2552871,size=2.43 MB}}}
```

Configuration can be set in `application.properties` file in order to start a different SOLR environment.

```
# SINGLE - http
solr.url=http://localhost:8083/solr
solr.sharding=

# SINGLE - https
# solr.url=https://localhost:8083/solr/alfresco
# solr.sharding=

# SHARDING - http (use local IP for Shard instance)
# solr.url=http://localhost:8083/solr/alfresco
# solr.sharding=http://10.5.1.151:8083/solr/alfresco,http://10.5.1.151:8084/solr/alfresco

# SHARDING - https (use local IP for Shard instance)
# solr.url=https://localhost:8083/solr/alfresco
# solr.sharding=https://10.5.1.151:8083/solr/alfresco,https://10.5.1.151:8084/solr/alfresco

# SSL - Add your local path to Docker keystore and trustore for Alfresco
alfresco.encryption.ssl.truststore.location=/Users/aborroy/Downloads/tmp/https/keystores/alfresco/ssl.truststore
alfresco.encryption.ssl.truststore.passwordFileLocation=/Users/aborroy/Downloads/tmp/https/keystores/alfresco/ssl-truststore-passwords.properties
alfresco.encryption.ssl.truststore.type=JCEKS
alfresco.encryption.ssl.keystore.location=/Users/aborroy/Downloads/tmp/https/keystores/alfresco/ssl.keystore
alfresco.encryption.ssl.keystore.passwordFileLocation=/Users/aborroy/Downloads/tmp/https/keystores/alfresco/ssl-keystore-passwords.properties
alfresco.encryption.ssl.keystore.type=JCEKS
```
