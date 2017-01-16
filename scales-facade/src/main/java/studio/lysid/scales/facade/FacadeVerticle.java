package studio.lysid.scales.facade;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import studio.lysid.scales.Defaults;
import studio.lysid.scales.deploy.EventBusServiceHelper;
import studio.lysid.scales.deploy.Service;
import studio.lysid.scales.query.scale.QueryScaleService;

public class FacadeVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(FacadeVerticle.class.getName());

    private Router router;
    private EventBusServiceHelper eventBusServiceHelper;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        this.eventBusServiceHelper = new EventBusServiceHelper(this.vertx, logger);
        this.router = Router.router(this.vertx);

        bindClientResources();
        bindServicesOnRoutes();

        int httpPort = this.config().getInteger("http.port", Defaults.FacadeHttpPort);
        vertx.createHttpServer()
                .requestHandler(router::accept)
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

    private void bindClientResources() {
        this.router.route(HttpMethod.GET, "/*").handler(StaticHandler.create("public"));
    }

    private void bindServicesOnRoutes() {
        bindQueryServices();
    }

    private void bindQueryServices() {
        QueryScaleService queryScaleService = eventBusServiceHelper.getProxy(QueryScaleService.class, Service.QueryScale.address);
        router.route(HttpMethod.GET, "/scale/42").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            logger.info("Request received: " + routingContext.request().uri());

            response.putHeader("content-type", "text/html");
            queryScaleService.findScaleById("42", ar -> {
                if (ar.succeeded()) {
                    response.end("<h1>Scale #42</h1><p>" + ar.result() + "</p>");
                } else {
                    response.end("Scale #42 does not exist!");
                }
            });
        });
    }

}