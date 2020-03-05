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
package org.kie.pmml.models.regression.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;

import static org.kie.pmml.commons.Constants.EXPECTED_TWO_ENTRIES_RETRIEVED;

public abstract class KiePMMLRegressionClassificationTable extends KiePMMLRegressionTable {

    protected REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod;
    protected OP_TYPE opType;
    protected List<KiePMMLOutputField> outputFields = new ArrayList<>();
    protected Map<String, Object> outputFieldsMap = new HashMap<>();
    protected Map<String, KiePMMLRegressionTable> categoryTableMap = new HashMap<>();

    public Object evaluateRegression(Map<String, Object> input) {
        final LinkedHashMap<String, Double> resultMap = categoryTableMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          entry -> (Double) entry.getValue().evaluateRegression(input),
                                          (o1, o2) -> o1,
                                          LinkedHashMap::new));
        final LinkedHashMap<String, Double> probabilityMap = getProbabilityMap(resultMap);
        final Map.Entry<String, Double> predictedEntry = Collections.max(probabilityMap.entrySet(), Comparator.comparing(Map.Entry::getValue));
        populateOutputFieldsMap(predictedEntry, probabilityMap);
        return predictedEntry.getKey();
    }

    protected abstract LinkedHashMap<String, Double> getProbabilityMap(final LinkedHashMap<String, Double> resultMap);

    protected abstract void populateOutputFieldsMap(final Map.Entry<String, Double> predictedEntry, final LinkedHashMap<String, Double> probabilityMap);

    protected void updateResult(final AtomicReference<Double> toUpdate) {
        // NOOP
    }

    @Override
    public Map<String, Object> getOutputFieldsMap() {
        return Collections.unmodifiableMap(outputFieldsMap);
    }

    protected LinkedHashMap<String, Double> getProbabilityMap(final LinkedHashMap<String, Double> resultMap, DoubleUnaryOperator firstItemOperator, DoubleUnaryOperator secondItemOperator) {
        if (resultMap.size() != 2) {
            throw new RuntimeException(String.format(EXPECTED_TWO_ENTRIES_RETRIEVED, resultMap.size()));
        }
        LinkedHashMap<String, Double> toReturn = new LinkedHashMap<>();
        String[] resultMapKeys = resultMap.keySet().toArray(new String[0]);
        double firstItem = firstItemOperator.applyAsDouble(resultMap.get(resultMapKeys[0]));
        double secondItem = secondItemOperator.applyAsDouble(firstItem);
        toReturn.put(resultMapKeys[0], firstItem);
        toReturn.put(resultMapKeys[1], secondItem);
        return toReturn;
    }
}