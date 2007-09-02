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

package org.efaps.webapp.components.menu;

import java.util.Map;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.ui.CommandAbstract;
import org.efaps.util.EFapsException;
import org.efaps.webapp.models.FormModel;
import org.efaps.webapp.models.MenuItemModel;
import org.efaps.webapp.models.TableModel;
import org.efaps.webapp.pages.WebFormPage;
import org.efaps.webapp.pages.WebTablePage;

/**
 * @author jmo
 * @version $Id$
 */
public class MenuItemAjaxSubmitComponent extends WebComponent {

  private static final long serialVersionUID = 1L;

  public MenuItemAjaxSubmitComponent(final String id, final IModel _menuItem,
                                     final Form _form) {
    super(id, _menuItem);
    this.add(new SubmitAndUpdateBehavior(_form, _menuItem));
  }

  public SubmitAndUpdateBehavior getSubmitBehaviour() {
    return (SubmitAndUpdateBehavior) super.getBehaviors().get(0);
  }

  @Override
  protected void onRender(final MarkupStream _markupStream) {
    _markupStream.next();
  }

  public class SubmitAndUpdateBehavior extends AjaxFormSubmitBehavior {

    private static final long serialVersionUID = 1L;

    private final Form form;

    private final IModel model;

    public String getJavaScript() {
      String script =  super.getEventHandler().toString();
      return "javascript:" + script.replace("'", "\"");
    }

    public SubmitAndUpdateBehavior(final Form _form, final IModel _model) {
      super(_form, "onCLick");
      this.form = _form;
      this.model = _model;
    }

    @Override
    protected void onSubmit(final AjaxRequestTarget _target) {

      CommandAbstract command = ((MenuItemModel) this.model).getCommand();
      Map<?, ?> para = this.form.getRequest().getParameterMap();

      if (command.hasEvents(EventType.UI_COMMAND_EXECUTE)) {
        try {
          String[] oids = (String[]) para.get("selectedRow");
          if (oids != null) {

            command.executeEvents(EventType.UI_COMMAND_EXECUTE,
                ParameterValues.OTHERS, oids);

          } else {
            command.executeEvents(EventType.UI_COMMAND_EXECUTE);
          }
        } catch (EFapsException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      this.form.getPage().getModel().detach();
      Page page = null;
      if (this.form.getPage().getModel() instanceof TableModel) {

        page = new WebTablePage(this.form.getPage().getModel());

      } else if (this.form.getPage().getModel() instanceof FormModel) {
        page = new WebFormPage(this.form.getPage().getModel());
      }
      this.form.setResponsePage(page);
    }
  }
}