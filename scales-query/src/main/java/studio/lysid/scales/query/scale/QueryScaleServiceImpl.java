package studio.lysid.scales.query.scale;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

public class QueryScaleServiceImpl implements QueryScaleService {

    private static final Logger logger = LoggerFactory.getLogger(QueryScaleServiceImpl.class);
    private static final String ScaleCollection = "scale";

    private final MongoClient db;

    public QueryScaleServiceImpl(MongoClient db) {
        super();
        this.db = db;
    }

    @Override
    public void findScaleById(int id, Handler<AsyncResult<JsonObject>> handler) {
        logger.debug("findScaleById called with parameters: id={0}", id);

        db.findOne(ScaleCollection, new JsonObject().put("_id", Integer.toString(id)), new JsonObject(),
            res -> {
                if (res.succeeded() && res.result() != null) {
                    handler.handle(Future.succeededFuture(res.result()));
                } else {
                    handler.handle(Future.failedFuture("Scale #" + id + " does not exist"));
                }
            });
    }
}
