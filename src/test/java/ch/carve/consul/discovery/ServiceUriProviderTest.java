package ch.carve.consul.discovery;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
public class ServiceUriProviderTest {

    @Test
    public void testCreateUri() throws Exception {
        ServiceUriProvider uriProvider = new ServiceUriProvider("hello", Collections.emptyList());
        assertThrows(NoServiceRegisteredException.class, () -> uriProvider.createUri("/"));
    }

}
