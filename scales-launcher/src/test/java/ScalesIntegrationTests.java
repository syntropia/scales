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
