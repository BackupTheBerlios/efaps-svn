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

package org.efaps.ui.wicket.models;

import java.util.List;
import java.util.UUID;

import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.ui.field.FieldTable;
import org.efaps.db.Instance;
import org.efaps.util.EFapsException;

public class FieldTableModel extends TableModel {

  private static final long serialVersionUID = 1L;

  private final long id;

  public FieldTableModel(final UUID _commanduuid, final String _oid,
                         final FieldTable _fieldTable) {
    super(_commanduuid, _oid);
    setTableUUID(_fieldTable.getTargetTable().getUUID());
    this.id = _fieldTable.getId();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected List<List<Instance>> getInstanceLists() throws EFapsException {
    final List<Return> ret =
        FieldTable.get(this.id).executeEvents(EventType.UI_TABLE_EVALUATE,
            ParameterValues.INSTANCE, new Instance(super.getOid()));
    final List<List<Instance>> lists =
        (List<List<Instance>>) ret.get(0).get(ReturnValues.VALUES);
    return lists;
  }
}