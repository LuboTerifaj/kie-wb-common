/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.project.client.type;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.categories.Process;
import org.kie.workbench.common.dmn.api.resource.DMNDefinitionSetResourceType;
import org.kie.workbench.common.dmn.project.client.resources.DMNProjectImageResources;
import org.kie.workbench.common.stunner.project.client.type.AbstractStunnerClientResourceType;

@ApplicationScoped
public class DMNDiagramResourceType extends AbstractStunnerClientResourceType<DMNDefinitionSetResourceType> {

    private static final Image ICON = new Image(DMNProjectImageResources.INSTANCE.dmnIcon());

    protected DMNDiagramResourceType() {
        this(null,
             null);
    }

    @Inject
    public DMNDiagramResourceType(final DMNDefinitionSetResourceType definitionSetResourceType,
                                  final Process category) {
        super(definitionSetResourceType,
              category);
    }

    @Override
    public IsWidget getIcon() {
        return ICON;
    }
}
