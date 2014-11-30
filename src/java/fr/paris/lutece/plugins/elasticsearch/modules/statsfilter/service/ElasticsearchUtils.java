/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.elasticsearch.modules.statsfilter.service;

import fr.paris.lutece.portal.service.util.AppLogService;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;

import org.elasticsearch.client.Client;

import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.indices.IndexAlreadyExistsException;

/**
 *
 * @author pierre
 */
public class ElasticsearchUtils
{

    public static void createIndex(Client client, String strIndex, String strDocumentType)
    {
        try
        {
            XContentBuilder mappingBuilder = jsonBuilder().startObject().startObject(strDocumentType)
                    .startObject("_timestamp").field("enabled", "true").endObject()
                    .endObject().endObject();
            System.out.println(mappingBuilder.string());

            CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(strIndex);
            createIndexRequestBuilder.addMapping(strDocumentType, mappingBuilder);
            CreateIndexResponse createIndexResponse = createIndexRequestBuilder.execute().actionGet( Constants.TIMEOUT );

            if (createIndexResponse != null && createIndexResponse.isAcknowledged())
            {
                AppLogService.info("Elasticsearch index : '" + strIndex + "' created for document type : '"
                        + strDocumentType + "'");
            }
            else
            {
                AppLogService.info("Elasticsearch failed to create index : '" + strIndex + "' !");
            }

        }
        catch (IndexAlreadyExistsException e)
        {
            AppLogService.info("Elasticsearch index : '" + strIndex + "' already exists !"  );      
        }
        catch (IOException ex)
        {
            AppLogService.error("Error creating Elasticsearch index", ex);
        }
    }

    public static void deleteIndex(Client client, String strIndex)
    {
        DeleteIndexRequestBuilder deleteIndex = client.admin().indices().prepareDelete(strIndex);
        deleteIndex.execute().actionGet();
        AppLogService.info("Elasticsearch index '" + strIndex + "' deleted !");
    }

    public static boolean isIndexExist(Client client, String strIndex)
    {
        ActionFuture<IndicesExistsResponse> exists = client.admin().indices()
                .exists(new IndicesExistsRequest(strIndex));
        IndicesExistsResponse actionGet = exists.actionGet();

        return actionGet.isExists();
    }
}
