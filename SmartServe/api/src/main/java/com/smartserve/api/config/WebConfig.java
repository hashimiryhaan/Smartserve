package com.smartserve.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expose the 'uploads' directory so the browser can see the images
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
