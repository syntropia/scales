package studio.lysid.scales.facade;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import studio.lysid.scales.Defaults;
import studio.lysid.scales.deploy.EventBusServiceHelper;
import studio.lysid.scales.deploy.Service;
import studio.lysid.scales.query.scale.QueryScaleService;

public class FacadeVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(FacadeVerticle.class.getName());

    private QueryScaleService queryScaleService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        EventBusServiceHelper eventBusServiceHelper = new EventBusServiceHelper(this.vertx, logger);
        this.queryScaleService = eventBusServiceHelper.getProxy(QueryScaleService.class, Service.QueryScale.address);

        int httpPort = this.config().getInteger("http.port", Defaults.FacadeHttpPort);

        vertx.createHttpServer()
                .requestHandler(req -> {
                    logger.info("Request received: " + req.uri());
                    this.queryScaleService.findScaleById("42", qsar -> {
                        if (qsar.succeeded()) {
                            req.response().end("<h1>We've got the scale #" + qsar.result() + "</h1>");
                        } else {
                            req.response().end("We found nothing.");
                        }
                    });
                })
                .listen(httpPort,
                        result -> {
                            if (result.succeeded()) {
                                logger.info("HTTP server now listening on port {0}", httpPort);
                                startFuture.complete();
                            } else {
                                logger.fatal("Error when starting HTTP server", result.cause());
                                startFuture.fail(result.cause());
                            }
                        });
    }
}