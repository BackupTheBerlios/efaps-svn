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

package org.efaps.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.efaps.admin.datamodel.Type;
import org.efaps.admin.user.Person;
import org.efaps.admin.user.UserAttributesSet;
import org.efaps.admin.user.UserAttributesSet.UserAttributesDefinition;
import org.efaps.db.databases.AbstractDatabase;
import org.efaps.db.transaction.ConnectionResource;
import org.efaps.db.transaction.JDBCStoreResource;
import org.efaps.db.transaction.StoreResource;
import org.efaps.db.transaction.VFSStoreResource;
import org.efaps.util.EFapsException;

/**
 * @author tmo
 * @version $Id$
 */
public class Context {

  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Logging instance used in this class.
   */
  private static final Logger LOG = LoggerFactory.getLogger(Context.class);

  /**
   * The static variable holds the resource name for the JDBC database
   * connection.
   */
  private static final String RESOURCE_DATASOURCE   = "eFaps/jdbc";

  /**
   * The static variable holds the resource name for the database type.
   */
  private static final String RESOURCE_DBTYPE = "eFaps/dbType";

  /**
   * Resource name of the transaction manager.
   */
  private static final String RESOURCE_TRANSMANAG = "eFaps/transactionManager";

  /**
   * Static variable storing the database type.
   */
  private static AbstractDatabase DBTYPE = null;

  /**
   *
   */
  private static DataSource DATASOURCE = null;

  /**
   * Stores the transaction manager.
   *
   * @see #setTransactionManager
   */
  private static TransactionManager TRANSMANAG = null;

  static  {
    try {
      InitialContext initCtx = new InitialContext();
      javax.naming.Context envCtx = (javax.naming.Context) initCtx.lookup("java:comp/env");

      DBTYPE = (AbstractDatabase) envCtx.lookup(RESOURCE_DBTYPE);
      DATASOURCE = (DataSource) envCtx.lookup(RESOURCE_DATASOURCE);
      TRANSMANAG = (TransactionManager) envCtx.lookup(RESOURCE_TRANSMANAG);
    } catch (NamingException e) {
      e.printStackTrace();
      throw new Error(e);
    }
  }

  /**
   * Each thread has his own context object. The value is automatically
   * assigned from the filter class.
   */
  private static ThreadLocal<Context> THREADCONTEXT
                                              = new ThreadLocal<Context>();

  /////////////////////////////////////////////////////////////////////////////
  // instance variables

  /**
   * The instance variable stores all open instances of {@link StoreResource}.
   *
   * @see #getStoreResource(Instance)
   * @see #getStoreResource(Type,long)
   */
  private final Set<StoreResource> storeStore = new HashSet<StoreResource>();

  /**
   * Stores all created connection resources.
   */
  private final Set<ConnectionResource> connectionStore
                                      = new HashSet<ConnectionResource>();

  /**
   * Stack used to store returned connections for reuse.
   */
  private final Stack<ConnectionResource> connectionStack
                                      = new Stack<ConnectionResource>();

  private final Transaction transaction;

  /**
   * This is the instance variable for the SQL Connection to the database.
   *
   * @see #getConnection
   * @see #setConnection
   */
  private Connection connection = null;

  /**
   * This instance variable represents the user name of the context.
   *
   * @see #getPerson
   */
  private Person person = null;

  /**
   * The instance variable stores the locale object defined by the user
   * interface (locale object of the current logged in eFaps user).
   * The information is needed to create localised information within eFaps.
   *
   * @see #getLocale
   * @see #setLocale(Locale)
   */
  private  Locale locale;

  /**
   * The parameters used to open a new thread context are stored in this
   * instance variable (e.g. the request parameters from a http servlet are
   * stored in this variable).
   *
   * @see #getParameters
   */
  private final Map<String, String[]> parameters;

  /**
   * The file parameters used to open a new thread context are stored in this
   * instance variable (e.g. the request parameters from a http servlet or
   * in the shell the parameters from the command shell). The file item
   * represents one file which includes an input stream, the name and the
   * length of the file.
   *
   * @see #getFileParameters
   * @todo replace FileItem against own implementation
   */
  private final Map<String, FileItem> fileParameters;

  /**
   * A map to be able to set attributes with a lifetime of a request (e.g.
   * servlet request).
   *
   * @see #containsRequestAttribute
   * @see #getRequestAttribute
   * @see #setRequestAttribute
   */
  private final Map<String, Object> requestAttributes = new HashMap<String, Object>();

  /**
   * A map to be able to set attributes with a lifetime of a session (e.g. as
   * long as the user is logged in).
   *
   * @see #containsSessionAttribute
   * @see #getSessionAttribute
   * @see #setSessionAttribute
   */
  private Map<String, Object> sessionAttributes = new HashMap<String, Object>();

  /////////////////////////////////////////////////////////////////////////////
  // constructors / destructors

  /**
   * Constructor for
   *
   * @see #person
   * @see #locale
   */
  private Context(final Transaction _transaction,
                  final Person _person,
                  final Locale _locale,
                  final Map < String, Object > _sessionAttributes,
                  final Map < String, String[] > _parameters,
                  final Map < String, FileItem > _fileParameters)
                                                      throws EFapsException  {
    if (LOG.isDebugEnabled())  {
      LOG.debug("create new context for " + _person);
    }
    this.transaction = _transaction;
    this.person = _person;
    this.locale = _locale;
    this.parameters = (_parameters == null)
                              ? new HashMap < String, String[] > ()
                              : _parameters;
    this.fileParameters = (_fileParameters == null)
                              ? new HashMap < String, FileItem > ()
                              : _fileParameters;
    this.sessionAttributes = (_sessionAttributes == null)
                              ? new HashMap < String, Object > ()
                              : _sessionAttributes;
try  {
    setConnection(DATASOURCE.getConnection());
  getConnection().setAutoCommit(true);
} catch (SQLException e)  {
  LOG.error("could not get a sql connection", e);
// TODO: LOG + Exception
e.printStackTrace();
}
  }


  /**
   * Destructor of class <code>Context</code>.
   */
  @Override
  public final void finalize()  {
    if (LOG.isDebugEnabled())  {
      LOG.debug("finalize context for " + this.person);
      LOG.debug("connection is " + getConnection());
    }
    try  {
      getConnection().close();
    } catch (Exception e)  {
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  // instance methods

  /**
   * The method tests if all resources (JDBC connection and store resources)
   * are closed, that means that the resources are freeed and returned for
   * reuse.
   *
   * @return <i>true</i> if all resources are closed, otherwise <i>false</i>
   *         is returned
   * @see #connectionStore
   * @see #storeStore
   */
  public boolean allConnectionClosed()  {
    boolean closed = true;

    for (ConnectionResource con : this.connectionStore)  {
      if (con.isOpened())  {
        closed = false;
        break;
      }
    }

    if (closed)  {
      for (StoreResource store : this.storeStore)  {
        if (store.isOpened())  {
          closed = false;
          break;
        }
      }
    }

    return closed;
  }

  /**
   * Close this contexts, meaning this context object is removed as thread
   * context.<br/>
   * If not all connection are closed, all connection are closed.
   * @todo better description
   */
  public void close()  {
    if (LOG.isDebugEnabled())  {
      LOG.debug("close context for " + this.person);
      LOG.debug("connection is " + getConnection());
    }
    try  {
      getConnection().close();
    } catch (Exception e)  {
    }
    setConnection(null);
    if ((THREADCONTEXT.get() != null) && (THREADCONTEXT.get() == this))  {
      THREADCONTEXT.set(null);
    }
    // check if all JDBC connection are close...
    for (ConnectionResource con : this.connectionStore)  {
      try  {
        if ((con.getConnection() != null) && !con.getConnection().isClosed())  {
          con.getConnection().close();
          LOG.error("connection was not closed!");
        }
      } catch (SQLException e)  {
        LOG.error("QLException is thrown while trying to get close status of "
                  + "connection or while trying to close", e);
      }
    }
  }


  /**
   *
   */
  public void abort() throws EFapsException  {
    try  {
      this.transaction.setRollbackOnly();
    } catch (SystemException e)  {
      throw new EFapsException(getClass(), "abort.SystemException", e);
    }
  }

  /**
   * Returns a opened connection resource. If a previous close connection
   * resource already exists, this already existing connection resource is
   * returned.
   *
   * @return opened connection resource
   */
  public ConnectionResource getConnectionResource() throws EFapsException  {
    ConnectionResource con = null;

    if (this.connectionStack.isEmpty())  {
try  {
      con = new ConnectionResource(this, DATASOURCE.getConnection());
} catch (SQLException e)  {
e.printStackTrace();
  throw new EFapsException(getClass(), "getConnectionResource.SQLException", e);
}
      this.connectionStore.add(con);
    } else  {
      con = this.connectionStack.pop();
    }

    con.open();
//System.out.println("getConnectionResource.con="+con);

    return con;
  }

  /**
   *
   */
  public void returnConnectionResource(ConnectionResource _con)  {
//System.out.println("returnConnectionResource.con="+_con);
    if (_con == null)  {
// throw new EFapsException();
    }
    this.connectionStack.push(_con);
  }

  /**
   * @see #getStoreResource(Type,long)
   */
  public StoreResource getStoreResource(final Instance _instance) throws EFapsException  {
    return getStoreResource(_instance.getType(), _instance.getId());
  }

  /**
   *
   */
  public StoreResource getStoreResource(final Type _type, final long _fileId) throws EFapsException  {
    StoreResource storeRsrc = null;

// TODO: dynamic class loading instead of hard coded store resource name
String provider  = _type.getProperty("StoreResource");
if (provider.equals("org.efaps.db.transaction.JDBCStoreResource"))  {
  storeRsrc = new JDBCStoreResource(this, _type, _fileId);
} else  {
  storeRsrc = new VFSStoreResource(this, _type, _fileId);
}
    storeRsrc.open();
    this.storeStore.add(storeRsrc);
    return storeRsrc;
  }

  /**
   * If a person is assigned to this context, the id of this person is
   * returned. Otherwise the default person id value is returned. The method
   * guarantees to return value which is valid!<br/>
   * The value could be used e.g. if a a value is inserted into the database
   * and the person id is needed for the creator and / or modifier.
   *
   * @return person id of current person or default person id value
   */
  public long getPersonId()  {
    long ret = 1;

    if (this.person != null)  {
      ret = this.person.getId();
    }
    return ret;
  }

  /**
   *
   */
  public String getParameter(final String _key)  {
    String value = null;
    if (this.parameters != null)  {
      String[] values = this.parameters.get(_key);
      if ((values != null) && (values.length > 0))  {
        value = values[0];
      }
    }
    return value;
  }

  /**
   * Returns true if request attributes maps one or more keys to the specified
   * object. More formally, returns <i>true</i> if and only if the request
   * attributes contains at least one mapping to a object o such that
   * (o==null ? o==null : o.equals(o)).
   *
   * @param _key  key whose presence in the request attributes is to be tested
   * @return <i>true</i> if the request attributes contains a mapping for given
   *         key, otherwise <i>false</i>
   * @see #requestAttributes
   * @see #getRequestAttribute
   * @see #setRequestAttribute
   */
  public boolean containsRequestAttribute(final String _key) {
      return this.requestAttributes.containsKey(_key);
  }

  /**
   * Returns the object to which this request attributes maps the specified
   * key. Returns <code>null</code> if the request attributes  contains no
   * mapping for this key. A return value of <code>null</code> does not
   * necessarily indicate that the request attributes contains no mapping for
   * the key; it's also possible that the request attributes explicitly maps
   * the key to null. The {@link #containsRequestAttribute} operation may be
   * used to distinguish these two cases.<br/>
   * More formally, if the request attributes contains a mapping from a key k
   * to a object o such that (key==null ? k==null : key.equals(k)), then this
   * method returns o; otherwise it returns <code>null</code> (there can be at
   * most one such mapping).
   *
   * @param _key    key name of the mapped attribute to be returned
   * @return object to which the request attribute contains a mapping for
   *         specified key, or <code>null</code> if not specified in the
   *         request attributes
   * @see #requestAttributes
   * @see #containsRequestAttribute
   * @see #setRequestAttribute
   */
  public Object getRequestAttribute(final String _key) {
      return this.requestAttributes.get(_key);
  }

  /**
   * Associates the specified value with the specified key in the request
   * attributes. If the request attributes previously contained a mapping for
   * this key, the old value is replaced by the specified value.
   *
   * @param _key    key name of the attribute to set
   * @return
   * @see #requestAttributes
   * @see #containsRequestAttribute
   * @see #getRequestAttribute
   */
  public Object setRequestAttribute(final String _key, final Object _value) {
      return this.requestAttributes.put(_key, _value);
  }

  /**
   * Returns true if session attributes maps one or more keys to the specified
   * object. More formally, returns <i>true</i> if and only if the session
   * attributes contains at least one mapping to a object o such that
   * (o==null ? o==null : o.equals(o)).
   *
   * @param _key  key whose presence in the session attributes is to be tested
   * @return <i>true</i> if the session attributes contains a mapping for given
   *         key, otherwise <i>false</i>
   * @see #sessionAttributes
   * @see #getSessionAttribute
   * @see #setSessionAttribute
   */
  public boolean containsSessionAttribute(final String _key) {
      return this.sessionAttributes.containsKey(_key);
  }

  /**
   * Returns the object to which this session attributes maps the specified
   * key. Returns <code>null</code> if the session attributes  contains no
   * mapping for this key. A return value of <code>null</code> does not
   * necessarily indicate that the session attributes contains no mapping for
   * the key; it's also possible that the session attributes explicitly maps
   * the key to null. The {@link #containsSessionAttribute} operation may be
   * used to distinguish these two cases.<br/>
   * More formally, if the session attributes contains a mapping from a key k
   * to a object o such that (key==null ? k==null : key.equals(k)), then this
   * method returns o; otherwise it returns <code>null</code> (there can be at
   * most one such mapping).
   *
   * @param _key    key name of the mapped attribute to be returned
   * @return object to which the session attribute contains a mapping for
   *         specified key, or <code>null</code> if not specified in the
   *         session attributes
   * @see #sessionAttributes
   * @see #containsSessionAttribute
   * @see #setSessionAttribute
   */
  public Object getSessionAttribute(final String _key) {
      return this.sessionAttributes.get(_key);
  }

  /**
   * Associates the specified value with the specified key in the session
   * attributes. If the session attributes previously contained a mapping for
   * this key, the old value is replaced by the specified value.
   *
   * @param _key    key name of the attribute to set
   * @return
   * @see #sessionAttributes
   * @see #containsSessionAttribute
   * @see #getSessionAttribute
   */
  public Object setSessionAttribute(final String _key, final Object _value) {
    return this.sessionAttributes.put(_key, _value);
  }

  /**
   * This method retriefs a UserAttribute of the Person this Context belongs to.
   * The UserAttributes are stored in the {@link #sessionAttributes} Map,
   * therefore are thought to be valid for one session.
   *
   * @param _key
   *                key to Search for
   * @return String with the value
   * @throws EFapsException
   */
  public String getUserAttribute(final String _key) throws EFapsException {
    if (containsSessionAttribute(UserAttributesSet.CONTEXTMAPKEY)) {
      return ((UserAttributesSet) getSessionAttribute(UserAttributesSet.CONTEXTMAPKEY))
          .getString(_key);
    } else {
      throw new EFapsException(Context.class,
          "getUserAttribute.NoSessionAttribute");
    }
  }

  /**
   * This method determines if UserAttribute of the Person this Context belongs
   * to exists.The UserAttributes are stored in the {@link #sessionAttributes}
   * Map, therefore are thought to be valid for one session.
   *
   * @param _key
   *                key to Search for
   * @return true if found, else false
   * @throws EFapsException
   */
  public boolean containsUserAttribute(final String _key) throws EFapsException {
    if (containsSessionAttribute(UserAttributesSet.CONTEXTMAPKEY)) {
      return ((UserAttributesSet) getSessionAttribute(UserAttributesSet.CONTEXTMAPKEY))
          .containsKey(_key);
    } else {
      throw new EFapsException(Context.class,
          "getUserAttribute.NoSessionAttribute");
    }
  }

  /**
   * set a new UserAttribute for the UserAttribute of the Person this
   * Context.The UserAttributes are stored in the {@link #sessionAttributes}
   * Map, therefore are thought to be valid for one session.
   *
   * @param _key
   *                Key of the UserAttribute
   * @param _value
   *                Value of the UserAttribute
   * @throws EFapsException
   */
  public void setUserAttribute(final String _key, final String _value)
                                                                      throws EFapsException {
    if (containsSessionAttribute(UserAttributesSet.CONTEXTMAPKEY)) {
      ((UserAttributesSet) getSessionAttribute(UserAttributesSet.CONTEXTMAPKEY)).set(
          _key, _value);
    } else {
      throw new EFapsException(Context.class,
          "getUserAttributes.NoSessionAttribute");
    }
  }

  public void setUserAttribute(final String _key, final String _value,
                               final UserAttributesDefinition _definition)
                                                                          throws EFapsException {
    if (containsSessionAttribute(UserAttributesSet.CONTEXTMAPKEY)) {
      ((UserAttributesSet) getSessionAttribute(UserAttributesSet.CONTEXTMAPKEY))
          .set(_key, _value, _definition);
    } else {
      throw new EFapsException(Context.class,
          "getUserAttributes.NoSessionAttribute");
    }
  }



  public UserAttributesSet getUserAttributes() throws EFapsException {
    if (containsSessionAttribute(UserAttributesSet.CONTEXTMAPKEY)) {
      return (UserAttributesSet) getSessionAttribute(UserAttributesSet.CONTEXTMAPKEY);
    } else {
      throw new EFapsException(Context.class,
          "getUserAttributes.NoSessionAttribute");
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  // instance getter and setter methods

  /**
   * This is the getter method for instance variable {@link #connection}.
   *
   * @return value of instance variable {@link #connection}
   * @see #connection
   * @see #setConnection
   */
  public final Connection getConnection()  {
    return this.connection;
  }

  /**
   * This is the setter method for instance variable {@link #connection}.
   *
   * @param _connection new value for instance variable {@link #connection}
   * @see #connection
   * @see #getConnection
   */
  private void setConnection(final Connection _connection)  {
    this.connection = _connection;
  }

  /**
   * This is the getter method for instance variable {@link #transaction}.
   *
   * @return value of instance variable {@link #transaction}
   * @see #transaction
   */
  public final Transaction getTransaction()  {
    return this.transaction;
  }

  /**
   * This is the getter method for instance variable {@link #person}.
   *
   * @return value of instance variable {@link #person}
   * @see #person
   */
  public final Person getPerson()  {
    return this.person;
  }

  /**
   * This is the getter method for instance variable {@link #locale}.
   *
   * @return value of instance variable {@link #locale}
   * @see #locale
   */
  public final Locale getLocale()  {
    return this.locale;
  }

  /**
   * This is the setter method for the instance variable
   * {@link #locale}.
   *
   * @param _locale
   *                the locale to set
   */
  public void setLocale(final Locale _locale) {
    this.locale = _locale;
  }

  /**
   * This is the getter method for instance variable {@link #parameters}.
   *
   * @return value of instance variable {@link #parameters}
   * @see #parameters
   */
  public final Map < String, String[] > getParameters()  {
    return this.parameters;
  }

  /**
   * This is the getter method for instance variable {@link #fileParameters}.
   *
   * @return value of instance variable {@link #fileParameters}
   * @see #fileParameters
   */
  public final Map < String, FileItem > getFileParameters()  {
    return this.fileParameters;
  }

  /////////////////////////////////////////////////////////////////////////////
  // static methods

  /**
   * The method checks if for the current thread a context object is defined.
   * This found context object is returned.
   *
   * @return defined context object of current thread
   * @throws EFapsException if no context object for current thread is defined
   * @see #THREADCONTEXT
   */
  public static Context getThreadContext() throws EFapsException  {
    Context context = THREADCONTEXT.get();
    if (context == null)  {
      throw new EFapsException(Context.class,
          "getThreadContext.NoContext4ThreadDefined");
    }
    return context;
  }

  /**
   * @see #begin(String, Locale, Map, Map, Map)
   */
  public static Context begin() throws EFapsException {
    return begin(null, null, null, null, null);
  }

  /**
   * @todo embed exceptions
   * @see #begin(String, Locale, Map, Map, Map)
   */
  public static Context begin(final String _userName)
                                           throws EFapsException  {
    return begin(_userName, null, null, null, null);
  }

  /**
   * For current thread a new context object must be created.
   *
   * @param _transaction    transaction of the new thread
   * @param _userName       name of current user to set
   * @param _locale         locale instance (which langage settings has the
   *                        user)
   * @param _parameters     map with parameters for this thread context
   * @param _fileParameters map with file parameters
   * @return new context of thread
   * @throws EFapsException if a new transaction could not be started or
   *                        if current thread context is already set
   * @see #THREADCONTEXT
   */
  public static Context begin(final String _userName,
                              final Locale _locale,
                              final Map<String, Object> _sessionAttributes,
                              final Map<String, String[]> _parameters,
                              final Map<String, FileItem> _fileParameters)
                                           throws EFapsException  {
    if (THREADCONTEXT.get() != null)  {
      throw new EFapsException(Context.class,
          "begin.Context4ThreadAlreadSet");
    }

    try  {
      TRANSMANAG.begin();
    } catch (SystemException e)  {
      throw new EFapsException(Context.class, "begin.beginSystemException", e);
    } catch (NotSupportedException e) {
      throw new EFapsException(Context.class,
                               "begin.beginNotSupportedException", e);
    }
    Transaction transaction;
    try  {
      transaction = TRANSMANAG.getTransaction();
    } catch (SystemException e)  {
      throw new EFapsException(Context.class,
          "begin.getTransactionSystemException", e);
    }
    final Context context = new Context(transaction,
                                        null,
                                        (_locale == null) ? Locale.ENGLISH : _locale,
                                        _sessionAttributes,
                                        _parameters,
                                        _fileParameters);
    THREADCONTEXT.set(context);
    if (_userName != null)  {
      context.person = Person.get(_userName);
    }
    return context;
  }

  /**
   * @throws EFapsException if commit of the transaction manager failed
   * @todo description
   */
  public static void commit() throws EFapsException  {
    try  {
      TRANSMANAG.commit();
    } catch (IllegalStateException e) {
      throw new EFapsException(Context.class,
          "commit.IllegalStateException",
          e);
    } catch (SecurityException e) {
      throw new EFapsException(Context.class,
          "commit.SecurityException",
          e);
    } catch (HeuristicMixedException e) {
      throw new EFapsException(Context.class,
          "commit.HeuristicMixedException",
          e);
    } catch (HeuristicRollbackException e) {
      throw new EFapsException(Context.class,
          "commit.HeuristicRollbackException",
          e);
    } catch (RollbackException e) {
      throw new EFapsException(Context.class,
          "commit.RollbackException",
          e);
    } catch (SystemException e) {
      throw new EFapsException(Context.class,
          "commit.SystemException",
          e);
    } finally  {
      getThreadContext().close();
    }
  }

  /**
   * @throws EFapsException if roll back of the transaction manager failed
   */
  public static void rollback()
          throws EFapsException  {
    try  {
      TRANSMANAG.rollback();
    } catch (IllegalStateException e)  {
      throw new EFapsException(Context.class,
          "rollback.IllegalStateException",
          e);
    } catch (SecurityException e)  {
      throw new EFapsException(Context.class,
          "rollback.SecurityException",
          e);
    } catch (SystemException e)  {
      throw new EFapsException(Context.class,
          "rollback.SystemException",
          e);
    } finally  {
      getThreadContext().close();
    }
  }

  /**
   * Is the status of transaction manager active?
   *
   * @return <i>true</i> if transaction manager is active, otherwise
   *         <i>false</i>
   * @throws EFapsException if the status of the transaction manager could not
   *                        be evaluated
   * @see #TRANSMANAG
   */
  public static boolean isTMActive() throws EFapsException  {
    try  {
      return TRANSMANAG.getStatus() == Status.STATUS_ACTIVE;
    } catch (SystemException e)  {
      throw new EFapsException(Context.class,
                               "isTMActive.SystemException",
                               e);
    }
  }

  /**
   * Is a transaction associated with a target object for transaction manager?
   *
   * @return <i>true</i> if a transaction associated, otherwise <i>false</i>
   * @throws EFapsException if the status of the transaction manager could not
   *                        be evaluated
   * @see #TRANSMANAG
   */
  public static boolean isTMNoTransaction() throws EFapsException  {
    try  {
      return TRANSMANAG.getStatus() == Status.STATUS_NO_TRANSACTION;
    } catch (SystemException e)  {
      throw new EFapsException(Context.class,
                               "isTMNoTransaction.SystemException",
                               e);
    }
  }

  /**
   * Is the status of transaction manager marked roll back?
   *
   * @return <i>true</i> if transaction manager is marked roll back, otherwise
   *         <i>false</i>
   * @throws EFapsException if the status of the transaction manager could not
   *                        be evaluated
   * @see #TRANSMANAG
   */
  public static boolean isTMMarkedRollback() throws EFapsException  {
    try  {
      return TRANSMANAG.getStatus() == Status.STATUS_MARKED_ROLLBACK;
    } catch (SystemException e)  {
      throw new EFapsException(Context.class,
                               "isTMMarkedRollback.SystemException",
                               e);
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  // static getter and setter methods

  /**
   * Returns the database type of the default connection (database where the
   * data model definition is stored).
   *
   * @see #DBTYPE
   */
  public static AbstractDatabase getDbType()  {
    return DBTYPE;
  }




}