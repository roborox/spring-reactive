package ru.roborox.consul;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConsulSupplierPostProcessor implements BeanPostProcessor {
    private ConsulValueSupplierUpdater updater;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            DynamicValue dynamicValue = field.getAnnotation(DynamicValue.class);
            if (dynamicValue != null) {
                String value = dynamicValue.value();
                field.setAccessible(true);
                ConsulValueSupplier supplier = new ConsulValueSupplier(value);
                try {
                    updater.registerSupplier(value, supplier);
                    field.set(bean, supplier);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for (Method method : bean.getClass().getMethods()) {
            DynamicValue dynamicValue = method.getAnnotation(DynamicValue.class);
            if (dynamicValue != null) {
                String value = dynamicValue.value();
                if (method.getName().startsWith("set") && method.getParameters().length == 1) {
                    ConsulValueSupplier supplier = new ConsulValueSupplier(value);
                    try {
                        updater.registerSupplier(value, supplier);
                        method.invoke(bean, supplier);
                    } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return bean;
    }
    
    @Autowired
    public void setUpdater(ConsulValueSupplierUpdater updater) {
        this.updater = updater;
    }
}
