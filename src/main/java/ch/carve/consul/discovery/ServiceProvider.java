package ch.carve.consul.discovery;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a createUri method which uses a list of hosts provided by a service
 * discovery.
 * 
 * @author rik
 *
 */
public class ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

    private String serviceName;
    private List<String> hosts;
    private String currentHost;

    public ServiceProvider(String serviceName, List<String> hosts) {
        this.serviceName = serviceName;
        this.hosts = hosts;
    }

    /**
     * Creates an URI out of a provided path and a available server
     * @param path
     * @return
     */
    public URI createUri(String path) {
        currentHost = filter(hosts);
        logger.debug("resolved to {}", currentHost);
        URI newUri = URI.create("http://" + currentHost + path);
        return newUri;
    }

    /**
     * Get <host:port> of an available server as string
     * @return
     */
    public String getHostPort() {
        return filter(hosts);
    }

    /**
     * Notify after an error occurred with this service. removes erroneous host
     * from list
     */
    public void notifyError() {
        logger.info("Host {} of service {} marked erroneous", currentHost, serviceName);
        hosts.remove(currentHost);
    }

    private String filter(List<String> list) {
        // future: get local host first if available, or loadbalance
        if (!isNullOrEmpty(list)) {
            return list.get(0);
        } else {
            throw new NoServiceRegisteredException(serviceName);
        }
    }

    private static boolean isNullOrEmpty(final Collection<?> c) {
        return c == null || c.isEmpty();
    }
}
