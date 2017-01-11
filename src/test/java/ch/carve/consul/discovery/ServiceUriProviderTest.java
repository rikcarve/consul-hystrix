package ch.carve.consul.discovery;

import java.util.Collections;

import org.junit.Test;

public class ServiceUriProviderTest {

    @Test(expected = NoServiceRegisteredException.class)
    public void testCreateUri() throws Exception {
        ServiceUriProvider uriProvider = new ServiceUriProvider("hello", Collections.emptyList());
        uriProvider.createUri("/");
    }

}
