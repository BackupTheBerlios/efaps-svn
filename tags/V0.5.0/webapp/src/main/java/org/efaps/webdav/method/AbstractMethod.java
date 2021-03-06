/*
 * Copyright 2006 The eFaps Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.webdav.method;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.efaps.webdav.AbstractResource;
import org.efaps.webdav.CollectionResource;
import org.efaps.webdav.SourceResource;
import org.efaps.webdav.TeamCenterWebDAVImpl;

/**
 * @author tmo
 * @version $Id$
 * @todo description
 */
public abstract class AbstractMethod  {


  /**
   * Simple date format for the creation date ISO representation (partial).
   */
  private static final SimpleDateFormat creationDateFormat
                        = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  {
    creationDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
  }

  /**
   * HTTP date format used to format last modified dates.
   */
  private static final SimpleDateFormat modifiedDateFormat
            = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", 
                                   Locale.US);  {
    modifiedDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
  }


  /**
   * The enum defines the DAV properties described in RFC2518 chapter 13.
   */
  public enum DAVProperty  {
    /**
     * Records the time and date the resource was created.
     */
    creationdate {
      String makeXML(Object _creationDate) {
System.out.println("_creationDate="+_creationDate);
        StringBuffer ret = new StringBuffer();
        ret.append('<').append(name()).append(">")
           .append(creationDateFormat.format(_creationDate))
           .append("</").append(name()).append(">");
        return ret.toString();
      }
    },

    /**
     * Provides a name for the resource that is suitable for presentation to a
     * user.
     */
    displayname {
      String makeXML(Object _name) {
        StringBuffer ret = new StringBuffer();
        ret.append('<').append(name()).append(">")
           .append("<![CDATA[").append(_name).append("]]>")
           .append("</").append(name()).append(">");
        return ret.toString();
      }
    },

    /**
     * Contains the Content-Language header returned by a GET without accept
     * headers
     */
    getcontentlanguage {
    },

    /**
     * Contains the Content-Length header returned by a GET without accept
     * headers.
     */
    getcontentlength {
    },

    /**
     * Contains the Content-Type header returned by a GET without accept
     * headers.
     */
    getcontenttype {
    },

    /**
     * Contains the ETag header returned by a GET without accept headers.
     */
    getetag {
    },

    /**
     * Contains the Last-Modified header returned by a GET method without
     * accept headers.
     */
    getlastmodified {
      String makeXML(Object _modifiedDate) {
System.out.println("_modifiedDate="+_modifiedDate);
        StringBuffer ret = new StringBuffer();
        ret.append('<').append(name()).append(">")
           .append(modifiedDateFormat.format(_modifiedDate))
           .append("</").append(name()).append(">");
        return ret.toString();
      }
    },

    /**
     * Describes the active locks on a resource.
     */
    lockdiscovery {
    },

    /**
     * Specifies the nature of the resource.
     */
    resourcetype {
    },

    /**
     * The destination of the source link identifies the resource that contains
     * the unprocessed source of the link's source.
     */
    source {
    },

    /**
     * To provide a listing of the lock capabilities supported by the resource.
     */
    supportedlock {
    };


    String makeXML(Object _text) {
      StringBuffer ret = new StringBuffer();
      ret.append('<').append(name()).append(">")
         .append(_text)
         .append("</").append(name()).append(">");
      return ret.toString();
    }
  }


  public enum DepthHeader  {
    /** 
     * The method is to be applied only to the resource. 
     */
    depth0,
    /** 
     * The method is to be applied to the resource and its immediate children. 
     */
    depth1,
    /** 
     * The method is to be applied to the resource and all its progeny.
     */
    infity;
  }

  public enum Status {

    CONFLICT(HttpServletResponse.SC_CONFLICT, "Conflict"),
    CREATED(HttpServletResponse.SC_CREATED, "Created"),
    FORBIDDEN(HttpServletResponse.SC_FORBIDDEN, "Forbidden"),
    NO_CONTENT(HttpServletResponse.SC_NO_CONTENT, "No Content");

    /**
     * The variable stores the code number of the status flag.
     */
    public final int code;

    /**
     * The variable stores the text to the code of the status flag.
     */
    public final String text;

    private Status(final int _code, final String _text)  {
      this.code = _code;
      this.text = _text;
    }
  }


protected TeamCenterWebDAVImpl webDAVImpl = new TeamCenterWebDAVImpl();


  public DepthHeader getDepthHeader(final HttpServletRequest _request)  {
    DepthHeader ret = DepthHeader.infity;
    
    String depthStr = _request.getHeader("Depth");
    if (depthStr != null)  {
      depthStr = depthStr.trim();
      if ("0".equals(depthStr))  {
        ret = DepthHeader.depth0;
      } else if ("1".equals(depthStr))  {
        ret = DepthHeader.depth1;
      }
    }
    return ret;
  }

/*

  if (property.equals("creationdate")) {
    writeProperty(_writer, null, null, "creationdate", creationDateFormat.format(_created));
  } else if (property.equals("displayname")) {
      writeElement(_writer, null, null, "displayname", OPENING);
      writeData(_writer, _resourceName);
      writeElement(_writer, null, null, "displayname", CLOSING);
  } else if (property.equals("getcontentlanguage")) {
      writeElement(_writer, null, null, "getcontentlanguage", NO_CONTENT);
  } else if (property.equals("getcontentlength")) {
      writeProperty(_writer, null, null, "getcontentlength", "0");
  } else if (property.equals("getcontenttype")) {
      writeProperty(_writer, null, null, "getcontenttype", "");
  } else if (property.equals("getetag")) {
      writeProperty(_writer, null, null, "getetag", "");
  } else if (property.equals("getlastmodified")) {
    writeProperty(_writer, null, null, "getlastmodified", modifedFormat.format(_modified));
  } else if (property.equals("resourcetype")) {
      writeProperty(_writer, null, null, "resourcetype", COLLECTION_TYPE);
  } else if (property.equals("source")) {
    writeProperty(_writer, null, null, "source", "");
  } else if (property.equals("supportedlock")) {
    String supportedLocks = "<lockentry>"
        + "<lockscope><exclusive/></lockscope>"
        + "<locktype><write/></locktype>"
        + "</lockentry>" + "<lockentry>"
        + "<lockscope><shared/></lockscope>"
        + "<locktype><write/></locktype>"
        + "</lockentry>";
    writeElement(_writer, null, null, "supportedlock", OPENING);
    writeText(_writer, supportedLocks);
    writeElement(_writer, null, null, "supportedlock", CLOSING);
  } else if (property.equals("lockdiscovery")) {
//      if (!generateLockDiscovery(path, generatedXML))
//          propertiesNotFound.addElement(property);
  } else {
//      propertiesNotFound.addElement(property);
  }
*/
  abstract public void run(final HttpServletRequest _request, HttpServletResponse _response) throws IOException, ServletException;

  /////////////////////////////////////////////////////////////////////////////

  protected AbstractResource getResource4Path(final String _uri)  {
    String[] uri = _uri.toString().split("/");
    
    AbstractResource resource = null;
    CollectionResource collection = null;
    if (uri.length > 2)  {
      collection = getCollection(uri.length - 2, uri);
    }
    if ((uri.length == 2) || (collection != null))  {
      resource = getCollection(collection, uri[uri.length - 1]);
      if (resource == null)  {
        resource = getSource(collection, uri[uri.length - 1]);
      }
    }
    return resource;
  }

  protected CollectionResource getCollection4ParentPath(final String _uri)  {
    CollectionResource collection = null;
    String[] uri = _uri.toString().split("/");
    
    if (uri.length > 2)  {
      collection = getCollection(uri.length - 2, uri);
    }
    return collection;
  }

  protected CollectionResource getCollection(final int _endIndex, 
                                             final String[] _uri)  {

    CollectionResource collection = null;
    for (int i = 1; 
         (i <= _endIndex) && ((collection != null) || (i == 1)); 
         i++)  {
      collection = getCollection(collection, _uri[i]);
    }

    return collection;
  }

  protected CollectionResource getCollection(final CollectionResource _collection, 
                                             final String _name)  {
    return this.webDAVImpl.getCollection(_collection, _name);
  }
  
  protected SourceResource getSource(final CollectionResource _collection, 
                                     final String _name)  {
    return this.webDAVImpl.getSource(_collection, _name);
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
   * Return JAXP document builder instance.
   */
  protected DocumentBuilder getDocumentBuilder() throws ServletException {
    DocumentBuilder documentBuilder = null;
    DocumentBuilderFactory documentBuilderFactory = null;
    try {
      documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch(ParserConfigurationException e) {
throw new ServletException("webdavservlet.jaxpfailed");
//      throw new ServletException
//          (sm.getString("webdavservlet.jaxpfailed"));
    }
    return documentBuilder;
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
   * Opening tag.
   */
  public static final int OPENING = 0;


  /**
   * Closing tag.
   */
  public static final int CLOSING = 1;


  /**
   * Element with no content.
   */
  public static final int NO_CONTENT = 2;

  /**
   * Write text.
   *
   * @param text Text to append
   */
  public void writeText(Writer _writer, String text) throws IOException  {
System.out.println(text);
    _writer.write(text);
  }

  /**
   * Write an element.
   *
   * @param namespace Namespace abbreviation
   * @param namespaceInfo Namespace info
   * @param name Element name
   * @param type Element type
   */
  public void writeElement(Writer _writer, String name, int type) throws IOException  {
    switch (type) {
    case OPENING:
      writeText(_writer, "<" + name + ">");
      break;
    case CLOSING:
      writeText(_writer, "</" + name + ">\n");
      break;
    case NO_CONTENT:
    default:
      writeText(_writer, "<" + name + "/>");
      break;
    }
  }

  /**
   * Write XML Header.
   */
  public void writeXMLHeader(final Writer _writer) throws IOException  {
    _writer.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
  }
}
