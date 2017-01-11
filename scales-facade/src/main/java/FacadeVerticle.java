import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class FacadeVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        vertx.createHttpServer()
                .requestHandler(req -> {
                    req.response().end("<h1>Hello, world!</h1>");
                })
                .listen(this.config().getInteger("http.port", Defaults.httpPort),
                        result -> {
                            if (result.succeeded()) {
                                startFuture.complete();
                            } else {
                                startFuture.fail(result.cause());
                            }
                        });
    }
}
