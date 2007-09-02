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

package org.efaps.webapp.components.modalwindow;

import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;

/**
 * @author jmo
 * @version $Id$
 * 
 */
public class ModalWindowContainer extends ModalWindow {

  private static final long serialVersionUID = 1L;

  private boolean updateParent;

  public ModalWindowContainer(String id) {
    super(id);
    super.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
  }

  public void setUpdateParent(boolean _update) {
    this.updateParent = _update;
  }

  public boolean isUpdateParent() {
    return this.updateParent;
  }

}