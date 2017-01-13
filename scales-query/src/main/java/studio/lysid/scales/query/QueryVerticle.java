package studio.lysid.scales.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.servicediscovery.Record;
import studio.lysid.scales.deploy.DeployHelper;
import studio.lysid.scales.deploy.Services;
import studio.lysid.scales.query.scale.QueryScaleService;
import studio.lysid.scales.query.scale.QueryScaleServiceImpl;

import java.util.Arrays;

public class QueryVerticle extends AbstractVerticle {


    private DeployHelper deployHelper;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        // Deploy services on discovery
        this.deployHelper = new DeployHelper(this.vertx);

        // Scale service
        Future<Record> scaleServiceFuture =  Future.future();
        QueryScaleServiceImpl scaleService = new QueryScaleServiceImpl();
        deployHelper.deployService(Services.QueryScale, QueryScaleService.class, scaleService, scaleServiceFuture.completer());

        // Ready when all services are deployed
        CompositeFuture.all(Arrays.asList(
                scaleServiceFuture
        )).setHandler(ar -> {
           if (ar.succeeded()) {
               startFuture.complete();
           } else {
               startFuture.fail(ar.cause());
           }
        });
    }


}
