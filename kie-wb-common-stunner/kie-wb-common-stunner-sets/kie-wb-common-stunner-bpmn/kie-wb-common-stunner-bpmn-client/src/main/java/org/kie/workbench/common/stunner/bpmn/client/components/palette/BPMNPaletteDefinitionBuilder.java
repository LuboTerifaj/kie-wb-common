/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.components.palette;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.bpmn.client.resources.BPMNImageResources;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTaskFactory;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteCategory;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinition;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.CategoryBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.GroupBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.ItemBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.CategoryDefinitionProvider;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteGroup;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteItem;
import org.kie.workbench.common.stunner.core.client.components.palette.ExpandedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.shape.SvgDataUriGlyph.Builder;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

import static org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionProviders.isType;

@ApplicationScoped
@BPMN
public class BPMNPaletteDefinitionBuilder
        implements PaletteDefinitionBuilder<AbstractCanvasHandler, DefaultPaletteDefinition> {

    private static final CategoryDefinitionProvider CATEGORY_DEFINITION =
            new CategoryDefinitionProvider(BPMNCategories.class)
                    .put(BPMNCategories.START_EVENTS,
                         category -> category
                                 .bindToDefinition(StartNoneEvent.class)
                                 .useGlyph(Builder.build(BPMNImageResources.INSTANCE.categoryStartEvents().getSafeUri())))
                    .put(BPMNCategories.INTERMEDIATE_EVENTS,
                         category -> category
                                 .bindToDefinition(IntermediateTimerEvent.class)
                                 .useGlyph(Builder.build(BPMNImageResources.INSTANCE.categoryIntermediateEvents().getSafeUri())))
                    .put(BPMNCategories.END_EVENTS,
                         category -> category
                                 .bindToDefinition(EndNoneEvent.class)
                                 .useGlyph(Builder.build(BPMNImageResources.INSTANCE.categoryEndEvents().getSafeUri())))
                    .put(BPMNCategories.ACTIVITIES,
                         category -> category
                                 .bindToDefinition(NoneTask.class)
                                 .useGlyph(Builder.build(BPMNImageResources.INSTANCE.categoryActivity().getSafeUri())))
                    .put(BPMNCategories.SUB_PROCESSES,
                         category -> category
                                 .bindToDefinition(ReusableSubprocess.class)
                                 .useGlyph(Builder.build(BPMNImageResources.INSTANCE.categorySubProcess().getSafeUri())))
                    .put(BPMNCategories.GATEWAYS,
                         category -> category
                                 .bindToDefinition(ParallelGateway.class)
                                 .useGlyph(Builder.build(BPMNImageResources.INSTANCE.categoryGateway().getSafeUri())))
                    .put(BPMNCategories.CONTAINERS,
                         category -> category
                                 .bindToDefinition(Lane.class)
                                 .useGlyph(Builder.build(BPMNImageResources.INSTANCE.categoryContainer().getSafeUri())))
                    .put(BPMNCategories.CONNECTING_OBJECTS,
                         category -> category
                                 .bindToDefinition(SequenceFlow.class)
                                 .useGlyph(Builder.build(BPMNImageResources.INSTANCE.categorySequence().getSafeUri())))
                    .put(BPMNCategories.SERVICE_TASKS,
                         category -> category
                                 .useGlyph(Builder.build(BPMNImageResources.INSTANCE.categoryServiceTasks().getSafeUri())));

    //palette categories order customization.
    private static final AbstractPaletteDefinitionBuilder.ItemPriorityProvider ITEM_PRIORITIES = id -> {
        if (BPMNCategories.START_EVENTS.equals(id)) {
            return 100;
        }
        if (BPMNCategories.INTERMEDIATE_EVENTS.equals(id)) {
            return 200;
        }
        if (BPMNCategories.END_EVENTS.equals(id)) {
            return 300;
        }
        if (BPMNCategories.ACTIVITIES.equals(id)) {
            return 400;
        }
        if (BPMNCategories.SUB_PROCESSES.equals(id)) {
            return 500;
        }
        if (BPMNCategories.GATEWAYS.equals(id)) {
            return 600;
        }
        if (BPMNCategories.CONTAINERS.equals(id)) {
            return 700;
        }
        if (BPMNCategories.SERVICE_TASKS.equals(id)) {
            return 800;
        }

        if ("org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent".equals(id)) {
            return 1000;
        }
        if ("org.kie.workbench.common.stunner.bpmn.definition.BaseThrowingIntermediateEvent".equals(id)) {
            return 2000;
        }

        return -1;
    };

    private static final Map<String, String> CUSTOM_GROUPS = new HashMap<String, String>() {
        {
            put(Lane.class.getName(),
                "org.kie.workbench.common.stunner.bpmn.definition.customGroup.Containers");
        }
    };

    private final DefinitionManager definitionManager;
    private final ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder;
    private final StunnerTranslationService translationService;
    private final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry;
    private final Function<WorkItemDefinition, ServiceTask> serviceTaskBuilder;

    // CDI proxy.
    protected BPMNPaletteDefinitionBuilder() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public BPMNPaletteDefinitionBuilder(final DefinitionManager definitionManager,
                                        final ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder,
                                        final StunnerTranslationService translationService,
                                        final ManagedInstance<WorkItemDefinitionRegistry> workItemDefinitionRegistry) {
        this(definitionManager,
             paletteDefinitionBuilder,
             translationService,
             workItemDefinitionRegistry::get,
             wid -> new ServiceTaskFactory.ServiceTaskBuilder(wid).build());
    }

    BPMNPaletteDefinitionBuilder(final DefinitionManager definitionManager,
                                 final ExpandedPaletteDefinitionBuilder paletteDefinitionBuilder,
                                 final StunnerTranslationService translationService,
                                 final Supplier<WorkItemDefinitionRegistry> workItemDefinitionRegistry,
                                 final Function<WorkItemDefinition, ServiceTask> serviceTaskBuilder) {
        this.definitionManager = definitionManager;
        this.paletteDefinitionBuilder = paletteDefinitionBuilder;
        this.translationService = translationService;
        this.workItemDefinitionRegistry = workItemDefinitionRegistry;
        this.serviceTaskBuilder = serviceTaskBuilder;
    }

    @PostConstruct
    public void init() {
        paletteDefinitionBuilder
                .itemFilter(isDefinitionAllowed())
                .itemPriorities(ITEM_PRIORITIES)
                .categoryFilter(category -> !BPMNCategories.CONNECTING_OBJECTS.equals(category))
                .categoryDefinitionIdProvider(CATEGORY_DEFINITION.definitionIdProvider())
                .categoryGlyphProvider(CATEGORY_DEFINITION.glyphProvider())
                .categoryMessages(CATEGORY_DEFINITION.categoryMessageProvider(translationService))
                .customGroupIdProvider(CUSTOM_GROUPS::get)
                .customGroupMessages(new DefaultPaletteDefinitionProviders.DefaultCustomGroupMessageProvider(translationService));
    }

    @Override
    public void build(final AbstractCanvasHandler canvasHandler,
                      final Consumer<DefaultPaletteDefinition> paletteDefinitionConsumer) {
        paletteDefinitionBuilder
                .build(canvasHandler,
                       paletteDefinition -> createPaletteServiceTasksCategory(paletteDefinition,
                                                                              paletteDefinitionConsumer));
    }

    ExpandedPaletteDefinitionBuilder getPaletteDefinitionBuilder() {
        return paletteDefinitionBuilder;
    }

    private Predicate<String> isDefinitionAllowed() {
        return isType(BPMNDiagramImpl.class)
                .or(isType(NoneTask.class))
                .or(isType(SequenceFlow.class))
                .negate();
    }

    private void createPaletteServiceTasksCategory(final DefaultPaletteDefinition paletteDefinition,
                                                   final Consumer<DefaultPaletteDefinition> callback) {
        final ExpandedPaletteDefinitionBuilder.ItemMessageProvider categoryMessageProvider =
                paletteDefinitionBuilder.getCategoryMessageProvider();
        final Function<String, Glyph> categoryGlyphProvider = paletteDefinitionBuilder.getCategoryGlyphProvider();
        final Collection<WorkItemDefinition> workItemDefinitions = workItemDefinitionRegistry.get().items();
        if (!workItemDefinitions.isEmpty()) {
            final String serviceTasksTitle = categoryMessageProvider.getTitle(BPMNCategories.SERVICE_TASKS);
            final String serviceTasksDesc = categoryMessageProvider.getDescription(BPMNCategories.SERVICE_TASKS);
            final DefaultPaletteCategory workItemsCategory = new CategoryBuilder()
                    .setItemId(BPMNCategories.SERVICE_TASKS)
                    .setTitle(serviceTasksTitle)
                    .setDescription(serviceTasksDesc)
                    .setTooltip(serviceTasksTitle)
                    .setGlyph(categoryGlyphProvider
                                      .apply(BPMNCategories.SERVICE_TASKS))
                    .build();

            int i = 0;
            for (final WorkItemDefinition workItemDefinition : workItemDefinitions) {
                final ServiceTask serviceTask = serviceTaskBuilder.apply(workItemDefinition);
                final DefinitionAdapter<Object> adapter =
                        definitionManager.adapters().registry().getDefinitionAdapter(serviceTask.getClass());
                final String category = adapter.getCategory(serviceTask);
                DefaultPaletteGroup subcategoryGroup = null;
                final Optional<DefaultPaletteItem> subcategoryGroupOp = workItemsCategory.getItems().stream()
                        .filter(item -> category.equals(item.getId()))
                        .findFirst();
                if (!subcategoryGroupOp.isPresent()) {
                    subcategoryGroup = new GroupBuilder()
                            .setItemId(category)
                            .setTitle(category)
                            .setDescription(category)
                            .build();
                    workItemsCategory.getItems().add(subcategoryGroup);
                } else {
                    subcategoryGroup = (DefaultPaletteGroup) subcategoryGroupOp.get();
                }
                final String defId = adapter.getId(serviceTask);
                final String title = adapter.getTitle(serviceTask);
                final String description = adapter.getDescription(serviceTask);
                final DefaultPaletteItem item =
                        new ItemBuilder()
                                .setItemId(defId)
                                .setDefinitionId(defId)
                                .setTitle(title)
                                .setDescription(description)
                                .build();
                subcategoryGroup.getItems().add(item);

                // Link the first work item definition found to the work item's palette category.
                if (0 == i) {
                    workItemsCategory.setDefinitionId(defId);
                }
                i++;
            }
            paletteDefinition.getItems().add(workItemsCategory);
        }
        callback.accept(paletteDefinition);
    }
}
