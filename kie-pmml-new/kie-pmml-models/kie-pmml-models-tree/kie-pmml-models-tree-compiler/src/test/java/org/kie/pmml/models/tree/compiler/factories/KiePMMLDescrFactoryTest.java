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

package org.kie.pmml.models.tree.compiler.factories;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.Value;
import org.dmg.pmml.tree.TreeModel;
import org.drools.compiler.lang.api.CEDescrBuilder;
import org.drools.compiler.lang.api.DescrFactory;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.EnumLiteralDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.tree.model.enums.OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.compiler.lang.descr.ExprConstraintDescr.Type.NAMED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class KiePMMLDescrFactoryTest {

    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelFactoryTest.class);
    private static final String PACKAGE_NAME = "package";
    private static final String CURRENT_RULE = "currentRule";
    private PackageDescrBuilder builder;
    private RuleDescrBuilder ruleBuilder;
    private CEDescrBuilder<RuleDescrBuilder, AndDescr> lhsBuilder;
    private PMML pmml;

    @Before
    public void setUp() throws Exception {
        pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof TreeModel);
        builder = DescrFactory.newPackage().name(PACKAGE_NAME);
        ruleBuilder = builder.newRule().name(CURRENT_RULE);
        lhsBuilder = ruleBuilder.lhs();
    }

    @Test
    public void getBaseDescr() {
        PackageDescr retrieved = KiePMMLDescrFactory.getBaseDescr(pmml.getDataDictionary(), (TreeModel) pmml.getModels().get(0), "org.test.package");
        assertNotNull(retrieved);
    }

    @Test
    public void declarePredicate() {
        Predicate predicate = getSimplePredicate("VALUE");
        KiePMMLDescrFactory.declarePredicate(lhsBuilder, predicate);
        assertEquals(1, lhsBuilder.getDescr().getDescrs().size());
        final AndDescr descr = lhsBuilder.getDescr();
        commonVerifySimplePredicate(descr, (SimplePredicate) predicate);
        List<SimplePredicate> predicates = IntStream.range(0, 3).mapToObj(index -> getSimplePredicate("VALUE-" + index)).collect(Collectors.toList());
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            predicate = new CompoundPredicate();
            ((CompoundPredicate) predicate).setBooleanOperator(operator);
            predicates.forEach(((CompoundPredicate) predicate)::addPredicates);
            descr.getDescrs().clear();
            assertTrue(descr.getDescrs().isEmpty());
            KiePMMLDescrFactory.declarePredicate(lhsBuilder, predicate);
            assertEquals(1, descr.getDescrs().size());
            commonVerifySimplePredicates((ConditionalElementDescr) descr.getDescrs().get(0), predicates, operator);
        }
    }

    @Test
    public void declareSimplePredicate() {
        SimplePredicate predicate = getSimplePredicate("VALUE");
        final CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr> andBuilder = lhsBuilder.and();
        assertTrue(andBuilder.getDescr().getDescrs().isEmpty());
        KiePMMLDescrFactory.declareSimplePredicate(andBuilder, predicate);
        final AndDescr descr = andBuilder.getDescr();
        assertEquals(1, descr.getDescrs().size());
        commonVerifySimplePredicate(descr, predicate);
    }

    @Test
    public void declareCompoundPredicate() {
        List<SimplePredicate> predicates = IntStream.range(0, 3).mapToObj(index -> getSimplePredicate("VALUE-" + index)).collect(Collectors.toList());
        final AndDescr descr = lhsBuilder.getDescr();
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            CompoundPredicate predicate = new CompoundPredicate();
            predicate.setBooleanOperator(operator);
            predicates.forEach(predicate::addPredicates);
            descr.getDescrs().clear();
            assertTrue(descr.getDescrs().isEmpty());
            KiePMMLDescrFactory.declareCompoundPredicate(lhsBuilder, predicate);
            assertEquals(1, descr.getDescrs().size());
            commonVerifySimplePredicates((ConditionalElementDescr) descr.getDescrs().get(0), predicates, operator);
        }
    }

    @Test
    public void declareSimplePredicates() {
        List<SimplePredicate> predicates = IntStream.range(0, 3).mapToObj(index -> getSimplePredicate("VALUE-" + index)).collect(Collectors.toList());
        final CEDescrBuilder<CEDescrBuilder<RuleDescrBuilder, AndDescr>, AndDescr> andBuilder = lhsBuilder.and();
        assertTrue(andBuilder.getDescr().getDescrs().isEmpty());
        final AndDescr descr = andBuilder.getDescr();
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            descr.getDescrs().clear();
            assertTrue(descr.getDescrs().isEmpty());
            KiePMMLDescrFactory.declareSimplePredicates(andBuilder, "SIMPLEPREDICATE", predicates, operator);
            assertEquals(1, descr.getDescrs().size());
            commonVerifySimplePredicates(descr, predicates, operator);
        }
    }

    @Test
    public void declareTypes() {
        List<DataField> dataFields = Arrays.asList(getTypeDataField(), getEnumDataField());
        DataDictionary dataDictionary = new DataDictionary(dataFields);
        assertTrue(builder.getDescr().getEnumDeclarations().isEmpty());
        assertTrue(builder.getDescr().getTypeDeclarations().isEmpty());
        KiePMMLDescrFactory.declareTypes(builder, dataDictionary);
        commonVerifyTypeDeclarationDescr(dataFields.get(0));
        commonVerifyEnumDeclarationDescr(dataFields.get(1));
    }

    @Test
    public void declareType() {
        DataField dataField = getEnumDataField();
        assertTrue(builder.getDescr().getEnumDeclarations().isEmpty());
        assertTrue(builder.getDescr().getTypeDeclarations().isEmpty());
        KiePMMLDescrFactory.declareType(builder, dataField);
        assertTrue(builder.getDescr().getTypeDeclarations().isEmpty());
        commonVerifyEnumDeclarationDescr(dataField);
        dataField = getTypeDataField();
        KiePMMLDescrFactory.declareType(builder, dataField);
        commonVerifyTypeDeclarationDescr(dataField);
    }

    @Test
    public void declareEnumType() {
        DataField dataField = getEnumDataField();
        assertTrue(builder.getDescr().getEnumDeclarations().isEmpty());
        KiePMMLDescrFactory.declareEnumType(builder, dataField);
        commonVerifyEnumDeclarationDescr(dataField);
    }

    private void commonVerifySimplePredicate(AndDescr descr, SimplePredicate predicate) {
        final PatternDescr baseDescr = (PatternDescr) descr.getDescrs().get(0);
        assertEquals("SIMPLEPREDICATE", baseDescr.getObjectType());
        assertEquals(1, baseDescr.getDescrs().size());
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) baseDescr.getDescrs().get(0);
        assertEquals(NAMED, exprConstraintDescr.getType());
        String expected = String.format("value %s \"%s\"", OPERATOR.byName(predicate.getOperator().value()).getOperator(), predicate.getValue());
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    private void commonVerifySimplePredicates(ConditionalElementDescr descr, List<SimplePredicate> predicates, CompoundPredicate.BooleanOperator operator) {
        final PatternDescr patternDescr = (PatternDescr) descr.getDescrs().get(0);
        assertEquals("SIMPLEPREDICATE", patternDescr.getObjectType());
        if (operator.equals(CompoundPredicate.BooleanOperator.OR)) {
            commonVerifySimplePredicatesOR(patternDescr, predicates);
        } else {
            int expectedDescrs = predicates.size();
            assertEquals(expectedDescrs, patternDescr.getDescrs().size());
            for (int i = 0; i < predicates.size(); i++) {
                SimplePredicate predicate = predicates.get(i);
                ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) patternDescr.getDescrs().get(i);
                assertEquals(NAMED, exprConstraintDescr.getType());
                String expected = String.format("value %s \"%s\"", OPERATOR.byName(predicate.getOperator().value()).getOperator(), predicate.getValue());
                assertEquals(expected, exprConstraintDescr.getExpression());
            }
        }
    }

    private void commonVerifySimplePredicatesOR(PatternDescr baseDescr, List<SimplePredicate> predicates) {
        int expectedDescrs = 1;
        assertEquals(expectedDescrs, baseDescr.getDescrs().size());
        ExprConstraintDescr exprConstraintDescr = (ExprConstraintDescr) baseDescr.getDescrs().get(0);
        assertEquals(NAMED, exprConstraintDescr.getType());
        StringBuilder builder = new StringBuilder();
        String operatorString = " || ";
        for (int i = 0; i < predicates.size(); i++) {
            SimplePredicate predicate = predicates.get(i);
            if (i > 0) {
                builder.append(operatorString);
            }
            builder.append(String.format("value %s \"%s\"", OPERATOR.byName(predicate.getOperator().value()).getOperator(), predicate.getValue()));
        }
        String expected = builder.toString();
        assertEquals(expected, exprConstraintDescr.getExpression());
    }

    private void commonVerifyEnumDeclarationDescr(DataField dataField) {
        assertEquals(1, builder.getDescr().getEnumDeclarations().size());
        final EnumDeclarationDescr enumDeclarationDescr = builder.getDescr().getEnumDeclarations().get(0);
        assertEquals("DATAFIELD", enumDeclarationDescr.getTypeName());
        assertEquals(1, enumDeclarationDescr.getFields().size());
        assertTrue(enumDeclarationDescr.getFields().containsKey("value"));
        assertEquals(String.class.getName(), enumDeclarationDescr.getFields().get("value").getPattern().getObjectType());
        final List<EnumLiteralDescr> literals = enumDeclarationDescr.getLiterals();
        assertEquals(dataField.getValues().size(), literals.size());
        dataField.getValues().forEach(value -> {
            EnumLiteralDescr enumLiteralDescr = literals.stream().filter(enumLiteralDescr1 -> value.getValue().equals(enumLiteralDescr1.getName())).findFirst().orElse(null);
            assertNotNull(enumLiteralDescr);
            assertEquals(1, enumLiteralDescr.getConstructorArgs().size());
            assertEquals("\"" + value.getValue() + "\"", enumLiteralDescr.getConstructorArgs().get(0));
        });
    }

    private void commonVerifyTypeDeclarationDescr(DataField dataField) {
        assertEquals(1, builder.getDescr().getTypeDeclarations().size());
        final TypeDeclarationDescr typeDeclarationDescr = builder.getDescr().getTypeDeclarations().get(0);
        assertEquals("DATAFIELD", typeDeclarationDescr.getTypeName());
        assertEquals(1, typeDeclarationDescr.getFields().size());
        assertTrue(typeDeclarationDescr.getFields().containsKey("value"));
        assertEquals("Date", typeDeclarationDescr.getFields().get("value").getPattern().getObjectType());
    }

    private SimplePredicate getSimplePredicate(String value) {
        SimplePredicate toReturn = new SimplePredicate();
        toReturn.setField(FieldName.create("SimplePredicate"));
        toReturn.setOperator(SimplePredicate.Operator.LESS_THAN);
        toReturn.setValue(value);
        return toReturn;
    }

    private DataField getEnumDataField() {
        DataField toReturn = new DataField();
        toReturn.setOpType(OpType.CATEGORICAL);
        toReturn.setDataType(DataType.STRING);
        toReturn.setName(FieldName.create("dataField"));
        toReturn.addValues(new Value("VALUE_1"), new Value("VALUE_2"));
        return toReturn;
    }

    private DataField getTypeDataField() {
        DataField toReturn = new DataField();
        toReturn.setOpType(OpType.CONTINUOUS);
        toReturn.setDataType(DataType.DATE);
        toReturn.setName(FieldName.create("dataField"));
        return toReturn;
    }
}