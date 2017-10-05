package ch.carve.consul.discovery;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

public class ServiceUriProviderTest {

    @RepeatedTest(value = 3)
    @DisplayName("Repeat")
    public void testCreateUri() throws Exception {
        ServiceUriProvider uriProvider = new ServiceUriProvider("hello", Collections.emptyList());
        assertThrows(NoServiceRegisteredException.class, () -> uriProvider.createUri("/"));
    }

}
