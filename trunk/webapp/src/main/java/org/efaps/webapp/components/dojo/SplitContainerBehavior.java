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

package org.efaps.webapp.components.dojo;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;

public class SplitContainerBehavior extends AbstractDojoBehavior {

  public enum Orientation {
    HORIZONTAL("horizontal"),
    VERTICAL("vertical");

    private Orientation(String _value) {
      this.value = _value;
    }

    public String value;

  }

  private static final long serialVersionUID = 1L;

  private Orientation orientation = Orientation.HORIZONTAL;

  private int sizerWidth = 5;

  private boolean activeSizing = false;

  private String style = "width: 100%; height: 100%;";

  @Override
  public void onComponentTag(final Component _component, final ComponentTag _tag) {
    super.onComponentTag(_component, _tag);
    _tag.put("dojoType", "dijit.layout.SplitContainer");
    _tag.put("orientation", this.orientation.value);
    _tag.put("sizerWidth", this.sizerWidth);
    _tag.put("activeSizing", this.activeSizing);
    _tag.put("style", this.style);
  }

  /**
   * This is the setter method for the instance variable {@link #orientation}.
   *
   * @param _orientation
   *                the orientation to set
   */
  public void setOrientation(Orientation _orientation) {
    this.orientation = _orientation;
  }

  /**
   * This is the setter method for the instance variable {@link #sizerWidth}.
   *
   * @param _sizerWidth
   *                the sizerWidth to set
   */
  public void setSizerWidth(int _sizerWidth) {
    this.sizerWidth = _sizerWidth;
  }

  /**
   * This is the setter method for the instance variable {@link #activeSizing}.
   *
   * @param _activeSizing
   *                the activeSizing to set
   */
  public void setActiveSizing(Boolean _activeSizing) {
    this.activeSizing = _activeSizing;
  }

  /**
   * This is the setter method for the instance variable {@link #style}.
   *
   * @param _style
   *                the style to set
   */
  public void setStyle(String _style) {
    this.style = _style;
  }

}