package ch.carve.consul;

import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/healthcheck")
@Singleton
public class SimpleHealthcheck {
	
	private boolean isFail = false;
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAnswer() {
		if (isFail) {
			isFail = false;
			System.out.println("healthcheck failed");
			return Response.status(Status.SERVICE_UNAVAILABLE).build();
		} else {
			return Response.ok().build();
		}
	}
	
	@GET
	@Path("/setForFail")
	public Response setForFail() {
		isFail = true;
		return Response.ok().build();
	}
}
