package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(classes = MockContext.class)
public class ConsulPropertySourceConfigurationTest {
    @Autowired
    ConsulPropertySourceConfiguration configuration;
    @Autowired
    ObjectWithFields objectWithFields;
    @Autowired
    ConsulValueSupplierUpdater updater;
    static ConsulClient consulClient = new ConsulClient("127.0.0.1");

    static {
        System.setProperty("key3", "value3System");
        consulClient.setKVValue("common/key1", "value1Common");
        consulClient.setKVValue("common/key2", "value2Common");
        consulClient.setKVValue("consul/common/key2", "value2ConsulCommon");
        consulClient.setKVValue("consul/common/key4", "value4ConsulCommon");
        consulClient.setKVValue("consul/common/key5", "value5ConsulCommon");
        consulClient.setKVValue("consul/test/key5", "value5test");
        consulClient.setKVValue("consul/test/key6", "value6test");
        consulClient.setKVValue("consul/common/httpPort", "8081");
        consulClient.deleteKVValue("common/key8");
        consulClient.deleteKVValue("common/dmitry/key8");
        consulClient.deleteKVValue("consul/common/key8");
        consulClient.deleteKVValue("consul/test/key8");
    }
    
    @Test
    public void load() {
        assertEquals(configuration.getHttpPort().intValue(), 8081);
        assertEquals(objectWithFields.value8.get(), "fallback");
        consulClient.setKVValue("common/key8", "value8");
        updater.run();
        assertEquals(objectWithFields.value8.get(), "value8");
        consulClient.setKVValue("consul/common/key8", "value81");
        updater.run();
        assertEquals(objectWithFields.value8.get(), "value81");
        consulClient.setKVValue("consul/test/key8", "value82");
        updater.run();
        assertEquals(objectWithFields.value8.get(), "value82");
    }
}
