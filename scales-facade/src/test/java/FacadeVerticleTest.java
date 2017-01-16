import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import studio.lysid.scales.Defaults;
import studio.lysid.scales.facade.FacadeVerticle;
import studio.lysid.scales.query.QueryVerticle;

@RunWith(VertxUnitRunner.class)
public class FacadeVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        this.vertx = Vertx.vertx();
        this.vertx.deployVerticle(QueryVerticle.class.getName(), qvar -> {
            if (qvar.succeeded()) {
                DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", Defaults.FacadeHttpPort));
                this.vertx.deployVerticle(FacadeVerticle.class.getName(), options, fvar -> {
                    if (fvar.succeeded()) {
                        context.async().complete();
                    } else {
                        context.fail(fvar.cause());
                    }
                });
            } else {
                context.fail(qvar.cause());
            }
        });
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testSampleService(TestContext context) {

        final Async async = context.async();
        vertx.createHttpClient().getNow(
                Defaults.FacadeHttpPort, "localhost", "/scale/42",
                response -> {
                    response.handler(body -> {
                        System.out.println("TEST Received response: " + body.toString());
                        context.assertTrue(body.toString().contains("#42"));
                        async.complete();
                    });
                });
    }
}
