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

package org.efaps.ui.wicket.components.table.header;

import org.apache.wicket.markup.html.link.Link;

import org.efaps.ui.wicket.models.HeaderModel;
import org.efaps.ui.wicket.models.TableModel;
import org.efaps.ui.wicket.models.TableModel.SortDirection;
import org.efaps.ui.wicket.pages.content.table.TablePage;


/**
 * @author jmox
 * @version $Id$
 */
public class SortLinkContainer extends Link {

  private static final long serialVersionUID = 1L;

  public SortLinkContainer(final String _id, final HeaderModel _model) {
    super(_id, _model);
  }

  @Override
  public void onClick() {

    TableModel tablemodel =
        (TableModel) this.findParent(TableHeaderPanel.class).getModel();
    HeaderModel model = (HeaderModel) super.getModel();
    tablemodel.setSortKey(model.getName());

    for (HeaderModel headermodel : tablemodel.getHeaders()) {
      if (!headermodel.equals(model)) {
        headermodel.setSortDirection(SortDirection.NONE);
      }
    }

    if (model.getSortDirection() == SortDirection.NONE
        || model.getSortDirection() == SortDirection.DESCENDING) {
      model.setSortDirection(SortDirection.ASCENDING);
    } else {
      model.setSortDirection(SortDirection.DESCENDING);
    }

    tablemodel.setSortDirection(model.getSortDirection());

    tablemodel.sort();
    this.getRequestCycle().setResponsePage(new TablePage(tablemodel));

  }

}