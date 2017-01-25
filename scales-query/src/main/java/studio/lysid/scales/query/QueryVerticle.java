package studio.lysid.scales.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import studio.lysid.scales.deploy.EventBusServiceHelper;
import studio.lysid.scales.deploy.Service;
import studio.lysid.scales.query.scale.QueryScaleService;
import studio.lysid.scales.query.scale.QueryScaleServiceImpl;

public class QueryVerticle extends AbstractVerticle {

    private final Logger logger = LoggerFactory.getLogger(QueryVerticle.class);

    private MongoClient db;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        JsonObject dbConfig = new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name","scales-query");
        this.db = MongoClient.createShared(this.vertx, dbConfig);

        new EventBusServiceHelper(this.vertx, logger)
                .publish(QueryScaleService.class, new QueryScaleServiceImpl(this.db), Service.QueryScale.address);


        startFuture.complete();
    }

}
