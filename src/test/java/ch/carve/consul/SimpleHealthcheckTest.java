package ch.carve.consul;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class SimpleHealthcheckTest {

    @Test
    public void testGetAnswer() throws Exception {
        SimpleHealthcheck healthCheck = new SimpleHealthcheck();
        Response response = healthCheck.getAnswer();
        Assert.assertEquals(Response.Status.OK, response.getStatusInfo());
    }

}
