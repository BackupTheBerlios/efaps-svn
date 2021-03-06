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

package org.efaps.db.transaction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import org.efaps.admin.datamodel.Type;
import org.efaps.db.Context;
import org.efaps.util.EFapsException;

/**
 * The class implements the {@link javax.transaction.XAResource} interface for
 * Apache Jakarta Commons Virtual File System.<br/>
 * All different virtual file systems could be used.
 * The algorithm is:
 * <ol>
 *   <li>check if the file already exists</li>
 *   <li></li>
 *   <li></li>
 * </ol>
 * The store implements the compress property setting on the type for
 * <code>ZIP</code> and <code>GZIP</code>.
 *
 * For each file id a new VFS store resource must be created.
 *
 * @author tmo
 * @version $Id$
 */
public class VFSStoreResource extends StoreResource  {

  /**
   * The virtual file systep store resource could handle three different
   * events:
   * <ul>
   *   <li>delete</li>
   *   <li>write</li>
   *   <li>read</li>
   * </ul>
   * These three events are defined here and set in instance variable
   * {@link #storeEvent}.
   */
  private enum StoreEvent  {DELETE, WRITE, READ, UNKNOWN};

  /**
   * Logging instance used in this class.
   */
  private final static Log LOG = LogFactory.getLog(VFSStoreResource.class);

  /**
   * Property Name of the number of sub directories.
   */
  private final static String PROPERTY_NUMBER_SUBDIRS 
                                                = "StoreNumberSubDirectories";

  /**
   * Property Name to define if the type if is used to define a sub directory.
   */
  private final static String PROPERTY_USE_TYPE = "StoreUseTypeIdInPath";

  /**
   * Extension of the temporary file in the store used in the transaction that
   * the original file is not overwritten.
   */
  public final static String EXTENSION_TEMP     = ".tmp";

  /**
   * Extension of a file in the store.
   */
  public final static String EXTENSION_NORMAL   = "";

  /**
   * Extension of a bakup file in the store.
   */
  public final static String EXTENSION_BAKUP    = ".bak";

  /**
   * Buffer used to copy from the input stream to the output stream.
   *
   * @see #read
   * @see #write
   */
  private final byte[] buffer = new byte[1024];

  /**
   * The factory bean is a pointer to the eFaps specific implementation of the
   * VFS file system manager
   *
   * @see VFSStoreFactoryBean
   * @see #VFSStoreResource
   */
  private final VFSStoreFactoryBean res;

  /**
   * @see #StoreEvent
   */
  private StoreEvent storeEvent = StoreEvent.UNKNOWN;

  /**
   * Stores the name of the file including the correct directory.
   */
  private String fileName = null;

  /**
   *
   */
  public VFSStoreResource(final Context _context, final Type _type, final long _fileId) throws EFapsException  {
    super(_context, _type, _fileId);

    StringBuffer fileName = new StringBuffer();

    String useTypeIdStr = getType().getProperty(PROPERTY_USE_TYPE);
    if ("true".equalsIgnoreCase(useTypeIdStr))  {
      fileName.append(getType().getId()).append("/");
    }

    String numberSubDirsStr =  getType().getProperty(PROPERTY_NUMBER_SUBDIRS);
    if (numberSubDirsStr != null)  {
      long numberSubDirs = Long.parseLong(numberSubDirsStr);
      String pathFormat = "%0" 
                          + Math.round(Math.log10(numberSubDirs) + 0.5d) 
                          + "d";
      fileName.append(String.format(pathFormat, 
                                         getFileId() % numberSubDirs))
                   .append("/");
    }
    fileName.append(getFileId());
    this.fileName = fileName.toString();

    try  {
      this.res = VFSStoreFactoryBean.getFileProvider(_type);
    } catch (EFapsException e)  {
      _context.abort();
      throw e;
    }
  }

  /**
   * The method writes the context (from the input stream) to a temporary file
   * (same file URL, but with extension {@link #EXTENSION_TEMP}).
   *
   * @param _in   input stream defined the content of the file
   * @param _size length of the content (or negative meaning that the length
   *              is not known; then the content gets the length of readable
   *              bytes from the input stream)
   * return size of the created temporary file object
   */
  public int write(final InputStream _in, final int _size)
                                                      throws EFapsException  {
    try  {
      this.storeEvent = StoreEvent.WRITE;
      int size = _size;

      FileObject tmpFile = this.res.findFile(this.fileName + EXTENSION_TEMP);
      if (!tmpFile.exists())  {
        tmpFile.createFile();
      }
      FileContent content = tmpFile.getContent();
      OutputStream out = content.getOutputStream(false);
      if (this.compress.equals(Compress.GZIP))  {
        out = new GZIPOutputStream(out);
      } else if (this.compress.equals(Compress.ZIP))  {
        out = new ZipOutputStream(out);
      }

      // if size is unkown!
      if (_size < 0)  {
        int length = 1;
        size = 0;
        while (length > 0)  {
          length = _in.read(this.buffer);
          if (length > 0)  {
            out.write(this.buffer, 0, length);
            size += length;
          }
        }
      } else  {
        int length = _size;
        while (length > 0)  {
          int readLength = (length < this.buffer.length ? length : this.buffer.length);
          _in.read(this.buffer, 0, readLength);
          out.write(this.buffer, 0 , readLength);
          length -= readLength;
        }
      }

      if (this.compress.equals(Compress.GZIP))  {
        out.close();
      } else if (this.compress.equals(Compress.ZIP))  {
        out.close();
      }

      tmpFile.close();
      return size;
    } catch (IOException e)  {
      LOG.error("write of content failed", e);
      throw new EFapsException(VFSStoreResource.class, "write.IOException", e);
    }
  }

  /**
   * Returns for the file the input stream.
   *
   * @return input stream of the file with the content
   */
  public StoreResourceInputStream read() throws EFapsException  {
    StoreResourceInputStream in = null;
    try  {
      this.storeEvent = StoreEvent.READ;

      FileObject file = this.res.findFile(this.fileName + EXTENSION_NORMAL);

      if (!file.isReadable())  {
        LOG.error("file for " + this.fileName + " not readable");
throw new EFapsException(VFSStoreResource.class, "#####file no readable");
      }

      in = new VFSStoreResourceInputStream(this, file);

    } catch (EFapsException e)  {
      throw e;
    } catch (Throwable e)  {
      LOG.error("read of " + this.fileName + " failed", e);
      throw new EFapsException(VFSStoreResource.class, "read.Throwable", e);
    }
    return in;
  }

  /**
   * Marks the file defined in {@link #fileId} as deleted. Because of
   * transaction handling, the delete itself is done inside {@link #commit}.
   */
  public void delete() throws EFapsException  {
    this.storeEvent = StoreEvent.DELETE;
  }

  /////////////////////////////////////////////////////////////////////////////
  // all further methods are implementing javax.transaction.xa.XAResource

  /**
          Ask the resource manager to prepare for a transaction commit of the transaction specified in xid.
   * (used for 2-phase commits)
   */
  public int prepare(final Xid _xid)  {
    if (LOG.isDebugEnabled())  {
      LOG.debug("prepare (xid=" + _xid + ")");
    }
    return 0;
  }

  /**
   * The method is called from the transaction manager if the complete
   * transaction is completed.<br/>
   * A file in the virtual file system is commited with the algorithmus:
   * <ol>
   * <li>an existing bakup file is deleted</li>
   * <li>the original file is moved to the bakup file</li>
   * <li>the new file is moded to the original name</li>
   * </ol>
   *
   * @param _xid      global transaction identifier (not used, because each
   *                  file with the file id gets a new VFS store resource
   *                  instance)
   * @param _onePhase <i>true</i> if it is a one phase commitment transaction
   *                  (not used)
   * @throws XAException if any exception occurs (catch on
   *         {@link java.lang.Throwable})
   */
  public void commit(final Xid _xid, final boolean _onePhase) throws XAException  {
    if (LOG.isDebugEnabled())  {
      LOG.debug("transaction commit");
    }
    if (this.storeEvent == StoreEvent.WRITE)  {
      try  {
        FileObject tmpFile = this.res.findFile(this.fileName + EXTENSION_TEMP);
        FileObject newFile = this.res.findFile(this.fileName + EXTENSION_NORMAL);
        FileObject bakFile = this.res.findFile(this.fileName + EXTENSION_BAKUP);
        if (bakFile.exists())  {
          bakFile.delete();
        }
        if (newFile.exists())  {
          newFile.moveTo(bakFile);
        }
        tmpFile.moveTo(newFile);
        tmpFile.close();
        newFile.close();
        bakFile.close();
      } catch (Throwable e)  {
        LOG.error("transaction commit fails for " + _xid + " (one phase = "
                                                          + _onePhase + ")", e);
        XAException xa = new XAException(XAException.XA_RBCOMMFAIL);
        xa.initCause(e);
        throw xa;
      }
    } else if (this.storeEvent == StoreEvent.DELETE)  {
      try  {
        FileObject curFile = this.res.findFile(this.fileName + EXTENSION_NORMAL);
        FileObject bakFile = this.res.findFile(this.fileName + EXTENSION_BAKUP);
        if (bakFile.exists())  {
          bakFile.delete();
        }
        if (curFile.exists())  {
          curFile.moveTo(bakFile);
        }
        bakFile.close();
        curFile.close();
      } catch (Throwable e)  {
        LOG.error("transaction commit fails for " + _xid + " (one phase = "
                                                          + _onePhase + ")", e);
        XAException xa = new XAException(XAException.XA_RBCOMMFAIL);
        xa.initCause(e);
        throw xa;
      }
    }
  }

  /**
   * If the file written in the virtual file system must be rolled back, only
   * the created temporary file (created from method {@link #write}) is
   * deleted.
   *
   * @param _xid      global transaction identifier (not used, because each
   *                  file with the file id gets a new VFS store resource
   *                  instance)
   * @throws XAException if any exception occurs (catch on
   *         {@link java.lang.Throwable})
   */
  public void rollback(final Xid _xid) throws XAException  {
    if (LOG.isDebugEnabled())  {
      LOG.debug("rollback (xid = " + _xid + ")");
    }
    try  {
      FileObject tmpFile = this.res.findFile(this.fileName + EXTENSION_TEMP);
      if (tmpFile.exists())  {
        tmpFile.delete();
      }
    } catch (Throwable e)  {
      LOG.error("transaction rollback fails for " + _xid, e);
      XAException xa = new XAException(XAException.XA_RBCOMMFAIL);
      xa.initCause(e);
      throw xa;
    }
  }

  /**
          Tells the resource manager to forget about a heuristically completed transaction branch.
   */
  public void forget(final Xid _xid)   {
    if (LOG.isDebugEnabled())  {
      LOG.debug("forget (xid=" + _xid + ")");
    }
  }

  /**
          Obtains the current transaction timeout value set for this XAResource instance.
   */
  public int getTransactionTimeout()  {
    if (LOG.isDebugEnabled())  {
      LOG.debug("getTransactionTimeout ");
    }
    return 0;
  }

  /**
          Obtains a list of prepared transaction branches from a resource manager.
   */
  public Xid[] recover(final int _flag)  {
    if (LOG.isDebugEnabled())  {
      LOG.debug("recover (flag = " + _flag + ")");
    }
    return null;
  }


  /**
          Sets the current transaction timeout value for this XAResource instance.
   */
  public boolean  setTransactionTimeout(final int _seconds)  {
    if (LOG.isDebugEnabled())  {
      LOG.debug("setTransactionTimeout (seconds = " + _seconds + ")");
    }
    return true;
  }

  /////////////////////////////////////////////////////////////////////////////
  // input stream wrapper class
  
  private class VFSStoreResourceInputStream extends StoreResourceInputStream  {
    
    private final FileObject file;
    
    VFSStoreResourceInputStream(final StoreResource _storeRes,
                                final FileObject _file) 
    throws IOException, FileSystemException  {
      super(_storeRes, _file.getContent().getInputStream());
      this.file = _file;
    }

    /**
     * The file object {@link #file} is closed. The method overwrites the 
     * method to close the input stream, because if the file is closed, the
     * input stream is also closed.
     */
    protected void beforeClose() throws IOException  {
      this.file.close();
    }
  }
}
