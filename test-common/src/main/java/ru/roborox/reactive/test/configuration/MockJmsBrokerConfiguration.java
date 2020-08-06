package ru.roborox.reactive.test.configuration;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyAcceptorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.core.settings.impl.AddressFullMessagePolicy;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Configuration
public class MockJmsBrokerConfiguration {
    @Value("${jms-brokerUrls}")
    private String jmsBrokerUrls;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public EmbeddedActiveMQ embeddedJMS() {
        EmbeddedActiveMQ jms = new EmbeddedActiveMQ();
        org.apache.activemq.artemis.core.config.Configuration configuration = new ConfigurationImpl();
        HashSet<TransportConfiguration> transports = new HashSet<>();
        Map<String, Object> transportConfig = new HashMap<>();
        String[] hostPort = jmsBrokerUrls.split(":");
        transportConfig.put(TransportConstants.HOST_PROP_NAME, hostPort[0]);
        transportConfig.put(TransportConstants.PORT_PROP_NAME, Integer.parseInt(hostPort[1]));
        transports.add(new TransportConfiguration(NettyAcceptorFactory.class.getName(), transportConfig));
        configuration.addConnectorConfiguration("self",
            new TransportConfiguration(NettyConnectorFactory.class.getName(), transportConfig));
        configuration.setAcceptorConfigurations(transports);
        configuration.setPersistenceEnabled(false);
        configuration.setSecurityEnabled(false);
        jms.setConfiguration(configuration);
        jms.setConfiguration(new ConfigurationImpl());

        AddressSettings addressSettings = new AddressSettings();
        addressSettings
            .setAddressFullMessagePolicy(AddressFullMessagePolicy.DROP)
            .setMaxDeliveryAttempts(3)
            .setRedeliveryDelay(0)
            .setAutoCreateQueues(true)
            .setAutoCreateAddresses(true);
        configuration.addAddressesSetting("#", new AddressSettings(addressSettings));

        return jms;
    }
}
