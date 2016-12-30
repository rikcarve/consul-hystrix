package ch.carve.consul;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hystrix")
public class HystrixResource {

    HelloWorldCommand helloCommand;

    @PostConstruct
    public void init() {
        helloCommand = new HelloWorldCommand();
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return helloCommand.execute();
    }
}
