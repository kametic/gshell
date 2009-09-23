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

package org.apache.maven.shell.core.commands;

import org.apache.maven.shell.cli.Argument;
import org.apache.maven.shell.cli.Option;
import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.io.IO;
import org.codehaus.plexus.component.annotations.Component;

import java.util.Iterator;
import java.util.List;

/**
 * Print all arguments to the commands standard output.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 1.0
 */
@Component(role=Command.class, hint="echo")
public class EchoCommand
    extends CommandSupport
{
    @Option(name="-n")
    private boolean trailingNewline = true;

    @Argument
    private List<String> args = null;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        if (args != null && !args.isEmpty()) {
            Iterator iter = args.iterator();
            
            while (iter.hasNext()) {
                io.out.print(iter.next());
                if (iter.hasNext()) {
                    io.out.print(" ");
                }
            }
        }

        if (trailingNewline) {
            io.out.println();
        }

        return Result.SUCCESS;
    }
}