package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ConsulPropertiesProvider {
    private static final Logger logger = LoggerFactory.getLogger(ConsulPropertiesProvider.class);
    private final ConsulClient consulClient;
    
    public ConsulPropertiesProvider(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    public Properties getProperties() {
        Properties result = new Properties();
        try {
            Set<String> services = consulClient.getCatalogServices(QueryParams.DEFAULT).getValue().keySet();
            for (String service : services) {
                String propertyName = service + "Urls";
                String propertyValue = "";
                Iterator<CatalogService> iterator = consulClient.getCatalogService(service, QueryParams.DEFAULT).getValue().iterator();
                while (iterator.hasNext()) {
                    CatalogService catalogService = iterator.next();
                    if (StringUtils.isNotBlank(catalogService.getServiceAddress())) {
                        propertyValue += catalogService.getServiceAddress() + ":" + catalogService.getServicePort();
                    } else {
                        propertyValue += catalogService.getAddress() + ":" + catalogService.getServicePort();
                    }
                    if (iterator.hasNext()) {
                        propertyValue += ",";
                    }
                }
                result.setProperty(propertyName, propertyValue);
            }
            putProperties(result, "common");
            Properties initProperties = initProperties();
            String user = initProperties.getProperty("appUser");
            if (StringUtils.isNotBlank(user)) {
                putProperties(result, user + "/common");
                String artifact = initProperties.getProperty("artifact");
                if (StringUtils.isNotBlank(artifact)) {
                    putProperties(result, user + "/" + artifact);
                }
            }
        } catch (RuntimeException e) {
            logger.error("Failed to load properties from consul: " + e.getMessage(), e);
        }
        return result;
    }

    private void putProperties(Properties result, String path) {
        Response<List<GetValue>> response = consulClient.getKVValues(path);
        if (response != null && response.getValue() != null) {
            for (GetValue kv : response.getValue()) {
                putProperty(result, kv);
            }
        }
    }

    private void putProperty(Properties result, GetValue kv) {
        String[] parts = kv.getKey().split("/");
        if (StringUtils.isNotBlank(parts[parts.length - 1])) {
            String value = kv.getDecodedValue();
            if (value == null) {
                value = "";
            }
            result.setProperty(parts[parts.length - 1], value);
        }
    }
    
    private Properties initProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = ConsulPropertiesProvider.class.getResourceAsStream("/init.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            logger.error("Not found init properties");
        }
        return properties;
    }
}
