package ch.carve.consul;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import ch.carve.consul.discovery.DiscoverableService;
import ch.carve.consul.discovery.ServiceUriProvider;

@Path("/discovery")
public class DiscoveryResource {

    private static final String SERVICE_NAME = "hello";
    private static final String PATH = "/hello/v1/hello";

    private static Client client = new ResteasyClientBuilder()
            .maxPooledPerRoute(20)
            .connectionPoolSize(60)
            .socketTimeout(10, TimeUnit.SECONDS)
            .build();

    @Inject
    @DiscoverableService(serviceName = SERVICE_NAME)
    private ServiceUriProvider service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws Exception {
        return client.target(service.createUri(PATH)).request().get(String.class);
    }
}
