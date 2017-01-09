package ch.carve.consul;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.client.Client;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

import ch.carve.consul.discovery.DiscoverableService;
import ch.carve.consul.discovery.ServiceUriProvider;

public class HelloWorldCommand extends HystrixCommand<String> {

    private static final URI uri = URI.create("http://hello/hello/v1/hello");
    private static final String SERVICE_NAME = "hello";
    private static final String PATH = "/hello/v1/hello";

    private static Client client = new ResteasyClientBuilder()
            .maxPooledPerRoute(20)
            .connectionPoolSize(60)
            .socketTimeout(10, TimeUnit.SECONDS)
            .build();

    @Inject
    @DiscoverableService(name = SERVICE_NAME)
    private ServiceUriProvider service;

    public HelloWorldCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(uri.getHost()))
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
