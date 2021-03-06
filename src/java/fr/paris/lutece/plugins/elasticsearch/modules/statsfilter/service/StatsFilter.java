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

import org.elasticsearch.action.index.IndexResponse;

import org.elasticsearch.client.Client;

import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import org.elasticsearch.node.Node;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class StatsFilter implements Filter
{
    private static Boolean _bInit = Boolean.FALSE;
    private Node _node;
    private Client _client;

    @Override
    public void init( FilterConfig fc ) throws ServletException
    {
        _node = nodeBuilder(  ).clusterName( Constants.CLUSTER_NAME ).node(  );
        _client = _node.client(  );
    }

    private void initIndex(  )
    {
        //        synchronized (_bInit)
        {
            if ( !_bInit )
            {
                if ( ElasticsearchUtils.isIndexExist( _client, Constants.INDEX_NAME ) )
                {
                    AppLogService.info( "Elasticsearch index '" + Constants.INDEX_NAME +
                        "' exists, should delete and recreate it !" );
                    ElasticsearchUtils.deleteIndex( _client, Constants.INDEX_NAME );
                    ElasticsearchUtils.createIndex( _client, Constants.INDEX_NAME, Constants.DOCUMENT_TYPE );
                }
                else
                {
                    AppLogService.info( "Elasticsearch index '" + Constants.INDEX_NAME + "' doesn't exist, should create it !" );
                    ElasticsearchUtils.createIndex( _client, Constants.INDEX_NAME, Constants.DOCUMENT_TYPE );
                }

                _bInit = Boolean.TRUE;
            }
        }
    }

    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
        throws IOException, ServletException
    {
        HttpServletRequest r = (HttpServletRequest) request;
        System.out.println( r.getRequestURI(  ) + r.getQueryString(  ) );

        initIndex();
        XContentBuilder jsonSource = jsonBuilder(  ).startObject(  ).field( "method", r.getMethod(  ) )
                                   .field( "uri", r.getRequestURI(  ) ).field( "query_string", r.getQueryString(  ) )
                                   .endObject(  );

        IndexResponse ir = _client.prepareIndex( Constants.INDEX_NAME, Constants.DOCUMENT_TYPE ).setSource( jsonSource.string(  ) ).execute(  )
                                  .actionGet(  );

        chain.doFilter( request, response );
    }

    @Override
    public void destroy(  )
    {
        _node.close(  );
    }
}
