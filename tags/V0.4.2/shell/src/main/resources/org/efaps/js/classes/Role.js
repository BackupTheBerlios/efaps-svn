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
 * Author:          tmo
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

importClass(Packages.org.efaps.db.Instance);
importClass(Packages.org.efaps.db.SearchQuery);
importClass(Packages.org.efaps.db.Insert);

function Role(_name)  {
  var query = new SearchQuery();
  query.setQueryTypes(Shell.getContext(), "Admin_UI_Search");
  query.addWhereExprEqValue(Shell.getContext(), "Name", _name);
  query.addSelect(Shell.getContext(), "OID");
  query.executeWithoutAccessCheck();

  if (query.next())  {
    var oid = query.get(Shell.getContext(), "OID");
    this.instance = new Instance(Shell.getContext(), oid);
  } else  {
    this.instance = null;
  }
  query.close();

  this.object = new EFapsInstance("Admin_User_Role", _name);

  // add helper methods for getter and setter 
  this._setAttrValue      = Abstract.prototype._setAttrValue;
  this._getAttrValue      = Abstract.prototype._getAttrValue;

  // add getter and setter methods
  this.getInstance        = Abstract.prototype.getInstance;
  this.getId              = Abstract.prototype.getId;
  this.getOid             = Abstract.prototype.getOid;
  this.getType            = Abstract.prototype.getType;
  this.getName            = Abstract.prototype.getName;
  this.getRevision        = Abstract.prototype.getRevision;
  this.setRevision        = Abstract.prototype.setRevision;
/*

  // add property methods
  this._writeProperties   = Abstract.prototype._writeProperties;
  this.addProperty        = Abstract.prototype.addProperty;
  this.cleanupProperties  = Abstract.prototype.cleanupProperties;
  this.deleteProperty     = Abstract.prototype.addProperty;
  this.printProperties    = Abstract.prototype.printProperties;

  // add child methods
  this._writeChilds       = Menu.prototype._writeChilds;
  this.cleanupChilds      = Menu.prototype.cleanupChilds;
  this.printChilds        = Menu.prototype.printChilds;
  this.addChild           = Menu.prototype.addChild;

  // add link methods
  this._createLink        = UIAbstract.prototype._createLink;
  this._writeLinks        = UIAbstract.prototype._writeLinks;
  this.cleanupLinks       = UIAbstract.prototype.cleanupLinks;
  this.addIcon            = Command.prototype.addIcon;
  this.addTargetForm      = Command.prototype.addTargetForm;
  this.addTargetMenu      = Command.prototype.addTargetMenu;
  this.addTargetSearch    = Command.prototype.addTargetSearch;
  this.addTargetTable     = Command.prototype.addTargetTable;
*/

  // add common methods
  this._writeHeader       = Abstract.prototype._writeHeader;
  this.cleanup            = Menu.prototype.cleanup;
  this.writeUpdateScript  = Menu.prototype.writeUpdateScript;
}

///////////////////////////////////////////////////////////////////////////////
// instance variables

/**
 * Prefix of script file names.
 */
Role.prototype.FILE_PREFIX = new String("ROLE_");

/**
 * Name of the variable used in update scripts.
 */
Role.prototype.VARNAME     = new String("ROLE");

///////////////////////////////////////////////////////////////////////////////
// to update the JAAS keys for roles

/**
 * @param _jaasSystem name of the JAAS system for which the key is added
 * @param _jaasKey    key in the JAAS system for this role
 */
Role.prototype.addJAASKey = function(_jaasSystem, _jaasKey)  {
  var query = new SearchQuery();
  query.setQueryTypes(Shell.getContext(), "Admin_User_JAASSystem");
  query.addWhereExprEqValue(Shell.getContext(), "Name", _jaasSystem);
  query.addSelect(Shell.getContext(), "ID");
  query.executeWithoutAccessCheck();
  if (query.next())  {
    var jaasSystemId = query.get(Shell.getContext(), "ID");
  }
  query.close();

  if (jaasSystemId)  {
    var insert = new Insert(Shell.getContext(), "Admin_User_JAASKey");
    insert.add(Shell.getContext(), "Key", _jaasKey);
    insert.add(Shell.getContext(), "UserLink", this.getId());
    insert.add(Shell.getContext(), "JAASSystemLink", jaasSystemId);
    insert.executeWithoutAccessCheck();
  }
}

///////////////////////////////////////////////////////////////////////////////
// common methods

Role.prototype.update = function(_fileName, _objName)  {
  if (this.getOid()==null || this.getOid()=="" || this.getOid()=="0")  {
    print("  - create");
    this.object.Name = _objName;
    this.object.create();
    this.instance = new Instance(Shell.getContext(), this.object.oid);
  } else  {
    print("  - cleanup");
//    this.cleanup();
  }
  ROLE = this;
  print("  - import");
  load(_fileName);
  ROLE = null;
}

///////////////////////////////////////////////////////////////////////////////
// static functions

/**
 * Extract the role name from a file name.
 *
 * @param (String)_fileName   file name
 * @return extracted role name
 */
function getRoleNameFromFileName(_fileName)  {
  var tmp = new String(_fileName);
  return tmp.substring(Role.prototype.FILE_PREFIX.length, tmp.length-3);
}

/**
 * Import exact one role.
 *
 * @param (File)_fileName   file name of the role to import
 */
function importRole(_fileName)  {
  var fileName = new File(_fileName);
  if (fileName.getName().startsWith(Role.prototype.FILE_PREFIX))  {
    var objName = getRoleNameFromFileName(fileName.getName());
    print("Import Role '"+objName+"'");
    var imp = new Role(objName);
    imp.update(_fileName, objName);
  }
}

/**
 * A list of Role Definition files is imported. The algorithmen checks first
 * for existance of a role and creates the not existant roles.
 *
 * @param _fileList array with list of files
 */
function importRoles(_fileList)  {
  print("");
  print("Import Roles");
  print("~~~~~~~~~~~~");

var fileList;
if (_fileList instanceof Object)  {
fileList = _fileList;
} else   {
fileList = eFapsGetAllFiles(_fileList, true);
}

  for (indx in fileList)  {
    importRole(fileList[indx]);
  }
}

/**
 * Create javascript update script files for all matching roles in given 
 * path for given author.
 *
 * @param _path   (String)  files are created in this path
 * @param _match  (String)  match for role names
 * @param _author (String)  author used in the header
 */
function createScriptRoles(_path, _match, _author)  {
  var query = new SearchQuery();
  query.setQueryTypes(Shell.getContext(), "Admin_User_Role");
  query.addWhereExprEqValue(Shell.getContext(), "Name", _match);
  query.addSelect(Shell.getContext(), "Name");
  
  query.executeWithoutAccessCheck();
  
  print("");
  print("Create Role Scripts:");
  print("~~~~~~~~~~~~~~~~~~~~");
  while (query.next())  {
    var name = query.get(Shell.getContext(), "Name");
    print("  - '"+  name  +"'");
    var search = new Search(name);
    search.writeUpdateScript(_path, _author);
  }

  query.close();
}
