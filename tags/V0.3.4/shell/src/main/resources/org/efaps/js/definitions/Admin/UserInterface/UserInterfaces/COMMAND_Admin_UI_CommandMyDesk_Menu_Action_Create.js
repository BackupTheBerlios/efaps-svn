/*******************************************************************************
* Admin_UI_Command:
* ~~~~~~~~~~~~~~~~~
* Admin_UI_CommandMyDesk_Menu_Action_Create
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
  addProperty("TargetCreateType",       "Admin_UI_Command");
  addProperty("TargetMode",             "create");
  addTargetForm("Admin_UI_AbstractForm");
}
