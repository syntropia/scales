package studio.lysid.scales.query.scale;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class QueryScaleServiceImpl implements QueryScaleService {

    private static final Logger logger = LoggerFactory.getLogger(QueryScaleServiceImpl.class);

    @Override
    public void findScaleById(String id, Handler<AsyncResult<String>> handler) {
        handler.handle(Future.succeededFuture("This is the scale #" + id));
    }
}
