package com.storeshop.unit.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.storeshop.config.WebConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@DisplayName("Tests de la configuration WebConfig")
/**
 * Type : Test Unitaire
 */
@org.junit.jupiter.api.Tag("Unitaire")
class WebConfigTest {

  @Test
  @DisplayName("addResourceHandlers expose bien le dossier uploads")
  void addResourceHandlersRegistersUploadsMapping() {
    WebConfig config = new WebConfig();
    TestResourceHandlerRegistry registry =
        new TestResourceHandlerRegistry(new StaticApplicationContext(), new MockServletContext());

    config.addResourceHandlers(registry);

    SimpleUrlHandlerMapping handlerMapping = (SimpleUrlHandlerMapping) registry.exposedHandlerMapping();
    assertNotNull(handlerMapping);
    assertEquals("/uploads/**", handlerMapping.getUrlMap().keySet().iterator().next());

    Object handler = handlerMapping.getUrlMap().values().iterator().next();
    assertTrue(handler instanceof ResourceHttpRequestHandler);
    ResourceHttpRequestHandler resourceHandler = (ResourceHttpRequestHandler) handler;
    assertTrue(
        resourceHandler.getLocations().stream()
            .anyMatch(location -> location.toString().contains("src/main/resources/static/uploads/")));
  }

  private static final class TestResourceHandlerRegistry extends ResourceHandlerRegistry {

    private TestResourceHandlerRegistry(
        StaticApplicationContext applicationContext, MockServletContext servletContext) {
      super(applicationContext, servletContext);
    }

    private SimpleUrlHandlerMapping exposedHandlerMapping() {
      return (SimpleUrlHandlerMapping) super.getHandlerMapping();
    }
  }
}
