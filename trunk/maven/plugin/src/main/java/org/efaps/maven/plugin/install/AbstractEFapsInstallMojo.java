/*
 * Copyright 2003-2008 The eFaps Team
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
package org.efaps.maven.plugin.install;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.DirectoryScanner;
import org.jfrog.maven.annomojo.annotations.MojoParameter;

import org.efaps.maven.plugin.EFapsAbstractMojo;
import org.efaps.maven.plugin.goal.efaps.install.Application;
import org.efaps.update.Install.FileType;


/**
 * @author tmo
 * @author jmox
 * @version $Id$
 */
public abstract class AbstractEFapsInstallMojo extends EFapsAbstractMojo {

  // ///////////////////////////////////////////////////////////////////////////
  // static variables

  /**
   * Default Mapping of a a file extension to a Type for import and update
   */
  private final static Map<String, String> DEFAULT_TYPE_MAPPING =
      new HashMap<String, String>();
  static {
    DEFAULT_TYPE_MAPPING.put("css", FileType.CSS.type);
    DEFAULT_TYPE_MAPPING.put("java", FileType.JAVA.type);
    DEFAULT_TYPE_MAPPING.put("js", FileType.JS.type);
    DEFAULT_TYPE_MAPPING.put("xml", FileType.XML.type);
    DEFAULT_TYPE_MAPPING.put("xsl", FileType.XSL.type);
  }

  /**
   * Default list of includes used to evaluate the files to copy.
   *
   * @see #getFiles
   */
  private final static Set<String> DEFAULT_INCLUDES = new HashSet<String>();
  static {
    DEFAULT_INCLUDES.add("**/*.css");
    DEFAULT_INCLUDES.add("**/*.js");
    DEFAULT_INCLUDES.add("**/*.xml");
    DEFAULT_INCLUDES.add("**/*.xsl");
  }

  /**
   * Default list of excludes used to evaluate the files to copy.
   *
   * @see #getFiles
   */
  private final static Set<String> DEFAULT_EXCLUDES = new HashSet<String>();
  static  {
    DEFAULT_EXCLUDES.add("**/versions.xml");
  }

  /////////////////////////////////////////////////////////////////////////////
  // instance variables

  /**
   * Location of the version file (defining all versions to install).
   */
  @MojoParameter(expression = "${basedir}/src/main/efaps/versions.xml")
  private File versionFile;

  /**
   * Root Directory with the XML installation files.
   */
  @MojoParameter(expression = "${basedir}/src/main/efaps")
  private File eFapsDir;

  /**
   * List of includes.
   */
  @MojoParameter()
  private final List<String> includes = null;

  /**
   * List of excludes.
   */
  @MojoParameter()
  private final List<String> excludes = null;


  /**
   * Map of TypeMapping
   */
  @MojoParameter()
  private final Map<String, String> typeMapping = null;

  /////////////////////////////////////////////////////////////////////////////
  // instance methods

  /**
   * <code>null</code> is returned, of the version file could not be opened
   * and read.
   *
   * @return application instance with all version information
   * @todo description
   */
  protected Application getApplication() {
    Application appl = null;
    try {
      appl =
          Application.getApplication(this.versionFile.toURL(),
              getClasspathElements(), getEFapsDir());

      for (final String fileName : getFiles()) {
        final String type =
            getTypeMapping().get(
                fileName.substring(fileName.lastIndexOf(".") + 1));
        appl.addURL(new File(this.eFapsDir, fileName).toURL(), type);
      }
    } catch (final IOException e) {
      getLog().error(
          "Could not open / read version file " + "'" + this.versionFile + "'");
    } catch (final Exception e) {
      getLog().error(e);
    }
    return appl;
  }

  /**
   * Uses the {@link #includes} and {@link #excludes} together with the root
   * directory {@link #eFapsDir} to get all related and matched files.
   *
   * @see #includes defines includes; if not specified by maven, the default
   *      value is <code>**&#x002f;*.xml</code>
   * @see #excludes defines excludes; if not specified by maven , the default
   *      value is <code>**&#x002f;version.xml</code>
   * @see #DEFAULT_INCLUDES
   * @see #DEFAULT_EXCLUDES
   */
  protected String[] getFiles()  {
    final DirectoryScanner ds = new DirectoryScanner();
    final String[] includes = (this.includes == null)
                              ? DEFAULT_INCLUDES.toArray(new String[DEFAULT_INCLUDES.size()])
                              : this.includes.toArray(new String[this.includes.size()]);
    final String[] excludes  = (this.excludes == null)
                              ? DEFAULT_EXCLUDES.toArray(new String[DEFAULT_EXCLUDES.size()])
                              : this.excludes.toArray(new String[this.excludes.size()]);
    ds.setIncludes(includes);
    ds.setExcludes(excludes);
    ds.setBasedir(getEFapsDir().toString());
    ds.setCaseSensitive(true);
    ds.scan();

    return ds.getIncludedFiles();
  }

  /////////////////////////////////////////////////////////////////////////////
  // instance getter / setter methods

  /**
   * Getter method for instance variable {@see #eFapsDir}.
   *
   * @return value of instance variable eFapsDir
   * @see #eFapsDir
   */
  protected File getEFapsDir() {
    return this.eFapsDir;
  }

  /**
   * this method return the TypeMapping for import and update. The mapping can
   * be defined in the pom.xml with the parameter {@link #typeMapping}. In the
   * case that the mapping is not defined in the pom.xml a default
   * {@link #DEFAULT_TYPE_MAPPING} will be returned.
   *
   * @return Map containing the mapping of file extension to type
   * @see #typeMapping
   * @see #DEFAULT_TYPE_MAPPING
   */
  protected Map<String, String> getTypeMapping() {
    return (this.typeMapping == null) ? DEFAULT_TYPE_MAPPING : this.typeMapping;
  }

  /**
   * Getter method for instance variable {@see #versionFile}.
   *
   * @return value of instance variable versionFile
   * @see #versionFile
   */
  protected File getVersionFile() {
    return this.versionFile;
  }
}
