package ch.carve.consul;

import java.util.List;
import java.util.Map;

/**
 * Interface a service dicovery backend must implement.
 * 
 * @author rik
 *
 */
public interface ServiceDiscoveryBackend {

    /**
     * Updates list of servers for given service directly in the provided map
     * 
     * @param service
     * @param hosts
     */
    void updateListAsync(String service, Map<String, List<String>> hosts);

    /**
     * Return an list of servers for given service
     * 
     * @param service
     * @return
     */
    List<String> getUpdatedListOfServers(String service);

}