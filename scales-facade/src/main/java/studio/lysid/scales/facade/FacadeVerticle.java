package studio.lysid.scales.facade;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import studio.lysid.scales.Defaults;
import studio.lysid.scales.query.scale.QueryScaleService;

import java.util.Arrays;

public class FacadeVerticle extends AbstractVerticle {

    private QueryScaleService queryScaleService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        ServiceDiscovery discovery = ServiceDiscovery.create(this.vertx);

        // Resolve Query services
        Future<QueryScaleService> queryScaleServiceFuture = Future.future();
        EventBusService.getProxy(discovery, QueryScaleService.class, ar -> {
            if (ar.succeeded()) {
                this.queryScaleService = ar.result();
                queryScaleServiceFuture.complete();
            } else {
                queryScaleServiceFuture.fail(ar.cause());
            }
        });

        CompositeFuture.all(Arrays.asList(
                queryScaleServiceFuture
        )).setHandler(ar -> {
            if (ar.succeeded()) {
                vertx.createHttpServer()
                        .requestHandler(req -> {
                            this.queryScaleService.findScaleById("42", qsar -> {
                                if (qsar.succeeded()) {
                                    req.response().end("<h1>We've got the scale #" + qsar.result()  + "</h1>");
                                } else {
                                    req.response().end("We found nothing.");
                                }
                            });
                        })
                        .listen(this.config().getInteger("http.port", Defaults.FacadeHttpPort),
                                result -> {
                                    if (result.succeeded()) {
                                        startFuture.complete();
                                    } else {
                                        startFuture.fail(result.cause());
                                    }
                                });
            } else {
                startFuture.fail(ar.cause());
            }
        });

    }
}
