/*
 * Copyright (C) 2009 the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.geronimo.gshell.commands.text;

import org.sonatype.gshell.core.command.CommandActionSupport;
import org.sonatype.gshell.cli.Argument;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.command.Command;

import java.util.Collection;

/**
 * Prints formatted output.
 *
 * @version $Rev$ $Date$
 */
@Command
public class PrintfCommand
    extends CommandActionSupport
{
    @Argument(index=0, required=true)
    private String format;

    @Argument(index=1, multiValued=true, required=true)
    private Collection<String> arguments = null;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        io.out.printf(format, arguments.toArray());

        return Result.SUCCESS;
    }
}