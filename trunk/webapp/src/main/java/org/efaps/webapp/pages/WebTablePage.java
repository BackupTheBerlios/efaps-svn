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

package org.efaps.webapp.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.resources.StyleSheetReference;
import org.apache.wicket.model.IModel;

import org.efaps.webapp.components.FormContainer;
import org.efaps.webapp.components.table.WebTableContainer;
import org.efaps.webapp.components.table.header.TableHeaderPanel;
import org.efaps.webapp.models.TableModel;

/**
 * @author jmo
 * @version $Id$
 */
public class WebTablePage extends ContentPage {

  private static final long serialVersionUID = 7564911406648729094L;

  public WebTablePage(final PageParameters _parameters)  {
    this(new TableModel(_parameters));
  }

  public WebTablePage(final IModel _model) {
    super(_model);
    this.addComponents();
  }

  protected void addComponents() {
    this.add(new StyleSheetReference("webtablecss", getClass(),
        "webtablepage/WebTablePage.css"));
    try {

      TableModel model = (TableModel) super.getModel();
      if (!model.isInitialised()) {
        model.execute();
      }
      this.add(new TableHeaderPanel("header", model));

      FormContainer form = new FormContainer("form");
      this.add(form);
      super.addComponents(form);

      form.add(new WebTableContainer("formtable", model, this));
    } catch (Exception e) {

      e.printStackTrace();
    }
  }

}
