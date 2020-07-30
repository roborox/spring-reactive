package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsulService {
    public static final Logger logger = LoggerFactory.getLogger(ConsulService.class);

    private final boolean web;
    private final ConsulClient client;
    private final Environment propertySource;
    private final ConsulPropertySourceConfiguration configuration;

    private volatile String serviceId;

    public ConsulService(boolean web, ConsulClient client, Environment propertySource, ConsulPropertySourceConfiguration configuration) {
        this.web = web;
        this.client = client;
        this.propertySource = propertySource;
        this.configuration = configuration;
    }

    @PostConstruct
    public void init() throws UnknownHostException {
        final List<String> tags;
        if (web) {
            tags = new ArrayList<>();
            String domain = System.getProperty("web.domain");
            String webDomainTag = getWebDomainTag();
            if (domain != null && !existsDomainTag(webDomainTag, tags)) {
                tags.add(webDomainTag + ":" + domain);
            }
        } else {
            tags = Collections.emptyList();
        }
        this.serviceId = registerInConsul(tags);
    }

    @PreDestroy
    public void destroy() {
        if (serviceId != null) {
            client.agentServiceDeregister(serviceId);
        }
    }

    private String registerInConsul(List<String> tags) throws UnknownHostException {
        try {
            NewService service = new NewService();
            service.setAddress(InetAddress.getLocalHost().getHostAddress());
            Integer port = configuration.getHttpPort();
            String version = propertySource.getProperty("version");
            String checkPath = propertySource.getProperty("checkPath");
            String user = propertySource.getProperty("appUser");
            String artifact = propertySource.getProperty("artifact");
            String appName = propertySource.getProperty("consulAppName", user + "-" + artifact);
            NewService.Check check;
            if (web && port > 0) {
                service.setPort(port);
                check = createHttpCheck(port);
            } else {
                check = createScriptCheck(checkPath);
            }
            service.setName(appName);
            service.setCheck(check);
            String serviceId = appName + "-" + InetAddress.getLocalHost().getHostName();
            service.setId(serviceId);
            List<String> tagsList = new ArrayList<>();
            if (tags != null) {
                tagsList.addAll(tags);
            }
            tagsList.add("Version: " + version);
            tagsList.add(user);
            service.setTags(tagsList);
            logger.info("Registering service {} in consul", serviceId);
            client.agentServiceRegister(service);
            return serviceId;
        } catch (RuntimeException e) {
            logger.info("Registration in consul failed: " + e.getMessage());
            return null;
        }
    }

    private static NewService.Check createHttpCheck(Integer port) {
        NewService.Check check = new NewService.Check();
        check.setHttp("http://127.0.0.1:" + port + "/ping");
        check.setTimeout("1s");
        check.setInterval("10s");
        return check;
    }

    private static NewService.Check createScriptCheck(String checkPath) {
        NewService.Check check = new NewService.Check();
        check.setScript(checkPath);
        check.setTimeout("5s");
        check.setTtl("30s");
        return check;
    }

    private static String getWebDomainTag() {
        String webTag = System.getProperty("web.tag");
        if (webTag != null) {
            return webTag;
        } else {
            return "web";
        }
    }

    private static boolean existsDomainTag(String tagName, List<String> tags) {
        for (String tag : tags) {
            if (tag.startsWith(tagName + ":")) {
                return true;
            }
        }
        return false;
    }
}
