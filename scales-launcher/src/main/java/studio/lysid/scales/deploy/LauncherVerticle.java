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

package studio.lysid.scales.deploy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class LauncherVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(LauncherVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        DeploymentOptionsParser.parseVerticleDeploymentOptionsJsonFile();

        // We can safely deploy Command, EventStore, and Query in parallel.
        // Facade must be the last one.

        Future<Void> startEventStoreFuture = startVerticle(this.vertx, Verticle.EventStore);
        Future<Void> startCommandFuture = startVerticle(this.vertx, Verticle.Command);
        Future<Void> startQueryFuture = startVerticle(this.vertx, Verticle.Query);

        CompositeFuture.all(startCommandFuture, startEventStoreFuture, startQueryFuture).setHandler(ar -> {
            if (ar.succeeded()) {
                startFuture.setHandler(startVerticle(this.vertx, Verticle.Facade).completer());
            } else {
                startFuture.fail(ar.cause());
            }}
        );
    }

    public static Future<Void> startVerticle(Vertx vertx, Verticle verticle) {
        Future<Void> startFuture = Future.future();

        int instancesToStart = verticle.deploymentOptions.getInstances();
        if (instancesToStart == 0) {
            startFuture.complete();
        } else {
            logger.info("Starting verticle [{0}] with {1} instance(s)...", verticle.shortName, instancesToStart);

            if (verticle.implementation == null) {
                /* TODO For initializing projects only */
                startFuture.complete();
                logger.info("Verticle [{0}] not yet implemented, skipping.", verticle.shortName);
            } else {
                logger.info("Deploying verticle class [{0}]", verticle.implementation.getName());
                vertx.deployVerticle(verticle.implementation.getName(), verticle.deploymentOptions, ar -> {
                    if (ar.succeeded()) {
                        logger.info("Verticle [{0}] started successfully.", verticle.shortName);
                        startFuture.complete();
                    } else {
                        startFuture.fail(ar.cause());
                    }
                });
            }
        }

        return startFuture;
    }
}
