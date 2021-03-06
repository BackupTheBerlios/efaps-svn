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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.InputSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.efaps.webdav.WebDAVRequest;
import org.efaps.webdav.resource.AbstractResource;
import org.efaps.webdav.resource.CollectionResource;
import org.efaps.webdav.resource.SourceResource;

/**
 * @see RFC 2518 - 8.1 PROPFIND
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class PropFindMethod extends AbstractMethod  {

  /////////////////////////////////////////////////////////////////////////////
  
  final static String DAV_XML_NAMESPACE = "DAV:";
  

  /**
   * FIND_BY_PROPERTY - Specify a property mask.
   * FIND_ALL_PROP - Display all properties.
   * FIND_PROPERTY_NAMES - Return property names.
   */
  static enum FindProperty {FIND_BY_PROPERTY, FIND_ALL_PROP, FIND_PROPERTY_NAMES};


  public void run(final WebDAVRequest _request, 
                  final HttpServletResponse _response) throws IOException, ServletException  {
try  {
Writer writer = _response.getWriter();

    FindProperty type = FindProperty.FIND_ALL_PROP;
    Node propNode = null;


    WebDAVRequest.DepthHeader depthHeader = _request.getDepthHeader();



if (_request.isInputAvailable())  {
try {
      Document document = _request.getDocument();

      // Get the root element of the document
      Element rootElement = document.getDocumentElement();
if (rootElement.getNamespaceURI().equals(DAV_XML_NAMESPACE)
    && rootElement.getLocalName().equals("propfind"))  {


      NodeList childList = rootElement.getChildNodes();

      for (int i=0; i < childList.getLength(); i++) {
        Node currentNode = childList.item(i);
        switch (currentNode.getNodeType()) {
        case Node.TEXT_NODE:
          break;
        case Node.ELEMENT_NODE:
          if (currentNode.getLocalName().equals("prop")) {
              type = FindProperty.FIND_BY_PROPERTY;
              propNode = currentNode;
          }
          if (currentNode.getLocalName().equals("propname")) {
              type = FindProperty.FIND_PROPERTY_NAMES;
          }
          if (currentNode.getLocalName().equals("allprop")) {
              type = FindProperty.FIND_ALL_PROP;
          }
          break;
        }
      }
} else  {
  _response.setStatus(Status.BAD_REQUEST.code);
  return;
}
    } catch(Exception e) {
_response.setStatus(Status.BAD_REQUEST.code);
return;
        // Most likely there was no content : we use the defaults.
        // TODO : Enhance that !
    }
}

    // Properties which are to be displayed.
    DAVProperty[] properties = null;

    if (type == FindProperty.FIND_BY_PROPERTY) {
      List<DAVProperty> propertiesList = new ArrayList<DAVProperty>();
      NodeList childList = propNode.getChildNodes();

      for (int i=0; i < childList.getLength(); i++) {
        Node curNode = childList.item(i);
        if ((curNode.getNodeType() == Node.ELEMENT_NODE)
            && (curNode.getNamespaceURI().equals(DAV_XML_NAMESPACE)))  {
          try  {
            propertiesList.add(DAVProperty.valueOf(curNode.getLocalName()));
          } catch (IllegalArgumentException e)  {
// TODO property is not found =>  404 (Not Found)
          }
        }
      }

      properties = propertiesList.toArray(new DAVProperty[propertiesList.size()]);
    } else if (type == FindProperty.FIND_ALL_PROP)  {
      properties = DAVProperty.values();
    }
System.out.println("properties="+properties);

writeXMLHeader(writer);
writeElement(writer, "multistatus xmlns=\"DAV:\"", OPENING);

  AbstractResource resource  = null;

  resource = getResource4Path(_request.getPathInfo());
  if (resource == null)  {
// was fuer status muss da zurueckgegeben werden????
System.out.println("did not found " + _request.getPathInfo());
  } else  {
    write(writer, _request.getRequestURI(), properties, resource);
  }

  if ((depthHeader == WebDAVRequest.DepthHeader.depth1)
      && (resource instanceof CollectionResource))  {
    
    List < AbstractResource > subs = ((CollectionResource) resource).getSubs();
    
    for (AbstractResource subResource : subs)  {
      write(writer, 
            _request.getRequestURI() + "/" + subResource.getName(), 
            properties, 
            subResource);
    }
  }
writeElement(writer, "multistatus", CLOSING);
} catch (Exception e)  {
  e.printStackTrace();
}



  }

  String makeResourceName(String _path, String _name)  {
    StringBuffer ret = new StringBuffer(_path.length() + _name.length() + 1);
    ret.append(_path);
    if (!_path.endsWith("/"))  {
      ret.append("/");
    }
    ret.append(_name);
    return ret.toString();
  }


  protected void write(final Writer _writer, 
                       final String _resourceName, 
                       final DAVProperty[] _properties, 
                       final AbstractResource _resource) throws IOException  {
    writeElement(_writer, "response", OPENING);

    writeElement(_writer, "href", OPENING);
    writeText(_writer, _resourceName);
    writeElement(_writer, "href", CLOSING);

    writeElement(_writer, "propstat", OPENING);

    writeElement(_writer, "prop", OPENING);

    for (DAVProperty property : _properties)  {
      writeText(_writer, property.makeXML(_resource));
    }

    writeElement(_writer, "prop", CLOSING);

    writeElement(_writer, "status", OPENING);
    writeText(_writer, "HTTP/1.1 " + HttpServletResponse.SC_OK + " OK");
    writeElement(_writer, "status", CLOSING);

    writeElement(_writer, "propstat", CLOSING);
    writeElement(_writer, "response", CLOSING);
  }

/*
property.equals("supportedlock"
String supportedLocks = "<lockentry>"
        + "<lockscope><exclusive/></lockscope>"
        + "<locktype><write/></locktype>"
        + "</lockentry>" + "<lockentry>"
        + "<lockscope><shared/></lockscope>"
        + "<locktype><write/></locktype>"
        + "</lockentry>";
    writeElement(_writer, "supportedlock", OPENING);
    writeText(_writer, supportedLocks);
    writeElement(_writer, "supportedlock", CLOSING);
*/
}