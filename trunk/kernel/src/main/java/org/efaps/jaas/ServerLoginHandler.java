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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.jaas;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;

import org.efaps.admin.user.Person;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author jmox
 * @version $Id$
 */
public class ServerLoginHandler implements CallbackHandler {

  /**
   * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
   * @param _callback
   * @throws IOException
   * @throws UnsupportedCallbackException
   */
  public void handle(final Callback[] _callbacks) throws IOException,
      UnsupportedCallbackException {
    String name = null;
    PasswordCallback pwdCallback = null;
    for (int i = 0; i < _callbacks.length; i++) {
      if (_callbacks[i] instanceof NameCallback) {
        name = ((NameCallback) _callbacks[i]).getName();
        if (name == null) {
          name = ((NameCallback) _callbacks[i]).getDefaultName();
        }
      } else if (_callbacks[i] instanceof PasswordCallback) {
        pwdCallback = ((PasswordCallback) _callbacks[i]);
      } else if (_callbacks[i] instanceof AuthorizeCallback) {
        final AuthorizeCallback authCallback
                                          = ((AuthorizeCallback) _callbacks[i]);
        final String authenId = authCallback.getAuthenticationID();
        final String authorId = authCallback.getAuthorizationID();
        if (authenId.equals(authorId)) {
          authCallback.setAuthorized(true);
          authCallback.setAuthorizedID(authorId);
        }
      }
    }
    if (name != null && pwdCallback != null) {
      try {
        final Person person = Person.get(name);
        if (person != null) {
          pwdCallback.setPassword(person.getPassword().toCharArray());
        } else {
          //fehler schmeissen
        }
      } catch (final EFapsException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
