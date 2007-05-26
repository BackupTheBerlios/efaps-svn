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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.importer;

import java.util.HashMap;
import java.util.Map;

/**
 * Thsi Class represents the Possibility to define default Values, for the Case
 * a Foreign-Object returns a invalid Value. <br>
 * <br>
 * Example for the XML-Structure:<br>
 * &lt;definition&gt;<br>
 * &lt;default type="Admin_User_Person" name="Creator"&gt;1&lt;/default&gt;<br>
 * &lt;/definition&gt;
 * 
 * @author jmo
 * 
 */
public class DefaultObject {
  /**
   * contains the Defaults defined for this Import
   */
  final static Map<String, String> DEFAULTS = new HashMap<String, String>();

  /**
   * adds a new Dafult to the DEFAULTS
   * 
   * @param _type
   *          String containing the Type of the Object
   * @param _name
   *          String containing the Name of the Attribute
   * @param _value
   *          Value to be Set if the Default will be inserte
   */
  public void addDefault(final String _type, final String _name,
                         final String _value) {
    DEFAULTS.put(_type + "/" + _name, _value);

  }

  /**
   * get the DefaultValue of the Object
   * 
   * @param _type
   *          String containing the Type of the Object
   * @param _name
   *          String containing the Name of the Attribute
   * @return String containing the DefaultValue
   */
  public static String getDefault(final String _type, final String _name) {
    return (String) DEFAULTS.get(_type + "/" + _name);
  }

}