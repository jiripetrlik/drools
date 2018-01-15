package org.kie.pmml.pmml_4_2;

import org.assertj.core.api.Assertions;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

public class PMMLKieBaseUtil {

    public static KieBase createKieBaseWithPMML(String modelResourcePath) {
        Resource res = ResourceFactory.newClassPathResource(modelResourcePath);
        KieBase kieBase = new KieHelper().addResource(res, ResourceType.PMML).build();

        Assertions.assertThat(kieBase).isNotNull();

        return kieBase;
    }
}
