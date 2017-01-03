package ch.carve.consul;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

@Path("/forward")
public class ForwardResource {

    private static URI uri = URI.create("http://hello/hello/v1/hello");

    static int count = 0;
    static long nanos = 0;

    private static Client client = new ResteasyClientBuilder()
            .maxPooledPerRoute(20)
            .connectionPoolSize(60)
            .socketTimeout(10, TimeUnit.SECONDS)
            .register(ResolveHostFilter.class)
            .build();

    @PostConstruct
    public void initRestClient() {
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String forward() {
        long start = System.nanoTime();
        String response = client.target(uri).request().get(String.class);
        nanos += (System.nanoTime() - start);
        count++;
        return response;
    }

    @GET
    @Path("/reset")
    @Produces(MediaType.TEXT_PLAIN)
    public void reset() {
        count = 0;
        nanos = 0;
    }

    @GET
    @Path("/avg")
    @Produces(MediaType.TEXT_PLAIN)
    public String avg() {
        return String.valueOf(nanos / 1000000 / count);
    }
}