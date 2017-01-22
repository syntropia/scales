package studio.lysid.scales.deploy;

import io.vertx.core.AbstractVerticle;

public class LauncherVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        System.out.println("This is the main verticle speaking.");
    }
}
