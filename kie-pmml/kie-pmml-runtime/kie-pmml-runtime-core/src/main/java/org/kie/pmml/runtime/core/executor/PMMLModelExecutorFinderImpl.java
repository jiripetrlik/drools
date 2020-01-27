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
package org.kie.pmml.runtime.core.executor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class PMMLModelExecutorFinderImpl implements PMMLModelExecutorFinder {

    private static final Logger log = Logger.getLogger(PMMLModelExecutorFinderImpl.class.getName());

    private ServiceLoader<PMMLModelExecutor> loader = ServiceLoader.load(PMMLModelExecutor.class);

    @Override
    @SuppressWarnings("rawtypes")
    public List<PMMLModelExecutor> getImplementations(boolean refresh) {
        log.info("getImplementations " + refresh);
        List<PMMLModelExecutor> toReturn = new ArrayList<>();
        Iterator<PMMLModelExecutor> providers = getProviders(refresh);
        providers.forEachRemaining(toReturn::add);
        log.info("toReturn " + toReturn + " " + toReturn.size());
        toReturn.forEach(provider -> log.info(provider.getPMMLModelType() + " : " + provider.toString()));
        return toReturn;
    }

    @SuppressWarnings("rawtypes")
    private Iterator<PMMLModelExecutor> getProviders(boolean refresh) {
        if (refresh) {
            loader.reload();
        }
        return loader.iterator();
    }

}
