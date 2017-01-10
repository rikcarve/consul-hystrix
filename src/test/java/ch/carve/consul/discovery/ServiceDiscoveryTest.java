package ch.carve.consul.discovery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceDiscoveryTest {
    private static final String SERVICE_NAME = "hello";

    private static final String SINGLE_HOST = "192.168.99.100:8080";

    @Mock
    ServiceDiscoveryBackend backend;

    @InjectMocks
    ServiceDiscovery serviceDiscovery;

    private InjectionPoint ip = mock(InjectionPoint.class);

    @Before
    public void initTest() {
        Annotated annotated = mock(Annotated.class);
        when(annotated.getAnnotation(DiscoverableService.class)).thenReturn(new DiscoverableService() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String serviceName() {
                return SERVICE_NAME;
            }
        });
        when(ip.getAnnotated()).thenReturn(annotated);
    }

    @Test
    public void testSimple() {
        when(backend.getUpdatedListOfServers(SERVICE_NAME)).thenReturn(Arrays.asList(SINGLE_HOST));
        ServiceUriProvider uriProvider = serviceDiscovery.getServiceUriProvider(ip);
        URI uri = uriProvider.createUri("/");
        Assert.assertEquals("http://" + SINGLE_HOST + "/", uri.toString());
    }
}
