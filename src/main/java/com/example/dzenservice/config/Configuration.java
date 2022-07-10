package com.example.dzenservice.config;

import com.example.dzenservice.controllers.PreferenceMapRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.dzenservice.controllers"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger (PreferenceMapRestController.class);
    }
}
