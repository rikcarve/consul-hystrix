package ch.carve.consul.discovery;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ServiceUriProviderProducer {
    private static final Logger logger = LoggerFactory.getLogger(ServiceUriProviderProducer.class);

    private Map<String, List<String>> services = new ConcurrentHashMap<>();

    @Inject
    private ServiceDiscoveryBackend backend;

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void timer() {
        logger.info("timer");
        services.keySet().stream().forEach((k) -> backend.updateListAsync(k, services));
    }

    @Produces
    @DiscoverableService(name = "producer")
    public ServiceUriProvider getServiceUriProvider(InjectionPoint ip) {
        logger.info("produce service provider");
        String serviceName = ip.getAnnotated().getAnnotation(DiscoverableService.class).name();
        List<String> hosts = services.get(serviceName);
        if (isNullOrEmpty(hosts)) {
            hosts = backend.getUpdatedListOfServers(serviceName);
            services.put(serviceName, hosts);
        }
        return new ServiceUriProvider(serviceName, hosts);
    }

    private static boolean isNullOrEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
    }

}
