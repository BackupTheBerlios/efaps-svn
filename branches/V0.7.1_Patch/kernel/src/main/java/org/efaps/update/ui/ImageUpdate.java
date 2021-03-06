/*
 * Copyright 2003-2007 The eFaps Team
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

package org.efaps.update.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.SAXException;

import org.efaps.db.Checkin;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.update.AbstractUpdate;
import org.efaps.util.EFapsException;

/**
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class ImageUpdate extends AbstractUpdate  {

  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Logging instance used to give logging information of this class.
   */
  private final static Log LOG = LogFactory.getLog(ImageUpdate.class);

  /////////////////////////////////////////////////////////////////////////////
  // constructors

  /**
   *
   */
  public ImageUpdate() {
    super("Admin_UI_Image", null);
  }

  /////////////////////////////////////////////////////////////////////////////
  // instance methods

  /**
   * Sets the root path in which the image file is located. The value is set
   * for each single definition of the image update.
   *
   * @param _root   name of the path where the image file is located
   */
  protected void setRoot(final String _root) {
    for (DefinitionAbstract def : getDefinitions()) {
      ((ImageDefinition) def).setRoot(_root);
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  // static methods

  public static ImageUpdate readXMLFile(final URL _url)  {
    ImageUpdate update = null;
    try  {
      Digester digester = new Digester();
      digester.setValidating(false);
      digester.addObjectCreate("ui-image", ImageUpdate.class);

      digester.addCallMethod("ui-image/uuid", "setUUID", 1);
      digester.addCallParam("ui-image/uuid", 0);

      digester.addObjectCreate("ui-image/definition", ImageDefinition.class);
      digester.addSetNext("ui-image/definition", "addDefinition");

      digester.addCallMethod("ui-image/definition/version", "setVersion", 4);
      digester.addCallParam("ui-image/definition/version/application", 0);
      digester.addCallParam("ui-image/definition/version/global", 1);
      digester.addCallParam("ui-image/definition/version/local", 2);
      digester.addCallParam("ui-image/definition/version/mode", 3);
      
      digester.addCallMethod("ui-image/definition/name", "setName", 1);
      digester.addCallParam("ui-image/definition/name", 0);

      digester.addCallMethod("ui-image/definition/property", "addProperty", 2);
      digester.addCallParam("ui-image/definition/property", 0, "name");
      digester.addCallParam("ui-image/definition/property", 1);

      digester.addCallMethod("ui-image/definition/file", "setFile", 1);
      digester.addCallParam("ui-image/definition/file", 0);

      update = (ImageUpdate) digester.parse(_url);
      
      if (update != null)  {
        String urlStr = _url.toString();
        int i = urlStr.lastIndexOf("/");
        urlStr = urlStr.substring(0, i + 1);
        update.setRoot(urlStr);
        update.setURL(_url);
      }
    } catch (IOException e) {
      LOG.error(_url.toString() + " is not readable", e);
    } catch (SAXException e) {
      LOG.error(_url.toString() + " seems to be invalide XML", e);
    }
    return update;
  }

  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////
  // class for the definitions

  public static class ImageDefinition extends DefinitionAbstract {

    /** Name of the Image file (incl. the path) to import. */
    private String file = null;

    /** Name of the root path used to initialise the path for the image. */
    private String root = null;

    ///////////////////////////////////////////////////////////////////////////
    // instance methods

    /**
     * Updates / creates the instance in the database. Uses
     * {@link AbstractUpdate.updateInDB} for the update. If a file name is
     * given, this file is checked in the created image instance.
     *
     * @param _instance     instance to update (or null if instance is to
     *                      create)
     * @param _allLinkTypes
     * @param _insert       insert instance (if new instance is to create)
     */
    public Instance updateInDB(final Instance _instance,
                           final Set < Link > _allLinkTypes,
                           final Insert _insert) throws EFapsException, Exception  {

      Instance instance = super.updateInDB(_instance, _allLinkTypes, _insert);

      if (this.file != null)  {
        InputStream in = new URL(this.root + this.file).openStream();
        Checkin checkin = new Checkin(instance);
        checkin.executeWithoutAccessCheck(this.file, 
                                          in, 
                                          in.available());
        in.close();
      }
      return instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    // instance getter / setter methods

    /**
     * This is the setter method for instance variable {@link #file}.
     *
     * @param _number new value for instance variable {@link #file}
     * @see #file
     */
    public void setFile(final String _file)  {
      this.file = _file;
    }
    
    /**
     * This is the setter method for instance variable {@link #root}.
     * 
     * @param _root   new value for instance variable {@link #root}
     * @see #root
     */
    public void setRoot(final String _root) {
      this.root = _root;
    }

    /**
     * Returns a string representation with values of all instance variables
     * of a field.
     *
     * @return string representation of this definition of a column
     */
    public String toString()  {
      return new ToStringBuilder(this)
              .appendSuper(super.toString())
              .append("file", this.file)
              .append("root", this.root).toString();
    }
  }
}
