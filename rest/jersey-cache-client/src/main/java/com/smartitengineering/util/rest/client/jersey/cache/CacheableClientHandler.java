/*
 * This is a utility project for wide range of applications
 *
 * Copyright (C) 2010  Imran M Yousuf (imyousuf@smartitengineering.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  10-1  USA
 */
package com.smartitengineering.util.rest.client.jersey.cache;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.TerminatingClientHandler;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.core.util.ReaderWriter;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.httpclient.HttpClient;
import org.codehaus.httpcache4j.HTTPMethod;
import org.codehaus.httpcache4j.HTTPRequest;
import org.codehaus.httpcache4j.HTTPResponse;
import org.codehaus.httpcache4j.Header;
import org.codehaus.httpcache4j.Headers;
import org.codehaus.httpcache4j.cache.CacheStorage;
import org.codehaus.httpcache4j.cache.HTTPCache;
import org.codehaus.httpcache4j.cache.MemoryCacheStorage;
import org.codehaus.httpcache4j.client.HTTPClientResponseResolver;

/**
 *
 * @author imyousuf
 */
public class CacheableClientHandler
    extends TerminatingClientHandler {

  private final HTTPCache cache;

  public CacheableClientHandler(HttpClient httpClient) {
    this(httpClient, new MemoryCacheStorage());
  }

  public CacheableClientHandler(HttpClient httpClient,
                                CacheStorage storage) {
    cache = new HTTPCache(storage, new HTTPClientResponseResolver(httpClient));
  }

  @Override
  public ClientResponse handle(ClientRequest cr)
      throws ClientHandlerException {
    HTTPMethod method = HTTPMethod.valueOf(cr.getMethod());
    HTTPResponse cachedResponse = cache.doCachedRequest(new HTTPRequest(cr.getURI(), method));
    Headers headers = cachedResponse.getHeaders();
    InBoundHeaders inBoundHeaders = getInBoundHeaders(headers);
    final InputStream entity = getEntityStream(cachedResponse);
    ClientResponse response = new ClientResponse(cachedResponse.getStatus().getCode(), inBoundHeaders, entity,
          getMessageBodyWorkers());
      return response;
  }


  public HTTPCache getCache() {
    return cache;
  }

  //HUH?
  protected InputStream getEntityStream(HTTPResponse cachedResponse) {
    final InputStream entity;
    if (cachedResponse.hasPayload()) {
      final InputStream inputStream = cachedResponse.getPayload().getInputStream();
      if (inputStream.markSupported()) {
        entity = inputStream;
      }
      else {
        entity = new BufferedInputStream(inputStream, ReaderWriter.BUFFER_SIZE);
      }
    }
    else {
      entity = new ByteArrayInputStream(new byte[0]);
    }
    return entity;
  }

  protected InBoundHeaders getInBoundHeaders(Headers headers) {
    InBoundHeaders inBoundHeaders = new InBoundHeaders();
    for (Header header : headers) {
      List<String> list = inBoundHeaders.get(header.getName());
      if (list == null) {
        list = new ArrayList<String>();
      }
      list.add(header.getValue());
      inBoundHeaders.put(header.getName(), list);
    }
    return inBoundHeaders;
  }
}