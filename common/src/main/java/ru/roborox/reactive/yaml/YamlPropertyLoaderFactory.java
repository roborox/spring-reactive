package ru.roborox.reactive.yaml;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;

public class YamlPropertyLoaderFactory extends DefaultPropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        if (resource == null){
            return super.createPropertySource(name, resource);
        }

        final List<PropertySource<?>> loaded = new YamlPropertySourceLoader().load(resource.getResource().getFilename(), resource.getResource());

        final CompositePropertySource result = new CompositePropertySource("composite");
        loaded.forEach(result::addPropertySource);
        return result;
    }
}