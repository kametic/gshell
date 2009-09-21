/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.shell;

import jline.Completor;
import org.apache.maven.shell.console.completer.AggregateCompleter;
import org.apache.maven.shell.core.MainSupport;
import org.apache.maven.shell.core.ShellBuilder;
import org.apache.maven.shell.core.impl.console.ConsoleErrorHandlerImpl;
import org.apache.maven.shell.core.impl.console.ConsolePrompterImpl;
import org.codehaus.plexus.PlexusContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command-line bootstrap for Apache Maven Shell (<tt>shell</tt>).
 *
 * @version $Rev$ $Date$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Main
    extends MainSupport
{
    protected Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected Branding createBranding() {
        return new BrandingImpl();
    }

    @Override
    protected Shell createShell() throws Exception {
        PlexusContainer container = ShellBuilder.createContainer();
        
        Shell shell = new ShellBuilder()
                .setContainer(container)
                .setBranding(getBranding())
                .setIo(io)
                .setVariables(vars)
                .setPrompter(new ConsolePrompterImpl(vars, getBranding()))
                .setErrorHandler(new ConsoleErrorHandlerImpl(io))
                .addCompleter(new AggregateCompleter(
                        container.lookup(Completor.class, "alias-name"),
                        container.lookup(Completor.class, "commands")
                ))
                .create();

        log.debug("Created shell: {}", shell);

        return shell;
    }

    public static void main(final String[] args) throws Exception {
        new Main().boot(args);
    }
}