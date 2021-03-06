/*
 * Copyright 2003 - 2009 The eFaps Team
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

import org.efaps.ui.wicket.models.objects.UIStructurBrowser;


public class StructurBrowserModel extends AbstractModel<UIStructurBrowser>{

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private UIStructurBrowser uistruct;

  public StructurBrowserModel (final UIStructurBrowser _uitable) {
    this.uistruct = _uitable;
  }

  public UIStructurBrowser getObject() {
    return this.uistruct;
  }

  public void setObject(final UIStructurBrowser _uitable) {
    this.uistruct = _uitable;
  }

}
