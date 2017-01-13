# consul-hystrix
[![Build Status](https://travis-ci.org/rikcarve/consul-hystrix.svg?branch=master)](https://travis-ci.org/rikcarve/consul-hystrix)
[![codecov](https://codecov.io/gh/rikcarve/consul-hystrix/branch/master/graph/badge.svg)](https://codecov.io/gh/rikcarve/consul-hystrix)

## Overview
Show how to use consul for service discovery together with hystrix for resilience

## Usage
### Service Discovery
Inject ServiceUriProvider:
```java
    @Inject
    @DiscoverableService(serviceName = SERVICE_NAME)
    private ServiceUriProvider service;
```

Use createUri to resolve host:port:
```java
    service.createUri(PATH)
```

### Hystrix
use fallback for notifying ServicecUriProvider an error and make a second call with a new server:
```java
    @Override
    protected String getFallback() {
        service.notifyError();
        return client.target(service.createUri(PATH)).request().get(String.class);
    }
```
