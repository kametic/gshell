/*
 * Copyright (C) 2010 the original author or authors.
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

package org.sonatype.gshell.help;

/**
 * Filter to include pages which are NOT of the given type.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
public class PageExcludeTypeFilter
    implements HelpPageFilter
{
    private Class<? extends HelpPage> type;

    public PageExcludeTypeFilter(final Class<? extends HelpPage> type) {
        assert type != null;
        this.type = type;
    }

    public boolean accept(final HelpPage page) {
        assert page != null;
        return !type.isAssignableFrom(page.getClass());
    }
}