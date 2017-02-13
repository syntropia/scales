/*
 * Copyright (C) 2017 Frederic Monjo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package studio.lysid.scales.facade;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import studio.lysid.scales.deploy.service.EventBusServiceHelper;
import studio.lysid.scales.deploy.service.Service;
import studio.lysid.scales.query.scale.QueryScaleService;

public class FacadeVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(FacadeVerticle.class.getName());

    private Router router;
    private EventBusServiceHelper eventBusServiceHelper;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        this.eventBusServiceHelper = new EventBusServiceHelper(this.vertx, logger);
        this.router = Router.router(this.vertx);

        bindServicesOnRoutes();

        // Static resources should be the last route because
        // it binds everything else to the public folder static resources
        bindClientStaticResources();

        Integer httpPort = this.config().getInteger("http.port");
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

    private void bindClientStaticResources() {
        this.router.route(HttpMethod.GET, "/*").handler(StaticHandler.create("public"));
    }

    private void bindServicesOnRoutes() {
        bindQueryServices();
    }

    private void bindQueryServices() {
        QueryScaleService queryScaleService = eventBusServiceHelper.getProxy(QueryScaleService.class, Service.QueryScale.address);
        router.route(HttpMethod.GET, "/scale/:id").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            logger.info("Request received: " + routingContext.request().uri());
            int id = Integer.parseInt(routingContext.request().getParam("id"));

            response.putHeader("content-type", "text/html");
            queryScaleService.findScaleById(id, ar -> {
                if (ar.succeeded()) {
                    response.end(ar.result().encodePrettily());
                } else {
                    response.setStatusCode(404).end("Scale #" + id + " does not exist!");
                }
            });
        });
    }

}