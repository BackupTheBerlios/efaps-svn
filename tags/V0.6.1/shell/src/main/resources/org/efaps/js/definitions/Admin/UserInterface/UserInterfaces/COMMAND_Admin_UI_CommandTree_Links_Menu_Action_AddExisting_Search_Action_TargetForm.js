/*******************************************************************************
* Admin_UI_Command:
* ~~~~~~~~~~~~~~~~~
* Admin_UI_CommandTree_Links_Menu_Action_AddExisting_Search_Action_TargetForm
*
* Description:
* ~~~~~~~~~~~~
*
* History:
* ~~~~~~~~
* Revision: $Rev$
* Date:     $Date$
* By:       $Author$
*
* Author:
* ~~~~~~~
* TMO
*******************************************************************************/

with (COMMAND)  {
  addProperty("ConnectChildAttribute",  "To");
  addProperty("ConnectParentAttribute", "From");
  addProperty("ConnectType",            "Admin_UI_LinkTargetForm");
  addProperty("Label",                  "Admin_UI_LinkTargetForm.Label");
  addProperty("ResultTable",            "Admin_UI_AbstractList");
  addProperty("SearchForm",             "Admin_UI_AbstractForm");
  addProperty("SearchType",             "Admin_UI_Form");
  addProperty("TargetMode",             "connect");
}
