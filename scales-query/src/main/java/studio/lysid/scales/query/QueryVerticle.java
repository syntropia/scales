package studio.lysid.scales.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import studio.lysid.scales.deploy.DeployHelper;
import studio.lysid.scales.deploy.Services;
import studio.lysid.scales.query.scale.QueryScaleService;
import studio.lysid.scales.query.scale.QueryScaleServiceImpl;

import java.util.Arrays;
import java.util.List;

public class QueryVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(QueryVerticle.class);
    private DeployHelper deployHelper;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        this.deployHelper = new DeployHelper(this.vertx, logger);

        List<DeployHelper.Service> services = Arrays.asList(
                new DeployHelper.Service(Services.QueryScale, QueryScaleService.class, new QueryScaleServiceImpl())
        );
        logger.info("start: deploying and publishing {0} services", services.size());
        this.deployHelper.deployAndPublishServices(services, ar -> {
            if (ar.succeeded()) {
                logger.info("start: all services successfully deployed on the EventBus and published to Discovery");
                startFuture.complete();
            } else {
                logger.fatal("start: error when deploying and publishing services", ar.cause());
                startFuture.fail(ar.cause());
            }
        });
    }


}
