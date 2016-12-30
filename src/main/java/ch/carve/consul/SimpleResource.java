package ch.carve.consul;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/simple")
public class SimpleResource {
	@GET
	@Path("/answer")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer getAnswer() {
		return 42;
	}
	
}
