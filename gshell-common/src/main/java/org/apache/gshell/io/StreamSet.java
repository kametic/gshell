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

package org.apache.gshell.io;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * A set of input, output and error streams.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class StreamSet
{
    public final InputStream in;

    public final PrintStream out;

    public final PrintStream err;

    public StreamSet(final InputStream in, final PrintStream out, final PrintStream err) {
        assert in != null;
        assert out != null;
        assert err != null;

        this.in = in;
        this.out = out;
        this.err = err;
    }

    public StreamSet(final InputStream in, final PrintStream out) {
        this(in, out, out);
    }

    public boolean isCombinedOutput() {
        return out == err;
    }

    public InputStream getInput() {
        return in;
    }

    public PrintStream getOutput(final OutputType type) {
        assert type != null;
        return type.get(this);
    }

    public void flush() {
        Flusher.flush(out);

        if (!isCombinedOutput()) {
            Flusher.flush(err);
        }
    }

    public void close() {
        Closer.close(in, out);

        if (!isCombinedOutput()) {
            Closer.close(err);
        }
    }

    /**
     * Create a new pair as System is currently configured.
     */
    public static StreamSet system() {
        return new StreamSet(System.in, System.out, System.err);
    }

    /**
     * Install the given stream pair as the System streams.
     */
    public static void system(final StreamSet streams) {
        assert streams != null;

        System.setIn(streams.in);
        System.setOut(streams.out);
        System.setErr(streams.err);
    }

    /**
     * The original System streams (as they were when this class loads).
     */
    public static final StreamSet SYSTEM = system();

    /**
     * Output stream type.
     */
    public static enum OutputType
    {
        OUT, ERR;

        private PrintStream get(final StreamSet set) {
            assert set != null;

            switch (this) {
                case OUT:
                    return set.out;

                case ERR:
                    return set.err;
            }

            // Should never happen
            throw new InternalError();
        }
    }
}