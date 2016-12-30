package ch.carve.consul;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.ServiceHealth;

public class ConsulUriBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ConsulUriBuilder.class);

    private static Consul consul = Consul.builder().withUrl("http://192.168.99.100:8500").build();
    private String service = null;
    private List<String> availableServices = null;
    private boolean needReload = true;

    @Schedule(second = "*/30", minute = "*", hour = "*", persistent = false)
    public void reload() {
        logger.debug("Timer scheduled reload");
        if (service != null) {
            availableServices = getUpdatedListOfServers(service);
        }
    }

    public void setService(String service) {
        this.service = service;
    }

    public URI build(URI uri) {
        String host = uri.getHost();
        if (service == null) {
            service = host;
        }
        if (needReload || availableServices == null) {
            reload();
            logger.debug("serverlist reloaded, count: {}", availableServices.size());
            needReload = false;
        }
        String newHost = availableServices.get(0);
        URI newUri = URI.create("http://" + newHost + uri.getPath());
        logger.debug("URL: {}", newUri);
        return newUri;
    }

    public void setReload() {
        needReload = true;
    }

    private List<String> getUpdatedListOfServers(String service) {
        List<String> result = new ArrayList<>();
        List<ServiceHealth> nodes = consul.healthClient().getHealthyServiceInstances(service).getResponse();
        for (ServiceHealth node : nodes) {
            result.add(node.getService().getAddress() + ":" + node.getService().getPort());
        }
        return result;
    }

}
