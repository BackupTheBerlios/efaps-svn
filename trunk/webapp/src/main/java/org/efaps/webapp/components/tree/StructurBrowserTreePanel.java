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

package org.efaps.webapp.components.tree;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.Panel;

import org.efaps.webapp.models.StructurBrowserModel;

public class StructurBrowserTreePanel extends Panel {

  private static final long serialVersionUID = 1L;

  public StructurBrowserTreePanel(final String _id,
                                  final PageParameters _parameters) {
    this(_id, new StructurBrowserModel(_parameters));
  }

  public StructurBrowserTreePanel(String _id, StructurBrowserModel _model) {
    super(_id, _model);

    StructurBrowserModel model = (StructurBrowserModel) super.getModel();
    if (!model.isInitialised()) {
      model.execute();
    }

    StructurBrowserTree tree =
        new StructurBrowserTree("tree", model.getTreeModel());
    this.add(tree);

  }
}