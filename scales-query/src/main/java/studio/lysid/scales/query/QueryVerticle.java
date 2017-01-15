package studio.lysid.scales.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import studio.lysid.scales.deploy.EventBusServiceHelper;
import studio.lysid.scales.deploy.Service;
import studio.lysid.scales.query.scale.QueryScaleService;
import studio.lysid.scales.query.scale.QueryScaleServiceImpl;

public class QueryVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(QueryVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        new EventBusServiceHelper(this.vertx, logger)
                .publish(QueryScaleService.class, new QueryScaleServiceImpl(), Service.QueryScale.address);
        startFuture.complete();
    }

}
