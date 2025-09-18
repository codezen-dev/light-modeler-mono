package com.sysml.lightmodel.dsl;

import com.sysml.lightmodel.semantic.DslRenderUtils;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.Usage;

public class ActionUsageDslRenderer implements DslRenderer {
    @Override
    public String render(Element element, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = DslRenderUtils.indent(indent);
        DslRenderUtils.appendDocumentation(sb, element, indentStr);

        String typeStr = element.getDefinitionName();
        if (typeStr == null && element instanceof Usage usage && usage.getResolvedDefinition() != null) {
            typeStr = usage.getResolvedDefinition().getName();
        }
        if (typeStr == null) {
            typeStr = "null";
        }

        sb.append(indentStr)
                .append("action ").append(element.getName())
                .append(": ").append(typeStr)
                .append(DslRenderHelper.renderMultiplicity(element))
                .append(DslRenderHelper.renderMetadata(element))
                .append(DslRenderHelper.renderDocumentation(element))
                .append("\n");
        return sb.toString();
    }
}

