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
package org.kie.pmml.compiler;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.compiler.executor.PMMLCompilerExecutor;
import org.kie.pmml.compiler.executor.PMMLCompilerExecutorImpl;
import org.kie.pmml.compiler.implementations.AlgorithmImplementationProviderFinderImpl;
import org.kie.pmml.marshaller.executor.PMMLMarshallerExecutor;

/**
 * Testing class - to be removed sooner or later
 */
public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        PMMLCompilerExecutor executor = new PMMLCompilerExecutorImpl(getPMMLMarshallerExecutor(), new AlgorithmImplementationProviderFinderImpl());
        log.info("Calling executor... " + executor.toString());
        final List<KiePMMLModel> retrieved = executor.getResults("SOURCE");
        log.info("KiePMMLModel retrieved " + retrieved + " " + retrieved.size());
        log.info("..done. Bye!!!");
    }

    private static PMMLMarshallerExecutor getPMMLMarshallerExecutor() {
        return source -> () -> IntStream
                .range(0, 4)
                .mapToObj(ind -> "NODE-" + ind)
                .collect(Collectors.toList());
    }


}
