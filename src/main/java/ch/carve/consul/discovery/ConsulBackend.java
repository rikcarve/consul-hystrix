package ch.carve.consul.discovery;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.ServiceHealth;

/**
 * Service discovery backend using consul
 * 
 * @author rik
 *
 */
@Singleton
public class ConsulBackend implements ServiceDiscoveryBackend {
    private static final Logger logger = LoggerFactory.getLogger(ConsulBackend.class);

    private static Consul consul = Consul.builder().withUrl(System.getProperty("consul.url", "http://192.168.99.100:8500")).build();

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.carve.consul.ServiceDiscoveryBackend#updateListAsync(java.lang.String,
     * java.util.Map)
     */
    @Override
    @Asynchronous
    public void updateListAsync(String service, Map<String, List<String>> hosts) {
        hosts.put(service, getUpdatedListOfServers(service));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * ch.carve.consul.ServiceDiscoveryBackend#getUpdatedListOfServers(java.lang
     * .String)
     */
    @Override
    public List<String> getUpdatedListOfServers(String service) {
        // List<String> result = Collections.synchronizedList(new
        // ArrayList<>());
        List<String> result = new CopyOnWriteArrayList<String>();
        List<ServiceHealth> nodes = consul.healthClient().getHealthyServiceInstances(service).getResponse();
        for (ServiceHealth node : nodes) {
            result.add(node.getService().getAddress() + ":" + node.getService().getPort());
        }
        logger.info("updated server list for service {} : {}", service, result);
        return result;
    }

}
