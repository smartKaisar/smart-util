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
package com.smartitengineering.util.rest.atom.server;

import java.net.URI;
import java.util.Date;
import javax.ws.rs.core.MediaType;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

public abstract class AbstractResource extends com.smartitengineering.util.rest.server.AbstractResource {

  private final Factory abderaFactory = Abdera.getNewFactory();

  protected Factory getAbderaFactory() {
    return abderaFactory;
  }

  protected Feed getFeed() {
    return getFeed(getUriInfo().getRequestUri().toString(), getUriInfo().getAbsolutePath().toString(), new Date());
  }

  protected Feed getFeed(String title) {
    return getFeed(getUriInfo().getRequestUri().toString(), title, new Date());
  }

  protected Feed getFeed(String title,
                         Date updated) {
    return getFeed(getUriInfo().getRequestUri().toString(), title, updated);
  }

  protected Feed getFeed(String id,
                         String title,
                         Date updated) {
    Feed feed = getBaseFeed();
    feed.setId(id);
    feed.setTitle(title);
    feed.setUpdated(updated);
    return feed;
  }

  protected Feed getBaseFeed() {
    Feed feed = abderaFactory.newFeed();
    feed.addLink(getSelfLink());
    feed.addAuthor(getAuthor());
    return feed;
  }

  protected Link getSelfLink() {
    Link selfLink = abderaFactory.newLink();
    selfLink.setHref(getUriInfo().getRequestUri().toString());
    selfLink.setRel(Link.REL_SELF);
    selfLink.setMimeType(MediaType.APPLICATION_ATOM_XML);
    return selfLink;
  }

  protected Entry getEntry(final String id, final String name, final Date updated, Link... links) {
    Entry entry = getAbderaFactory().newEntry();
    entry.setId(id);
    entry.setTitle(name);
    entry.setUpdated(updated);
    for (Link link : links) {
      entry.addLink(link);
    }
    return entry;
  }

  protected Link getLink(URI uri, String rel, String mimeType) {
    Link link = getAbderaFactory().newLink();
    link.setRel(rel);
    link.setHref(uri.toString());
    link.setMimeType(mimeType);
    return link;
  }

  protected abstract String getAuthor();
}
