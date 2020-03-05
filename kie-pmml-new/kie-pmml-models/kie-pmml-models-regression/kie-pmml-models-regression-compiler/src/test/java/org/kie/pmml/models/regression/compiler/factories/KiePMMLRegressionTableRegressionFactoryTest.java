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

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.validator.Java8Validator;
import com.github.javaparser.ast.validator.ProblemReporter;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.Test;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.kie.test.util.filesystem.FileUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getCategoricalPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getPredictorTerm;

public class KiePMMLRegressionTableRegressionFactoryTest {

    private RegressionTable regressionTable;
    private List<CategoricalPredictor> categoricalPredictors;
    private List<NumericPredictor> numericPredictors;
    private List<PredictorTerm> predictorTerms;

    @Test
    public void getRegressionTableTest() throws Exception {
        regressionTable = getRegressionTable(3.5, "professional");
        List<RegressionTable> regressionTables = Collections.singletonList(regressionTable);
        Map<String, KiePMMLTableSourceCategory> retrieved = KiePMMLRegressionTableRegressionFactory.getRegressionTables(regressionTables, RegressionModel.NormalizationMethod.CAUCHIT, "targetField");
        assertNotNull(retrieved);
        commonValidateKiePMMLRegressionTable(retrieved.values().iterator().next().getSource(), "KiePMMLRegressionTableRegression1.java");
    }

    private void commonValidateKiePMMLRegressionTable(String retrieved, String reference) {
        try {
            final CompilationUnit parsed = StaticJavaParser.parse(retrieved);
            final Java8Validator validator = new Java8Validator();
            final ProblemReporter problemReporter = new ProblemReporter(problem -> fail(problem.getMessage()));
            validator.accept(parsed.findRootNode(), problemReporter);
        } catch (Exception e) {
           fail("Failed to match with " + reference + " due to " + e.getMessage());
        }
    }

    private RegressionTable getRegressionTable(double intercept, Object targetCategory) {
        categoricalPredictors = new ArrayList<>();
        numericPredictors = new ArrayList<>();
        predictorTerms = new ArrayList<>();
        numericPredictors.add(getNumericPredictor("NumPred-" + 3, 1, 32.55));
        IntStream.range(0, 3).forEach(i -> {
            IntStream.range(0, 2).forEach(j -> categoricalPredictors.add(getCategoricalPredictor("CatPred-" + i, 27.12, 3.46)));
            numericPredictors.add(getNumericPredictor("NumPred-" + i, 2, 13.11));
            predictorTerms.add(getPredictorTerm("PredTerm-" + i, 32.29,
                                                Arrays.asList(categoricalPredictors.get(0).getName().getValue(),
                                                              numericPredictors.get(0).getName().getValue())));
        });
        return PMMLModelTestUtils.getRegressionTable(categoricalPredictors, numericPredictors, predictorTerms, intercept, targetCategory);
    }
}