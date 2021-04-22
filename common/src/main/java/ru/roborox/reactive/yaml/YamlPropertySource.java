package ru.roborox.reactive.yaml;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@PropertySource(value = {}, factory = YamlPropertyLoaderFactory.class)
public @interface YamlPropertySource {
    @AliasFor(annotation = PropertySource.class, attribute = "value")
    String[] value() default {};
}
