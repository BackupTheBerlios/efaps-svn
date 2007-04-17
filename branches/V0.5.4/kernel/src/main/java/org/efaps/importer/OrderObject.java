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

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class OrderObject implements Comparator<AbstractObject> {

  private final String                       type;

  private final boolean                      ascending;

  private final Map<Integer, OrderAttribute> orderAttributes = new TreeMap<Integer, OrderAttribute>();

  public OrderObject() {
    this.type = null;
    this.ascending = true;
  }

  public OrderObject(final String _type, final String _direction) {
    this.type = _type;
    this.ascending = !"descending".equalsIgnoreCase(_direction);
  }

  public String getType() {
    return this.type;
  }

  public void addAttribute(final Integer _index, final String _name,
      final String _criteria) {
    this.orderAttributes.put(_index, new OrderAttribute(_name, _criteria));
  }

  public int compare(final AbstractObject _arg0, final AbstractObject _arg1) {
    // TODO 1 ersetzen,ascending beruecksichtigen

    if (this.ascending) {
      if (this.orderAttributes.get(1).getCriteria().equalsIgnoreCase(
          "numerical")) {
        Long comp1 = Long.parseLong((String) _arg0
            .getAttribute(this.orderAttributes.get(1).getName()));
        Long comp2 = Long.parseLong((String) _arg1
            .getAttribute(this.orderAttributes.get(1).getName()));

        if (comp1 > comp2)

          return 1;

        else if (comp1 < comp2)

          return -1;

        else

          return 0;

      } else {

        String comp1 = (String) _arg0.getAttribute(this.orderAttributes.get(1)
            .getName());
        String comp2 = (String) _arg1.getAttribute(this.orderAttributes.get(1)
            .getName());

        return comp1.compareToIgnoreCase(comp2);
      }
    }
    return 0;

  }

  private class OrderAttribute {
    private final String name;

    private final String criteria;

    public OrderAttribute(final String _name, final String _criteria) {
      this.name = _name;
      this.criteria = _criteria;
    }

    public String getName() {
      return this.name;
    }

    public String getCriteria() {
      return this.criteria;
    }
  }

}
