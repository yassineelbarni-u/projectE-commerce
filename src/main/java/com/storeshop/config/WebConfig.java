package com.storeshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
/**
 * Web MVC configuration for static file exposure.
 *
 * <p>Uploaded product images are stored on disk, so this mapping lets templates access them via
 * URLs like {@code /uploads/example.jpg}.
 */
public class WebConfig implements WebMvcConfigurer {

  @Value("${app.upload.dir:uploads}")
  private String uploadDir;

  /**
   * Maps URL path {@code /uploads/**} to the local upload folder.
   *
   * @param registry Spring registry where static resource handlers are configured
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String normalized = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
    registry
        .addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + normalized);
  }
}
