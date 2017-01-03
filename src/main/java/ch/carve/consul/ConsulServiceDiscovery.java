package ch.carve.consul;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Schedule;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.ServiceHealth;

@Singleton
public class ConsulServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ConsulServiceDiscovery.class);

    private static Consul consul = Consul.builder().withUrl("http://192.168.99.100:8500").build();

    private Map<String, List<String>> hosts = new HashMap<>();

    @Schedule(second = "*/10", minute = "*", hour = "*", persistent = false)
    public void timer() {
        hosts.keySet().stream().forEach((k) -> hosts.put(k, getUpdatedListOfServers(k)));
    }

    public URI resolve(URI uri) {
        String service = uri.getHost();
        List<String> list = hosts.get(service);
        if (list == null || list == Collections.EMPTY_LIST) {
            list = getUpdatedListOfServers(service);
            hosts.put(service, list);
        }
        String newHost = filter(list);
        URI newUri = URI.create("http://" + newHost + uri.getPath());
        return newUri;
    }

    public void notifyError(String service) {
        logger.info("Service {} marked for reload", service);
        hosts.remove(service);
    }

    private String filter(List<String> list) {
        return list.get(0);
    }

    private List<String> getUpdatedListOfServers(String service) {
        List<String> result = new ArrayList<>();
        List<ServiceHealth> nodes = consul.healthClient().getHealthyServiceInstances(service).getResponse();
        for (ServiceHealth node : nodes) {
            result.add(node.getService().getAddress() + ":" + node.getService().getPort());
        }
        logger.info("updated server list for service {} : {}", service, result);
        return result;
    }

}
