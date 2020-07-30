package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan
class ConsulConfiguration {

	@Autowired
	private Environment environment;
	@Autowired
	private ConsulPropertySourceConfiguration configuration;

	@Bean
	public ConsulClient consulClient() {
		return new ConsulClient("127.0.0.1");
	}

	@Bean
	@ConditionalOnWebApplication
	public ConsulService webAppConsulService() {
		return new ConsulService(true, consulClient(), environment, configuration);
	}

	@Bean
	@ConditionalOnNotWebApplication
	public ConsulService notWebAppConsulService() {
		return new ConsulService(false, consulClient(), environment, configuration);
	}
}
