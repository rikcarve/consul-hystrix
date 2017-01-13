package ch.carve.consul;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hystrix")
public class HystrixResource {

    @Inject
    private HelloWorldCommand helloCommand;

    // @Inject
    // private WorldCommand worldCommand;

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return helloCommand.execute();// + " " + worldCommand.execute();
    }
}
