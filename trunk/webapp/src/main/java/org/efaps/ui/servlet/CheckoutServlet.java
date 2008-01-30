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

package org.efaps.ui.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.efaps.db.Checkout;

/**
 * The servlet checks out files from objects.
 *
 * @author tmo
 * @version $Id$
 */
public class CheckoutServlet extends HttpServlet {

  private static final long   serialVersionUID = 7810426422513524710L;

  /**
   * name of the object id parameter
   */
  final private static String PARAM_OID        = "oid";

  /**
   * The method checks the file from the object out and returns them in a output
   * stream to the web client. The object id must be given with paramter
   * {@link #PARAM_OID}.<br/>
   *
   * @param _req
   *          request variable
   * @param _res
   *          response variable
   * @see #PARAM_ATTRNAME
   * @see #PARAM_OID
   */
  @Override
  protected void doGet(HttpServletRequest _req, HttpServletResponse _res)
                                                                         throws ServletException,
                                                                         IOException {
    final String oid = _req.getParameter(PARAM_OID);

    try {
      final Checkout checkout = new Checkout(oid);
      checkout.preprocess();

      _res.setContentType(getServletContext().getMimeType(
          checkout.getFileName()));
      _res.addHeader("Content-Disposition", "inline; filename=\""
          + checkout.getFileName() + "\"");

      checkout.execute(_res.getOutputStream());

    } catch (final IOException e) {
      throw e;
    } catch (final ServletException e) {
      throw e;
    } catch (final Throwable e) {
      throw new ServletException(e);
    }
  }
}
