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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.apache.wicket.Component;
import org.apache.wicket.IClusterable;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tree.table.IColumn;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.markup.html.tree.ITreeStateListener;
import org.apache.wicket.model.AbstractReadOnlyModel;

import org.efaps.admin.ui.CommandAbstract;
import org.efaps.admin.ui.Menu;
import org.efaps.db.Instance;
import org.efaps.webapp.models.StructurBrowserModel;
import org.efaps.webapp.models.StructurBrowserModel.BogusNode;
import org.efaps.webapp.pages.ContentContainerPage;
import org.efaps.webapp.pages.ErrorPage;

public class StructurBrowserTreeTable extends TreeTable {

  private static final long serialVersionUID = 1L;

  private static final ResourceReference CSS =
      new ResourceReference(StructurBrowserTreeTable.class, "StructurTree.css");

  public StructurBrowserTreeTable(final String _id, final TreeModel _treeModel,
                                  final IColumn[] _columns) {
    super(_id, _treeModel, _columns);
    this.setRootLess(true);

    ITreeState treeState = this.getTreeState();
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
   * @see org.apache.wicket.extensions.markup.html.tree.table.TreeTable#newTreePanel(org.apache.wicket.MarkupContainer,
   *      java.lang.String, javax.swing.tree.TreeNode, int,
   *      org.apache.wicket.extensions.markup.html.tree.table.TreeTable.IRenderNodeCallback)
   */
  @Override
  protected Component newTreePanel(MarkupContainer parent, String id,
                                   TreeNode node, int level,
                                   IRenderNodeCallback renderNodeCallback) {

    return new StructurBrowserTreeFragment(id, node, level, renderNodeCallback);

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
        StructurBrowserModel model =

        (StructurBrowserModel) ((DefaultMutableTreeNode) _node).getUserObject();

        if (model.getOid() != null) {
          instance = new Instance(model.getOid());
          Menu menu = null;
          try {
            menu = instance.getType().getTreeMenu();
          } catch (Exception e) {
            throw new RestartResponseException(new ErrorPage(e));
          }
          if (menu == null) {
            Exception ex =
                new Exception("no tree menu defined for type "
                    + instance.getType().getName());
            throw new RestartResponseException(new ErrorPage(ex));
          }
          PageParameters parameters = new PageParameters();
          parameters.add("command", menu.getUUID().toString());
          parameters.add("oid", model.getOid());
          ContentContainerPage page;
          if (model.getTarget() == CommandAbstract.TARGET_POPUP) {
            page = new ContentContainerPage(parameters);
          } else {
            page = new ContentContainerPage(parameters, getPage().getPageMap());
          }

          setResponsePage(page);

        }
      }
    });
  }

  public class AsyncronTreeUpdateListener implements ITreeStateListener,
      IClusterable {

    private static final long serialVersionUID = 1L;

    public void allNodesCollapsed() {
      // not needed here
    }

    public void allNodesExpanded() {
      // not needed here
    }

    public void nodeCollapsed(final TreeNode _node) {
      // not needed here
    }

    public void nodeExpanded(final TreeNode _node) {
      if (!_node.isLeaf() && _node.getChildAt(0) instanceof BogusNode) {

        StructurBrowserModel model =
            (StructurBrowserModel) ((DefaultMutableTreeNode) _node)
                .getUserObject();
        model.addChildren((DefaultMutableTreeNode) _node);
      }

    }

    public void nodeSelected(final TreeNode _node) {
      // not needed here
    }

    public void nodeUnselected(final TreeNode _node) {
      // not needed here
    }

  }

  private class StructurBrowserTreeFragment extends Fragment {

    private static final long serialVersionUID = 1L;

    public StructurBrowserTreeFragment(
                                       String id,
                                       final TreeNode node,
                                       int level,
                                       final IRenderNodeCallback renderNodeCallback) {
      super(id, "fragment");

      add(newIndentation(this, "indent", node, level));

      add(newJunctionLink(this, "link", "image", node));

      MarkupContainer nodeLink = newNodeLink(this, "nodeLink", node);
      add(nodeLink);

      nodeLink.add(newNodeIcon(nodeLink, "icon", node));

      nodeLink.add(new Label("label", new AbstractReadOnlyModel() {

        private static final long serialVersionUID = 1L;

        /**
         * @see org.apache.wicket.model.AbstractReadOnlyModel#getObject()
         */
        @Override
        public Object getObject() {
          return renderNodeCallback.renderNode(node);
        }
      }));
    }
  }

}