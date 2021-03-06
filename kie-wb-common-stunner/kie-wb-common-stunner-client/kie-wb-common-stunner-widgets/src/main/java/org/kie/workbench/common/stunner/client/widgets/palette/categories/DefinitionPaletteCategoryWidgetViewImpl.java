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

package org.kie.workbench.common.stunner.client.widgets.palette.categories;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidget;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidget;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteCategory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;

@Templated
@Dependent
public class DefinitionPaletteCategoryWidgetViewImpl implements DefinitionPaletteCategoryWidgetView,
                                                                IsElement {

    private static final String SHOW_FLYOUT_CSS = "kie-palette-show-flyout";

    @Inject
    private Document document;

    @Inject
    @DataField
    private ListItem listGroupItem;

    @Inject
    @DataField
    private Button categoryIcon;

    @Inject
    @DataField
    private Div floatingPanel;

    @Inject
    @DataField
    private Button closeCategoryButton;

    @Inject
    private DOMGlyphRenderers domGlyphRenderers;

    private Presenter presenter;

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render(Glyph glyph,
                       double width,
                       double height) {
        DefaultPaletteCategory category = presenter.getCategory();
        final org.jboss.errai.common.client.api.IsElement glyphElement =
                domGlyphRenderers.render(glyph,
                                         width,
                                         height);
        categoryIcon.appendChild(glyphElement.getElement());
    }

    @Override
    public void addItem(DefinitionPaletteItemWidget item) {
        floatingPanel.appendChild(item.getElement());
    }

    @Override
    public void addGroup(DefinitionPaletteGroupWidget groupWidget) {
        HTMLElement groupHeader = document.createElement("h5");

        groupHeader.setTextContent(groupWidget.getItem().getTitle());
        floatingPanel.appendChild(groupHeader);

        floatingPanel.appendChild(groupWidget.getElement());
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            DOMUtil.addCSSClass(listGroupItem,
                                SHOW_FLYOUT_CSS);
        } else {
            DOMUtil.removeCSSClass(listGroupItem,
                                   SHOW_FLYOUT_CSS);
        }
    }

    @Override
    public boolean isVisible() {
        return DOMUtil.hasCSSClass(listGroupItem,
                                   "kie-palette-show-flyout");
    }

    @EventHandler("categoryIcon")
    public void onMouseDown(MouseDownEvent mouseDownEvent) {
        presenter.onMouseDown(mouseDownEvent.getClientX(),
                              mouseDownEvent.getClientY(),
                              mouseDownEvent.getX(),
                              mouseDownEvent.getY());
    }

    @EventHandler("closeCategoryButton")
    public void onClose(ClickEvent mouseClickEvent) {
        presenter.onClose();
    }
}
