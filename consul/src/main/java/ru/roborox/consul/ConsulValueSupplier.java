package ru.roborox.consul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConsulValueSupplier implements Supplier<String> {
    private static final Logger logger = LoggerFactory.getLogger(ConsulValueSupplier.class);
    private volatile String currentValue;
    private final String name;
    private volatile Consumer<String> consumer = value -> {};
    
    public ConsulValueSupplier(String name) {
        this.name = name;
    }

    @Override
    public String get() {
        return currentValue;
    }
    
    public void setCurrentValue(String currentValue) {
        if (!Objects.equals(this.currentValue, currentValue)) {
            logger.info("Updating property {} from {} to {}", name, this.currentValue, currentValue);
        }
        this.currentValue = currentValue;
        consumer.accept(currentValue);
    }
    
    public void onUpdate(Consumer<String> consumer) {
        this.consumer = consumer;
    }
}
