package ch.carve.consul;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.ServiceHealth;

public class ResolveHostFilter implements ClientRequestFilter, ClientResponseFilter {

    private static Consul consul = Consul.builder().withHostAndPort(HostAndPort.fromParts("192.168.99.100", 8500))
            .build();

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        String host = requestContext.getUri().getHost();
        System.out.println(host);
        String newHost = getUpdatedListOfServers(host).get(0);
        URI newUri = URI.create("http://" + newHost + requestContext.getUri().getPath());
        System.out.println(newUri.toString());
        requestContext.setUri(newUri);
    }

    private List<String> getUpdatedListOfServers(String service) {
        List<String> result = new ArrayList<>();
        List<ServiceHealth> nodes = consul.healthClient().getHealthyServiceInstances(service).getResponse();
        for (ServiceHealth node : nodes) {
            result.add(node.getService().getAddress() + ":" + node.getService().getPort());
        }
        return result;
    }

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        System.out.println(responseContext.getStatusInfo());
    }

}
