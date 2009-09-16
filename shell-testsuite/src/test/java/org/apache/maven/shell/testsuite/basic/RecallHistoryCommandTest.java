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

package org.apache.maven.shell.testsuite.basic;

import org.apache.maven.shell.History;
import org.apache.maven.shell.cli.ProcessingException;
import org.apache.maven.shell.testsuite.CommandTestSupport;

/**
 * Tests for the {@link RecallHistoryCommand}.
 *
 * @version $Rev$ $Date$
 */
public class RecallHistoryCommandTest
    extends CommandTestSupport
{
    public RecallHistoryCommandTest() {
        super("recall");
    }

    public void testDependenciesRegistered() throws Exception {
        assertTrue(commandRegistry.containsCommand("set"));
    }

    @Override
    public void testDefault() throws Exception {
        try {
            super.testDefault();
            fail();
        }
        catch (ProcessingException e) {
            // expected
        }
    }

    public void testTooManyArguments() throws Exception {
        try {
            executeWithArgs("1 2");
            fail();
        }
        catch (ProcessingException e) {
            // expected
        }
    }

    public void testIndexOutOfRange() throws Exception {
        Object result = executeWithArgs(String.valueOf(getShell().getHistory().size()));
        assertEqualsFailure(result);
    }

    public void testInvalidIndex() throws Exception {
        try {
            executeWithArgs("foo");
            fail();
        }
        catch (NumberFormatException e) {
            // expected
        }
    }

    public void testRecallElement() throws Exception {
        History history = getShell().getHistory();

        // Clear history and make sure there is no foo variable
        history.clear();
        assertFalse(vars.contains("foo"));

        // Then add 2 elements, both setting foo
        history.add("set foo bar");
        history.add("set foo baz");

        assertEquals(2, getShell().getHistory().size());

        // Recall the first, which sets foo to bar
        Object result = executeWithArgs("0");
        assertEqualsSuccess(result);

        // Make sure it executed
        assertEquals("bar", vars.get("foo"));
    }
}