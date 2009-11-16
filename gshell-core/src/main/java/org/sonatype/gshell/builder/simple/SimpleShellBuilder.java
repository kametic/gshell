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

package org.sonatype.gshell.builder.simple;

import jline.console.completers.AggregateCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.gshell.Branding;
import org.sonatype.gshell.Shell;
import org.sonatype.gshell.ShellFactory;
import org.sonatype.gshell.ShellImpl;
import org.sonatype.gshell.Variables;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandAction;
import org.sonatype.gshell.command.CommandDocumenter;
import org.sonatype.gshell.command.CommandDocumenterImpl;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.commands.AliasCommand;
import org.sonatype.gshell.commands.EchoCommand;
import org.sonatype.gshell.commands.ExitCommand;
import org.sonatype.gshell.commands.HelpCommand;
import org.sonatype.gshell.commands.HistoryCommand;
import org.sonatype.gshell.commands.InfoCommand;
import org.sonatype.gshell.commands.RecallHistoryCommand;
import org.sonatype.gshell.commands.SetCommand;
import org.sonatype.gshell.commands.SourceCommand;
import org.sonatype.gshell.commands.UnaliasCommand;
import org.sonatype.gshell.commands.UnsetCommand;
import org.sonatype.gshell.commands.preference.ExportPreferencesCommand;
import org.sonatype.gshell.commands.preference.GetPreferenceCommand;
import org.sonatype.gshell.commands.preference.ImportPreferencesCommand;
import org.sonatype.gshell.commands.preference.ListPreferencesCommand;
import org.sonatype.gshell.commands.preference.RemovePreferencesCommand;
import org.sonatype.gshell.commands.preference.SetPreferenceCommand;
import org.sonatype.gshell.commands.preference.UnsetPreferenceCommand;
import org.sonatype.gshell.console.ConsoleErrorHandler;
import org.sonatype.gshell.console.ConsolePrompt;
import org.sonatype.gshell.console.completer.AliasNameCompleter;
import org.sonatype.gshell.console.completer.CommandNameCompleter;
import org.sonatype.gshell.console.completer.CommandsCompleter;
import org.sonatype.gshell.console.completer.FileNameCompleter;
import org.sonatype.gshell.console.completer.VariableNameCompleter;
import org.sonatype.gshell.event.EventManager;
import org.sonatype.gshell.event.EventManagerImpl;
import org.sonatype.gshell.execute.CommandExecutor;
import org.sonatype.gshell.execute.CommandExecutorImpl;
import org.sonatype.gshell.execute.CommandLineParser;
import org.sonatype.gshell.parser.CommandLineParserImpl;
import org.sonatype.gshell.registry.AliasRegistry;
import org.sonatype.gshell.registry.AliasRegistryImpl;
import org.sonatype.gshell.registry.CommandRegistrar;
import org.sonatype.gshell.registry.CommandRegistry;
import org.sonatype.gshell.registry.CommandRegistryImpl;
import org.sonatype.gshell.registry.CommandResolver;
import org.sonatype.gshell.registry.CommandResolverImpl;

/**
 * Builds {@link Shell} instances w/o any IoC container.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class SimpleShellBuilder
    implements ShellFactory
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Components components;

    protected Branding branding;

    protected IO io;

    protected Variables variables;

    protected boolean registerCommands = true;

    protected ConsolePrompt prompt;

    protected ConsoleErrorHandler errorHandler;

    public SimpleShellBuilder() throws Exception {
        this.components = new ComponentsImpl();
    }

    public Components getComponents() {
        return components;
    }

    public SimpleShellBuilder setBranding(final Branding branding) {
        this.branding = branding;
        return this;
    }

    public SimpleShellBuilder setIo(final IO io) {
        this.io = io;
        return this;
    }

    public SimpleShellBuilder setVariables(final Variables variables) {
        this.variables = variables;
        return this;
    }

    public SimpleShellBuilder setRegisterCommands(final boolean flag) {
        this.registerCommands = flag;
        return this;
    }

    public SimpleShellBuilder setPrompt(final ConsolePrompt prompt) {
        this.prompt = prompt;
        return this;
    }

    public SimpleShellBuilder setErrorHandler(final ConsoleErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public Shell create() throws Exception {
        if (branding == null) {
            throw new IllegalStateException("Missing branding");
        }

        // Maybe register default commands
        if (registerCommands) {
            registerCommands(getComponents());
        }

        Shell shell = createShell(getComponents());
        log.debug("Created shell: {}", shell);

        return shell;
    }

    protected void registerCommand(final CommandAction command) throws Exception {
        String name = command.getClass().getAnnotation(Command.class).name();
        CommandRegistry registry = components.getCommandRegistry();
        registry.registerCommand(name, command);
    }

    protected void registerCommands(final Components components) throws Exception {
        assert components != null;

        CommandRegistry registry = components.getCommandRegistry();

        registerCommand(new HelpCommand(components.getAliasRegistry(), registry, components.getCommandDocumenter())
            .installCompleters(components.getAliasNameCompleter(), components.getCommandNameCompleter()));

        registerCommand(new InfoCommand());

        registerCommand(new ExitCommand());

        registerCommand(new SetCommand()
            .installCompleters(components.getVariableNameCompleter()));

        registerCommand(new UnsetCommand()
            .installCompleters(components.getVariableNameCompleter()));

        registerCommand(new HistoryCommand());

        registerCommand(new RecallHistoryCommand());

        registerCommand(new SourceCommand()
            .installCompleters(components.getFileNameCompleter()));

        registerCommand(new AliasCommand(components.getAliasRegistry()));

        registerCommand(new UnaliasCommand(components.getAliasRegistry())
            .installCompleters(components.getAliasNameCompleter()));

        registerCommand(new EchoCommand());

        registerCommand(new ListPreferencesCommand());
        registerCommand(new SetPreferenceCommand());
        registerCommand(new GetPreferenceCommand());
        registerCommand(new UnsetPreferenceCommand());
        registerCommand(new RemovePreferencesCommand());
        registerCommand(new ImportPreferencesCommand());
        registerCommand(new ExportPreferencesCommand());
    }

    protected Shell createShell(final Components components) throws Exception {
        assert components != null;

        // Create the shell instance
        ShellImpl shell = new ShellImpl(components.getEventManager(), components.getCommandExecutor(), branding, io, variables);
        shell.setPrompt(prompt);
        shell.setErrorHandler(errorHandler);
        shell.setCompleters(new AggregateCompleter(components.getAliasNameCompleter(), components.getCommandsCompleter()));

        return shell;
    }

    public static interface Components
    {
        AliasRegistry getAliasRegistry();

        CommandDocumenter getCommandDocumenter();

        CommandExecutor getCommandExecutor();

        CommandLineParser getCommandLineParser();

        CommandRegistrar getCommandRegistrar();

        CommandRegistry getCommandRegistry();

        CommandResolver getCommandResolver();

        EventManager getEventManager();

        FileNameCompleter getFileNameCompleter();

        VariableNameCompleter getVariableNameCompleter();

        AliasNameCompleter getAliasNameCompleter();

        CommandNameCompleter getCommandNameCompleter();

        CommandsCompleter getCommandsCompleter();
    }

    private class ComponentsImpl
        implements Components
    {
        private final EventManager eventManager;

        private final CommandRegistry commandRegistry;

        private final AliasRegistry aliasRegistry;

        private final CommandResolver commandResolver;

        private final CommandLineParser commandLineParser;

        private final CommandDocumenter commandDocumenter;

        private final CommandExecutor commandExecutor;

        private final CommandRegistrar commandRegistrar;

        private final AliasNameCompleter aliasNameCompleter;

        private final CommandNameCompleter commandNameCompleter;

        private final CommandsCompleter commandsCompleter;

        private final VariableNameCompleter variableNameCompleter;

        private final FileNameCompleter fileNameCompleter;

        public ComponentsImpl() throws Exception {
            // Core components
            eventManager = new EventManagerImpl();
            commandRegistry = new CommandRegistryImpl(eventManager);
            aliasRegistry = new AliasRegistryImpl(eventManager);
            commandResolver = new CommandResolverImpl(aliasRegistry, commandRegistry);
            commandLineParser = new CommandLineParserImpl();
            commandDocumenter = new CommandDocumenterImpl();
            commandExecutor = new CommandExecutorImpl(commandResolver, commandLineParser, commandDocumenter);
            commandRegistrar = new SimpleCommandRegistrar(commandRegistry);

            // Core completers
            aliasNameCompleter = new AliasNameCompleter(eventManager, aliasRegistry);
            commandNameCompleter = new CommandNameCompleter(eventManager, commandRegistry);
            commandsCompleter = new CommandsCompleter(eventManager, commandRegistry);
            variableNameCompleter = new VariableNameCompleter();
            fileNameCompleter = new FileNameCompleter();
        }

        public EventManager getEventManager() {
            return eventManager;
        }

        public CommandRegistry getCommandRegistry() {
            return commandRegistry;
        }

        public AliasRegistry getAliasRegistry() {
            return aliasRegistry;
        }

        public CommandResolver getCommandResolver() {
            return commandResolver;
        }

        public CommandLineParser getCommandLineParser() {
            return commandLineParser;
        }

        public CommandDocumenter getCommandDocumenter() {
            return commandDocumenter;
        }

        public CommandExecutor getCommandExecutor() {
            return commandExecutor;
        }

        public CommandRegistrar getCommandRegistrar() {
            return commandRegistrar;
        }

        public AliasNameCompleter getAliasNameCompleter() {
            return aliasNameCompleter;
        }

        public CommandNameCompleter getCommandNameCompleter() {
            return commandNameCompleter;
        }

        public CommandsCompleter getCommandsCompleter() {
            return commandsCompleter;
        }

        public VariableNameCompleter getVariableNameCompleter() {
            return variableNameCompleter;
        }

        public FileNameCompleter getFileNameCompleter() {
            return fileNameCompleter;
        }
    }
}