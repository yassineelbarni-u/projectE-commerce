package com.storeshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${app.upload.dir:uploads}")
  private String uploadDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Expose the upload directory as static resources accessible via /uploads/**
    String normalized = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
    registry
        .addResourceHandler("/uploads/**")
        .addResourceLocations("file:" + normalized);
  }
}
