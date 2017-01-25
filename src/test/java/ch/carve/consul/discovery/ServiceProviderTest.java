package ch.carve.consul.discovery;

import java.util.Collections;

import org.junit.Test;

public class ServiceProviderTest {

    @Test(expected = NoServiceRegisteredException.class)
    public void testCreateUri() throws Exception {
        ServiceProvider uriProvider = new ServiceProvider("hello", Collections.emptyList());
        uriProvider.createUri("/");
    }

}
