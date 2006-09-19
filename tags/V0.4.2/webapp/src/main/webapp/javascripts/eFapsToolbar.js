/*   mapb_menu  Copyright Miquel Angel Pintanel/imaginatic 2000-2003  Last Update 02/Sep/2003  Version 0.9      http://www.imaginatic.com/dhtml/index.html      mapbdhtml@wanadoo.es    This script and his components are free, but must be  maintained this text and the copyright to use it.    Version 0.2 Added autoHide feature  Version 0.3 Fixed bug in stylesheet  Version 0.4 Added compatibility NS6    Version 0.6 Support frames (first public release)      Update 0.6.1 Fixed a bug      Update 0.6.2 Previous fixed a bug makes a new bug    Version 0.7 Added You are here feature      Update 0.7.1 First level Items can be links now and can change color as other levels      Update 0.7.2 Fixed a bug of last update now works fine in horizontal      Update 0.7.3 Fixed bug in internal links      Update 0.7.4 Fixed bug in frames and added frames sample      Update 0.7.5 Minor bugs fixed    Version 0.8 Added background images. Menu can do pop-up. Colors and backgrounds only                defined in menu definition, no duplicated in CSS. Changed behaviour of links,                now like other elements      Update 0.8.1 Fixed bug in pop-up      Update 0.8.2 Cursor change on links, fixed empty cells, fixed YouAreHere bug in first level items, decoration and weight in links returns      Update 0.8.3 New approach to change cursor on links. Added 'onclick' mXtra.      Update 0.8.4 Another new approach to change cursor on links. Arranged freeze in Netscape 4.7      Update 0.8.5 Menus can open to left or right      Update 0.8.6 Added manual adjust of position    Update 0.8.7 Auto hide minor than one second. Mouseover/mouseout events.    Update 0.8.8 Fixed a bug in Netspae 6+  Version 0.9. Added relative positioned menus, arrow highlighting    Update 0.9.1 Fixed bug    Update 0.9.2 Fixed bug */var imgs_url = new Array ();function eFapsToolbar (_calcWindow, _idMenu, _xPos, _yPos) {  this.calcWindow = _calcWindow;  this.xPos = _xPos;  this.yPos = _yPos;  /////////////////////////////////////////////////////////////////////////////  // static variables for top menu  // bottom arrow image  this.imageBottom = 'url(../images/eFapsToolbarBottom.gif)';  // selected bottom arrow image  this.imageBottomSel = 'url(../images/eFapsToolbarBottomSelected.gif)';  // style class for top menu  this.classTopMenu = 'eFapsToolbarTop';  /////////////////////////////////////////////////////////////////////////////  // static variables for sub menus  // right arrow image  this.imageRight = 'url(../images/eFapsToolbarRight.gif)';  // selected right arrow image  this.imageRightSel = 'url(../images/eFapsToolbarRightSelected.gif)';  // style class for sub menus  this.classSubMenu = 'eFapsToolbarSubMenu';  // style class for sub menu left grey array  this.classSubMenuLeft = 'eFapsToolbarSubMenuLeft';  // null image for not defined right images in sub menus  this.imageNull = '../images/eFapsToolbarNullImage.gif';  /////////////////////////////////////////////////////////////////////////////  // static variables (others)  this.zIndx = 100;this.to_load = new Array ();      //private variables  this.idMenu = _idMenu;  this.pre_sm = this.idMenu + 'm';  this.pre_td = 'td';  this.pre_l = this.idMenu + 'l';  this.num_sm = -1;  this.autoHideStack = new Array();  this.autoHideSeconds = 3;  this.autoHidePass = 1;  this.openMenus = new Array ();  this.commandHolders = new Array();  }eFapsToolbar.prototype.autoHide = function() {  autoHide_time = ((this.autoHideSeconds * 100) > 1)? this.autoHideSeconds * 100 : 1;  if (this.autoHidePass > autoHide_time) {    this.hideAll();  } else {    this.autoHidePass++;  }}eFapsToolbar.prototype.hideAll = function() {  for (var i = 0;i < this.openMenus.length;i++){    this.openMenus[i].hide();  }  for (var i=0; i<this.autoHideStack.length; i++)  {    clearInterval(this.autoHideStack[i]);  }  this.autoHideStack = new Array();  this.openMenus = new Array ();}eFapsToolbar.prototype.createCommandHolder = function()  {  this.commandHolders[this.commandHolders.length] = new eFapsToolbarCommandHolder(this, '');  return this.commandHolders[this.commandHolders.length-1];}eFapsToolbar.prototype.onOver_sm = function(_command) {  for (var i=0; i<this.autoHideStack.length; i++)  {    clearInterval(this.autoHideStack[i]);  }  this.autoHideStack = new Array();  this.autoHidePass = 1;  if (_command.isActive)  {    _command.markAsSelected();  }  this.open_l(_command);}eFapsToolbar.prototype.onOut_sm = function(_command)  {  this.autoHideStack[this.autoHideStack.length] = setInterval ("top." + this.idMenu + ".autoHide()",10);  if (_command.isActive)  {    _command.markAsUnselected();  }}eFapsToolbar.prototype.open_l = function(_command)  {var what_sm = this.pre_sm + _command.suffix;  if (_command.subCommandHolder != null)  {    this.openMenus[this.openMenus.length] = _command.subCommandHolder;    _command.subCommandHolder.calcNewPosition();    _command.subCommandHolder.show();  }  var tempA = new Array ();  for (var i = 0; i < this.openMenus.length; i++){    if (this.openMenus[i].id.indexOf(this.idMenu) == -1 || (this.openMenus[i].id.length >= what_sm.length && this.openMenus[i].id.indexOf(this.pre_l + what_sm.substr(this.pre_sm.length)) == -1))  {      this.openMenus[i].hide();    } else {      tempA[tempA.length] = this.openMenus[i];    }  }  this.openMenus = tempA;}/** * The menu is created in given document. * * @param _document   (Document) */eFapsToolbar.prototype.doMenu = function(_document)  {//  var theL = '';var body = _document.getElementsByTagName("body")[0];  for (var i=0;i < this.commandHolders.length;i++)  {    var cmdHolder = this.commandHolders[i];var main = _document.createElement("table");main.id = cmdHolder.id;main.style.visibility = 'hidden';main.style.zIndex = (++this.zIndx);;body.appendChild(main);    if (i == 0)  {      main.className = this.classTopMenu;      main = main.insertRow(0);      main.className = this.classTopMenu;    } else {      main.className = this.classSubMenu;    }    for (j=0; j<cmdHolder.commands.length; j++)  {      command = cmdHolder.commands[j];if (command instanceof eFapsToolbarSeparator)  {      if (i==0)  {        var elem = _document.createElement("td");        elem.className = "eFapsToolbarTopSeparator";        main.appendChild(elem);      }} else  {var elem,elemL,elemM,elemR;      // is top menu      if (i==0)  {        elem = _document.createElement("td");        elemL = elem;        elemM = elem;        elemR = elem;        main.appendChild(elem);      } else  {        elem = main.insertRow(main.rows.length);        elemL = _document.createElement("td");        elemM = _document.createElement("td");        elemR = _document.createElement("td");        elemL.className = this.classSubMenuLeft;        elemM.className = command.className        elemR.className = command.className        elem.appendChild(elemL);        elem.appendChild(elemM);        elem.appendChild(elemR);      }      if (command.image)  {        var img = _document.createElement("img");        img.src = command.image;        img.border = 0;        elemL.appendChild(img);      }      elemM.appendChild(_document.createTextNode(command.text));      if (command.onImage!=null && command.offImage!=null)  {        var img = _document.createElement("img");        img.src = this.to_load[command.offImage];        img.border = 0;        img.id = "img_"+ this.pre_sm + command.suffix;        img.name = "img_"+ this.pre_sm + command.suffix;        elemR.appendChild(img);      }      elem.menu = this;      elem.command = command;      elem.className = command.className;      elem.id = command.id;      elem.onmouseover = function()  {        this.menu.onOver_sm(this.command);      }      elem.onmouseout = function()  {        this.menu.onOut_sm(this.command);      }      if ((command.submit && command.submitForm) || command.url || command.javaScript)  {        elem.onclick = function()  {          this.command.execute();        }      }    }}    if (i==0)  {      var td = _document.createElement("td");      td.width = "100%";      main.appendChild(td);    }  }  this.commandHolders[0].calcNewPosition();  this.commandHolders[0].show();}/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////function eFapsToolbarCommand(_commandHolder, _name, _text, _suffix)  {  this.name = _name;  this.commandHolder = _commandHolder;  this.text = _text;  this.suffix = _suffix;  this.isActive = true;  this.onImage = null;  this.offImage = null;if (this.suffix.substring(1).indexOf('_')>=0)  {  this.image = this.commandHolder.menu.imageNull;  this.className = this.commandHolder.menu.classSubMenu;} else  {  this.image = '';  this.className = this.commandHolder.menu.classTopMenu;}  this.id = this.commandHolder.menu.pre_td + this.commandHolder.menu.pre_sm + this.suffix  this.subCommandHolderId = '';  this.subCommandHolder = null;  this.url = null;  this.javaScript = null;  this.target = null;  this.height = -1;  this.width = -1;}/** * Adds a new command holder for this command. If the command holder owns no  * parent command (this means this command is in the top menu), the bottom  * array image is set, otherwise the right array image is set. * * @return new created instance of 'eFapsToolbarCommandHolder' */eFapsToolbarCommand.prototype.createCommandHolder = function()  {  this.subCommandHolder = new eFapsToolbarCommandHolder(this.commandHolder.menu, this.suffix);  this.subCommandHolder.parentCommand = this;  this.commandHolder.menu.commandHolders[this.commandHolder.menu.commandHolders.length] = this.subCommandHolder;  if (this.commandHolder.parentCommand)  {    this.offImage = new addToLoad(this.commandHolder.menu, this.commandHolder.menu.imageRight).indx;    this.onImage = new addToLoad(this.commandHolder.menu, this.commandHolder.menu.imageRightSel).indx  } else  {    this.offImage = new addToLoad(this.commandHolder.menu, this.commandHolder.menu.imageBottom).indx;    this.onImage = new addToLoad(this.commandHolder.menu, this.commandHolder.menu.imageBottomSel).indx  }  return this.subCommandHolder;}/** * The method sets the url of the menu / command. * * @param _url (String) url * @see #setJavaScript */eFapsToolbarCommand.prototype.setUrl = function(_url)  {  this.url = _url;}/** * Instead of an url a javascript funtion can be called. * * @param _javaScript (String) javascript function to call * @see #setUrl */eFapsToolbarCommand.prototype.setJavaScript = function(_javaScript)  {  this.javaScript = _javaScript;}/** * The method sets the icon of the menu / command. * * @param _icon (String) icon url */eFapsToolbarCommand.prototype.setIcon = function(_image)  {  this.image = _image;}/** * The method sets the target of the action of the menu / command. * * @param _target (String) target ('Content' or 'Popup') */eFapsToolbarCommand.prototype.setTarget = function(_target)  {  this.target = _target;}/** * @param _question (String) */eFapsToolbarCommand.prototype.setQuestion = function(_question)  {  this.question = _question;}/** * The methods sets that the command submits the form. * * @param _submit (Boolean) <i>true</i> means, the command submits the form, *                          otherwise only  a new window is opened */eFapsToolbarCommand.prototype.setSubmit = function(_submit)  {  this.submit = _submit;}/** * The methods sets the form submitted by this command. * * @param _submitForm (Form) form to submit * @see #setSubmit */eFapsToolbarCommand.prototype.setSubmitForm = function(_submitForm)  {  this.submitForm = _submitForm;}/** * Set the height for the new window to open from this command. * * @param _height (integer) new width for the popup window */eFapsToolbarCommand.prototype.setHeight = function(_height)  {  this.height = _height;}/** * Set the width for the new window to open from this command. * * @param _width (integer) new width for the popup window */eFapsToolbarCommand.prototype.setWidth = function(_width)  {  this.width = _width;}eFapsToolbarCommand.prototype.markAsSelected = function()  {  if (this.isSelectable())  {    var htmlObjs = this.getHtmlObjects();    for (var i=0; i<htmlObjs.length; i++)  {      htmlObjs[i].htmlObj.className = this.className + 'Selected';    }    if (this.onImage != null)  {      var imgName = 'img_' + this.commandHolder.menu.pre_sm + this.suffix;      for (var i=0; i<htmlObjs.length; i++)  {        htmlObjs[i].document.images[imgName].src = this.commandHolder.menu.to_load[this.onImage];      }    }  }}eFapsToolbarCommand.prototype.markAsUnselected = function()  {  var htmlObjs = this.getHtmlObjects();  for (var i=0; i<htmlObjs.length; i++)  {    htmlObjs[i].htmlObj.className = this.className;  }  if (this.offImage != null)  {    imgName = 'img_' + this.commandHolder.menu.pre_sm + this.suffix;    for (var i=0; i<htmlObjs.length; i++)  {      htmlObjs[i].document.images[imgName].src = this.commandHolder.menu.to_load[this.offImage];    }  }}/** * Returns the html object of the main document used to make the position. */eFapsToolbarCommand.prototype.getHtmlObjectOfMainDocument = function()  {  var theDoc = this.commandHolder.menu.calcWindow.document;  return theDoc.getElementById(this.id);}/** * Returns all html objects (html tables rows for pull down menus, html table  * columns for top menu) found with the current id. */eFapsToolbarCommand.prototype.getHtmlObjects = function()  {  var ret = new Array();  MYeFapsCommonFind(top, this.id, ret);  return ret;}/** * Executes a command if the user clicks on the command. */eFapsToolbarCommand.prototype.execute = function()  {  if (this.isSelectable())  {    this.commandHolder.menu.hideAll();    var bck = true;    if (this.question)  {      bck = confirm(this.question);    }    if (bck)  {      if (this.submit && this.submitForm)  {//            eFapsProcessStart();        this.submitForm.action = this.url;        if (this.submitForm.command)  {          this.submitForm.command.value = this.name;        }        this.submitForm.submit();      } else if (this.url!=null)  {        eFapsCommonOpenUrl(this.url, this.target, this.width, this.height);      } else if (this.javaScript!=null)  {        eval(this.javaScript);      }    }  }}/** * Tests, if the command is selectable (if no form is defined and it is not * a submit command or a checkbox is selected. * * @return true, if selectable, otherwise false  */eFapsToolbarCommand.prototype.isSelectable = function()  {  var selectable = false;  if (this.submit && this.submitForm)  {    var objs = document.getElementsByName("selectedRow");    for (var i=0; i<objs.length; i++)  {      if (objs[i].checked)  {        selectable = true;      }    }  } else  {    selectable = true;  }  return selectable;}/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////function eFapsToolbarSeparator()  {}/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////function eFapsToolbarCommandHolder(_menu, _suffix)  {  this.suffix = _suffix;  this.menu = _menu;  this.id = this.menu.pre_l + this.suffix;  this.commands = new Array();  this.parentCommand = null;  this._makeSubLink();}eFapsToolbarCommandHolder.prototype._makeSubLink = function()  {  for (var i=0;i < this.menu.commandHolders.length;i++)  {    var cmdHolder = this.menu.commandHolders[i];    for (j=0; j<cmdHolder.commands.length; j++)  {      var cmd = cmdHolder.commands[j];      if (cmd.subCommandHolderId == this.id)  {        cmd.subCommandHolder = this;        this.parentCommand = cmd;        break;      }    }  }}eFapsToolbarCommandHolder.prototype.addCommand = function(_name, _text)  {  this.commands[this.commands.length] = new eFapsToolbarCommand(this, _name, _text, this.suffix + "_" + this.commands.length);  return this.commands[this.commands.length-1];}eFapsToolbarCommandHolder.prototype.addSeparator = function()  {  this.commands[this.commands.length] = new eFapsToolbarSeparator();}/** * The function finds the frame with the name in the value of the parameter * <i>_target</i>. * * @param _current  (window)  current window object * @param _target   (string)  name of target * @return found window object */function MYeFapsCommonFind(_current, _target, _array)  {if (_current.frames && _current.frames.length>0)  {  for (var i=0; i < _current.frames.length; i++)  {    MYeFapsCommonFind(_current.frames[i], _target, _array);  }}var obj = _current.document.getElementById(_target);if (obj)  {  _array[_array.length] = new test(_current, _current.document, obj);}}function test(_win, _doc, _obj)  {this.window = _win;this.document = _doc;this.htmlObj = _obj;}/** * Returns all html objects (html tables) found with the current id. */eFapsToolbarCommandHolder.prototype.getHtmlObjects = function()  {  var ret = new Array();  MYeFapsCommonFind(top, this.id, ret);  return ret;}/** * Show this complete menu in all frames. */eFapsToolbarCommandHolder.prototype.show = function() {  var htmlObjs = this.getHtmlObjects();  for (var i=0; i<htmlObjs.length; i++)  {    htmlObjs[i].htmlObj.style.visibility = 'visible';  }}/** * Hide this complete menu in all frames. */eFapsToolbarCommandHolder.prototype.hide = function() {  var htmlObjs = this.getHtmlObjects();  for (var i=0; i<htmlObjs.length; i++)  {    htmlObjs[i].htmlObj.style.visibility = 'hidden';  }}eFapsToolbarCommandHolder.prototype.calcNewPosition = function()  {//if (top.frames[1] && top.frames[1].frames[1])  {top.frames[1].frames[1].outerWidth=200;}  if (this.parentCommand==null)  {      initialX = this.menu.xPos;      initialY = this.menu.yPos;  } else  {    var parentHtmlObj = this.parentCommand.getHtmlObjectOfMainDocument();    var initialX = 0;    var initialY = 0;    // correction, because first menu is not a pull down menu!    if (this.parentCommand.commandHolder.parentCommand!=null)  {      initialX += parentHtmlObj.offsetWidth;    } else  {      initialY += parentHtmlObj.offsetHeight;    }    // add the positions of parent objects (without frames)    while (parentHtmlObj!=null)  {      initialX += parentHtmlObj.offsetLeft;      initialY += parentHtmlObj.offsetTop;      parentHtmlObj = parentHtmlObj.offsetParent;    }  }  // add the positions of the parent frames  var currentWindow = this.menu.calcWindow;  var parentWindow = currentWindow.parent;  while (parentWindow!=currentWindow)  {    if (currentWindow.frameElement)  {      initialX += currentWindow.frameElement.offsetLeft;      initialY += currentWindow.frameElement.offsetTop;    }    currentWindow = parentWindow;    parentWindow = parentWindow.parent;  }  var htmlObjs = this.getHtmlObjects();  for (var i=0; i<htmlObjs.length; i++)  {    var y = initialY;    var x = initialX;    // calc from absolute position to relative position in frame    var win = htmlObjs[i].window;    while (win!=top)  {      if (win.frameElement)  {        x -= win.frameElement.offsetLeft;        y -= win.frameElement.offsetTop;      }      win = win.parent;    }    // set position    htmlObjs[i].htmlObj.style.top = y + 'px';    htmlObjs[i].htmlObj.style.left = x + 'px';  }}/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////function addNewImg (asrc){  var newimage = new Image ();  newimage.src = asrc;}function addToLoad(_menu, searchedString) {  extractURL = '';  if (searchedString.indexOf ('url(') > -1){    startStr = searchedString.indexOf ('(') + 1;    endStr = searchedString.length - startStr - 1;    extractURL = searchedString.substr(startStr,endStr);  } else if (searchedString.indexOf ('src') > -1){    firstposoff = searchedString.indexOf ('src="') + 5;    endposoff = searchedString.indexOf ('"',firstposoff);    extractURL = searchedString.substring (firstposoff,endposoff);    searchedString = 'url('+extractURL+')';  }    i = 0;    isMatch = false;    if (_menu.to_load.length > 0){    for (i = 0;i < _menu.to_load.length;i++){      if (_menu.to_load[i] == extractURL){        isMatch = true;        break      }    }    if (!isMatch){      oldLength = _menu.to_load.length;      _menu.to_load[_menu.to_load.length] = extractURL;      imgs_url[self.imgs_url.length] = searchedString;      addNewImg (extractURL)    }    this.indx = i;  } else {    _menu.to_load[0] = extractURL;    imgs_url[0] = searchedString;    addNewImg (extractURL);    this.indx = 0;  }  return this}