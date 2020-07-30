package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsulConfiguration {

	@Bean
	public ConsulClient consulClient() {
		return new ConsulClient("127.0.0.1");
	}
}
