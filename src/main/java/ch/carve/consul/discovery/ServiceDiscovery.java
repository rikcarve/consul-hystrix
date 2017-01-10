package ch.carve.consul.discovery;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
 * Service discovery which provides a producer for {@link ServiceUriProvider}'s.
 * Injected {@link ServiceDiscoveryBackend}.
 * 
 */
@Singleton
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private Map<String, List<String>> services = new ConcurrentHashMap<>();
    private Map<String, List<String>> envOverrides = new ConcurrentHashMap<>();

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

        // check for environment overrides
        List<String> envHosts = checkEnv(serviceName);
        if (!envHosts.isEmpty()) {
            return new ServiceUriProvider(serviceName, envHosts);
        }

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

    private List<String> checkEnv(String serviceName) {
        List<String> envHosts = envOverrides.get(serviceName);
        if (envHosts == null) {
            String env = System.getenv(serviceName + "-nodes");
            if (env != null) {
                logger.info("Environment override: using {} for service {}", env, serviceName);
                envHosts = Arrays.asList(env.split(","));
            } else {
                envHosts = Collections.emptyList();
            }
            envOverrides.put(serviceName, envHosts);
        }
        return envHosts;
    }

    private static boolean isNullOrEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
    }

}
