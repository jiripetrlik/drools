package org.kie.pmml.pmml_4_2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.ruleunit.RuleUnitRegistry;
import org.kie.api.KieBase;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.pmml.pmml_4_2.model.PMML4UnitImpl;
import org.kie.pmml.pmml_4_2.model.PMMLRequestData;
import org.kie.pmml.pmml_4_2.model.datatypes.PMML4Data;

public class PMMLExecutor {
    private KieBase kieBase;

    public PMMLExecutor(KieBase kieBase) {
        this.kieBase = kieBase;
    }

    public KieBase getKieBase() {
        return kieBase;
    }

    public PMML4Result run(PMMLRequestData requestData) {
        return run(requestData, Optional.empty());
    }

    public PMML4Result run(PMMLRequestData requestData, PMML4Data pmml4Data) {
        return run(requestData, Optional.of(pmml4Data));
    }

    private PMML4Result run(PMMLRequestData requestData,
            Optional<PMML4Data> data) {
        RuleUnitExecutor ruleUnitExecutor = RuleUnitExecutor.create().bind(kieBase);
        DataSource<PMMLRequestData> requestDataSource = ruleUnitExecutor.newDataSource("request");;
        DataSource<PMML4Result> resultDataSource = ruleUnitExecutor.newDataSource("results");
        DataSource<PMML4Data> pmmlDataSource = ruleUnitExecutor.newDataSource("pmmlData");

        requestDataSource.insert(requestData);
        PMML4Result resultHandler = new PMML4Result();
        resultDataSource.insert(resultHandler);
        if (data.isPresent()) {
            pmmlDataSource.insert(data.get());
        }

        ruleUnitExecutor.run(startingRuleUnit("RuleUnitIndicator", "SimpleScorecard",
                "org.drools.scorecards.example"));

        return resultHandler;
    }

    private List<String> calculatePossiblePackageNames(String modelId, String...knownPackageNames) {
        List<String> packageNames = new ArrayList<>();
        String javaModelId = modelId.replaceAll("\\s","");
        if (knownPackageNames != null && knownPackageNames.length > 0) {
            for (String knownPkgName: knownPackageNames) {
                packageNames.add(knownPkgName + "." + javaModelId);
            }
        }
        String basePkgName = PMML4UnitImpl.DEFAULT_ROOT_PACKAGE+"."+javaModelId;
        packageNames.add(basePkgName);
        return packageNames;
    }

    protected Class<? extends RuleUnit> startingRuleUnit(String startingRule, String modelId, String...knownPackageNames) {
        List<String> possiblePackages = calculatePossiblePackageNames(modelId, knownPackageNames);
        InternalKnowledgeBase internalKnowledgeBase = (InternalKnowledgeBase) kieBase;

        RuleUnitRegistry unitRegistry = internalKnowledgeBase.getRuleUnitRegistry();
        Map<String,InternalKnowledgePackage> pkgs = internalKnowledgeBase.getPackagesMap();
        RuleImpl ruleImpl = null;
        for (String pkgName: possiblePackages) {
            if (pkgs.containsKey(pkgName)) {
                InternalKnowledgePackage pkg = pkgs.get(pkgName);
                ruleImpl = pkg.getRule(startingRule);
                if (ruleImpl != null) {
                    RuleUnitDescr descr = unitRegistry.getRuleUnitFor(ruleImpl).orElse(null);
                    if (descr != null) {
                        return descr.getRuleUnitClass();
                    }
                }
            }
        }
        return null;
    }
}
