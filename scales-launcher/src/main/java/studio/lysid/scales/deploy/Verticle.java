package studio.lysid.scales.deploy;

import io.vertx.core.DeploymentOptions;
import studio.lysid.scales.facade.FacadeVerticle;
import studio.lysid.scales.query.QueryVerticle;

public enum Verticle {

    EventStore("eventstore", null/*TODO*/),
    Command("command", null/*TODO*/),
    Query("query", QueryVerticle.class),
    Facade("facade", FacadeVerticle.class);



    public final String shortName;
    public final Class<? extends io.vertx.core.Verticle> implementation;
    public final DeploymentOptions deploymentOptions;

    Verticle(String shortName, Class<? extends io.vertx.core.Verticle> implementation) {
        this.implementation = implementation;
        this.shortName = shortName;
        this.deploymentOptions = new DeploymentOptions();
    }
}
