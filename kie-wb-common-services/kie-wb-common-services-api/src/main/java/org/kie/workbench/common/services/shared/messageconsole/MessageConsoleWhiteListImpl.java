/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.shared.messageconsole;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.messageconsole.whitelist.MessageConsoleWhiteList;

@ApplicationScoped
public class MessageConsoleWhiteListImpl
        implements MessageConsoleWhiteList {

    private List<String> allowedPerspectives = new ArrayList<String>();

    public MessageConsoleWhiteListImpl() {
        allowedPerspectives.add("org.kie.workbench.drools.client.perspectives.DroolsAuthoringPerspective");
        allowedPerspectives.add("org.kie.workbench.client.perspectives.DroolsAuthoringPerspective");
        allowedPerspectives.add("org.drools.workbench.client.perspectives.AuthoringPerspective");
    }

    @Override
    public boolean contains(String perspective) {
        return allowedPerspectives.contains(perspective);
    }
}
