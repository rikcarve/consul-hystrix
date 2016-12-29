package ch.carve.jaxrs1;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.ServiceHealth;

@Singleton
public class ConsulServerList {

    private String consulUrl = "http://192.168.99.100:8500";
    private Consul consul = null;
    private List<String> servers = null;
    private String service = null;

    @Schedule(second = "*/30", minute = "*", hour = "*", persistent = false)
    public void reload() {
        List<ServiceHealth> nodes = consul.healthClient().getHealthyServiceInstances(service).getResponse();
        servers = nodes.stream()
                .map((node) -> node.getService().getAddress() + ":" + node.getService().getPort())
                .collect(Collectors.toList());
    }

    @PostConstruct
    public void init() {
        consul = Consul.builder().withUrl(consulUrl).build();
    }

    public String getHost(String service) {
        return "";
    }

    public void notifyError(String service) {

    }
}
