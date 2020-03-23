/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.client.editors.expressions.types.list;

import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumnRenderer;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridWidgetRegistry;

import static org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableHeaderUtilities.getEditableHeaderContent;

public class ListExpressionEditorColumnRenderer extends ExpressionEditorColumnRenderer {

    public ListExpressionEditorColumnRenderer(final GridWidgetRegistry registry) {
        super(registry);
    }

    @Override
    public Group renderHeaderContent(final List<GridColumn.HeaderMetaData> headerMetaData,
                                     final GridHeaderColumnRenderContext context,
                                     final int headerRowIndex,
                                     final double blockWidth,
                                     final double rowHeight) {
        return getEditableHeaderContent(() -> super.renderHeaderContent(headerMetaData,
                                                                        context,
                                                                        headerRowIndex,
                                                                        blockWidth,
                                                                        rowHeight),
                                        headerMetaData,
                                        context,
                                        headerRowIndex,
                                        blockWidth,
                                        rowHeight);
    }
}
