package ru.roborox.reactive.jms.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.core.remoting.impl.netty.TransportConstants;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import ru.roborox.reactive.jms.common.ReactiveJmsSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableJms
public class JmsConfiguration {
    public static final Logger logger = LoggerFactory.getLogger(JmsConfiguration.class);

    public static final String RAW_FACTORY = "rawJmsConnectionFactory";
    public static final String CACHING_FACTORY = "cachingJmsConnectionFactory";

    @Value("${jms-brokerUrls}")
    private String jsmBrokerUrls;
    @Value("${jmsReceiveTimeout:5000}")
    private long receiveTimeout;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ReactiveJmsSender reactiveJmsSender(JmsTemplate jmsTemplate) {
        return new ReactiveJmsSender(jmsTemplate);
    }

    @Bean
    public JmsTemplate jmsTemplate(MessageConverter messageConverter) {
        JmsTemplate template = new JmsTemplate();
        template.setMessageConverter(messageConverter);
        template.setConnectionFactory(cachingConnectionFactory());
        template.setReceiveTimeout(receiveTimeout);
        return template;
    }

    @Primary
    @Bean
    @Qualifier(CACHING_FACTORY)
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(jmsConnectionFactory());
        cachingConnectionFactory.setCacheProducers(true);
        cachingConnectionFactory.setSessionCacheSize(100);
        cachingConnectionFactory.setReconnectOnException(true);
        return cachingConnectionFactory;
    }

    @Bean(destroyMethod = "close")
    @Qualifier(RAW_FACTORY)
    public ActiveMQConnectionFactory jmsConnectionFactory() {
        return new ActiveMQConnectionFactory(serverLocator());
    }

    @Bean(name = "jmsListenerContainerFactory")
    public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory(MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setSessionTransacted(true);
        factory.setMessageConverter(messageConverter);
        factory.setAutoStartup(true);
        factory.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER);
        factory.setConcurrency("1-10");
        factory.setConnectionFactory(jmsConnectionFactory());
        return factory;
    }

    @Bean(name = "jmsTopicListenerContainerFactory")
    public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsTopicListenerContainerFactory(MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setSessionTransacted(true);
        factory.setMessageConverter(messageConverter);
        factory.setAutoStartup(true);
        factory.setPubSubDomain(true);
        factory.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONSUMER);
        factory.setConcurrency("1");
        factory.setConnectionFactory(jmsConnectionFactory());
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("type");
        messageConverter.setObjectMapper(objectMapper);
        return messageConverter;
    }

    @Bean
    protected ServerLocator serverLocator() {
        ServerLocator serverLocator = ActiveMQClient.createServerLocatorWithHA(getTransports());
        serverLocator.setBlockOnAcknowledge(false);
        serverLocator.setBlockOnDurableSend(false);
        serverLocator.setBlockOnNonDurableSend(false);
        return serverLocator;
    }

    private TransportConfiguration[] getTransports() {
        logger.info("using jsmBrokerUrls={}", jsmBrokerUrls);
        List<TransportConfiguration> configurations = new ArrayList<>();
        for (String node : jsmBrokerUrls.split(",")) {
            String[] hostPort = node.split(":");
            Map<String, Object> params = new HashMap<>();
            params.put(TransportConstants.HOST_PROP_NAME, hostPort[0]);
            params.put(TransportConstants.PORT_PROP_NAME, Integer.parseInt(hostPort[1]));
            TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
            configurations.add(transportConfiguration);
        }
        return configurations.toArray(new TransportConfiguration[configurations.size()]);
    }
}
