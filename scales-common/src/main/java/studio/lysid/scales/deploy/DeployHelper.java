package studio.lysid.scales.deploy;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ProxyHelper;
import studio.lysid.scales.Defaults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeployHelper {

    private Vertx vertx;
    private Logger logger;
    private ServiceDiscovery discovery;

    public Map<String, Record> publishedServices;

    public DeployHelper(Vertx vertx, Logger logger) {
        this.vertx = vertx;
        this.logger = logger;
        this.discovery = ServiceDiscovery.create(vertx);
        this.publishedServices = new HashMap<>(1);
    }

    public static class Service<T> {
        public final String name;
        public final String address;
        public final Class<T> interfaceType;
        public final T implementation;

        public Service(String name, Class<T> interfaceType, T implementation) {
            this.name = name;
            this.interfaceType = interfaceType;
            this.implementation = implementation;
            this.address = name + Defaults.SuffixAppendedToServiceNameToBuildServiceAddress;
        }
    }
    public Future<Record> deployAndPublishService(Service service) {
        logger.info("DeployHelper: deploying service [{0}] for interface {1} with implementation {2} at event bus address [{3}]", service.name, service.interfaceType.getName(), service.implementation.getClass().getName(), service.address);

        Future<Record> resultFuture = Future.future();

        ProxyHelper.registerService(service.interfaceType, this.vertx, service.implementation, service.address);
        Record record = EventBusService.createRecord(service.name, service.address, service.interfaceType);

        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                this.publishedServices.put(service.name, record);
                logger.info("DeployHelper: service [{0}] successfully published in discovery.", service.name);
                resultFuture.complete(record);
            } else {
                logger.error("DeployHelper: service [{0}] could not be published in discovery.", ar.cause(), service.name);
                resultFuture.fail(ar.cause());
            }
        });

        return resultFuture;
    }

    public void deployAndPublishServices(List<Service> services, Handler<AsyncResult<Void>> handler) {
        List<Future> deployFutures = services.stream()
                .map(service -> deployAndPublishService(service))
                .collect(Collectors.toList());

        CompositeFuture.all(deployFutures).setHandler(ar -> {
            if (ar.succeeded()) {
                handler.handle(Future.succeededFuture());
            } else {
                handler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

}
