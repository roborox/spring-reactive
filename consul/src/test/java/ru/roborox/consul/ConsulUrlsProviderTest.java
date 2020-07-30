package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsulUrlsProviderTest {
	ConsulUrlsProvider provider;
	private ConsulClient consulClient;
	
	@BeforeEach
	public void beforeClass() throws Exception {
		consulClient = new ConsulClient("localhost");
		provider = new ConsulUrlsProvider(consulClient, Arrays.asList("fallback"), "ConsulUrlsProviderTest");
		provider.start();
		for (CatalogService service : consulClient.getCatalogService("ConsulUrlsProviderTest", QueryParams.DEFAULT).getValue()) {
			consulClient.agentServiceDeregister(service.getServiceId());
		}
	}
	
	@AfterEach
	public void afterClass() throws Exception {
		provider.close();
		for (CatalogService service : consulClient.getCatalogService("ConsulUrlsProviderTest", QueryParams.DEFAULT).getValue()) {
			consulClient.agentServiceDeregister(service.getServiceId());
		}
	}

	@Test
	public void load() throws Exception {
		assertEquals(provider.getUrls(), Arrays.asList("fallback"));
		assertEquals(provider.next(), "fallback");
		assertEquals(provider.next(), "fallback");
		NewService service = new NewService();
		service.setAddress("127.0.0.1");
		service.setPort(666);
		service.setId("666");
		service.setName("ConsulUrlsProviderTest");
		consulClient.agentServiceRegister(service);
		Thread.sleep(100);
		
		assertEquals(provider.getUrls(), Arrays.asList("http://127.0.0.1:666"));
		assertEquals(provider.next(), "http://127.0.0.1:666");
		assertEquals(provider.next(), "http://127.0.0.1:666");
		
		NewService service2 = new NewService();
		service2.setAddress("127.0.0.1");
		service2.setPort(667);
		service2.setId("667");
		service2.setName("ConsulUrlsProviderTest");
		consulClient.agentServiceRegister(service2);
		Thread.sleep(100);
		
		assertEquals(provider.getUrls(), Arrays.asList("http://127.0.0.1:666", "http://127.0.0.1:667"));
		assertEquals(provider.next(), "http://127.0.0.1:666");
		assertEquals(provider.next(), "http://127.0.0.1:667");
		assertEquals(provider.next(), "http://127.0.0.1:666");
	}
}
