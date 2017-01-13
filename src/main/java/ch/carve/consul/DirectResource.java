package ch.carve.consul;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

@Path("/direct")
public class DirectResource {

    private static Client client = new ResteasyClientBuilder()
            .maxPooledPerRoute(20)
            .connectionPoolSize(60)
            .socketTimeout(10, TimeUnit.SECONDS)
            .build();

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAnswer() {
        return client.target("http://192.168.99.100:9000/hello/v1/hello").request().get(String.class);
    }

}
