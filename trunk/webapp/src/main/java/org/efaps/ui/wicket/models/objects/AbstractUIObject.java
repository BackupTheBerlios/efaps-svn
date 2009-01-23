/*
 * Copyright 2003 - 2009 The eFaps Team
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

package org.efaps.ui.wicket.models.objects;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.wicket.IClusterable;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;

import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.EventType;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.Command;
import org.efaps.admin.ui.Menu;
import org.efaps.admin.ui.Search;
import org.efaps.admin.ui.AbstractCommand.Target;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.beans.ValueList;
import org.efaps.beans.valueparser.ParseException;
import org.efaps.beans.valueparser.ValueParser;
import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.ui.wicket.EFapsSession;
import org.efaps.ui.wicket.Opener;
import org.efaps.ui.wicket.pages.error.ErrorPage;
import org.efaps.util.EFapsException;

/**
 * @author jmox
 * @version $Id$
 */
public abstract class AbstractUIObject implements IClusterable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * This instance variable stores the UUID of the CommandAbstract wich was
   * originaly called from the Frontend and let to the construction of this
   * model.
   *
   * @see #getCallingCommandUUID()
   * @see #getCallingCommand()
   * @see #setCallingCommandUUID(UUID)
   */
  private UUID callingCommandUUID;

  /**
   * The instance variable stores the UUID of the Command for this Model.
   *
   * @see #getCommandUUID()
   * @see #getCommand
   */
  private UUID commandUUID;

  /**
   * The instance variable is the flag if this class instance is already
   * initialised.
   *
   * @see #isInitialised
   * @see #setInitialised
   */
  private boolean initialised = false;

  /**
   * Stores the maximal group count for a row.
   *
   * @see #getMaxGroupCount
   * @see #setMaxGroupCount
   */
  private int maxGroupCount = 1;

  /**
   * The instance variable stores the mode of the form.
   *
   * @see #getMode
   * @see #setMode
   */
  private TargetMode mode = TargetMode.UNKNOWN;

  /**
   * This instance variable stores the OID of the Instance.
   *
   * @see #initialise
   * @see #getOid()
   */
  private String oid;

  /**
   * Store the instance which calls this form, table, menu etc.
   *
   * @see #initialise
   * @see #getCallInstance
   */
  private Instance callInstance = null;

  /**
   * This instance variable stores, if the Model is supposed to be submited.
   *
   * @see #isSubmit()
   * @see #setSubmit(boolean)
   */
  private boolean submit = false;

  /**
   * This instance variable stores the Target of this Model.
   *
   * @see #getTarget()
   */
  private Target target = Target.UNKNOWN;

  /**
   * In case that the model was opened as a popup this variable stores the id
   * of the opener, so that it can be accessed in the EFapsSession.
   */
  private String openerId;


  /**
   * Constructor evaluating the UUID for the command and the oid from an
   * Opener instance.
   *
   * @param _parameters  PageParameters for this Model
   */
  public AbstractUIObject(final PageParameters _parameters) {
    if (_parameters.get(Opener.OPENER_PARAKEY) instanceof String[]) {
      this.openerId = ((String[]) _parameters.get(Opener.OPENER_PARAKEY))[0];
    } else {
      this.openerId = (String) _parameters.get(Opener.OPENER_PARAKEY);
    }
    final Opener opener
                      = ((EFapsSession) Session.get()).getOpener(this.openerId);
    final AbstractUIObject uiObject
                           = ((AbstractUIObject) opener.getModel().getObject());
    initialise(uiObject.getCommandUUID(), uiObject.getOid(), this.openerId);
  }

  /**
   * Constructor.
   *
   * @param _commandUUID    UUID for this Model
   * @param _oid            oid for this Model
   */
  public AbstractUIObject(final UUID _commandUUID, final String _oid) {
    this(_commandUUID, _oid, null);
  }

  /**
   * Constructor.
   *
   * @param _commandUUID    UUID for this Model
   * @param _oid            oid for this Model
   * @param _openerId       id of the opener
   */
  public AbstractUIObject(final UUID _commandUUID, final String _oid,
                          final String _openerId) {
    initialise(_commandUUID, _oid, _openerId);
  }

  /**
   * Method initializes the model.
   *
   * @param _commandUUID    UUID for this Model
   * @param _oid            oid for this Model
   * @param _openerId       id of the opener
   */
  private void initialise(final UUID _commandUUID, final String _oid,
                          final String _openerId) {
    this.openerId = _openerId;

    this.oid = _oid;
    if ((this.oid != null) && (this.oid.length() > 0))  {
      this.callInstance = new Instance(this.oid);
    }
    final AbstractCommand command = getCommand(_commandUUID);
    this.commandUUID = command.getUUID();
    this.mode = command.getTargetMode();
    this.target = command.getTarget();
    this.submit = command.isSubmit();
    if (command.getTargetSearch() != null && !(this instanceof UIMenuItem)) {
      this.callingCommandUUID = this.commandUUID;
      this.commandUUID =
          command.getTargetSearch().getDefaultCommand().getUUID();
      setMode(TargetMode.SEARCH);
      if (command.hasEvents(EventType.UI_COMMAND_EXECUTE)) {
        this.submit = true;
      }
    }

  }

  /**
   * This Method resets the Model, so that the next time the Model is going to
   * be connected, the underlying Data will be received newly from the
   * eFapsDataBase.
   */
  public abstract void resetModel();

  /**
   * Get the CommandAbstract which was originally called from the Frontend and
   * let to the construction of this model.
   *
   * @see #callingCommandUUID
   * @return the calling CommandAbstract
   */
  public AbstractCommand getCallingCommand() {
    AbstractCommand cmd = Command.get(this.callingCommandUUID);
    if (cmd == null) {
      cmd = Menu.get(this.callingCommandUUID);
    }
    return cmd;
  }

  /**
   * This is the getter method for the instance variable
   * {@link #callingCommandUUID}.
   *
   * @return value of instance variable {@link #commandUUID}
   */
  public UUID getCallingCommandUUID() {
    return this.callingCommandUUID;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #callingCommandUUID}.
   *
   * @param _uuid
   *                UUID of the CommandAbstract
   */
  public void setCallingCommandUUID(final UUID _uuid) {
    this.callingCommandUUID = _uuid;
  }

  /**
   * get the CommandAbstract for the instance variable {@link #commandUUID}.
   *
   * @return CommandAbstract for the instance variable {@link #commandUUID}
   * @see #command
   */
  public AbstractCommand getCommand() {
    AbstractCommand cmd = Command.get(this.commandUUID);
    if (cmd == null) {
      cmd = Menu.get(this.commandUUID);
    }
    if (cmd == null) {
      cmd = Search.get(this.commandUUID);
    }
    return cmd;
  }

  /**
   * For given UUID of command / menu / Search, the related command / menu
   * /search Java instance is searched and, if found, returned.
   *
   * @param _uuid UUID of searched command object
   * @return found command / menu instance, or <code>null</code> if not found
   */
  protected AbstractCommand getCommand(final UUID _uuid) {
    AbstractCommand cmd = Command.get(_uuid);
    if (cmd == null) {
      cmd = Menu.get(_uuid);
      if (cmd == null) {
        cmd = Search.get(_uuid);
      }
    }
    return cmd;
  }

  /**
   * This is the getter method for the instance variable {@link #commandUUID}.
   *
   * @return value of instance variable {@link #commandUUID}
   */
  public UUID getCommandUUID() {
    return this.commandUUID;
  }

  /**
   * This is the setter method for the instance variable {@link #commandUUID}.
   *
   * @param _uuid
   *                UUID to set for teh instance varaiable {@link #commandUUID}.
   */
  public void setCommandUUID(final UUID _uuid) {
    this.commandUUID = _uuid;
  }

  /**
   * Getter method for instance variable {@link #openerId}.
   *
   * @return value of instance variable {@link #openerId}
   */
  public String getOpenerId() {
    return this.openerId;
  }

  /**
   * Setter method for instance variable {@link #openerId}.
   *
   * @param _openerId value for instance variable {@link #openerId}
   */
  public void setOpenerId(final String _openerId) {
    this.openerId = _openerId;
  }

  /**
   * This is the getter method for the instance variable {@link #maxGroupCount}.
   *
   * @return value of instance variable {@link #maxGroupCount}
   * @see #maxGroupCount
   * @see #setMaxGroupCount
   */
  public int getMaxGroupCount() {
    return this.maxGroupCount;
  }

  /**
   * This is the setter method for the instance variable {@link #maxGroupCount}.
   *
   * @param _maxGroupCount
   *                new value for instance variable {@link #maxGroupCount}
   * @see #maxGroupCount
   * @see #getMaxGroupCount
   */
  protected void setMaxGroupCount(final int _maxGroupCount) {
    this.maxGroupCount = _maxGroupCount;
  }

  /**
   * This is the getter method for the instance variable {@link #mode}.
   *
   * @return value of instance variable {@link #mode}
   * @see #mode
   * @see #setMode
   */
  public TargetMode getMode() {
    return this.mode;
  }

  /**
   * This is the setter method for the instance variable {@link #mode}.
   *
   * @param _mode
   *                new value for instance variable {@link #mode}
   * @see #mode
   * @see #getMode
   */
  protected void setMode(final TargetMode _mode) {
    this.mode = _mode;
  }

  /**
   * This is the getter method for the instance variable {@link #oid}.
   *
   * @return value of instance variable {@link #oid}
   */
  public String getOid() {
    return this.oid;
  }

  /**
   * This is the getter method for the instance variable {@link #callInstance}.
   *
   * @return value of instance variable {@link #callInstance}
   */
  public Instance getCallInstance() {
    return this.callInstance;
  }

  /**
   * This is the getter method for the instance variable {@link #target}.
   *
   * @return value of instance variable {@link #target}
   */
  public Target getTarget() {
    return this.target;
  }

  /**
   * This method retrieves the Value for the Titel from the eFaps Database.
   *
   * @return Value of the Title
   * @throws Exception
   */
  public String getTitle() {
    String title = DBProperties.getProperty(this.getCommand().getName()
                                                                + ".Title");
    try {

      if ((title != null) && (getCallInstance() != null)) {
        final SearchQuery query = new SearchQuery();
        query.setObject(getCallInstance());
        final ValueParser parser = new ValueParser(new StringReader(title));
        ValueList list;
        list = parser.ExpressionString();
        list.makeSelect(query);
        if (query.selectSize() > 0) {
          query.execute();
          if (query.next()) {
            title = list.makeString(getCallInstance(), query);
          }
          query.close();
        }
      }
    } catch (final ParseException e) {
      throw new RestartResponseException(new ErrorPage(new EFapsException(this
          .getClass(), "", "Error reading the Title")));
    } catch (final Exception e) {
      throw new RestartResponseException(new ErrorPage(new EFapsException(this
          .getClass(), "", "Error reading the Title")));
    }

    return title;
  }

  /**
   * Method to check if mode is create.
   *
   * @see #mode
   * @return true if mode is create
   */
  public boolean isCreateMode() {
    return getMode() == TargetMode.CREATE;
  }

  /**
   * Method to check if mode is edit.
   *
   * @see #mode
   * @return true if mode is edit
   */
  public boolean isEditMode() {
    return getMode() == TargetMode.EDIT;
  }

  /**
   * Method to check if mode is search.
   *
   * @see #mode
   * @return true if mode is search
   */
  public boolean isSearchMode() {
    return getMode() == TargetMode.SEARCH;
  }

  /**
   * Method to check if mode is view.
   *
   * @see #mode
   * @return true if mode is view
   */
  public boolean isViewMode() {
    return getMode() == TargetMode.VIEW || getMode() == TargetMode.UNKNOWN;
  }

  /**
   * This is the getter method for the instance variable {@link #initialised}.
   *
   * @return value of instance variable {@link #initialised}
   * @see #initialised
   * @see #setInitialised
   */
  public boolean isInitialised() {
    return this.initialised;
  }

  /**
   * This is the setter method for the instance variable {@link #initialised}.
   *
   * @param _initialised
   *                new value for instance variable {@link #initialised}
   * @see #initialised
   * @see #isInitialised
   */
  public void setInitialised(final boolean _initialised) {
    this.initialised = _initialised;
  }

  /**
   * This is the getter method for the instance variable {@link #submit}.
   *
   * @return value of instance variable {@link #submit}
   * @see #setSubmit(boolean)
   */
  public boolean isSubmit() {
    return this.submit;
  }

  /**
   * This is the setter method for the instance variable {@link #submit}.
   * @param _submit submit
   * @see #isSubmit()
   */
  public void setSubmit(final boolean _submit) {
    this.submit = _submit;
  }

  /**
   * This method executes the Events which are related to this Model. It will
   * take the Events of the CallingCommand {@link #callingCommandUUID}, if it
   * is declared, otherwise it will take the Events of the Command
   * {@link #commandUUID}. The Method also adds the oid {@link #oid} to the
   * Context, so that it is accessible for the esjp.<br>
   * This method throws an eFpasError to provide the possibility for different
   * responses in the components.
   *
   * @param _others   The values will be attached to the call as
   *                  {@link ParameterValues#OTHERS}
   * @throws EFapsException on error
   * @return  List of Returns
   */
  public List<Return> executeEvents(final Object _others)
      throws EFapsException {
    List<Return> ret = new ArrayList<Return>();
    AbstractCommand command;
    if (this.callingCommandUUID == null) {
      command = this.getCommand();
    } else {
      command = getCallingCommand();
    }

    if (command.hasEvents(EventType.UI_COMMAND_EXECUTE)) {
      if (getCallInstance() == null) {
        ret = command.executeEvents(EventType.UI_COMMAND_EXECUTE,
                                    ParameterValues.OTHERS, _others,
                                    ParameterValues.PARAMETERS,
                                    Context.getThreadContext().getParameters());
      } else {
        final String[] contextoid = { getOid() };
        Context.getThreadContext().getParameters().put("oid", contextoid);
        ret = command.executeEvents(EventType.UI_COMMAND_EXECUTE,
                               ParameterValues.CALL_INSTANCE, getCallInstance(),
                               ParameterValues.INSTANCE, getCallInstance(),
                               ParameterValues.OTHERS, _others,
                               ParameterValues.PARAMETERS,
                                    Context.getThreadContext().getParameters());
      }
    }
    return ret;
  }

  /**
   * This method executes the Validate-Events wich are related to this Model. It
   * will take the Events of the Command {@link #commandUUID}.
   *
   * @return List with Return from the esjp
   */
  public List<Return> validate() {
    final AbstractCommand command = this.getCommand();
    try {
      return command.executeEvents(EventType.UI_VALIDATE, (Object) null);
    } catch (final EFapsException e) {
      throw new RestartResponseException(new ErrorPage(e));
    }
  }
}
