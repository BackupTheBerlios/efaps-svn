/*******************************************************************************
* Admin_UI_Command:
* ~~~~~~~~~~~~~~~~~
* Admin_UI_TableMyDesk_Menu_Action_Create
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
  addProperty("Target",                 "popup");
  addProperty("TargetCreateType",       "Admin_UI_Table");
  addProperty("TargetMode",             "create");
  addTargetForm("Admin_UI_AbstractForm");
}
