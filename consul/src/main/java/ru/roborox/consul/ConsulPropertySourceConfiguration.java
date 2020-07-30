package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource(value = {"classpath:/init.properties", "classpath:/app.properties", "${config.path}"}, ignoreResourceNotFound = true)
class ConsulPropertySourceConfiguration {
    public static final Logger logger = LoggerFactory.getLogger(ConsulPropertySourceConfiguration.class);

    @Value("${httpPort:-1}")
    private Integer httpPort;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer configurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocalOverride(true);
        Properties properties;
        if (System.getProperty("doNotUseConsul") != null) {
            logger.info("ignoring consul properties");
            properties = new Properties();
        } else {
            properties = new ConsulPropertiesProvider(new ConsulClient("127.0.0.1")).getProperties();
            logger.info("added properties from consul: {}", properties.keySet());
        }
        Properties systemProperties = System.getProperties();
        Enumeration names = systemProperties.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            properties.setProperty(name, systemProperties.getProperty(name));
        }
        configurer.setProperties(properties);
        return configurer;
    }
    
    @Bean
    public ConsulValueSupplierUpdater updater() {
        return new ConsulValueSupplierUpdater();
    }
    
    @Bean(destroyMethod = "shutdown")
    public ExecutorService consulPropertiesUpdaterExecutor() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(updater(), 30, 30, TimeUnit.SECONDS);
        return executor;
    }
    
    @Bean
    public BeanPostProcessor consulValueSupplierBeanPostProcessor() {
        return new ConsulSupplierPostProcessor();
    }
    
    public Integer getHttpPort() {
        return httpPort;
    }
}
