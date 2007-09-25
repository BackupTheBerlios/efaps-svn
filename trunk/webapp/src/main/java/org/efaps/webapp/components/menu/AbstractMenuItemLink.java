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

import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.Link;

import org.efaps.webapp.models.MenuItemModel;

/**
 * @author jmo
 * @version $Id$
 */
public abstract class AbstractMenuItemLink extends Link {

  private static final long serialVersionUID = 1L;

  public AbstractMenuItemLink(final String _id, final MenuItemModel _model) {
    super(_id, _model);
  }

  /**
   * for the JSCookMenu nothing must be renderd, because JavaScript is used to
   * create the Menu
   *
   * @see org.apache.wicket.MarkupContainer#onRender(org.apache.wicket.markup.MarkupStream)
   */
  @Override
  protected void onRender(final MarkupStream _markupStream) {
    _markupStream.next();
  }

}