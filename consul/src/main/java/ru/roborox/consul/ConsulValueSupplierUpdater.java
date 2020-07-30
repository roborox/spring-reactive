package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConsulValueSupplierUpdater implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConsulValueSupplierUpdater.class);
    private final Map<String, ConsulValueSupplier> suppliers = new ConcurrentHashMap<String, ConsulValueSupplier>();
    private final ConsulClient consulClient = new ConsulClient();
    private String user;
    private String artifact;
    private Environment environment;
    
    @Override
    public void run() {
        try {
            for (String key : suppliers.keySet()) {
                suppliers.get(key).setCurrentValue(getValue(key));
            }
        } catch (RuntimeException e) {
            logger.debug(e.getMessage());
        }
    }

    private String getValue(String key) {
        String update = getValueOrDefault("common/" + key, null);
        update = getValueOrDefault(user + "/common/" + key, update);
        update = getValueOrDefault(user + "/" + artifact + "/" + key, update);
        if (update == null) {
            update = environment.getProperty(key);
        }
        return update;
    }
    
    private String getValueOrDefault(String key, String defaultValue) {
        GetValue value = consulClient.getKVValue(key).getValue();
        if (value != null) {
            return value.getDecodedValue();
        }
        return defaultValue;
    }
    
    public void registerSupplier(String name, ConsulValueSupplier supplier) {
        try {
            supplier.setCurrentValue(getValue(name));
        } catch (RuntimeException e) {
            logger.debug(e.getMessage());
            supplier.setCurrentValue(environment.getProperty(name));
        }
        suppliers.put(name, supplier);
    }
    
    @Value("${appUser}")
    public void setUser(String user) {
        this.user = user;
    }
    
    @Value("${artifact}")
    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }
    
    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
