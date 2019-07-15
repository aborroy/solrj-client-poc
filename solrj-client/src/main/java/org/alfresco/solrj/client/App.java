package org.alfresco.solrj.client;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.ShardParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner
{

    private static Logger log = LoggerFactory.getLogger(App.class);
    
    @Value("${solr.sharding}")
    String solrSharding;
    
    @Autowired
    HttpSolrClientService httpSolrClientService;
    
    public static void main(String[] args)
    {
        SpringApplication.run(App.class, args);
    }

    public void run(String... args) throws Exception
    {
        
        // SOLR QUERY, to be used from SolrQueryHTTPClient (alfresco-repository)
        SolrClient solrClient = httpSolrClientService.getSolrAlfrescoClient();
        
        SolrQuery query = new SolrQuery();
        
        // url.append(handler)
        query.setRequestHandler("/afts");
        // url.append("&q=")
        query.setQuery("cm:name:'*test*'");
        // url.append("&fq=")
        query.setFilterQueries("content.mimetype:'application/x-javascript'");
        // url.append("&start=")
        query.setStart(0);
        // url.append("&rows=")
        query.setRows(100);
        // url.append("&df=")
        query.set(CommonParams.DF, "text");
        // url.append("&locale=")
        query.set("locale", "en");
        
        // url.append("&shards=")
        if (!solrSharding.equals(""))
        {
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.add(ShardParams.SHARDS, solrSharding);
            query.add(params);
            log.info("Added shards parameter: " + solrSharding);
        }
        
        // TODO Response should be transformed to JSON (response.getBeans(...) ?)
        QueryResponse response = solrClient.query(query);
        log.info("----------QUERY RESULTS----------");
        response.getResponse().forEach((r) -> {
            log.info(r.getKey() + ":" + r.getValue());
        });
        
        
        // SOLR ADMIN, to be used from SolrAdminHTTPClient (alfresco-repository)
        SolrClient solrAdminClient = httpSolrClientService.getSolrAdminClient();
        
        CoreAdminRequest request = new CoreAdminRequest(); 
        request.setAction(CoreAdminAction.STATUS); 
        request.setCoreName("alfresco"); 

        request.setIndexInfoNeeded(true); 
        CoreAdminResponse adminResponse = request.process(solrAdminClient);
        log.info("----------ADMIN STATUS RESULTS----------");
        adminResponse.getResponse().forEach((r) -> {
            log.info(r.getKey() + ":" + r.getValue());
        });
        
        
        // EXIT
        System.exit(0);
        
    }
    
}
