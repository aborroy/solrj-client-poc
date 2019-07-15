package org.alfresco.solrj.client;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.alfresco.encryption.AlfrescoKeyStore;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HttpSolrClientService
{
    
    @Value("${solr.url}")
    String solrUrl;
    
    @Value("${alfresco.encryption.ssl.truststore.location}")
    String solrAlfrescoTruststore;
    @Value("${alfresco.encryption.ssl.truststore.passwordFileLocation}")
    String solrAlfrescoTruststorePasswordFile;
    @Value("${alfresco.encryption.ssl.truststore.type}")
    String solrAlfrescoTruststoreType;
    @Value("${alfresco.encryption.ssl.keystore.location}")
    String solrAlfrescoKeystore;
    @Value("${alfresco.encryption.ssl.keystore.passwordFileLocation}")
    String solrAlfrescoKeystorePasswordFile;
    @Value("${alfresco.encryption.ssl.keystore.type}")
    String solrAlfrescoKeystoreType;
    
    SolrClient solrAlfrescoClient;
    SolrClient solrArchiveClient;
    SolrClient solrAdminClient;
    
    @PostConstruct
    public void init() throws Exception
    {
        if (solrUrl.startsWith("https"))
        {
            SSLContext context = getSSLContext(
                    solrAlfrescoTruststore, 
                    solrAlfrescoTruststorePasswordFile, 
                    solrAlfrescoTruststoreType,
                    solrAlfrescoKeystore, 
                    solrAlfrescoKeystorePasswordFile, 
                    solrAlfrescoKeystoreType);
            SSLContext.setDefault(context);
            HttpClientUtil.resetConfigurers();
        }
        
        solrAlfrescoClient = new HttpSolrClient.Builder(solrUrl + "/alfresco").build();
        solrArchiveClient = new HttpSolrClient.Builder(solrUrl + "/archive").build();
        solrAdminClient = new HttpSolrClient.Builder(solrUrl).build();
    }

    public static SSLContext getSSLContext(
            String truststorePath, String truststorePasswordFile, String truststoreType, 
            String keystorePath, String keystorePasswordFile, String keystoreType)
            throws Exception
    {
        
        // Read passwords from password files
        Properties prop = new Properties();
        prop.load(new FileInputStream(keystorePasswordFile));
        String keystorePassword = prop.get(AlfrescoKeyStore.KEY_KEYSTORE_PASSWORD).toString();
        prop = new Properties();
        prop.load(new FileInputStream(truststorePasswordFile));
        String truststorePassword = prop.get(AlfrescoKeyStore.KEY_KEYSTORE_PASSWORD).toString();
        
        // Create SSLContext
        FileInputStream tsf = new FileInputStream(truststorePath);
        FileInputStream ksf = new FileInputStream(keystorePath);
        SSLContext ctx = SSLContext.getInstance("SSL");

        KeyStore ts = KeyStore.getInstance(keystoreType);
        ts.load(tsf, truststorePassword.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);

        KeyStore ks = KeyStore.getInstance(truststoreType);
        ks.load(ksf, keystorePassword.toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, keystorePassword.toCharArray());

        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        return ctx;
        
    }
    
    public SolrClient getSolrAlfrescoClient()
    {
        return solrAlfrescoClient;
    }
    
    public SolrClient getSolrArchiveClient()
    {
        return solrArchiveClient;
    }
    
    public SolrClient getSolrAdminClient()
    {
        return solrAdminClient;
    }
    
}
