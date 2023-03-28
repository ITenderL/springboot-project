package com.itender.swagger.config;

import com.google.common.collect.Lists;
import com.itender.swagger.constant.ResponseStatus;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.*;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author itender
 * @date 2023/2/3 11:37
 * @desc
 */
@EnableOpenApi
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket openApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("User Management")
                .apiInfo(apiInfo())
                .select()
                // .apis(RequestHandlerSelectors.withClassAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage("com.itender.swagger"))
                .paths(PathSelectors.any())
                .build()
                .globalRequestParameters(getGlobalRequestParameters())
                .globalResponses(HttpMethod.GET, getGlobalResponse());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger Api")
                .description("用户信息管理")
                .contact(new Contact("itender", "itender", "itender@163.com"))
                .termsOfServiceUrl("")
                .version("1.0.0")
                .build();
    }

    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> parameters = Lists.newArrayList();
        parameters.add(
                new RequestParameterBuilder()
                        .name("appKey")
                        .description("AppKey")
                        .required(false)
                        .in(ParameterType.QUERY)
                        .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                        .required(false)
                        .build()
        );
        return parameters;
    }

    private List<Response> getGlobalResponse() {
        return ResponseStatus.HTTP_STATUS_ALL.stream()
                .map(a -> new ResponseBuilder().code(a.getResponseCode()).description(a.getDescription()).build())
                .collect(Collectors.toList());
    }
}
