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
package org.kie.pmml.models.regression.compiler.factories;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dmg.pmml.regression.CategoricalPredictor;
import org.kie.pmml.models.regression.api.model.predictors.KiePMMLCategoricalPredictor;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;

public class KiePMMLCategoricalPredictorFactory {

    private KiePMMLCategoricalPredictorFactory() {
    }

    public static Set<KiePMMLCategoricalPredictor> getKiePMMLCategoricalPredictors(List<CategoricalPredictor> categoricalPredictors) {
        return categoricalPredictors.stream().map(KiePMMLCategoricalPredictorFactory::getKiePMMLCategoricalPredictor).collect(Collectors.toSet());
    }

    public static KiePMMLCategoricalPredictor getKiePMMLCategoricalPredictor(CategoricalPredictor categoricalPredictor) {
        return new KiePMMLCategoricalPredictor(categoricalPredictor.getName().getValue(),
                                               categoricalPredictor.getValue(),
                                               categoricalPredictor.getCoefficient(),
                                               getKiePMMLExtensions(categoricalPredictor.getExtensions()));
    }
}