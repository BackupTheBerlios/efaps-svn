/*******************************************************************************
* Admin_UI_Menu:
* ~~~~~~~~~~~~~~
* Admin_UI_SearchTree
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

with (MENU)  {
  addProperty("Target",                 "content");
  addProperty("TargetMode",             "view");
  addTargetForm("Admin_UI_AbstractForm");
  addTargetMenu("Admin_UI_SearchTree_Menu");
  addChild(new Command("Admin_UI_MenuTree_Childs"));
  addChild(new Command("Admin_UI_AbstractTree_Properties"));
  addChild(new Command("Admin_UI_DirectTree_Used"));
}
