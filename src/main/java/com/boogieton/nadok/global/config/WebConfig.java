package com.boogieton.nadok.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    //CORS
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://ideatone-fe.vercel.app/")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowCredentials(true);

    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){ // 👈 이 부분에 { }가 잘못 들어가 있습니다.
        String uploadDir = System.getProperty("user.dir") + "/uploads/profiles/";


        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
