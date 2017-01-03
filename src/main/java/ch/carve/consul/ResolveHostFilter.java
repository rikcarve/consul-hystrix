package ch.carve.consul;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.ServiceHealth;

public class ResolveHostFilter implements ClientRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ResolveHostFilter.class);

    private static Consul consul = Consul.builder().withUrl("http://192.168.99.100:8500").build();

    private List<String> availableServices = null;
    private boolean needReload = true;

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String host = requestContext.getUri().getHost();
        if (needReload || availableServices == null) {
            availableServices = getUpdatedListOfServers(host);
            logger.debug("serverlist updated, count: {}", availableServices.size());
            needReload = false;
        }
        String newHost = availableServices.get(0);
        URI newUri = URI.create("http://" + newHost + requestContext.getUri().getPath());
        logger.debug("URL: {}", newUri);
        requestContext.setUri(newUri);
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