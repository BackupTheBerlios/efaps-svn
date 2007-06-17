<%--
 
  Copyright 2003-2007 The eFaps Team
 
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 
  Author:          tmo
  Revision:        $Rev$
  Last Changed:    $Date$
  Last Changed By: $Author$
 
--%> 

<%@page errorPage="Exception.jsp"%>
<%@include file = "../common/StdTop.inc"%>

<jsp:useBean id="cache" class="org.efaps.beans.CacheSessionBean" scope="session"/>

<%-- /** constructor for the form bean **/ --%>
<c:set var="method" value="process"/>
<%@include file = "../common/FormBeanConstructor.inc"%>
<c:remove var="method"/>

<html>
  <script type="text/javascript" src="../javascripts/eFapsDefault.js"></script>
  <body>
    <script language="Javascript">
      if (top.opener.name=='TreeNavigation')  {
        eFapsCommonOpenUrl('MenuTree.jsp?oid=<%=uiObject.getInstance().getOid()%>', 'Content');
      } else  {
        top.opener.eFapsCommonRefresh();
      }
      eFapsCommonCloseWindow();
    </script>
  </body>
</html>

<%@include file = "../common/StdBottom.inc"%>
