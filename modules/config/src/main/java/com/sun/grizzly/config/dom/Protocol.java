/*
 *   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 *   Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.
 *
 *   The contents of this file are subject to the terms of either the GNU
 *   General Public License Version 2 only ("GPL") or the Common Development
 *   and Distribution License("CDDL") (collectively, the "License").  You
 *   may not use this file except in compliance with the License. You can obtain
 *   a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 *   or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 *   language governing permissions and limitations under the License.
 *
 *   When distributing the software, include this License Header Notice in each
 *   file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *   Sun designates this particular file as subject to the "Classpath" exception
 *   as provided by Sun in the GPL Version 2 section of the License file that
 *   accompanied this code.  If applicable, add the following below the License
 *   Header, with the fields enclosed by brackets [] replaced by your own
 *   identifying information: "Portions Copyrighted [year]
 *   [name of copyright owner]"
 *
 *   Contributor(s):
 *
 *   If you wish your version of this file to be governed by only the CDDL or
 *   only the GPL Version 2, indicate your decision by adding "[Contributor]
 *   elects to include this software in this distribution under the [CDDL or GPL
 *   Version 2] license."  If you don't indicate a single choice of license, a
 *   recipient has the option to distribute your version of this file under
 *   either the CDDL, the GPL Version 2 or to extend the choice of license to
 *   its licensees as provided above.  However, if you add GPL Version 2 code
 *   and therefore, elected the GPL Version 2 license, then the option applies
 *   only if the new code is made subject to such option by the copyright
 *   holder.
 *
 */
package com.sun.grizzly.config.dom;

import org.jvnet.hk2.component.Injectable;
import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBean;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;
import org.jvnet.hk2.config.DuckTyped;
import org.jvnet.hk2.config.Element;
import org.jvnet.hk2.config.types.PropertyBag;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

/**
 * Defines one single high-level protocol like: http, https, iiop, etc.
 */
@Configured
public interface Protocol extends ConfigBeanProxy, Injectable, PropertyBag {
    /**
     * Defines any HTTP settings for this Protocol
     */
    @Element
    Http getHttp();

    void setHttp(Http value);

    /**
     * Protocol name which could be used as reference
     */
    @Attribute(required = true, key = true)
    String getName();

    void setName(String value);

    /**
     * Defines port-unification logic.  If it is required to handle more than one high level protocol on a single
     * network-listener.
     */
    @Element
    PortUnification getPortUnification();

    void setPortUnification(PortUnification value);

    /**
     * Protocol chain instance handler logic.
     */
    @Element
    ProtocolChainInstanceHandler getProtocolChainInstanceHandler();

    void setProtocolChainInstanceHandler(ProtocolChainInstanceHandler value);

    /**
     * True means the protocol is secured and ssl element will be used to initialize security settings. False means that
     * protocol is not secured and ssl element, if present, will be ignored.
     */
    @Attribute(defaultValue = "false", dataType = Boolean.class)
    String getSecurityEnabled();

    void setSecurityEnabled(String value);

    /**
     * Protocol security (ssl) configuration.
     */
    @Element
    Ssl getSsl();

    void setSsl(Ssl value);

    @DuckTyped
    List<NetworkListener> findNetworkListeners();

    class Duck {
        static public List<NetworkListener> findNetworkListeners(Protocol protocol) {
            final Collection<NetworkListener> listeners = ConfigBean.unwrap(protocol).getHabitat()
                .getAllByContract(NetworkListener.class);
            List<NetworkListener> refs = new ArrayList<NetworkListener>();
            for (NetworkListener listener : listeners) {
                if (listener.getProtocol().equals(protocol.getName())) {
                    refs.add(listener);
                }
            }
            return refs;
        }
    }
}