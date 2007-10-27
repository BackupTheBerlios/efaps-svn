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

package org.efaps.ui.wicket.components.menu;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;

import org.efaps.ui.wicket.components.modalwindow.ModalWindowAjaxPageCreator;
import org.efaps.ui.wicket.components.modalwindow.ModalWindowContainer;
import org.efaps.ui.wicket.models.MenuItemModel;
import org.efaps.ui.wicket.pages.content.AbstractContentPage;
import org.efaps.ui.wicket.pages.main.MainPage;

/**
 * @author jmox
 * @version $Id$
 */
public class AjaxOpenModalComponent extends AbstractMenuItemAjaxComponent {

  private static final long serialVersionUID = 1L;

  public AjaxOpenModalComponent(final String _id, final MenuItemModel _menuItem) {
    super(_id, _menuItem);
    this.add(new AjaxOpenModalBehaviour());
  }

  @Override
  public String getJavaScript() {
    return ((AjaxOpenModalBehaviour) super.getBehaviors().get(0))
        .getJavaScript();
  }

  public class AjaxOpenModalBehaviour extends AjaxEventBehavior {

    private static final long serialVersionUID = 1L;

    public AjaxOpenModalBehaviour() {
      super("onclick");
    }

    public String getJavaScript() {
      String script = super.getCallbackScript().toString();
      return "javascript:" + script.replace("'", "\"");
    }

    @Override
    protected void onEvent(final AjaxRequestTarget _target) {
      ModalWindowContainer modal;
      if (super.getComponent().getPage() instanceof MainPage) {
        modal = ((MainPage) super.getComponent().getPage()).getModal();
      } else {
        modal = ((AbstractContentPage) super.getComponent().getPage()).getModal();
      }
      modal.reset();
      ModalWindowAjaxPageCreator pageCreator =
          new ModalWindowAjaxPageCreator((MenuItemModel) super.getComponent()
              .getModel(), modal);
      modal.setPageCreator(pageCreator);
      modal.setInitialHeight(((MenuItemModel) getModel()).getWindowHeight());
      modal.setInitialWidth(((MenuItemModel) getModel()).getWindowWidth());
      modal.show(_target);

    }
  }
}