package com.sysml.lightmodel;

import com.sysml.lightmodel.semantic.Definition;
import com.sysml.lightmodel.semantic.Element;
import com.sysml.lightmodel.semantic.TypeLibraryElement;
import com.sysml.lightmodel.semantic.Usage;
import com.sysml.lightmodel.service.DslDocumentService;
import com.sysml.lightmodel.service.SemanticElementService;
import com.sysml.lightmodel.service.TypeLibraryService;
import com.sysml.lightmodel.service.impl.DefaultDslImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultDslImportServiceTest {

    @Mock
    private TypeLibraryService typeLibraryService;

    @Mock
    private SemanticElementService semanticElementService;

    @Mock
    private DslDocumentService dslDocumentService;

    private DefaultDslImportService service;

    @BeforeEach
    void setUp() {
        service = new DefaultDslImportService(typeLibraryService, semanticElementService, dslDocumentService);
    }

    @Test
    void shouldParseTypeLibraryImports() {
        TypeLibraryElement real = new TypeLibraryElement();
        real.setName("Real");
        real.setType("PrimitiveType");
        real.setDocumentation("Real number");

        when(typeLibraryService.getAllTypeDefinitions()).thenReturn(List.of(real));

        String dsl = """
                import \"sys-types\"
                def structure Controller {
                    attr gain: Real
                }
                """;

        List<Element> elements = assertDoesNotThrow(() -> service.parseDsl(dsl));

        verify(typeLibraryService, times(1)).getAllTypeDefinitions();
        assertEquals(2, elements.size(), "Expected imported element plus parsed definition");

        Element imported = elements.get(0);
        assertEquals("Real", imported.getName());
        assertEquals("PrimitiveType", imported.getType());
        assertEquals("Real number", imported.getDocumentation());

        Definition controller = findDefinition(elements, "Controller");
        assertNotNull(controller, "Controller definition should be present");
    }

    @Test
    void shouldParseDefinitionsWithNestedUsages() {
        String dsl = """
                def structure Controller {
                    part plant: Plant
                    attr gain: Real
                }
                
                def structure Plant {
                    attr sensor: Sensor
                }
                """;

        List<Element> elements = assertDoesNotThrow(() -> service.parseDsl(dsl));

        Definition controller = findDefinition(elements, "Controller");
        assertNotNull(controller, "Controller definition missing");
        assertEquals("structure", controller.getType());
        assertNotNull(controller.getOwnedUsages(), "Controller usages should be parsed");
        assertEquals(2, controller.getOwnedUsages().size(), "Controller should have two usages");
        assertEquals(controller.getOwnedUsages(), controller.getChildren(), "Children should mirror owned usages");

        Usage plant = findUsage(controller, "plant");
        assertEquals("PartUsage", plant.getType());
        assertEquals("Plant", plant.getDefinitionName());
        assertSame(controller, plant.getParentDefinition(), "Parent definition should be set");

        Usage gain = findUsage(controller, "gain");
        assertEquals("AttributeUsage", gain.getType());
        assertEquals("Real", gain.getDefinitionName());

        Definition plantDef = findDefinition(elements, "Plant");
        assertNotNull(plantDef, "Plant definition missing");
        assertNotNull(plantDef.getOwnedUsages());
        assertEquals(1, plantDef.getOwnedUsages().size());
        assertEquals("sensor", plantDef.getOwnedUsages().get(0).getName());
    }

    @Test
    void shouldCaptureUsageMetadata() {
        String dsl = """
                def structure Controller {
                    attr gain: Real[0..1] = 42
                }
                """;

        List<Element> elements = assertDoesNotThrow(() -> service.parseDsl(dsl));

        Definition controller = findDefinition(elements, "Controller");
        assertNotNull(controller, "Controller definition should be parsed");
        Usage gain = findUsage(controller, "gain");

        assertEquals("0..1", gain.getMultiplicity(), "Multiplicity metadata should be captured");
        assertEquals("42", gain.getDefaultValue(), "Default value metadata should be captured");
    }

    @Test
    void shouldNotProducePartialDefinitionsWhenBracesMissing() {
        String invalidDsl = """
                def structure Broken {
                    attr name: String
                """;

        List<Element> elements = assertDoesNotThrow(() -> service.parseDsl(invalidDsl));
        assertTrue(elements.isEmpty(), "Parser should not emit incomplete definitions when braces are unmatched");
    }

    private Definition findDefinition(List<Element> elements, String name) {
        return elements.stream()
                .filter(Definition.class::isInstance)
                .map(Definition.class::cast)
                .filter(def -> name.equals(def.getName()))
                .findFirst()
                .orElse(null);
    }

    private Usage findUsage(Definition definition, String usageName) {
        assertNotNull(definition.getOwnedUsages(), "Owned usages are required for parsing assertions");
        return definition.getOwnedUsages().stream()
                .filter(u -> usageName.equals(u.getName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Usage '" + usageName + "' was not parsed"));
    }
}
