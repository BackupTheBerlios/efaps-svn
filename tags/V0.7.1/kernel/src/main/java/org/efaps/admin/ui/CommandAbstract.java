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

package org.efaps.admin.ui;

import java.util.List;
import java.util.Vector;

import org.efaps.admin.datamodel.Attribute;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.user.Role;
import org.efaps.servlet.RequestHandler;
import org.efaps.util.cache.CacheReloadException;

/**
 * This class represents the Commands wich enable the interaction with a User.<br>
 * Buttons in the UserInterface a represented by this Class.
 * 
 * @author tmo
 * @version $Id$
 */
public abstract class CommandAbstract extends UserInterfaceObject {

  /////////////////////////////////////////////////////////////////////////////
  // static Variables

  static public final int TABLE_SORT_DIRECTION_DESC = 1;

  static public final int TABLE_SORT_DIRECTION_ASC = 0;

  /**
   * The target of the href is the content frame.
   * 
   * @see #target
   */
  static public final int TARGET_CONTENT = 1;

  /**
   * The target of the href is the hidden frame.
   * 
   * @see #target
   */
  static public final int TARGET_HIDDEN = 3;

  static public final int TARGET_MODE_CONNECT = 4;

  static public final int TARGET_MODE_CREATE = 3;

  static public final int TARGET_MODE_EDIT = 2;

  static public final int TARGET_MODE_SEARCH = 5;

  static public final int TARGET_MODE_UNKNOWN = 0;

  static public final int TARGET_MODE_VIEW = 1;

  /**
   * The target of the href is a new window popped up.
   * 
   * @see #target
   */
  static public final int TARGET_POPUP = 2;

  /**
   * The target of the href is not known. This is maybe, if a javascript
   * function is directly called.
   * 
   * @see #target
   */
  static public final int TARGET_UNKNOWN = 0;

  // ///////////////////////////////////////////////////////////////////////////
  // instance Variables

  /**
   * The instance variable stores if the execution of the command needs a
   * confirmation of the user. The default value is <i>false</i>.
   * 
   * @see #isAskUser
   * @see #setAskUser
   */
  private boolean askUser = false;

  /**
   * If the instance variable is set to <i>tree</i>, the command is selected as
   * default command in the navigation tree.
   * 
   * @see #isDefaultSelected
   * @see #setDefaultSelected
   */
  private boolean defaultSelected = false;

  /**
   * Instance variable to hold the reference to the icon file.
   * 
   * @see #setIcon
   * @see #getIcon
   */
  private String icon = null;

  /**
   * The instance variable stores the label of this command instance. The
   * default value is set from the constructor to the name plus extension
   * '.Label'.
   * 
   * @see #getLabel
   * @see #setLabel
   */
  private String label = null;

  /**
   * Instance variable to hold the reference to call.
   * 
   * @see #setReference
   * @see #getReference
   */
  private String reference = null;

  /**
   * If the value is set to <i>true</i>. the commands submits the current form
   * to the given href url and the given target. The default value is <i>false</i>.
   * 
   * @see #isSubmit
   * @see #setSubmit
   */
  private boolean submit = false;

  /**
   * The target of the command is the content frame.
   * 
   * @see #isTargetContent
   * @see #isTargetPopup
   * @set #getTarget
   * @see #setTarget
   */
  private int target = TARGET_UNKNOWN;

  /**
   * The instance variable stores the height for the target bottom. Only a is
   * set, the value is used from the JSP pages.
   * 
   * @see #getTargetBottomHeight
   * @see #setTargetBottomHeight
   */
  private int targetBottomHeight = 0;

  /**
   * The instance variable stores the target connect attribute used for the
   * connect in a form.
   * 
   * @see #getTargetConnectAttribute
   * @see #setTargetConnectAttribute
   */
  private Attribute targetConnectAttribute = null;

  /**
   * The instance variable stores the create type for the target user interface
   * object.
   * 
   * @see #getTargetCreateType
   * @see #setTargetCreateType
   */
  private Type targetCreateType = null;

  /**
   * The instance variable stores the target user interface form object which is
   * shown by the this abstract commmand.
   * 
   * @see #getTargetForm
   * @see #setTargetForm
   */
  private Form targetForm = null;

  /**
   * The instance method stores the complete menu. Default value is a null and
   * no menu is shown.
   * 
   * @see #setTargetMenu
   * @see #getTargetMenu
   */
  private Menu targetMenu = null;

  /**
   * The instance variable stores the mode of the target user interface object.
   * 
   * @see #getTargetMode
   * @see #setTargetMode
   */
  private int targetMode = TARGET_MODE_UNKNOWN;

  /**
   * The instance variable stores the search of target user interface object.
   * 
   * @see #getTargetSearch
   * @see #setTargetSearch
   */
  private Search targetSearch = null;

  /**
   * Standard checkboxes for a table must be shown. The checkboxes are used e.g.
   * to delete selected.
   * 
   * @see #isTargetShowCheckBoxes
   * @see #setTargetShowCheckBoxes
   */
  private boolean targetShowCheckBoxes = false;

  /**
   * The instance variable stores the target user interface table object which
   * is shown by the this abstract commmand.
   * 
   * @see #getTargetTable
   * @see #setTargetTable
   */
  private Table targetTable = null;

  /**
   * The instance variable store the filters for the targer table.
   * 
   * @see #getTargetTableFilters
   * @see #setTargetTableFilters
   */
  private List<TargetTableFilter> targetTableFilters = null;

  /**
   * The instance variable stores for target user interface table object the
   * default sort direction. The default value is
   * {@link #TABLE_SORT_DIRECTION_ASC}.
   * 
   * @see #getTargetTableSortDirection
   * @see #setTargetTableSortDirection
   */
  private int targetTableSortDirection = TABLE_SORT_DIRECTION_ASC;

  /**
   * The instance variable stores for target user interface table object the
   * default sort key.
   * 
   * @see #getTargetTableSortKey
   * @see #setTargetTableSortKey
   */
  private String targetTableSortKey = null;

  /**
   * Sets the title of the target window.
   * 
   * @see #getTargetTitle
   * @see #setTargetTitle
   */
  private String targetTitle = null;

  /**
   * The instance variable stores the window height of the popup window ({@link #target}
   * is set to {@link #TARGET_POPUP}). The default value is <i>400</i>.
   * 
   * @see #getWindowHeight
   * @see #setWindowHeight
   */
  private int windowHeight = 400;

  /**
   * The instance variable stores the window width of the popup window ({@link #target}
   * is set to {@link #TARGET_POPUP}). The default value is <i>600</i>.
   * 
   * @see #getWindowWidth
   * @see #setWindowWidth
   */
  private int windowWidth = 600;

  /////////////////////////////////////////////////////////////////////////////
  // Constructors
  /**
   * Constructor to set the id and name of the command object. The constructor
   * also sets the label of the command and the titel of the target.
   * 
   * @param _id
   *          id of the command to set
   * @param _name
   *          name of the command to set
   * @see #label
   */
  protected CommandAbstract(long _id, String _name) {
    super(_id, _name);
    setLabel(_name + ".Label");
    setTargetTitle(_name + ".Title");
  }

  /////////////////////////////////////////////////////////////////////////////
  // instance Methods

  /**
   * Add a new role for access to this command.
   * 
   * @param _role
   * @see #access
   * @see #getAccess
   */
  protected void add(final Role _role) {
    getAccess().add(_role);
  }

  /////////////////////////////////////////////////////////////////////////////

  /**
   * Get the current icon reference value.
   * 
   * @return the value of the instance variable {@link #icon}.
   * @see #icon
   * @see #setIcon
   */
  public String getIcon() {
    return this.icon;
  }

  /**
   * Set the new icon reference value.
   * 
   * @param _icon
   *          new icon reference to set
   * @see #icon
   * @see #getIcon
   */
  public void setIcon(final String _icon) {
    this.icon = _icon;
  }

  /**
   * This method returns the Property of the Label and not the name
   * 
   * @return String
   */
  public String getLabelProperty() {
    return DBProperties.getProperty(this.label);
  }

  /**
   * This is the setter method for the instance variable {@link #label}.
   * 
   * @return value of instance variable {@link #label}
   * @see #label
   * @see #setLabel
   */
  public String getLabel() {
    return this.label;
  }

  /**
   * This is the setter method for the instance variable {@link #label}.
   * 
   * @param _label
   *          new value for instance variable {@link #label}
   * @see #label
   * @see #getLabel
   */
  public void setLabel(final String _label) {
    this.label = _label;
  }

  /**
   * Get the current reference value.
   * 
   * @return the value of the instance variable {@link #reference}.
   * @see #reference
   * @see #setReference
   */
  public String getReference() {
    return this.reference;
  }

  /**
   * Set the new reference value.
   * 
   * @param _reference
   *          new reference to set
   * @see #reference
   * @see #getReference
   */
  public void setReference(final String _reference) {
    this.reference = _reference;
  }

  /**
   * This is the setter method for the instance variable {@link #target}.
   * 
   * @return value of instance variable {@link #target}
   * @see #target
   * @see #setTarget
   */
  public int getTarget() {
    return this.target;
  }

  /**
   * This is the setter method for the instance variable {@link #target}.
   * 
   * @param _target
   *          new value for instance variable {@link #target}
   * @see #target
   * @see #getTarget
   */
  public void setTarget(final int _target) {
    this.target = _target;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetBottomHeight}.
   * 
   * @return value of instance variable {@link #targetBottomHeight}
   * @see #targetBottomHeight
   * @see #setTargetBottomHeight
   */
  public int getTargetBottomHeight() {
    return this.targetBottomHeight;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetBottomHeight}.
   * 
   * @param _targetBottomHeight
   *          new value for instance variable {@link #targetBottomHeight}
   * @see #targetBottomHeight
   * @see #getTargetBottomHeight
   */
  public void setTargetBottomHeight(final int _targetBottomHeight) {
    this.targetBottomHeight = _targetBottomHeight;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetConnectAttribute}.
   * 
   * @return value of instance variable {@link #targetConnectAttribute}
   * @see #targetConnectAttribute
   * @see #setTargetConnectAttribute
   */
  public Attribute getTargetConnectAttribute() {
    return this.targetConnectAttribute;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetConnectAttribute}.
   * 
   * @param _targetConnectAttribute
   *          new value for instance variable {@link #targetConnectAttribute}
   * @see #targetConnectAttribute
   * @see #getTargetConnectAttribute
   */
  public void setTargetConnectAttribute(final Attribute _targetConnectAttribute) {
    this.targetConnectAttribute = _targetConnectAttribute;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetCreateType}.
   * 
   * @return value of instance variable {@link #targetCreateType}
   * @see #targetCreateType
   * @see #setTargetCreateType
   */
  public Type getTargetCreateType() {
    return this.targetCreateType;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetCreateType}.
   * 
   * @param _targetCreateType
   *          new value for instance variable {@link #targetCreateType}
   * @see #targetCreateType
   * @see #getTargetCreateType
   */
  public void setTargetCreateType(final Type _targetCreateType) {
    this.targetCreateType = _targetCreateType;
  }

  /**
   * This is the setter method for the instance variable {@link #targetForm}.
   * 
   * @return value of instance variable {@link #targetForm}
   * @see #targetForm
   * @see #setTargetForm
   */
  public Form getTargetForm() {
    return this.targetForm;
  }

  /**
   * This is the setter method for the instance variable {@link #targetForm}.
   * 
   * @param _targetForm
   *          new value for instance variable {@link #targetForm}
   * @see #targetForm
   * @see #getTargetForm
   */
  public void setTargetForm(final Form _targetForm) {
    this.targetForm = _targetForm;
  }

  /**
   * This is the setter method for the instance variable {@link #targetMenu}.
   * 
   * @return value of instance variable {@link #targetMenu}
   * @see #targetMenu
   * @see #setTargetMenu
   */
  public Menu getTargetMenu() {
    return this.targetMenu;
  }

  /**
   * This is the setter method for the instance variable {@link #targetMenu}.
   * 
   * @param _targetMenu
   *          new value for instance variable {@link #targetMenu}
   * @see #targetMenu
   * @see #getTargetMenu
   */
  public void setTargetMenu(final Menu _targetMenu) {
    this.targetMenu = _targetMenu;
  }

  /**
   * This is the setter method for the instance variable {@link #targetMode}.
   * 
   * @return value of instance variable {@link #targetMode}
   * @see #targetMode
   * @see #setTargetMode
   */
  public int getTargetMode() {
    return this.targetMode;
  }

  /**
   * This is the setter method for the instance variable {@link #targetMode}.
   * 
   * @param _targetMode
   *          new value for instance variable {@link #targetMode}
   * @see #targetMode
   * @see #getTargetMode
   */
  public void setTargetMode(final int _targetMode) {
    this.targetMode = _targetMode;
  }

  /**
   * This is the setter method for the instance variable {@link #targetSearch}.
   * 
   * @return value of instance variable {@link #targetSearch}
   * @see #targetSearch
   * @see #setTargetSearch
   */
  public Search getTargetSearch() {
    return this.targetSearch;
  }

  /**
   * This is the setter method for the instance variable {@link #targetSearch}.
   * 
   * @param _targetSearch
   *          new value for instance variable {@link #targetSearch}
   * @see #targetSearch
   * @see #getTargetSearch
   */
  public void setTargetSearch(final Search _targetSearch) {
    this.targetSearch = _targetSearch;
  }

  /**
   * This is the setter method for the instance variable {@link #targetTable}.
   * 
   * @return value of instance variable {@link #targetTable}
   * @see #targetTable
   * @see #setTargetTable
   */
  public Table getTargetTable() {
    return this.targetTable;
  }

  /**
   * This is the setter method for the instance variable {@link #targetTable}.
   * 
   * @param _targetTable
   *          new value for instance variable {@link #targetTable}
   * @see #targetTable
   * @see #getTargetTable
   */
  public void setTargetTable(Table _targetTable) {
    this.targetTable = _targetTable;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetTableFilters}.
   * 
   * @return value of instance variable {@link #targetTableFilters}
   * @see #targetTableFilters
   * @see #setTargetTableFilters
   */
  public List<TargetTableFilter> getTargetTableFilters() {
    return this.targetTableFilters;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetTableFilters}.
   * 
   * @param _targetTableFilters
   *          new value for instance variable {@link #targetTableFilters}
   * @see #targetTableFilters
   * @see #getTargetTableFilters
   */
  private void setTargetTableFilters(
      final List<TargetTableFilter> _targetTableFilters) {
    this.targetTableFilters = _targetTableFilters;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetTableSortDirection}.
   * 
   * @return value of instance variable {@link #targetTableSortDirection}
   * @see #targetTableSortDirection
   * @see #setTargetTableSortDirection
   */
  public int getTargetTableSortDirection() {
    return this.targetTableSortDirection;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetTableSortDirection}.
   * 
   * @param _targetTableSortDirection
   *          new value for instance variable {@link #targetTableSortDirection}
   * @see #targetTableSortDirection
   * @see #getTargetTableSortDirection
   */
  public void setTargetTableSortDirection(final int _targetTableSortDirection) {
    this.targetTableSortDirection = _targetTableSortDirection;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetTableSortKey}.
   * 
   * @return value of instance variable {@link #targetTableSortKey}
   * @see #targetTableSortKey
   * @see #setTargetTableSortKey
   */
  public String getTargetTableSortKey() {
    return this.targetTableSortKey;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetTableSortKey}.
   * 
   * @param _targetTableSortKey
   *          new value for instance variable {@link #targetTableSortKey}
   * @see #targetTableSortKey
   * @see #getTargetTableSortKey
   */
  public void setTargetTableSortKey(final String _targetTableSortKey) {
    this.targetTableSortKey = _targetTableSortKey;
  }

  /**
   * This is the setter method for the instance variable {@link #targetTitle}.
   * 
   * @return value of instance variable {@link #targetTitle}
   * @see #targetTitle
   * @see #setTargetTitle
   */
  public String getTargetTitle() {
    return this.targetTitle;
  }

  /**
   * This is the setter method for the instance variable {@link #targetTitle}.
   * 
   * @param _targetTitle
   *          new value for instance variable {@link #targetTitle}
   * @see #targetTitle
   * @see #isTargetTitle
   */
  public void setTargetTitle(final String _targetTitle) {
    this.targetTitle = _targetTitle;
  }

  /**
   * The instance method returns the label of a command (or also menu). The
   * instance method looks in the DBProperties, if a property entry with prefix
   * <i>Command.</i> and name is found. This value is returned. If no entry is
   * found, the name of the command is returned.
   * 
   * @return label of the command (or menu)
   */
  public String getViewableName() {
    String name = getName();
    if (DBProperties.hasProperty("Command." + getName())) {
      name = DBProperties.getProperty("Command." + getName());
    }
    return name;
  }

  /**
   * This is the getter method for the instance variable {@link #windowHeight}.
   * 
   * @return value of instance variable {@link #windowHeight}
   * @see #windowHeight
   * @see #setWindowHeight
   */
  public int getWindowHeight() {
    return this.windowHeight;
  }

  /**
   * This is the setter method for the instance variable {@link #windowHeight}.
   * 
   * @param _windowHeight
   *          new value for instance variable {@link #windowHeight}
   * @see #windowHeight
   * @see #getWindowHeight
   */
  private void setWindowHeight(final int _windowHeight) {
    this.windowHeight = _windowHeight;
  }

  /**
   * This is the getter method for the instance variable {@link #windowWidth}.
   * 
   * @return value of instance variable {@link #windowWidth}
   * @see #windowWidth
   * @see #setWindowWidth
   */
  public int getWindowWidth() {
    return this.windowWidth;
  }

  /**
   * This is the setter method for the instance variable {@link #windowWidth}.
   * 
   * @param _windowWidth
   *          new value for instance variable {@link #windowWidth}
   * @see #windowWidth
   * @see #getWindowWidth
   */
  private void setWindowWidth(final int _windowWidth) {
    this.windowWidth = _windowWidth;
  }

  /**
   * This is the getter method for the instance variable {@link #askUser}.
   * 
   * @return value of instance variable {@link #askUser}
   * @see #askUser
   * @see #setAskUser
   */
  public boolean isAskUser() {
    return this.askUser;
  }

  /**
   * This is the setter method for the instance variable {@link #askUser}.
   * 
   * @param _askUser
   *          new value for instance variable {@link #askUser}
   * @see #askUser
   * @see #getAskUser
   */
  private void setAskUser(final boolean _askUser) {
    this.askUser = _askUser;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #defaultSelected}.
   * 
   * @return value of instance variable {@link #defaultSelected}
   * @see #defaultSelected
   * @see #setDefaultSelected
   */
  public boolean isDefaultSelected() {
    return this.defaultSelected;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #defaultSelected}.
   * 
   * @param _defaultSelected
   *          new value for instance variable {@link #defaultSelected}
   * @see #defaultSelected
   * @see #isDefaultSelected
   */
  public void setDefaultSelected(final boolean _defaultSelected) {
    this.defaultSelected = _defaultSelected;
  }

  /**
   * This is the setter method for the instance variable {@link #submit}.
   * 
   * @return value of instance variable {@link #submit}
   * @see #submit
   * @see #setSubmit
   */
  public boolean isSubmit() {
    return this.submit;
  }

  /**
   * This is the setter method for the instance variable {@link #submit}.
   * 
   * @param _submit
   *          new value for instance variable {@link #submit}
   * @see #submit
   * @see #isSubmit
   */
  public void setSubmit(final boolean _submit) {
    this.submit = _submit;
  }

  /**
   * Test, if the value of instance variable {@link #target} is equal to
   * {@link #TARGET_CONTENT}.
   * 
   * @return <i>true</i> if value is equal, otherwise false
   * @see #target
   * @see #getTarget
   */
  public boolean isTargetContent() {
    return getTarget() == TARGET_CONTENT;
  }

  /**
   * Test, if the value of instance variable {@link #target} is equal to
   * {@link #TARGET_HIDDEN}.
   * 
   * @return <i>true</i> if value is equal, otherwise false
   * @see #target
   * @see #getTarget
   */
  public boolean isTargetHidden() {
    return getTarget() == TARGET_HIDDEN;
  }

  /**
   * Test, if the value of instance variable {@link #target} is equal to
   * {@link #TARGET_POPUP}.
   * 
   * @return <i>true</i> if value is equal, otherwise false
   * @see #target
   * @see #getTarget
   */
  public boolean isTargetPopup() {
    return getTarget() == TARGET_POPUP;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetShowCheckBoxes}.
   * 
   * @return value of instance variable {@link #targetShowCheckBoxes}
   * @see #targetShowCheckBoxes
   * @see #setTargetShowCheckBoxes
   */
  public boolean isTargetShowCheckBoxes() {
    return this.targetShowCheckBoxes;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #targetShowCheckBoxes}.
   * 
   * @param _targetShowCheckBoxes
   *          new value for instance variable {@link #targetShowCheckBoxes}
   * @see #targetShowCheckBoxes
   * @see #isTargetShowCheckBoxes
   */
  public void setTargetShowCheckBoxes(final boolean _targetShowCheckBoxes) {
    this.targetShowCheckBoxes = _targetShowCheckBoxes;
  }

  /**
   * @param _context
   *          eFaps context for this request
   * @param _linkType
   *          type of the link property
   * @param _toId
   *          to id
   * @param _toType
   *          to type
   * @param _toName
   *          to name
   */
  protected void setLinkProperty(final EFapsClassName _linkType,
      final long _toId, final EFapsClassName _toType, final String _toName)
      throws Exception {
    switch (_linkType) {
      case LINK_ICON:
        setIcon(RequestHandler.replaceMacrosInUrl("${ROOTURL}/servlet/image/"
            + _toName));
      break;
      case LINK_TARGET_FORM:
        setTargetForm(Form.get(_toId));
      break;
      case LINK_TARGET_MENU:
        setTargetMenu(Menu.get(_toId));
      break;
      case LINK_TARGET_SEARCH:
        setTargetSearch(Search.get(_toName));
      break;
      case LINK_TARGET_TABLE:
        setTargetTable(Table.get(_toId));
      break;
      default:
        super.setLinkProperty(_linkType, _toId, _toType, _toName);
    }
  }

  /**
   * The instance method sets a new property value.
   * 
   * @param _context
   *          eFaps context for this request
   * @param _name
   *          name of the property
   * @param _value
   *          value of the property
   */
  protected void setProperty(final String _name, final String _value)
      throws CacheReloadException {
    if (_name.equals("AskUser")) {
      if ("true".equalsIgnoreCase(_value)) {
        setAskUser(true);
      } else {
        setAskUser(false);
      }
    } else if (_name.equals("DefaultSelected")) {
      if ("true".equalsIgnoreCase(_value)) {
        setDefaultSelected(true);
      } else {
        setDefaultSelected(false);
      }
    } else if (_name.equals("HRef")) {
      setReference(RequestHandler.replaceMacrosInUrl(_value));
    } else if (_name.equals("Icon")) {
      setIcon(RequestHandler.replaceMacrosInUrl(_value));
    } else if (_name.equals("Label")) {
      setLabel(_value);
    } else if (_name.equals("Submit")) {
      if (_value.equals("true")) {
        setSubmit(true);
      }
    } else if (_name.equals("Target")) {
      if (_value.equals("content")) {
        setTarget(TARGET_CONTENT);
      } else if (_value.equals("hidden")) {
        setTarget(TARGET_HIDDEN);
      } else if (_value.equals("popup")) {
        setTarget(TARGET_POPUP);
      }
    } else if (_name.equals("TargetBottomHeight")) {
      setTargetBottomHeight(Integer.parseInt(_value));
    } else if (_name.equals("TargetConnectAttribute")) {
      setTargetConnectAttribute(Attribute.get(_value));
      // "TargetConnectChildAttribute"
      // "TargetConnectParentAttribute"
      // "TargetConnectType"
    } else if (_name.equals("TargetCreateType")) {
      setTargetCreateType(Type.get(_value));
    } else if (_name.equals("TargetMode")) {
      if (_value.equals("create")) {
        setTargetMode(TARGET_MODE_CREATE);
      } else if (_value.equals("edit")) {
        setTargetMode(TARGET_MODE_EDIT);
      } else if (_value.equals("connect")) {
        setTargetMode(TARGET_MODE_CONNECT);
      } else if (_value.equals("search")) {
        setTargetMode(TARGET_MODE_SEARCH);
      } else if (_value.equals("view")) {
        setTargetMode(TARGET_MODE_VIEW);
      }
    } else if (_name.equals("TargetShowCheckBoxes")) {
      if ("true".equalsIgnoreCase(_value)) {
        setTargetShowCheckBoxes(true);
      } else {
        setTargetShowCheckBoxes(false);
      }
    } else if (_name.startsWith("TargetTableFilter")) {
      if (getTargetTableFilters() == null) {
        setTargetTableFilters(new Vector<TargetTableFilter>());
      }
      getTargetTableFilters().add(new TargetTableFilter(_value));
    } else if (_name.equals("TargetTableSortKey")) {
      setTargetTableSortKey(_value);
    } else if (_name.equals("TargetTableSortDirection")) {
      if (_value.equals("desc")) {
        setTargetTableSortDirection(TABLE_SORT_DIRECTION_DESC);
      } else {
        setTargetTableSortDirection(TABLE_SORT_DIRECTION_ASC);
      }
    } else if (_name.equals("TargetTitle")) {
      setTargetTitle(_value);
    } else if (_name.equals("WindowHeight")) {
      setWindowHeight(Integer.parseInt(_value));
    } else if (_name.equals("WindowWidth")) {
      setWindowWidth(Integer.parseInt(_value));
    } else {
      super.setProperty(_name, _value);
    }
  }

  /**
   * The class stores the filter of the target table.
   */
  public class TargetTableFilter {

    /**
     * The instance variable stores the sql clause.
     * 
     * @see #getClause
     * @see #setClause
     */
    private String clause = null;

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Constructor to create a new target table filter instance.
     * 
     * @param _clause
     *          sql where clause for this filter
     */
    private TargetTableFilter(final String _clause) {
      setClause(_clause);
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * This is the setter method for the instance variable {@link #clause}.
     * 
     * @return value of instance variable {@link #clause}
     * @see #clause
     * @see #setClause
     */
    public String getClause() {
      return this.clause;
    }

    /**
     * This is the setter method for the instance variable {@link #clause}.
     * 
     * @param _clause
     *          new value for instance variable {@link #clause}
     * @see #clause
     * @see #getClause
     */
    public void setClause(final String _clause) {
      this.clause = _clause;
    }
  }
}