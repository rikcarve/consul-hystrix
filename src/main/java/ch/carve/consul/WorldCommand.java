package ch.carve.consul;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.client.Client;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import ch.carve.consul.discovery.DiscoverableService;
import ch.carve.consul.discovery.ServiceUriProvider;

public class WorldCommand extends HystrixCommand<String> {

    private static final String SERVICE_NAME = "world";
    private static final String PATH = "/world/v1/world";

    private static Client client = new ResteasyClientBuilder()
            .maxPooledPerRoute(20)
            .connectionPoolSize(60)
            .socketTimeout(10, TimeUnit.SECONDS)
            .build();

    @Inject
    @DiscoverableService(serviceName = SERVICE_NAME)
    private ServiceUriProvider service;

    public WorldCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(SERVICE_NAME))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withCircuitBreakerRequestVolumeThreshold(5)));
    }

    @Override
    protected String run() throws Exception {
        return client.target(service.createUri(PATH)).request().get(String.class);
    }

    @Override
    protected String getFallback() {
        service.notifyError();
        return client.target(service.createUri(PATH)).request().get(String.class);
    }
}
