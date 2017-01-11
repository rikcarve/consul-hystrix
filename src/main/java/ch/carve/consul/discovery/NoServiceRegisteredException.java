package ch.carve.consul.discovery;

public class NoServiceRegisteredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NoServiceRegisteredException(String serviceName) {
        super(serviceName);
    }
}
