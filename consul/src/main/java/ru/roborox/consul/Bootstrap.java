package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.agent.model.NewService.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void start(Class config, String appName, String... tags) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage(), e));
        logger.info("Starting server");
        try {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(config, ConsulConfiguration.class);
            context.registerShutdownHook();
            ArrayList<String> tagsList = new ArrayList<>(asList(tags));
            String domain = System.getProperty("web.domain");
            String webDomainTag = getWebDomainTag();
            if (domain != null && !existsDomainTag(webDomainTag, tagsList)) {
                tagsList.add(webDomainTag + ":" + domain);
            }
            registerInConsul(true, context, appName, tagsList);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Server started");
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
    
    public static void startNoWeb(Class config, String appName, String... tags) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.error(e.getMessage(), e));
        logger.info("Starting server");
        try {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(config, ConsulConfiguration.class);
            context.registerShutdownHook();
            registerInConsul(false, context, appName, asList(tags));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("Server started");
    }

    private static void registerInConsul(boolean web, AnnotationConfigApplicationContext context, String appName, List<String> tags)
            throws UnknownHostException {
        try {
            ConsulClient client = context.getBean(ConsulClient.class);
            Environment propertySource = context.getBean(Environment.class);
            ConsulPropertySourceConfiguration configuration = context.getBean(ConsulPropertySourceConfiguration.class);
            NewService service = new NewService();
            service.setAddress(InetAddress.getLocalHost().getHostAddress());
            Integer port = configuration.getHttpPort();
            String version = propertySource.getProperty("version");
            String checkPath = propertySource.getProperty("checkPath");
            String user = propertySource.getProperty("appUser");
            Check check;
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
            tagsList.add("Version: " + version);
            tagsList.add(user);
            if (tags != null) {
                tagsList.addAll(tags);
            }
            service.setTags(tagsList);
            logger.info("Registering service {} in consul", serviceId);
            client.agentServiceRegister(service);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                client.agentServiceDeregister(serviceId);
            }));
        } catch (RuntimeException e) {
            logger.info("Registration in consul failed: " + e.getMessage());
        }
    }

    private static Check createHttpCheck(Integer port) {
        Check check = new Check();
        check.setHttp("http://127.0.0.1:" + port + "/ping");
        check.setTimeout("1s");
        check.setInterval("10s");
        return check;
    }

    private static Check createScriptCheck(String checkPath) {
        Check check = new Check();
        check.setScript(checkPath);
        check.setTimeout("5s");
        check.setInterval("30s");
        return check;
    }
}
