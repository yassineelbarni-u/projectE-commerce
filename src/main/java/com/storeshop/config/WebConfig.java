package com.storeshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Expose the upload directory as static resources accessible via /uploads/**
    registry
      .addResourceHandler("/uploads/**")
      .addResourceLocations("file:src/main/resources/static/uploads/");
  }
}
