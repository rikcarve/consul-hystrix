package ch.carve.consul;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.client.Client;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

public class HelloWorldCommand extends HystrixCommand<String> {

    private static URI uri = URI.create("http://hello/hello/v1/hello");
    private static Client client = new ResteasyClientBuilder()
            .maxPooledPerRoute(20)
            .connectionPoolSize(60)
            .socketTimeout(10, TimeUnit.SECONDS)
            .build();

    @Inject
    private ServiceDiscovery consul;

    public HelloWorldCommand() {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(uri.getHost()))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withCircuitBreakerRequestVolumeThreshold(5)));
    }

    @Override
    protected String run() throws Exception {
        return client.target(consul.resolve(uri)).request().get(String.class);
    }

    @Override
    protected String getFallback() {
        consul.notifyError(uri.getHost());
        return client.target(consul.resolve(uri)).request().get(String.class);
    }
}
