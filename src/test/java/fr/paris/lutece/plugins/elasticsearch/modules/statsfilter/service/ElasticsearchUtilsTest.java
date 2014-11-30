/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.elasticsearch.modules.statsfilter.service;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import org.junit.Test;

/**
 *
 * @author pierre
 */
public class ElasticsearchUtilsTest
{

    /**
     * Test of createIndex method, of class ElasticsearchUtils.
     */
    @Test
    public void testCreateIndex()
    {
        System.out.println("createIndex");
        Node node = nodeBuilder().clusterName(Constants.CLUSTER_NAME).node();
        Client client = node.client();
        String strIndex = Constants.INDEX_NAME;
        String strDocumentType = Constants.DOCUMENT_TYPE;
        ElasticsearchUtils.createIndex(client, strIndex, strDocumentType);
//        node.close();
    }

    /**
     * Test of deleteIndex method, of class ElasticsearchUtils.
     */
    @Test
    public void testDeleteIndex()
    {
        System.out.println("deleteIndex");
        Node node = nodeBuilder().clusterName(Constants.CLUSTER_NAME).node();
        Client client = node.client();
        String strIndex = Constants.INDEX_NAME;
        ElasticsearchUtils.deleteIndex(client, strIndex);
//        node.close();
    }

    /**
     * Test of isIndexExist method, of class ElasticsearchUtils.
     */
    @Test
    public void testIsIndexExist()
    {
        System.out.println("isIndexExist");
        Node node = nodeBuilder().clusterName(Constants.CLUSTER_NAME).node();
        Client client = node.client();
        String strIndex = Constants.INDEX_NAME;
        boolean result = ElasticsearchUtils.isIndexExist(client, strIndex);
        node.close();
    }

}
