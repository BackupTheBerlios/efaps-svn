/*******************************************************************************
* Admin_DataModel_Type:
* ~~~~~~~~~~~~~~~~~~~~~
* Admin_UI_Form
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

with (TYPE)  {
  setParentType(new Type("Admin_UI_Collection"));
  addProperty("Icon",                   "${COMMONURL}/Image.jsp?name=Admin_UI_FormImage");
  addProperty("Tree",                   "Admin_UI_FormTree");
}
