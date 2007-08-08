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

package org.efaps.webapp.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;

import org.efaps.admin.datamodel.ui.FieldValue;
import org.efaps.admin.ui.Field;
import org.efaps.webapp.models.IFormModel;

public class FormTable extends WebComponent {
  private static final long serialVersionUID = 1550111712776698728L;

  public FormTable(String id, IModel model) {
    super(id, model);
  }

  @Override
  protected void onComponentTagBody(MarkupStream _markupStream,
      ComponentTag _openTag) {
    try {
      super.replaceComponentTagBody(_markupStream, _openTag, getTable());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private String getTable() throws Exception {
    IFormModel model = (IFormModel) super.getModel();

    model.execute();

    int groupCount = 1;
    int rowGroupCount = 1;

    StringBuilder ret = new StringBuilder();

    ret.append("<table id=\"eFapsFormTabel\">");

    for (FieldValue value : model.getValues()) {
      // if the field is a group count field, store the value in the row group
      // count variable, (only if not create node or field is creatable)
      Field field = value.getFieldDef().getField();
      if (field != null) {
        if (field.getGroupCount() > 0
            && (!model.isCreateMode() || field.isCreatable())
            && (!model.isSearchMode() || field.isSearchable())) {
          rowGroupCount = field.getGroupCount();
        }

        // only if:
        // - in createmode field is createable
        // - in searchmode field is searchable
        // - otherwise show field

        if (value.getAttribute() != null
            && (!model.isCreateMode() || field.isCreatable())
            && (!model.isSearchMode() || field.isSearchable())) {

          if (groupCount == 1) {
            ret.append("<tr>");
          }

          if (field.isRequired()
              && (model.isSearchMode() || model.isCreateMode())) {
            ret.append("<td class=\"eFapsFormLabelRequired\">");
          } else {
            ret.append("<td class=\"eFapsFormLabel\">");
          }
          ret.append(value.getFieldDef().getLabel()).append("</td>")

          .append("<td class=\"eFapsFormInputField\" colspan=\"").append(
              2 * (model.getMaxGroupCount() - rowGroupCount) + 1).append("\">");

          if (field.getReference() != null
              && value.getInstance().getOid() != null && model.isCreateMode()
              && model.isSearchMode() != true) {
            String targetUrl =
                field.getReference() + "oid=" + value.getInstance().getOid();
            String targetWindow;
            if (field.isTargetPopup()) {
              targetWindow = "Popup";
            } else {
              targetWindow = "Content";
              if (model.getNodeId() != null) {
                targetUrl += "&nodeId=" + model.getNodeId();
              }
            }
            ret.append(
                "<a href=\"javascript:eFapsCommonOpenUrl('<c:out value=\"")
                .append(targetUrl).append("','").append(targetWindow).append(
                    "'\")>");

          }
          if (field.isShowTypeIcon() && value.getInstance().getOid() != null
              && model.isCreateMode() == false && model.isSearchMode() == false) {
            ret.append("<img src=").append(
                value.getInstance().getType().getIcon()).append("/>&nbsp;");

          }

          if (model.isCreateMode() && field.isCreatable()) {
            ret.append(value.getCreateHtml());
          } else if (model.isEditMode() && field.isEditable()) {
            ret.append(value.getEditHtml());
          } else if (model.isSearchMode() && field.isSearchable()) {
            ret.append(value.getSearchHtml());
          } else if (value.getAttribute() != null) {
            ret.append(value.getViewHtml());
          }

          if (field.getReference() != null
              && value.getInstance().getOid() != null && model.isCreateMode()
              && model.isSearchMode() != true) {
            ret.append("</a>");
          }
          ret.append("</td>");

          if (groupCount == rowGroupCount) {
            ret.append("</tr>");
          }
          if (groupCount < rowGroupCount) {
            groupCount++;
          } else if (groupCount == rowGroupCount) {
            groupCount = 1;
            rowGroupCount = 1;
          }

        }
      }

    }

    ret.append("</table>");
    return ret.toString();
  }
}
