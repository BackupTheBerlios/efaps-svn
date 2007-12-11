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

package org.efaps.ui.wicket.components.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.model.AbstractReadOnlyModel;

import org.efaps.admin.ui.Menu;
import org.efaps.admin.ui.AbstractCommand.Target;
import org.efaps.db.Instance;
import org.efaps.ui.wicket.models.StructurBrowserModel;
import org.efaps.ui.wicket.pages.contentcontainer.ContentContainerPage;
import org.efaps.ui.wicket.pages.error.ErrorPage;
import org.efaps.util.EFapsException;

/**
 * @author jmox
 * @version $Id:StructurBrowserTreeTable.java 1510 2007-10-18 14:35:40Z jmox $
 */
public class StructurBrowserTreeTable extends TreeTable {

  private static final long serialVersionUID = 1L;

  private static final ResourceReference CSS =
      new ResourceReference(StructurBrowserTreeTable.class,
          "StructurTreeTable.css");

  public StructurBrowserTreeTable(final String _id, final TreeModel _treeModel,
                                  final IColumn[] _columns) {
    super(_id, _treeModel, _columns);
    this.setRootLess(true);

    final ITreeState treeState = this.getTreeState();
    treeState.collapseAll();
    treeState.addTreeStateListener(new AsyncronTreeUpdateListener());
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.wicket.extensions.markup.html.tree.table.TreeTable#getCSS()
   */
  @Override
  protected ResourceReference getCSS() {
    return CSS;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree#newNodeIcon(org.apache.wicket.MarkupContainer,
   *      java.lang.String, javax.swing.tree.TreeNode)
   */
  @Override
  protected Component newNodeIcon(final MarkupContainer _parent,
                                  final String _wicketId, final TreeNode _node) {
    final StructurBrowserModel model =
        (StructurBrowserModel) ((DefaultMutableTreeNode) _node).getUserObject();
    if (model.getImage() != null) {

      return new WebMarkupContainer(_wicketId) {

        private static final long serialVersionUID = 1L;

        @Override
        protected void onComponentTag(ComponentTag tag) {
          super.onComponentTag(tag);
          tag.put("style", "background-image: url('" + model.getImage() + "')");
        }
      };
    } else {
      return super.newNodeIcon(_parent, _wicketId, _node);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.wicket.extensions.markup.html.tree.table.TreeTable#newTreePanel(org.apache.wicket.MarkupContainer,
   *      java.lang.String, javax.swing.tree.TreeNode, int,
   *      org.apache.wicket.extensions.markup.html.tree.table.TreeTable.IRenderNodeCallback)
   */
  @Override
  protected Component newTreePanel(final MarkupContainer _parent,
                                   final String _wicketId,
                                   final TreeNode _node, final int _level,
                                   final IRenderNodeCallback _renderNodeCallback) {

    return new StructurBrowserTreeFragment(_wicketId, _node, _level,
        _renderNodeCallback);

  }

  /*
   * (non-Javadoc)
   *
   * @see org.apache.wicket.extensions.markup.html.tree.DefaultAbstractTree#newNodeLink(org.apache.wicket.MarkupContainer,
   *      java.lang.String, javax.swing.tree.TreeNode)
   */
  @Override
  protected MarkupContainer newNodeLink(final MarkupContainer _parent,
                                        final String _id, final TreeNode _node) {

    return newLink(_parent, _id, new ILinkCallback() {

      private static final long serialVersionUID = 1L;

      public void onClick(AjaxRequestTarget target) {
        Instance instance = null;
        final StructurBrowserModel model =
            (StructurBrowserModel) ((DefaultMutableTreeNode) _node)
                .getUserObject();

        if (model.getOid() != null) {
          instance = new Instance(model.getOid());
          Menu menu = null;
          try {
            menu = Menu.getTypeTreeMenu(instance.getType());
          } catch (Exception e) {
            throw new RestartResponseException(new ErrorPage(e));
          }
          if (menu == null) {
            EFapsException excep =
                new EFapsException(this.getClass(), "newNodeLink.noTreeMenu",
                    instance.getType().getName());
            throw new RestartResponseException(new ErrorPage(excep));
          }
          final PageParameters parameters = new PageParameters();
          parameters.add("command", menu.getUUID().toString());
          parameters.add("oid", model.getOid());
          ContentContainerPage page;
          if (model.getTarget() == Target.POPUP) {
            page = new ContentContainerPage(parameters);
          } else {
            page =
                new ContentContainerPage(parameters, getPage().getPageMap(),
                    true);
          }

          setResponsePage(page);

        }
      }
    });
  }

  private class StructurBrowserTreeFragment extends Panel {

    private static final long serialVersionUID = 1L;

    public StructurBrowserTreeFragment(
                                       final String _wicketId,
                                       final TreeNode _node,
                                       int _level,
                                       final IRenderNodeCallback _renderNodeCallback) {
      super(_wicketId);

      add(newIndentation(this, "indent", _node, _level));

      add(newJunctionLink(this, "link", "image", _node));

      WebComponent direction = new WebComponent("direction");
      add(direction);
      StructurBrowserModel model =
          (StructurBrowserModel) ((DefaultMutableTreeNode) _node)
              .getUserObject();
      if (model.getDirection() == null) {
        direction.setVisible(false);
      } else if (model.getDirection()) {
        direction.add(new SimpleAttributeModifier("class", "directionDown"));
      } else {
        direction.add(new SimpleAttributeModifier("class", "directionUp"));
      }
      MarkupContainer nodeLink = newNodeLink(this, "nodeLink", _node);
      add(nodeLink);

      nodeLink.add(newNodeIcon(nodeLink, "icon", _node));

      nodeLink.add(new Label("label", new AbstractReadOnlyModel() {

        private static final long serialVersionUID = 1L;

        /**
         * @see org.apache.wicket.model.AbstractReadOnlyModel#getObject()
         */
        @Override
        public Object getObject() {
          return _renderNodeCallback.renderNode(_node);
        }
      }));
    }
  }

}
