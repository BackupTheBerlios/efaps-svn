/*
 * Copyright 2006 The eFaps Team
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

package org.efaps.webdav;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.efaps.db.Checkin;
import org.efaps.db.Checkout;
import org.efaps.db.Context;
import org.efaps.db.Delete;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.db.SearchQuery;
import org.efaps.util.EFapsException;
import org.efaps.webdav.CollectionResource;
import org.efaps.webdav.SourceResource;

/**
 *
 * @author tmo
 * @version $Id$
 * @todo description
 */
public class TeamCenterWebDAVImpl  {
  
  /////////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Logging instance used in this class.
   */
  private static final Log LOG = LogFactory.getLog(TeamCenterWebDAVImpl.class);

  
/*  getRoot(final String _name)   {
  }
  */
  
  /////////////////////////////////////////////////////////////////////////////
  // instance methods

  public List < AbstractResource > getSubs(final CollectionResource _collection)   {
    
    List < AbstractResource > subs = new ArrayList < AbstractResource > ();

    try  {
      Context context = Context.getThreadContext();

      if (_collection == null)  {
        SearchQuery query = new SearchQuery();
        query.setQueryTypes(context, "TeamCenter_RootFolder");
        query.addSelect(context, "OID");
        query.addSelect(context, "Name");
        query.addSelect(context, "Modified");
        query.addSelect(context, "Created");
        query.execute();
        while (query.next())  {
          String name = query.get(context, "Name").toString();
          subs.add(new CollectionResource(
              name,
              new Instance(query.get(context,"OID").toString()),
              (Date) query.get(context, "Created"),
              (Date) query.get(context, "Modified"),
              name
          ));
        }
        query.close();
      } else  {
        // append sub folders
        SearchQuery query = new SearchQuery();
        query.setExpand(context, 
                        _collection.getInstance(), 
                        "TeamCenter_Folder\\ParentFolder");
        query.addSelect(context, "OID");
        query.addSelect(context, "Name");
        query.addSelect(context, "Created");
        query.addSelect(context, "Modified");
        query.execute();
        while (query.next())  {
          String name = query.get(context, "Name").toString();
          subs.add(new CollectionResource(
              name,
              new Instance(query.get(context,"OID").toString()),
              (Date) query.get(context, "Created"),
              (Date) query.get(context, "Modified"),
              name
          ));
        }
        query.close();
    
        // append sub files
        query = new SearchQuery();
        query.setExpand(context, 
                        _collection.getInstance(), 
                        "TeamCenter_Document2Folder\\Folder.Document");
        query.addSelect(context, "OID");
        query.addSelect(context, "Name");
        query.addSelect(context, "FileLength");
        query.addSelect(context, "Created");
        query.addSelect(context, "Modified");
        query.execute();
        while (query.next())  {
          String name = (String) query.get(context, "Name");
          subs.add(new SourceResource(
              name,
              new Instance((String) query.get(context,"OID")),
              (Date) query.get(context, "Created"),
              (Date) query.get(context, "Modified"),
              name,
              (Long) query.get(context, "FileLength")
          ));
        }
        query.close();
      }
    } catch (Exception e)  {
      LOG.error("could not get subs from collection "
                + "'" + _collection.getName() + "'", e);
    }
    
    return subs;
  }


  /**
   * @param _collection collection resource representing the folder (if null,
   *                    means root folder)
   * @param _name       name of the searched collection resource
   * @return found collection resource for given instance or null if not found.
   * @todo use EFapsException instead of Exception
   */
  public CollectionResource getCollection(final CollectionResource _collection,
                                          final String _name)  {
    
    CollectionResource collection = null;
    
    try  {
      Context context = Context.getThreadContext();
      SearchQuery query = new SearchQuery();
      if (_collection == null)  {
        query.setQueryTypes(context, "TeamCenter_RootFolder");
        query.addWhereExprEqValue(context, "Name", _name);
      } else  {
        query.setQueryTypes(context, "TeamCenter_Folder");
        query.addWhereExprEqValue(context, "Name", _name);
        query.addWhereExprEqValue(context, 
                                  "ParentFolder", 
                                  "" + _collection.getInstance().getId());
      }
      query.addSelect(context, "OID");
      query.addSelect(context, "Created");
      query.addSelect(context, "Modified");
      query.execute();
      if (query.next())  {
        collection = new CollectionResource(
            _name,
            new Instance(query.get(context,"OID").toString()),
            (Date) query.get(context, "Created"),
            (Date) query.get(context, "Modified"),
            _name
        );
      }
      query.close();
    } catch (Exception e)  {
      LOG.error("could not get information about collection "
                + "'" + _name + "'", e);
    }
    return collection;
  }

  /**
   * @param _collection collection resource representing the folder (if null,
   *                    means root folder)
   * @param _name       name of the searched source resource
   * @return found source resource for given instance or null if not found.
   * @todo use EFapsException instead of Exception
   */
  public SourceResource getSource(final CollectionResource _collection,
                                  final String _name)  {
    
    SourceResource source = null;

    if (_collection != null)  {
      try  {
        Context context = Context.getThreadContext();
    
        SearchQuery query = new SearchQuery();
        query.setQueryTypes(context, "TeamCenter_Document2Folder");
        query.addWhereExprEqValue(context, 
                                  "Folder", 
                                  "" + _collection.getInstance().getId());
        query.addSelect(context, "Document.OID");
        query.addSelect(context, "Document.Name");
        query.execute();
        Instance instance = null;
        while (query.next())  {
          String docName = (String) query.get(context, "Document.Name");
          if ((docName != null) && _name.equals(docName))  {
            instance = new Instance((String) query.get(context,"Document.OID"));
            break;
          }
        }
        query.close();

        if (instance != null)  {
          query = new SearchQuery();
          query.setObject(context, instance);
          query.addSelect(context, "FileLength");
          query.addSelect(context, "Created");
          query.addSelect(context, "Modified");
          query.execute();
          query.next();
          source = new SourceResource(
              _name,
              instance,
              (Date) query.get(context, "Created"),
              (Date) query.get(context, "Modified"),
              _name,
              (Long) query.get(context, "FileLength")
          );
          query.close();
        }
      } catch (Exception e)  {
        LOG.error("could not get information about source '" + _name + "'", e);
      }
    }
    return source;
  }


  public boolean deleteCollection(final CollectionResource _collection)  {
    boolean ok = false;
    
    try  {
      Delete delete = new Delete(_collection.getInstance());
      delete.execute();
      ok = true;
    } catch (Exception e)  {
      LOG.error("could not delete collection "
                + "'" + _collection.getName() + "'", e);
    }
    return ok;
  }
  
  public boolean deleteSource(final SourceResource _source)  {
    boolean ok = false;

    try  {
      Delete delete = new Delete(_source.getInstance());
      delete.execute();
      ok = true;
    } catch (Exception e)  {
      LOG.error("could not delete source "
                + "'" + _source.getName() + "'", e);
    }
    return ok;
  }

  public boolean createCollection(final CollectionResource _collection, 
                                  final String _name)  {
    boolean ok = false;

    try  {
      Context context = Context.getThreadContext();

      Insert insert = new Insert(context, "TeamCenter_Folder");
      insert.add(context, 
                 "ParentFolder", 
                 "" + _collection.getInstance().getId());
      insert.add(context, "Name", _name);
      insert.execute();
      insert.close();
      ok = true;
    } catch (Exception e)  {
      LOG.error("could not create collection "
                + "'" + _name + "'", e);
    }
    return ok;
  }

  public boolean createSource(final CollectionResource _collection, 
                              final String _name)  {
    boolean ok = false;

    try  {
      Context context = Context.getThreadContext();

      Insert insert = new Insert(context, "TeamCenter_Document");
      insert.add(context, "Name", _name);
      insert.execute();
      Instance fileInstance = insert.getInstance();
      insert.close();
          
      insert = new Insert(context, "TeamCenter_Document2Folder");
      insert.add(context, "Document", "" + fileInstance.getId());
      insert.add(context, 
                 "Folder", 
                 "" + _collection.getInstance().getId());
      insert.execute();
      insert.close();

      ok = true;
    } catch (Exception e)  {
      LOG.error("could not create source "
                + "'" + _name + "'", e);
    }
    return ok;
  }

  
  public boolean checkinSource(final SourceResource _source, 
                               final InputStream _inputStream)  {

    boolean ok = false;

    try  {
      Checkin checkin = new Checkin(_source.getInstance());
      checkin.execute(_source.getName(), _inputStream, -1);
      
      ok = true;
    } catch (EFapsException e)  {
      LOG.error("could not checkin source "
                + "'" + _source.getName() + "'", e);
    }
    return ok;
  }

  public boolean checkoutSource(final SourceResource _source, 
                                final OutputStream _outputStream)  {

    boolean ok = false;

    try  {
      Checkout checkout = new Checkout(_source.getInstance());
      checkout.preprocess();
      checkout.execute(_outputStream);
      
      ok = true;
    } catch (Exception e)  {
      LOG.error("could not checkout source "
                + "'" + _source.getName() + "'", e);
    }
    return ok;
  }
}
