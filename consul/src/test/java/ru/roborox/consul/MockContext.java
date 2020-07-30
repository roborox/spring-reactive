package ru.roborox.consul;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Configuration
@EnableRoboroxConsul
public class MockContext {
    @Value("${key1}")
    String value1;
    @Value("${key2}")
    String value2;
    @Value("${key3}")
    String value3;
    @Value("${key4}")
    String value4;
    @Value("${key5}")
    String value5;
    @Value("${key6}")
    String value6;
    @Value("${key7}")
    String value7;

    @Bean
    public Object testBean() throws Exception {
        assertEquals(value1, "value1Common");
        assertEquals(value2, "value2ConsulCommon");
        assertEquals(value3, "value3System");
        assertEquals(value4, "value4ConsulCommon");
        assertEquals(value5, "value5test");
        assertEquals(value6, "value6test");
        assertEquals(value7, "value7");
        return new Object();
    }
    
    @Bean
    public ObjectWithFields objectWithFields() {
        return new ObjectWithFields();
    }
}
