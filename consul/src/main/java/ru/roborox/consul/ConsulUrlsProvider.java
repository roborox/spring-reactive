package ru.roborox.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.catalog.model.CatalogService;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsulUrlsProvider implements UrlsProvider, AutoCloseable {
	private static final Logger logger = LoggerFactory.getLogger(ConsulUrlsProvider.class);
	private final ConsulClient consulClient;
	private final List<String> fallback;
	private final String serviceName;
	private volatile List<String> currentUrls;
	private volatile Long index = -1L;
	private volatile boolean shouldRun = true;
	private final Thread refreshThread;
	private final AtomicInteger position = new AtomicInteger(0);
	
	public ConsulUrlsProvider(ConsulClient consulClient, List<String> fallback, String serviceName) {
		this.consulClient = consulClient;
		this.fallback = Collections.unmodifiableList(new ArrayList<String>(fallback));
		this.serviceName = serviceName;
		refreshThread = new Thread(() -> {
			while (shouldRun) {
				refreshCurrentUrls(new QueryParams(300, index));
			}
		}, serviceName + "-consul");
		refreshCurrentUrls(QueryParams.DEFAULT);
	}

	@Override
	public List<String> getUrls() {
		return currentUrls;
	}
	
	@Override
	public String next() {
		List<String> urls = new ArrayList<>(currentUrls);
		int currentPosition = position.getAndIncrement();
		return urls.get(Math.abs(currentPosition % urls.size()));
	}
	
	private void refreshCurrentUrls(QueryParams params) {
		logger.info("Loading service {} urls", serviceName);
		try {
			Response<List<CatalogService>> consulResponse = consulClient.getCatalogService(serviceName, params);
			index = consulResponse.getConsulIndex();
			List<CatalogService> services = consulResponse.getValue();
			if (services.isEmpty()) {
				currentUrls = fallback;
			} else {
				List<String> urls = new ArrayList<String>(services.size());
				for (CatalogService service : services) {
					urls.add("http://" + service.getServiceAddress() + ":" + service.getServicePort());
				}
				this.currentUrls = urls;
			}
			logger.info("currentUrl={}", currentUrls);
		} catch (RuntimeException e) {
			logger.error("Error during consul access: " + e.getMessage(), e);
			currentUrls = fallback;
			if (params.getWaitTime() > 0) {
				try {
					Thread.sleep(params.getWaitTime() * DateUtils.MILLIS_PER_SECOND);
				} catch (InterruptedException e1) {
					logger.error("Interrupted");
				}
			}
		}
	}
	
	public void start() {
		refreshThread.start();
	}

	@Override
	public void close() throws Exception {
		shouldRun = false;
		refreshThread.interrupt();
	}
}
