package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.Element;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AttributeUsageDslRendererTest {

    @Test
    void rendersDirectionDefaultAndMultiplicity() {
        Element element = new Element();
        element.setName("speed");
        element.setDefinitionName("Real");

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("direction", "in");
        metadata.put("multiplicity", "0..*");
        metadata.put("defaultValue", "3.14");
        metadata.put("unit", "mps");
        element.setMetadata(metadata);

        String rendered = new AttributeUsageDslRenderer().render(element, 0);

        assertEquals("attr «in» speed: Real[0..*] = 3.14 { unit = \"mps\" }\n", rendered);
    }

    @Test
    void omitsDefaultMultiplicityWhenSingleCardinality() {
        Element element = new Element();
        element.setName("count");
        element.setDefinitionName("Integer");

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("direction", "out");
        metadata.put("multiplicity", "1..1");
        metadata.put("defaultValue", "42");
        element.setMetadata(metadata);

        String rendered = new AttributeUsageDslRenderer().render(element, 0);

        assertEquals("attr «out» count: Integer = 42\n", rendered);
    }
}
