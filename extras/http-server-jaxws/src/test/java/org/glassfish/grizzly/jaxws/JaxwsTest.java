/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.grizzly.jaxws;

import java.io.IOException;
import java.util.Random;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.jaxws.addclient.AddServiceService;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic Grizzly JAX-WS {@link HttpHandler} test.
 * 
 * @author Alexey Stashok
 */
public class JaxwsTest {
    private static final int PORT = 19881;
    
    private HttpServer httpServer;
    
    @Test
    public void testSync() throws Exception {
        startServer(new JaxwsHandler(new AddService(), false));
        
        try {
            test(10);
        } finally {
            stopServer();
        }
    }
    
    @Test
    public void testAsync() throws Exception {
        startServer(new JaxwsHandler(new AddService(), true));
        
        try {
            test(10);
        } finally {
            stopServer();
        }
        
    }
    
    private void startServer(HttpHandler httpHandler) throws IOException {
        httpServer = new HttpServer();
        NetworkListener networkListener = new NetworkListener("jaxws-listener", "0.0.0.0", PORT);
        
        httpServer.getServerConfiguration().addHttpHandler(new StaticHttpHandler(), "/add"); // make sure JAX-WS Handler is not default
        httpServer.getServerConfiguration().addHttpHandler(httpHandler,
                HttpHandlerRegistration.bulder()
                    .contextPath("/add/a/b")
                    .urlPattern("/")
                    .build());
        httpServer.addListener(networkListener);
        
        httpServer.start();        
    }
    
    private void stopServer() {
        httpServer.shutdownNow();
    }

    private void test(int n) {
        final Random random = new Random();
        
        AddServiceService service = new AddServiceService();
        org.glassfish.grizzly.jaxws.addclient.AddService port = service.getAddServicePort();
        for (int i=0; i<n; i++) {
            final int value1 = random.nextInt(1000);
            final int value2 = random.nextInt(1000);
            final int result = port.add(value1, value2);
            
            assertEquals(value1 + value2, result);
        }
    }
}
