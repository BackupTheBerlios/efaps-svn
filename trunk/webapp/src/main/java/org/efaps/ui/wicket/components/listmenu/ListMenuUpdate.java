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
 * Revision:        $Rev:1510 $
 * Last Changed:    $Date:2007-10-18 09:35:40 -0500 (Thu, 18 Oct 2007) $
 * Last Changed By: $Author:jmox $
 */

package org.efaps.ui.wicket.components.listmenu;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.Model;

import org.efaps.admin.ui.Menu;
import org.efaps.ui.wicket.EFapsSession;
import org.efaps.ui.wicket.components.listmenu.ListMenuPanel.Rows;
import org.efaps.ui.wicket.components.listmenu.ListMenuPanel.StyleClassName;
import org.efaps.ui.wicket.models.MenuItemModel;

/**
 * This class provides some static methods to update a ListMenu.
 *
 * @author jmox
 * @version $Id:ListMenuUpdate.java 1510 2007-10-18 14:35:40Z jmox $
 */
public class ListMenuUpdate {

  /**
   * private constructur (Singleton)
   */
  private ListMenuUpdate() {
    // making a Singleton
  }

  /**
   * Update the ListMenu by injecting a new SubListMenu into it.
   *
   * @param _target
   *                AjaxRequestTarget used to update the ListMenu
   * @param _menukey
   *                Key to identify the ListMenu
   * @param _menu
   *                active Menu, used to evaluate if it can be reused
   * @param _parameters
   *                PageParameters to create a new ListMenu
   * @param _oid
   *                OID of the current Instance
   */
  @SuppressWarnings("unchecked")
  public static void update(final AjaxRequestTarget _target,
                            final String _menukey, final Menu _menu,
                            final PageParameters _parameters, final String _oid) {
    // deselect the item
    final Component comp =
        ((EFapsSession) (Session.get())).getFromCache(_menukey);
    deselectItem(_menukey, comp, _target);
    // find the ListMenuPanel we want do update
    final MarkupContainer listitem = comp.findParent(ListItem.class);
    ListMenuPanel listmenupanel = null;
    Iterator<?> childs = listitem.iterator();
    while (childs.hasNext()) {
      final Object child = childs.next();
      if (child instanceof ListMenuPanel) {
        listmenupanel = (ListMenuPanel) child;
        break;
      }
    }

    // find the row to get the model
    Rows rows = null;
    childs = listmenupanel.iterator();
    while (childs.hasNext()) {
      final Object child = childs.next();
      if (child instanceof Rows) {
        rows = (Rows) child;
        break;
      }
    }
    int level = ((MenuItemModel) comp.getModel()).getLevel();
    boolean old = false;
    int index = 0;
    // if we have now rows, we need to create a ne ListMenuPanel
    if (rows == null) {
      listmenupanel =
          new ListMenuPanel("nested", _menukey, _parameters, true, level + 1);
      listmenupanel.setOutputMarkupId(true);
      listitem.replace(listmenupanel);
    } else {
      // check if an Item is allready in the Menu
      final List<Object> list = rows.getList();
      level++;
      for (Object item : rows.getList()) {
        if (item instanceof MenuItemModel) {
          if (((MenuItemModel) item).getOid().equals(_oid)
              && ((MenuItemModel) item).getCommandUUID()
                  .equals(_menu.getUUID())) {
            old = true;
            break;
          }
          index++;
        }

      }
      // if it is a ol item select it, else add a new List into the existing
      // ListMenuPanel
      if (old) {
        setSelectedItem(_menukey, listmenupanel.getHeaderComponents()
            .get(index), _target);
      } else {
        final MenuItemModel model = new MenuItemModel(_menu.getUUID(), _oid);
        model.setLevel(level);
        model.setHeader(true);
        model.setSelected(true);
        list.add(model);
        if (model.hasChilds()) {
          for (MenuItemModel item : model.getChilds()) {
            item.setLevel(level);
          }
          list.add(model.getChilds());
        }
      }
    }
    _target.addComponent(listmenupanel);
  }

  /**
   * add a new Menu to the ListMenuPanel, using PageParameters to create the new
   * Menu
   *
   * @param _target
   *                AjaxRequestTarget used to update the ListMenu
   * @param _menukey
   *                Key to identify the ListMenu
   * @param _parameters
   *                parameters for the ListMenuPanel
   */
  public static void newMenu(final AjaxRequestTarget _target,
                             final String _menukey,
                             final PageParameters _parameters) {
    Component comp = ((EFapsSession) (Session.get())).getFromCache(_menukey);
    comp = comp.findParent(ListMenuPanel.class);

    while (comp.findParent(ListMenuPanel.class) != null) {
      comp = comp.findParent(ListMenuPanel.class);
    }
    final ListMenuPanel newlistmenu =
        new ListMenuPanel("menu", _menukey, _parameters, true);
    newlistmenu.setOutputMarkupId(true);
    comp.replaceWith(newlistmenu);

    _target.addComponent(newlistmenu.getParent());
  }

  /**
   * This Method sets the Style of a Component as Selected and caches the
   * Selected Component in the Session. The Style of the previously chached
   * Component is changed to unselected.
   *
   * @param _menukey
   *                Key of the ListMenu to be updated
   * @param _component
   *                Component to be selected
   * @param _target
   *                AjaxRequestTarget used to update the ListMenu
   * @see #setSelectedItem(String, Component, AjaxRequestTarget, boolean)
   */
  public static void setSelectedItem(final String _menukey,
                                     final Component _component,
                                     final AjaxRequestTarget _target) {
    setSelectedItem(_menukey, _component, _target, true);
  }

  /**
   * This Method sets the Style of a Component as Selected and caches the
   * Selected Component in the Session. Depending on <i>_deselect</i> the Style
   * of the previously chached Component is changed to unselected.
   *
   * @param _menukey
   *                Key of the ListMenu to be updated
   * @param _component
   *                Component to be selected
   * @param _target
   *                AjaxRequestTarget used to update the ListMenu
   * @param _deselect
   *                should the previous Component be deselected
   * @see #setSelectedItem(String, Component, AjaxRequestTarget)
   */
  public static void setSelectedItem(final String _menukey,
                                     final Component _component,
                                     final AjaxRequestTarget _target,
                                     final boolean _deselect) {
    _component.add(new AttributeModifier("class", new Model(
        StyleClassName.ITEM_SELECTED.name)));
    final EFapsSession session = (EFapsSession) (Session.get());
    final Component previous = session.getFromCache(_menukey);

    if (previous instanceof ListMenuAjaxLinkContainer
        && !previous.equals(_component)
        && _deselect) {
      deselectItem(_menukey, previous, _target);

    }
    session.putIntoCache(_menukey, _component);
    if (_target != null) {
      _target.addComponent(_component);
      changeBrothers(_component, _target, true);
    }
  }

  /**
   * Method to change the StyleClass of the Components wich are on the same
   * level, as the <i>_component</i>.
   *
   * @param _component
   *                Component whos brothers should be changed
   * @param _target
   *                AjaxRequestTarget used to update the ListMenu
   * @param _selected
   *                should it be changed to selected or unselected
   */
  private static void changeBrothers(final Component _component,
                                     final AjaxRequestTarget _target,
                                     final boolean _selected) {
    final WebMarkupContainer parent =
        (WebMarkupContainer) _component.getParent();
    final Iterator<?> iter = parent.iterator();
    while (iter.hasNext()) {
      final Component child = (Component) iter.next();
      if (child instanceof AbstractAjaxLink) {
        String classname;
        if (_selected) {
          classname = ((AbstractAjaxLink) child).getSelectedStyleClass().name;
        } else {
          classname = ((AbstractAjaxLink) child).getStyleClass().name;
        }
        child.add(new AttributeModifier("class", new Model(classname)));
        _target.addComponent(child);
      }
    }
  }

  /**
   * deselect a ListMenuItem
   *
   * @param _menukey
   *                Key of the ListMenu to be updated
   * @param _component
   *                Component to be deselected
   * @param _target
   *                AjaxRequestTarget used to update the ListMenu
   */
  public static void deselectItem(final String _menukey,
                                  final Component _component,
                                  final AjaxRequestTarget _target) {
    final String styleClass =
        ((ListMenuAjaxLinkContainer) _component).getDefaultStyleClass().name;
    _component.add(new AttributeModifier("class", new Model(styleClass)));
    if (_target != null) {
      _target.addComponent(_component);
      changeBrothers(_component, _target, false);
    }
    ((EFapsSession) (Session.get())).removeFromCache(_menukey);
  }

}