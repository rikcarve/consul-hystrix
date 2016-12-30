package ch.carve.consul;

public class World {
    private String ipAddress;
    private String helloWorld;
    private int port;

    public World() {
    }

    public World(String helloWorld) {
        this.helloWorld = helloWorld;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHelloWorld() {
        return helloWorld;
    }

    public void setHelloWorld(String helloWorld) {
        this.helloWorld = helloWorld;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
