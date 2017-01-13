package studio.lysid.scales.deploy;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ProxyHelper;
import studio.lysid.scales.Defaults;

import java.util.HashMap;
import java.util.Map;

public class DeployHelper {


    private Vertx vertx;
    private ServiceDiscovery discovery;

    public Map<String, Record> publishedServices;


    public DeployHelper(Vertx vertx) {
        this.vertx = vertx;
        this.discovery = ServiceDiscovery.create(vertx);
        this.publishedServices = new HashMap<>(1);
    }

    public <T> void deployService(String serviceName, Class<T> serviceInterfaceClass, T serviceImplementation, Handler<AsyncResult<Record>> resultHandler) {

        String serviceAddress = serviceName + Defaults.SuffixAppendedToServiceNameToBuildServiceAddress;
        ProxyHelper.registerService(serviceInterfaceClass, this.vertx, serviceImplementation, serviceAddress);
        Record record = EventBusService.createRecord(serviceName, serviceAddress, serviceInterfaceClass);

        discovery.publish(record, ar -> {
            if (ar.succeeded()) {
                this.publishedServices.put(serviceName, record);
                resultHandler.handle(Future.succeededFuture(record));
            } else {
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }
}
