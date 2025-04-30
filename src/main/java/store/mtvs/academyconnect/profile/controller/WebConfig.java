package store.mtvs.academyconnect.profile.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${profile.upload.dir:C:/upload/profile}") // application.properties 에 profile.upload.dir=C:/upload/profile
    private String uploadDir;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/student/profile-images/**")
                .addResourceLocations("file:///" + uploadDir + "/");
    }
}
