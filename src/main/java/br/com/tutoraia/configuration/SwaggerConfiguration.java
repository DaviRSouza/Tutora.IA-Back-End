package br.com.tutoraia.configuration;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private static final String SERVICE_NAME = "API-TUTORA.IA";
    private static final String SERVICE_DESCRIPTION = "API TUTORA.IA";
    private static final String SERVICE_VERSION = "1.0.0";
    private static final String BASE_PACKAGE = "br.com.tutora-ia.controller";

    @Bean
    public Docket apiConfigDocs() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("v1").select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .paths(PathSelectors.any()).build()
                .apiInfo(metaData())
                .securitySchemes(Collections.singletonList(apiKey()));
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder().title(SERVICE_NAME)
                .description(SERVICE_DESCRIPTION).version(SERVICE_VERSION).build();
    }

    private ApiKey apiKey() {
        return new ApiKey("Bearer Token", "Authorization", "header");
    }
}
