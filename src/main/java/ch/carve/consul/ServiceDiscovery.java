package ch.carve.consul;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service Discovery with Consul as backend.
 */
@Singleton
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private Map<String, List<String>> hosts = new ConcurrentHashMap<>(16, 0.75f, 1);

    @Inject
    private ServiceDiscoveryBackend backend;

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void timer() {
        logger.info("timer");
        hosts.keySet().stream().forEach((k) -> backend.updateListAsync(k, hosts));
    }

    /**
     * Exchange service name with a healthy host:port in {@link URI}
     * 
     * @param uri
     * @return
     */
    public URI resolve(URI uri) {
        logger.info("resolve");
        String service = uri.getHost();
        List<String> list = hosts.get(service);
        if (list == null || list == Collections.EMPTY_LIST) {
            list = backend.getUpdatedListOfServers(service);
            hosts.put(service, list);
        }
        String newHost = filter(list);
        URI newUri = URI.create("http://" + newHost + uri.getPath());
        return newUri;
    }

    /**
     * Notify after an error occurred with this service. removes erroneous host from list
     * 
     * @param service
     * @param host 
     */
    public void notifyError(String service, String hostPort) {
        logger.info("Host {} of service {} marked erroneous", hostPort, service);
        hosts.get(service).remove(hostPort);
    }

    private String filter(List<String> list) {
        // future: get local host first if available, or loadbalance
        return list.get(0);
    }

}
