package com.itender;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author itender
 * @date 2023/11/15/ 18:01
 * @desc
 */
public class Test {
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan(
            excludeFilters = {@ComponentScan.Filter(
                    type = FilterType.CUSTOM,
                    classes = {TypeExcludeFilter.class}
            ), @ComponentScan.Filter(
                    type = FilterType.CUSTOM,
                    classes = {AutoConfigurationExcludeFilter.class}
            )}
    )
    public @interface SpringBootApplication {
        @AliasFor(
                annotation = EnableAutoConfiguration.class
        )
        Class<?>[] exclude() default {};

        @AliasFor(
                annotation = EnableAutoConfiguration.class
        )
        String[] excludeName() default {};

        @AliasFor(
                annotation = ComponentScan.class,
                attribute = "basePackages"
        )
        String[] scanBasePackages() default {};

        @AliasFor(
                annotation = ComponentScan.class,
                attribute = "basePackageClasses"
        )
        Class<?>[] scanBasePackageClasses() default {};

        @AliasFor(
                annotation = ComponentScan.class,
                attribute = "nameGenerator"
        )
        Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

        @AliasFor(
                annotation = Configuration.class
        )
        boolean proxyBeanMethods() default true;
    }
}
