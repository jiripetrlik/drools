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

package org.kie.pmml.models.regression.model.predictors;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KiePMMLCategoricalPredictorTest {

    private static final String NAME = "NAME";
    private static final Object VALUE = "VALUE";
    private static final double COEFFICIENT = 35.6;

    private KiePMMLCategoricalPredictor predictor;

    @Before
    public void setup() {
        predictor = new KiePMMLCategoricalPredictor(NAME, VALUE, COEFFICIENT, Collections.emptyList());
    }

    @Test
    public void evaluateSameValue() {
        assertEquals(COEFFICIENT, predictor.evaluate(VALUE), 0.0);
    }

    @Test
    public void evaluateDifferentValue() {
        assertEquals(0.0, predictor.evaluate("UNKNOWN"), 0.0);
    }
}