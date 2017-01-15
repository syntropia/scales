import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import studio.lysid.scales.facade.FacadeVerticle;
import studio.lysid.scales.query.QueryVerticle;

@RunWith(VertxUnitRunner.class)
public class FacadeVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        this.vertx = Vertx.vertx();
        this.vertx.deployVerticle(QueryVerticle.class.getName(), ar -> {
            if (ar.succeeded()) {
                this.vertx.deployVerticle(FacadeVerticle.class.getName(), context.asyncAssertSuccess());
            } else {
                context.fail(ar.cause());
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

        vertx.createHttpClient().getNow(8080, "localhost", "/",
                response -> {
                    response.handler(body -> {
                        context.assertTrue(body.toString().contains("scale #42"));
                        async.complete();
                    });
                });
    }
}
