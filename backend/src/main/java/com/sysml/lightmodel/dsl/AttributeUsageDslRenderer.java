package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AttributeUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = DslRenderUtils.indent(indent);
        DslRenderUtils.appendDocumentation(sb, element, indentStr);

        // 修复 null 类型显示
        String typeStr = element.getDefinitionName();
        if (typeStr == null && element.getResolvedDefinition() != null) {
            typeStr = element.getResolvedDefinition().getName();
        }
        if (typeStr == null) {
            typeStr = "null";
        }

        Map<String, Object> metadata = element.getMetadata();
        String direction = extractMetadataValue(metadata, "direction");
        String multiplicity = formatMultiplicity(extractMetadataValue(metadata, "multiplicity"));
        String defaultValue = extractMetadataValue(metadata, "defaultValue");

        sb.append(indentStr)
                .append("attr ");
        if (direction != null && !direction.isBlank()) {
            sb.append("«").append(direction).append("» ");
        }
        sb.append(element.getName())
                .append(": ").append(typeStr);

        if (!multiplicity.isEmpty()) {
            sb.append(multiplicity);
        }
        if (defaultValue != null && !defaultValue.isBlank()) {
            sb.append(" = ").append(defaultValue);
        }

        String metadataStr = DslRenderHelper.renderMetadata(element,
                Set.of("multiplicity", "defaultValue", "direction"));
        if (!metadataStr.isEmpty()) {
            if (defaultValue != null && !defaultValue.isBlank()) {
                sb.append(' ');
            }
            sb.append(metadataStr);
        }

        sb.append(DslRenderHelper.renderDocumentation(element))
                .append("\n");
        return sb.toString();
    }

    private String extractMetadataValue(Map<String, Object> metadata, String key) {
        if (metadata == null || key == null) {
            return null;
        }
        Object value = metadata.get(key);
        return value != null ? Objects.toString(value) : null;
    }

    private String formatMultiplicity(String rawMultiplicity) {
        if (rawMultiplicity == null || rawMultiplicity.isBlank()) {
            return "";
        }
        String multiplicity = rawMultiplicity.trim();
        if (multiplicity.startsWith("[") && multiplicity.endsWith("]") && multiplicity.length() > 2) {
            multiplicity = multiplicity.substring(1, multiplicity.length() - 1).trim();
        }
        multiplicity = multiplicity.replaceAll("\\s+", "");
        if ("1..1".equals(multiplicity)) {
            return "";
        }
        if (multiplicity.contains("..")) {
            String[] parts = multiplicity.split("\\.\\.", -1);
            if (parts.length == 2) {
                String lower = parts[0].isEmpty() ? "1" : parts[0];
                String upper = parts[1].isEmpty() ? "*" : parts[1];
                multiplicity = lower + ".." + upper;
            }
        }
        return "[" + multiplicity + "]";
    }
}

