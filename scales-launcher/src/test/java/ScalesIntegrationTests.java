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

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import studio.lysid.scales.deploy.LauncherVerticle;
import studio.lysid.scales.deploy.Verticle;

@RunWith(VertxUnitRunner.class)
public class ScalesIntegrationTests {

    private Vertx vertx;

    @BeforeClass
    public void verticlesSetUp(TestContext context) {
        this.vertx = Vertx.vertx();
        this.vertx.deployVerticle(LauncherVerticle.class.getName(), ar -> {
            if (ar.succeeded()) {
                context.async().complete();
            } else {
                context.fail(ar.cause());
            }
        });
    }

    @AfterClass
    public void verticlesTearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }



    @Test
    public void testSampleService(TestContext context) {

        final Async async = context.async();
        HttpClient client = vertx.createHttpClient();
        int deployedHttpPort = Verticle.Facade.deploymentOptions.getConfig().getInteger("http-port");

        // Any id should be accepted in this first test implementation
        client.getNow(
                deployedHttpPort, "localhost", "/scale/13",
                response -> {
                    response.handler(body -> {
                        context.assertTrue(body.toString().contains("#13"));
                    });
                });

        // Negative ids are incorrect, they should be rejected
        client.getNow(
                deployedHttpPort, "localhost", "/scale/-13",
                response -> {
                    response.handler(body -> {
                        context.assertTrue(body.toString().contains("does not exist"));
                    });
                });

        async.complete();
    }
}
