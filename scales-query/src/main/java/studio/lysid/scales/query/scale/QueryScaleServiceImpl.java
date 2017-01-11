package studio.lysid.scales.query.scale;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class QueryScaleServiceImpl implements QueryScaleService {

    @Override
    public void findScaleById(String id, Handler<AsyncResult<String>> handler) {
        handler.handle(Future.succeededFuture("This is the scale #" + id));
    }
}
