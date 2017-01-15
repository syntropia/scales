package studio.lysid.scales.deploy;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.serviceproxy.ProxyHelper;

public class EventBusServiceHelper {

    private final Vertx vertx;
    private final Logger logger;

    public EventBusServiceHelper(Vertx vertx, Logger logger) {
        this.vertx = vertx;
        this.logger = logger;
    }

    @Fluent
    public <T extends EventBusService> EventBusServiceHelper publish(Class<T> serviceInterface, T serviceImplementation, String address) {
        this.logger.info("Publishing {0} on EventBus, listening at {1}", serviceInterface.getName(), address);
        ProxyHelper.registerService(serviceInterface, this.vertx, serviceImplementation, address);
        return this;
    }

    public <T extends EventBusService> T getProxy(Class<T> serviceInterface, String address) {
        this.logger.info("Creating EventBus proxy for {0}, calling at {1}", serviceInterface.getName(), address);
        return ProxyHelper.createProxy(serviceInterface, this.vertx, address);
    }

}
