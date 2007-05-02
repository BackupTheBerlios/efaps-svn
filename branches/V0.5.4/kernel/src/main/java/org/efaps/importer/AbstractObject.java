/*
 * Copyright 2003 - 2007 The eFaps Team
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
 * Revision:        $Rev: 732 $
 * Last Changed:    $Date: 2007-03-18 22:17:23 +0000 (Sun, 18 Mar 2007) $
 * Last Changed By: $Author: jmo $
 */

package org.efaps.importer;

import java.util.Map;
import java.util.Set;

/**
 * Main Class for Importing Objects into the Database connected to eFaps
 * 
 * @author jmo
 * 
 */
public abstract class AbstractObject {

  public abstract Set<ForeignObject> getLinks();

  /**
   * get the Type of the Object
   * 
   * @return the Type of the Object
   */
  public abstract String getType();

  public abstract Map<String, Object> getAttributes();

  /**
   * get the ID of the Object
   * 
   * @return Strng with the ID of the Object
   */
  public abstract String getID();

  /**
   * sets the ID of the Object
   * 
   * @param _id
   */
  public abstract void setID(String _id);

  public abstract String getParrentAttribute();

  public abstract boolean isCheckinObject();

  public abstract Set<String> getUniqueAttributes();

  public abstract Object getAttribute(final String _attribute);

  /**
   * has the Object Childs?
   * 
   * @return true if the Object has Childs, otherwise false
   */
  public abstract boolean hasChilds();

  public abstract void dbCheckObjectIn();

  /**
   * Method that executes the Insert into the Database
   */
  public abstract void dbAddChilds();

  public abstract String dbUpdateOrInsert(final AbstractObject _parent,
                                          final String _ID);
}