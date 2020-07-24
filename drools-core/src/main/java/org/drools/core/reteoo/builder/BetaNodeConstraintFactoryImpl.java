/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo.builder;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.core.spi.BetaNodeFieldConstraint;

public class BetaNodeConstraintFactoryImpl implements BetaNodeConstraintFactory {

    @Override
    public SingleBetaConstraints createSingleBetaConstraints(final BetaNodeFieldConstraint constraint,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndex) {
        return new SingleBetaConstraints(constraint, conf, disableIndex);
    }

    @Override
    public DoubleBetaConstraints createDoubleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndexing) {
        return new DoubleBetaConstraints(constraints, conf, disableIndexing);
    }

    @Override
    public TripleBetaConstraints createTripleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                             final RuleBaseConfiguration conf,
                                                             final boolean disableIndexing) {
        return new TripleBetaConstraints(constraints, conf, disableIndexing);
    }

    @Override
    public QuadroupleBetaConstraints createQuadroupleBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                                     final RuleBaseConfiguration conf,
                                                                     final boolean disableIndexing) {
        return new QuadroupleBetaConstraints(constraints, conf, disableIndexing);
    }

    @Override
    public DefaultBetaConstraints createDefaultBetaConstraints(final BetaNodeFieldConstraint[] constraints,
                                                               final RuleBaseConfiguration conf,
                                                               final boolean disableIndexing) {
        return new DefaultBetaConstraints(constraints, conf, disableIndexing);
    }
}
