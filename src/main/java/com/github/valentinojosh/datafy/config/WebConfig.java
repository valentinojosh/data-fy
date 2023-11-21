package com.github.valentinojosh.datafy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        //Optional: Currently have it set at class level, given only two controller classes
        //https://data-fy.netlify.app/
        //http://localhost:3000/
        //CORS override to ensure no outside source attempts to reach api routes
//        @Override
//        public void addCorsMappings(CorsRegistry registry) {
//            registry.addMapping("/api/**")
//                    .allowedOrigins("http://localhost:3000")
//                    .allowedMethods("GET", "POST", "PUT")
//                    .allowCredentials(true);
//        }
    }