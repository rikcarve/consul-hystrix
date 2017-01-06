package ch.carve.consul;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hystrix")
public class HystrixResource {

    @Inject
    HelloWorldCommand helloCommand;

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return helloCommand.execute();
        // Future<String> future = helloCommand.queue();
        // StringBuilder builder = new StringBuilder();
        // for (int i = 0; i < 10; i++) {
        // builder.append(String.valueOf(i));
        // }
        // builder.append(" ");
        // try {
        // builder.append(future.get());
        // } catch (InterruptedException | ExecutionException e) {
        // e.printStackTrace();
        // }
        // return builder.toString();
    }
}
