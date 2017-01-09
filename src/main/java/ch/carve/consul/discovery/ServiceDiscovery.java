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

/**
 * Service discovery which provides a producer for {@link ServiceUriProvider}'s
 * Injected backend
 */
@Singleton
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private Map<String, List<String>> services = new ConcurrentHashMap<>();

    @Inject
    private ServiceDiscoveryBackend backend;

    @Schedule(second = "*/30", minute = "*", hour = "*", persistent = false)
    public void timer() {
        logger.info("timer");
        services.keySet().stream().forEach((k) -> backend.updateListAsync(k, services));
    }

    @Produces
    @DiscoverableService(serviceName = "producer")
    public ServiceUriProvider getServiceUriProvider(InjectionPoint ip) {
        logger.info("produce service provider");
        String serviceName = ip.getAnnotated().getAnnotation(DiscoverableService.class).serviceName();
        List<String> hosts = services.get(serviceName);
        if (isNullOrEmpty(hosts)) {
            hosts = backend.getUpdatedListOfServers(serviceName);
            if (isNullOrEmpty(hosts)) {
                throw new NoServiceRegisteredException(serviceName);
            }
            services.put(serviceName, hosts);
        }
        return new ServiceUriProvider(serviceName, hosts);
    }

    private static boolean isNullOrEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
    }

}
