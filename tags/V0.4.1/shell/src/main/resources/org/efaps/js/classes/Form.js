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

function Form(_name)  {
  this.object = new EFapsInstance("Admin_UI_Form", _name);
  this.instance = new Instance(Shell.getContext(), this.object.oid);

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

  // add field methods
  this._writeFields       = UICollection.prototype._writeFields;
  this.cleanupFields      = UICollection.prototype.cleanupFields;
  this.addField           = UICollection.prototype.addField;

  // add property methods
  this._writeProperties   = Abstract.prototype._writeProperties;
  this.addProperty        = Abstract.prototype.addProperty;
  this.cleanupProperties  = Abstract.prototype.cleanupProperties;
  this.deleteProperty     = Abstract.prototype.addProperty;
  this.printProperties    = Abstract.prototype.printProperties;

  // add common methods
  this._writeHeader       = Abstract.prototype._writeHeader;
  this.cleanup            = UICollection.prototype.cleanup;
  this.writeUpdateScript  = UICollection.prototype.writeUpdateScript;
}

///////////////////////////////////////////////////////////////////////////////
// instance variables

/**
 * Prefix of script file names.
 */
Form.prototype.FILE_PREFIX = new String("FORM_");

/**
 * Name of the variable used in update scripts.
 */
Form.prototype.VARNAME     = new String("FORM");

///////////////////////////////////////////////////////////////////////////////
// common methods

Form.prototype._create = function(_name)  {
  var insert = new Insert(Shell.context, "Admin_UI_Form");
  insert.add(Shell.context, "Name", _name);
  insert.executeWithoutAccessCheck();
  this.instance = insert.getInstance();
}

Form.prototype.update = function(_fileName, _objName)  {
  if (this.getOid()==null || this.getOid()=="" || this.getOid()=="0")  {
    print("  - create");
    this._create(_objName);
  } else  {
    print("  - cleanup");
    this.cleanup();
  }
  FORM = this;
  print("  - import");
  load(_fileName);
  FORM = null;
}

///////////////////////////////////////////////////////////////////////////////
// static functions

/**
 * Extract the form name from a file name.
 *
 * @param (String)_fileName   file name
 * @return extracted form name
 */
function getFormNameFromFileName(_fileName)  {
  var tmp = new String(_fileName);
  return tmp.substring(Form.prototype.FILE_PREFIX.length, tmp.length-3);
}

/**
 * Import exact one form.
 *
 * @param (File)_fileName   file name of the form to import
 */
function importForm(_fileName)  {
  var fileName = new File(_fileName);
  if (fileName.getName().startsWith(Form.prototype.FILE_PREFIX) && fileName.getName().endsWith(".js"))  {
    var objName = getFormNameFromFileName(fileName.getName());
    print("Import Form '"+objName+"'");
    var imp = new Form(objName);
    imp.update(_fileName, objName);
  }
}

/**
 * A list of Form Definition files is imported. The algorithmen checks first
 * for existance of a form and creates the not existant forms.
 *
 * @param _fileList array with list of files
 */
function importForms(_fileList)  {
  print("");
  print("Import Forms");
  print("~~~~~~~~~~~~");
  for (indx in _fileList)  {
    importForm(_fileList[indx]);
  }
  print("");
}


/*
  var codeRev = getCodeRevision(path+"/"+fileName);
  var dbRev = FORM.getRevision();

  if (codeRev==null || codeRev.length==0 || codeRev!=dbRev)  {
    if (codeRev!=null)  {
      print("  - update to revision '"+codeRev+"'");
      FORM.setRevision(codeRev);
    } else  {
      print("  - update always (no code revision)");
    }
*/

/**
 * @param _path   (String)  files are created in this path
 * @param _match  (String)  match for menu names
 * @param _author (String)  author used in the header
 */
function createScriptForms(_path, _match, _author)  {
  var query = new SearchQuery();
  query.setQueryTypes(Shell.getContext(), "Admin_UI_Form");
  query.addWhereExprEqValue(Shell.getContext(), "Name", _match);
  query.addSelect(Shell.getContext(), "Name");
  
  query.executeWithoutAccessCheck();
  
  print("Create Form Scripts:");
  print("~~~~~~~~~~~~~~~~~~~~");
  while (query.next())  {
    var name = query.get(Shell.getContext(), "Name");
    print("  - '"+  name  +"'");
    var form = new Form(name);
    form.writeUpdateScript(_path, _author);
  }

  query.close();
}
