package ch.carve.consul.discovery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServiceDiscoveryTest {
    private static final String SERVICE_NAME = "hello";

    private static final String HOST_1 = "192.168.99.100:8080";
    private static final String HOST_2 = "192.168.99.100:8081";

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
    public void testSingleUri() {
        when(backend.getUpdatedListOfServers(SERVICE_NAME)).thenReturn(Arrays.asList(HOST_1));
        ServiceUriProvider uriProvider = serviceDiscovery.getServiceUriProvider(ip);
        URI uri = uriProvider.createUri("/");
        Assert.assertEquals("http://" + HOST_1 + "/", uri.toString());
    }

    @Test
    public void testHostPort() {
        when(backend.getUpdatedListOfServers(SERVICE_NAME)).thenReturn(Arrays.asList(HOST_1));
        ServiceUriProvider uriProvider = serviceDiscovery.getServiceUriProvider(ip);
        String hostPort = uriProvider.getHostPort();
        Assert.assertEquals(HOST_1, hostPort);
    }

    @Test
    public void testNotifyError() {
        when(backend.getUpdatedListOfServers(SERVICE_NAME)).thenReturn(new LinkedList<>(Arrays.asList(HOST_1, HOST_2)));
        ServiceUriProvider uriProvider = serviceDiscovery.getServiceUriProvider(ip);
        URI uri = uriProvider.createUri("/");
        uriProvider.notifyError();
        uri = uriProvider.createUri("/");
        Assert.assertEquals("http://" + HOST_2 + "/", uri.toString());
    }

    @Test
    public void testTimer() {
        when(backend.getUpdatedListOfServers(SERVICE_NAME)).thenReturn(Arrays.asList(HOST_1));
        serviceDiscovery.getServiceUriProvider(ip);
        serviceDiscovery.timer();
        verify(backend, times(1)).updateListAsync(Mockito.anyString(), Mockito.anyMap());
    }

    @Test(expected = NoServiceRegisteredException.class)
    public void testNoServiceRegistered() {
        serviceDiscovery.getServiceUriProvider(ip);
    }

    @Test
    public void testEnvOverride() {
        System.setProperty(SERVICE_NAME + "-nodes", HOST_1);
        ServiceUriProvider uriProvider = serviceDiscovery.getServiceUriProvider(ip);
        URI uri = uriProvider.createUri("/");
        Assert.assertEquals("http://" + HOST_1 + "/", uri.toString());
        System.clearProperty(SERVICE_NAME + "-nodes");
    }
}
