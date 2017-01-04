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

    private Map<String, List<String>> hosts = new ConcurrentHashMap<>();

    @Inject
    private ServiceDiscoveryBackend backend;

    @Schedule(second = "*/30", minute = "*", hour = "*", persistent = false)
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
     * Notify after an error occured with this service. Triggers a reload of
     * hosts from the consul server
     * 
     * @param service
     */
    public void notifyError(String service) {
        logger.info("Service {} marked for reload", service);
        hosts.remove(service);
    }

    private String filter(List<String> list) {
        // future: get local host first if available, or loadbalance
        return list.get(0);
    }

}
