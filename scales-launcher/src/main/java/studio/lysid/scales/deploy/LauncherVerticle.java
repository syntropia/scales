package studio.lysid.scales.deploy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import studio.lysid.scales.facade.FacadeVerticle;
import studio.lysid.scales.query.QueryVerticle;

public class LauncherVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        // TODO Handle an optional deployment config file if provided in classpath

        // Default deployment is one instance of each verticle.
        // We can safely deploy Command, EventStore, and Query in parallel.
        // Facade must be the last one.

        Future<Void> startCommandFuture = Future.future();
        DeploymentOptions commandDeploymentOptions = new DeploymentOptions();
        startCommandVerticle(commandDeploymentOptions, startCommandFuture);

        Future<Void> startEventStoreFuture = Future.future();
        DeploymentOptions eventStoreDeploymentOptions = new DeploymentOptions();
        startEventStoreVerticle(eventStoreDeploymentOptions, startEventStoreFuture);

        Future<Void> startQueryFuture = Future.future();
        DeploymentOptions queryDeploymentOptions = new DeploymentOptions();
        startQueryVerticle(queryDeploymentOptions, startQueryFuture);

        CompositeFuture.all(startCommandFuture, startEventStoreFuture, startQueryFuture).setHandler(ar -> {
            if (ar.succeeded()) {
                DeploymentOptions facadeDeploymentOptions = new DeploymentOptions();
                startFacadeVerticle(facadeDeploymentOptions, startFuture);
            } else {
                startFuture.fail(ar.cause());
            }}
        );
    }

    private void startCommandVerticle(DeploymentOptions options, Future<Void> startFuture) {
        // TODO Deploy Command verticle when available
        startFuture.complete();
    }

    private void startEventStoreVerticle(DeploymentOptions options, Future<Void> startFuture) {
        // TODO Deploy EventStore verticle when available
        startFuture.complete();
    }

    private void startQueryVerticle(DeploymentOptions options, Future<Void> startFuture) {
        this.vertx.deployVerticle(new QueryVerticle(), options, ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }

    private void startFacadeVerticle(DeploymentOptions options, Future<Void> startFuture) {
        this.vertx.deployVerticle(new FacadeVerticle(), options, ar -> {
            if (ar.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(ar.cause());
            }
        });
    }
}
