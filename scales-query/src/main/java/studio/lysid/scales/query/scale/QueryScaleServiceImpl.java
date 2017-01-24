package studio.lysid.scales.query.scale;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class QueryScaleServiceImpl implements QueryScaleService {

    private static final Logger logger = LoggerFactory.getLogger(QueryScaleServiceImpl.class);

    @Override
    public void findScaleById(int id, Handler<AsyncResult<JsonObject>> handler) {
        logger.debug("findScaleById called with parameters: id={0}", id);
        if (id > 0) {
            handler.handle(Future.succeededFuture(
                    new JsonObject().put("id", id).put("title", "This is the scale #" + id))
            );
        } else {
            handler.handle(Future.failedFuture("Scale #" + id + " does not exist"));
        }
    }
}
