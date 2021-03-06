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
 * Revision:        $Rev:1522 $
 * Last Changed:    $Date:2007-10-23 23:27:31 +0200 (Di, 23 Okt 2007) $
 * Last Changed By: $Author:tmo $
 */

package org.efaps.maven.derby;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.tools.plugin.Goal;

/**
 * Starts the derby database. The derby database runs without security manager.
 *
 * @author tmo
 * @version $Id:DerbyStartMojo.java 1522 2007-10-23 21:27:31Z tmo $
 */
@Goal(name = "start")
public class StartMojo extends DerbyAbstractMojo
{
  /**
   * Executes the start of a derby database.
   */
  public void execute() throws MojoExecutionException  {
    getLog().info("Start Derby Database");
    initSystemProperties();
    NetworkServerControl.main(new String[]{"-noSecurityManager", "start"});
  }
}
