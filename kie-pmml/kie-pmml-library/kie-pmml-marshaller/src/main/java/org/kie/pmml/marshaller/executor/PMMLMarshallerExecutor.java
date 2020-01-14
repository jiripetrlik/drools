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
package org.kie.pmml.marshaller.executor;

import org.kie.pmml.marshaller.model.CommonPMMLModel;

/**
 * Actual implementation is required to retrieve a
 * <code>CommonPMMLModel</code> out from the given <b>PMML</b> string
 */
public interface PMMLMarshallerExecutor {

    /**
     * Transform a <b>PMML</b> source to <code>CommonPMMLModel</code>
     * @param source
     * @return
     */
    CommonPMMLModel parse(String source);

}
